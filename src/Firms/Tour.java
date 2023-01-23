package Firms;

public class Tour
{
    String name;
    String country;
    int num;
    String dateOfSale;
    String dateStart;
    int sum;
    int isSoldOut;
    String comment;

    public Tour(String name, String country, int num, String dateOfSale, String dateStart, int sum, int isSoldOut, String comment)
    {
        this.name = name;
        this.country = country;
        this.num = num;
        this.dateOfSale = dateOfSale;
        this.dateStart = dateStart;
        this.sum = sum;
        this.isSoldOut = isSoldOut;
        this.comment = comment;
    }
}
