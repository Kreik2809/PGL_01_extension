package tool.sql;

import java.util.ArrayList;

public class PortfolioDB extends DB
{
    public static final String[] COLUMNS = {"ID", "NAME"};
    private PortfolioDB() {
        super("PORTFOLIO", COLUMNS);
    }

    public static ArrayList<String> getPortfolioDB(String id)
    {
        PortfolioDB db = new PortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        return db.getDataDB(liste);
    }

    public static boolean addPortfolioDB(String id, String name)
    {
        PortfolioDB db = new PortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], id));
        liste2.add(new DataTable(COLUMNS[1], name));
        return db.addDataDB(liste, liste2);
    }

    public static boolean modifyPortfolioDB(String oldID, String newID, String name)
    {
        PortfolioDB db = new PortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], oldID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newID));
        liste2.add(new DataTable(COLUMNS[1], name));
        return db.modifyDataDB(liste, liste2);
    }

    public static String getNewID()
    {
        PortfolioDB db = new PortfolioDB();
        return db.getNewID(COLUMNS[0]);
    }

    public static boolean deletePortfolioDB(String id)
    {
        PortfolioDB db = new PortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        return db.deleteDataDB(liste);
    }
}
