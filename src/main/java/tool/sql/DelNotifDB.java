package tool.sql;

import java.util.ArrayList;

public class DelNotifDB extends DB{

    private static String[] COLUMNS = {"USERNAME", "EAN"};

    protected DelNotifDB() {
        super("DELNOTIF", COLUMNS);
    }

    public static ArrayList<String> getDelNotifDB(String username)
    {
        DelNotifDB db = new DelNotifDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], username));
        return db.getDataDB(liste);
    }

    public static boolean addDelNotifDB(String username, String ean)
    {
        DelNotifDB db = new DelNotifDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], username));
        liste.add(new DataTable(COLUMNS[1], ean));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], username));
        liste2.add(new DataTable(COLUMNS[1], ean));
        return db.addDataDB(liste, liste2);
    }

    public static boolean deleteDelNotifDB(String id, String ean){
        DelNotifDB db = new DelNotifDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        liste.add(new DataTable(COLUMNS[1], ean));
        return db.deleteDataDB(liste);
    }
}
