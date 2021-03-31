package tool.sql;

import java.sql.Date;
import java.util.ArrayList;

public class DataSupplyPointDB extends DB
{
    public static final String[] COLUMNS = {"EAN18", "DATE", "VALUE"};

    private DataSupplyPointDB() {
        super("DATA_SUPPLY_POINT", COLUMNS);
    }

    public static ArrayList<String> getDataSupplyPointDB(String EAN18, Date date)
    {
        DataSupplyPointDB db = new DataSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        liste.add(new DataTable(COLUMNS[1], date.toString()));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getDataSupplyPointByEAN18DB(String EAN18)
    {
        DataSupplyPointDB db = new DataSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        return db.getDataDB(liste);
    }

    public static boolean addDataSupplyPointDB(String EAN18, Date date, double value)
    {
        DataSupplyPointDB db = new DataSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        liste.add(new DataTable(COLUMNS[1], date.toString()));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], EAN18));
        liste2.add(new DataTable(COLUMNS[1], date.toString()));
        liste2.add(new DataTable(COLUMNS[2], String.valueOf(value)));
        return db.addDataDB(liste, liste2);
    }

    public static boolean modifyDataSupplyPointDB(String oldEAN18, Date oldDate, String newEAN18, Date newDate, double value)
    {
        DataSupplyPointDB db = new DataSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], oldEAN18));
        liste.add(new DataTable(COLUMNS[1], oldDate.toString()));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newEAN18));
        liste2.add(new DataTable(COLUMNS[1], newDate.toString()));
        liste2.add(new DataTable(COLUMNS[2], String.valueOf(value)));
        return db.modifyDataDB(liste, liste2);
    }

    public static boolean deleteDataSupplyPointDB(String ean, Date date){
        DataSupplyPointDB db = new DataSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], ean));
        liste.add(new DataTable(COLUMNS[1], date.toString()));
        return db.deleteDataDB(liste);
    }
}
