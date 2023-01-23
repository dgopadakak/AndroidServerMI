package Firms;

import java.util.ArrayList;
import java.util.Objects;

public class TravelCompanyOperator
{
    private final int id = 1;
    private ArrayList<TravelCompany> travelCompanies = new ArrayList<>();

    public void addTour(String groupName, Tour tour)
    {
        boolean isNewGroupNeeded = true;
        for (TravelCompany travelCompany : travelCompanies)
        {
            if (Objects.equals(travelCompany.name, groupName))
            {
                isNewGroupNeeded = false;
                travelCompany.listOfTours.add(tour);
                break;
            }
        }
        if (isNewGroupNeeded)
        {
            ArrayList<Tour> tempArrayList = new ArrayList<>();
            tempArrayList.add(tour);
            travelCompanies.add(new TravelCompany(groupName, tempArrayList));
        }
    }

    public void delTour(int groupId, int examId)
    {
        travelCompanies.get(groupId).listOfTours.remove(examId);
    }

    public void editTour(int groupId, int examId, Tour newTour)
    {
        travelCompanies.get(groupId).listOfTours.set(examId, newTour);
    }

    public ArrayList<TravelCompany> getTravelCompanies()
    {
        return travelCompanies;
    }

    public void setTravelCompanies(ArrayList<TravelCompany> travelCompanies)
    {
        this.travelCompanies = travelCompanies;
    }
}
