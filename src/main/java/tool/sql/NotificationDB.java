package tool.sql;

import tool.AccessLevelType;

import java.util.ArrayList;

public class NotificationDB extends DB
{
    private static String[] COLUMNS = {"USERNAME", "ACCESS", "PORTFOLIO_ID"};

    private NotificationDB()
    {
        super("NOTIFICATION", COLUMNS);
    }

    public static ArrayList<String> getNotificationDB(String username, String portfolioID)
    {
        NotificationDB db = new NotificationDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], username));
        liste.add(new DataTable(COLUMNS[2], portfolioID));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getNotificationDB(String username)
    {
        NotificationDB db = new NotificationDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], username));
        return db.getDataDB(liste);
    }


    public static boolean addNotificationDB(String username, String portfolioID, AccessLevelType access)
    {
        NotificationDB db = new NotificationDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], username));
        liste.add(new DataTable(COLUMNS[2], portfolioID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], username));
        liste2.add(new DataTable(COLUMNS[2], portfolioID));
        liste2.add(new DataTable(COLUMNS[1], access.name()));
        return db.addDataDB(liste, liste2);
    }

    public static boolean modifyNotificationDB(String oldConsumerID, String oldPortfolioID,  String newConsumerID, String newPortfolioID, AccessLevelType access)
    {
        NotificationDB db = new NotificationDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], oldConsumerID));
        liste.add(new DataTable(COLUMNS[2], oldPortfolioID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newConsumerID));
        liste2.add(new DataTable(COLUMNS[2], newPortfolioID));
        liste2.add(new DataTable(COLUMNS[1], access.name()));
        return db.modifyDataDB(liste, liste2);
    }

    public static boolean deleteNotificationDB(String username, String portfolioID)
    {
        NotificationDB db = new NotificationDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], username));
        liste.add(new DataTable(COLUMNS[2], portfolioID));
        return db.deleteDataDB(liste);
    }
}
