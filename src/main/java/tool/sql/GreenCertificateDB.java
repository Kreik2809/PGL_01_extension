package tool.sql;

import user.Period;

import java.util.ArrayList;

public class GreenCertificateDB extends DB{

    private static String[] COLUMNS = {"ID", "METERID", "REGIONALID", "VALUE", "ASKINGDATE", "VALIDATIONDATE"};

    protected GreenCertificateDB() {
        super("GREENCERTIFICATE", COLUMNS);
    }

    public static ArrayList<String> getGreenCertificateByidDB(String id)
    {
        GreenCertificateDB db = new GreenCertificateDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getGreenCertificateByMeterIDDB(String meterId)
    {
        GreenCertificateDB db = new GreenCertificateDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[1], meterId));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getGreenCertificateByRegionalIDDB(String regionalID)
    {
        GreenCertificateDB db = new GreenCertificateDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[2], regionalID));
        return db.getDataDB(liste);
    }

    public static boolean addGreenCertificateDB(String meterID, String regionalID, String value, String aDate, String vDate)
    {
        GreenCertificateDB db = new GreenCertificateDB();
        String id = String.valueOf(getNewID());
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], id));
        liste2.add(new DataTable(COLUMNS[1], meterID));
        liste2.add(new DataTable(COLUMNS[2], regionalID));
        liste2.add(new DataTable(COLUMNS[3], value));
        liste2.add(new DataTable(COLUMNS[4], aDate));
        liste2.add(new DataTable(COLUMNS[5], vDate));
        return db.addDataDB(liste, liste2);
    }

    public static boolean deleteGreenCertificateDB(String id)
    {
        GreenCertificateDB db = new GreenCertificateDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        return db.deleteDataDB(liste);
    }

    public static boolean modifyContractDB(String LastID, String newID, String EAN, String rID, String v, String aDate, String vDate)
    {
        GreenCertificateDB db = new GreenCertificateDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], LastID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newID));
        liste2.add(new DataTable(COLUMNS[1], EAN));
        liste2.add(new DataTable(COLUMNS[2], rID));
        liste2.add(new DataTable(COLUMNS[3], v));
        liste2.add(new DataTable(COLUMNS[4], aDate));
        liste2.add(new DataTable(COLUMNS[5], vDate));
        return db.modifyDataDB(liste, liste2);
    }

    public static String getNewID()
    {
        GreenCertificateDB db = new GreenCertificateDB();
        return db.getNewID(COLUMNS[0]);
    }
}
