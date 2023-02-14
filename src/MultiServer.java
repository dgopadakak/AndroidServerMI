import Firms.Tour;
import Firms.TravelCompanyOperator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MultiServer
{
    private ServerSocket serverSocket;
    private static TravelCompanyOperator tco = new TravelCompanyOperator();
    private static String goJSON;
    private final static String filePath = "info.txt";

    public void start(int port) throws IOException
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try {
            goJSON = readFile(filePath, StandardCharsets.UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        tco = gson.fromJson(goJSON, TravelCompanyOperator.class);

//        tco.addTour("Сто Дорог", new Tour("Солнечная Турция", "Турция", 3,
//                "12.12.2022", "01.02.2023", 450000, 0,
//                "Посетите солнечную Турцию!"));
//
//        tco.addTour("Глобус", new Tour("Горячая Мексика", "Мексика", 5,
//                "20.12.2022", "15.02.2023", 2000000, 1,
//                "Узнайте все тайны горячей Мексики!"));
//
//        tco.addTour("Карта Мира", new Tour("Загадочный Египет", "Египет", 10,
//                "25.12.2022", "20.03.2023", 1000000, 1,
//                "Посетите загадочный Египет!"));
//        tco.addTour("Карта Мира", new Tour("Великий Китай", "Китай", 2,
//                "10.12.2022", "01.03.2022", 800000, 0,
//                "Посетите необъятный Китай!"));
//        tco.addTour("Карта Мира", new Tour("Интересный Вьетнам", "Вьетнам", 5,
//                "21.03.2021", "14.05.2021", 500000, 1,
//                "Посетите интересный Вьетнам и попробуйте местную кухню!"));
//
//        tco.addTour("Голд аэроплан", new Tour("Неповторимая Сирия", "Сирия", 100,
//                "01.05.2022", "03.05.2022", 9000000, 1,
//                "На свой страх и риск"));
//
//        tco.addTour("White Star Line", new Tour("Холодная атлантика", "США", 1316,
//                "02.01.1912", "02.04.1912", 100000, 1,
//                "Поплавайте на двери"));
//
//        goJSON = gson.toJson(tco);
//        writeFile(filePath, goJSON);
//        System.out.println("Done!");

        serverSocket = new ServerSocket(port);
        while (true)
        {
            new EchoClientHandler(serverSocket.accept()).start();
        }
    }

    public void stop() throws IOException
    {
        serverSocket.close();
    }

    private static class EchoClientHandler extends Thread
    {
        private final Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run()
        {
            try
            {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            String inputLine = null;
            while (true)
            {
                try
                {
                    if ((inputLine = in.readLine()) == null) break;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                if (".".equals(inputLine))
                {
                    out.println("bye");
                    break;
                }
                if ("REFRESH".equals(inputLine))
                {
                    out.println(goJSON);
                }
                if (inputLine != null)
                {
                    if ('d' == inputLine.charAt(0))     // d0,1
                    {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        String[] ids = inputLine.substring(1).split(",");
                        int groupID = Integer.parseInt(ids[0]);
                        int examID = Integer.parseInt(ids[1]);
                        tco.delTour(groupID, examID);
                        goJSON = gson.toJson(tco);
                        writeFile(filePath, goJSON);
                        out.println(goJSON);
                    }
                    if ('e' == inputLine.charAt(0))     // e0,3##json
                    {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        String[] parts = inputLine.substring(1).split("##");
                        String[] ids = parts[0].split(",");
                        int groupID = Integer.parseInt(ids[0]);
                        int examID = Integer.parseInt(ids[1]);
                        Tour tempTour = gson.fromJson(parts[1], Tour.class);
                        tco.editTour(groupID, examID, tempTour);
                        goJSON = gson.toJson(tco);
                        writeFile(filePath, goJSON);
                        out.println(goJSON);
                    }
                    if ('u' == inputLine.charAt(0))     // ujson
                    {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        TravelCompanyOperator tempGo = gson.fromJson(inputLine.substring(1), TravelCompanyOperator.class);
                        tco.setTravelCompanies(tempGo.getTravelCompanies());
                        goJSON = gson.toJson(tco);
                        writeFile(filePath, goJSON);
                    }
                    if ('a' == inputLine.charAt(0))
                    {
                        GsonBuilder gsonBuilder = new GsonBuilder();        // agroupName##json
                        Gson gson = gsonBuilder.create();
                        String[] parts = inputLine.substring(1).split("##");
                        Tour tempTour = gson.fromJson(parts[1], Tour.class);
                        tco.addTour(parts[0], tempTour);
                        goJSON = gson.toJson(tco);
                        writeFile(filePath, goJSON);
                        out.println(goJSON);
                    }
                }
            }

            try
            {
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            out.close();
            try
            {
                clientSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void writeFile(String path, String text)
    {
        try(FileWriter writer = new FileWriter(path, false))
        {
            writer.write(text);
            writer.flush();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}
