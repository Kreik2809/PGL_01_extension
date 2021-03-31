package tool.sql;

import user.EnergyType;
import user.SupplyPointType;

import java.sql.Date;
import java.util.ArrayList;

public class SupplyPointDB extends DB
{
    private static String[] COLUMNS = {"EAN18", "ENERGYTYPE", "NAME", "STATE", "CONTRACTID", "DAYSTART", "SUPPLIERID", "TYPE"};

    private SupplyPointDB() {
        super("SupplyPoint", COLUMNS);
    }

    public static ArrayList<String> getSupplyPointDB(String EAN18)
    {
        SupplyPointDB db = new SupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getAllSupplyPointDB()
    {
        SupplyPointDB db = new SupplyPointDB();
        return db.getAllDataDB();
    }



    public static ArrayList<String> getSupplyPointByContractIDDB(String id)
    {
        SupplyPointDB db = new SupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[4], id));
        return db.getDataDB(liste);
    }


    public static boolean addSupplyPointDB(String EAN18, EnergyType energyType, String name, boolean state, String contractID, Date dateStart, String supplierID)
    {
        SupplyPointDB db = new SupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], EAN18));
        liste2.add(new DataTable(COLUMNS[1], energyType.name()));
        liste2.add(new DataTable(COLUMNS[2], name));
        if(state)
            liste2.add(new DataTable(COLUMNS[3], String.valueOf(1) ));
        else
            liste2.add(new DataTable(COLUMNS[3], String.valueOf(0) ));
        liste2.add(new DataTable(COLUMNS[4], contractID));
        liste2.add(new DataTable(COLUMNS[5], dateStart.toString()));
        liste2.add(new DataTable(COLUMNS[6], supplierID));
        return db.addDataDB(liste, liste2);
    }

    public static boolean addSupplyPointDB(String EAN18, EnergyType energyType, String name, boolean state,  Date dateStart, SupplyPointType st)
    {
        SupplyPointDB db = new SupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], EAN18));
        liste2.add(new DataTable(COLUMNS[1], energyType.name()));
        liste2.add(new DataTable(COLUMNS[2], name));
        if(state)
            liste2.add(new DataTable(COLUMNS[3], String.valueOf(1) ));
        else
            liste2.add(new DataTable(COLUMNS[3], String.valueOf(0)));
        if(dateStart == null)
            liste2.add(new DataTable(COLUMNS[5], "-1"));
        else
            liste2.add(new DataTable(COLUMNS[5], dateStart.toString()));
        liste2.add(new DataTable(COLUMNS[7], st.name()));
        return db.addDataDB(liste, liste2);
    }

    public static boolean modifySupplyPointDB(String oldEAN18, String newEAN18, EnergyType energyType, String name, Boolean state, String contractID, String dateStart, String supplierID, String sType)    {
        SupplyPointDB db = new SupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], oldEAN18));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newEAN18));
        liste2.add(new DataTable(COLUMNS[1], energyType.name()));
        liste2.add(new DataTable(COLUMNS[2], name));
        if(state)
            liste2.add(new DataTable(COLUMNS[3], String.valueOf(1) ));
        else
            liste2.add(new DataTable(COLUMNS[3], String.valueOf(0) ));
        liste2.add(new DataTable(COLUMNS[4], contractID));
        if(dateStart == null)
            liste2.add(new DataTable(COLUMNS[5], "-1"));
        else
            liste2.add(new DataTable(COLUMNS[5], dateStart));
        liste2.add(new DataTable(COLUMNS[6], supplierID));
        liste2.add(new DataTable(COLUMNS[7], sType));
        return db.modifyDataDB(liste, liste2);
    }

    public static boolean deleteSupplyPointDB(String EAN18)
    {
        SupplyPointDB db = new SupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        return db.deleteDataDB(liste);
    }
}
