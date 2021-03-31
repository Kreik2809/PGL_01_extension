package tool.sql;

import java.util.ArrayList;

public class PortfolioSupplyPointDB extends DB
{
    private static String[] COLUMNS = {"PORTFOLIO_ID", "SUPPLY_POINT_ID"};

    private PortfolioSupplyPointDB()
    {
        super("PORTFOLIO_SUPPLY_POINT", COLUMNS);
    }

    public static ArrayList<String> getPortfolioSupplyPointDB(String portfolioID, String supplyPointID)
    {
        PortfolioSupplyPointDB db = new PortfolioSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], portfolioID));
        liste.add(new DataTable(COLUMNS[1], supplyPointID));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getPortfolioSupplyPointByIDDB(String portfolioID)
    {
        PortfolioSupplyPointDB db = new PortfolioSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], portfolioID));
        return db.getDataDB(liste);
    }

    public static boolean addPortfolioSupplyPointDB(String portfolioID, String supplyPointID)
    {
        PortfolioSupplyPointDB db = new PortfolioSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], portfolioID));
        liste.add(new DataTable(COLUMNS[1], supplyPointID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], portfolioID));
        liste2.add(new DataTable(COLUMNS[1], supplyPointID));
        return db.addDataDB(liste, liste2);
    }

    public static boolean modifyPortfolioSupplyPointDB(String oldPortfolioID, String oldSupplyPointID, String newPortfolioID, String newSupplyPointID)
    {
        PortfolioSupplyPointDB db = new PortfolioSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], oldPortfolioID));
        liste.add(new DataTable(COLUMNS[1], oldSupplyPointID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newPortfolioID));
        liste2.add(new DataTable(COLUMNS[1], newSupplyPointID));
        return db.modifyDataDB(liste, liste2);
    }

    public static boolean deletePortfolioSupplyPointDB(String portfolioID, String supplyPointID)
    {
        PortfolioSupplyPointDB db = new PortfolioSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], portfolioID));
        liste.add(new DataTable(COLUMNS[1], supplyPointID));
        return db.deleteDataDB(liste);
    }

    public static boolean deletePortfolioSupplyPointDB(String portfolioID)
    {
        PortfolioSupplyPointDB db = new PortfolioSupplyPointDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], portfolioID));
        return db.deleteDataDB(liste);
    }

}
