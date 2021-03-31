package tool.sql;

import java.sql.Date;
import java.util.ArrayList;

public class SupplyPointHistoryDB extends DB
{
    private static String[] COLUMNS = {"EAN18", "USERNAME", "OPEN", "CLOSE"};

    private SupplyPointHistoryDB() {
        super("SUPPLY_POINT_HISTORY", COLUMNS);
    }

    public static ArrayList<String> getSupplyPointHistoryDB(String EAN18, String username)
    {
        SupplyPointHistoryDB db = new SupplyPointHistoryDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        liste.add(new DataTable(COLUMNS[1], username));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getSupplyPointHistoryByUsernameDB(String username)
    {
        SupplyPointHistoryDB db = new SupplyPointHistoryDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[1], username));
        return db.getDataDB(liste);
    }

    public static boolean addSupplyPointHistoryDB(String EAN18, String username, Date start, Date end)
    {
        SupplyPointHistoryDB db = new SupplyPointHistoryDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        liste.add(new DataTable(COLUMNS[1], username));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], EAN18));
        liste2.add(new DataTable(COLUMNS[1], username));
        liste2.add(new DataTable(COLUMNS[2], start.toString()));
        liste2.add(new DataTable(COLUMNS[3], end.toString()));
        return db.addDataDB(liste, liste2);
    }

    public static boolean modifySupplyPointHistoryDB(String oldEAN18, String oldUsername, String newEAN18, String newUsername, Date start, Date end)
    {
        SupplyPointHistoryDB db = new SupplyPointHistoryDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], oldEAN18));
        liste.add(new DataTable(COLUMNS[1], oldUsername));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newEAN18));
        liste2.add(new DataTable(COLUMNS[1], newUsername));
        liste2.add(new DataTable(COLUMNS[2], start.toString()));
        liste2.add(new DataTable(COLUMNS[3], end.toString()));
        return db.modifyDataDB(liste, liste2);
    }
}
