package tool.sql;

import tool.AccessLevelType;

import java.util.ArrayList;

public class EnergyConsumerPortfolioDB extends DB
{
    public static final String[] COLUMNS = {"USERNAME", "PORTFOLIO_ID", "ACCESS"};

    private EnergyConsumerPortfolioDB()
    {
        super("ENERGY_CONSUMER_PORTFOLIO", COLUMNS);
    }

    public static ArrayList<String> getEnergyConsumerPortfolioDB(String consumerID, String portfolioID)
    {
        EnergyConsumerPortfolioDB db = new EnergyConsumerPortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], consumerID));
        liste.add(new DataTable(COLUMNS[1], portfolioID));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getEnergyConsumerPortfolioByPortfolioIDDB(String portfolioID)
    {
        EnergyConsumerPortfolioDB db = new EnergyConsumerPortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[1], portfolioID));
        return db.getDataDB(liste);
    }

    public static ArrayList<String> getEnergyConsumerPortfolioManagerDB(String portfolioID)
    {
        EnergyConsumerPortfolioDB db = new EnergyConsumerPortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[2], AccessLevelType.MANAGING.name()));
        liste.add(new DataTable(COLUMNS[1], portfolioID));
        return db.getDataDB(liste);
    }

    /**
     *
     * @param username le nom d'utilisateur
     * @return la liste contenant les String ("USERNAME", "PORTFOLIO_ID", "ACCESS")
     */
    public static ArrayList<String> getEnergyConsumerPortfolioByUsernameDB(String username)
    {
        EnergyConsumerPortfolioDB db = new EnergyConsumerPortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], username));
        return db.getDataDB(liste);
    }

    public static boolean addEnergyConsumerPortfolioDB(String consumerID, String portfolioID, AccessLevelType access)
    {
        EnergyConsumerPortfolioDB db = new EnergyConsumerPortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], consumerID));
        liste.add(new DataTable(COLUMNS[1], portfolioID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], consumerID));
        liste2.add(new DataTable(COLUMNS[1], portfolioID));
        liste2.add(new DataTable(COLUMNS[2], access.name()));
        return db.addDataDB(liste, liste2);
    }

    public static boolean modifyEnergyConsumerPortfolioDB(String oldConsumerID, String oldPortfolioID,  String newConsumerID, String newPortfolioID, AccessLevelType access)
    {
        EnergyConsumerPortfolioDB db = new EnergyConsumerPortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], oldConsumerID));
        liste.add(new DataTable(COLUMNS[1], oldPortfolioID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newConsumerID));
        liste2.add(new DataTable(COLUMNS[1], newPortfolioID));
        liste2.add(new DataTable(COLUMNS[2], access.name()));
        return db.modifyDataDB(liste, liste2);
    }

    public static boolean deleteEnergyConsumerPortfolioDB(String consumerID, String portfolioID)
    {
        EnergyConsumerPortfolioDB db = new EnergyConsumerPortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], consumerID));
        liste.add(new DataTable(COLUMNS[1], portfolioID));
        return db.deleteDataDB(liste);
    }

    public static boolean deleteEnergyConsumerPortfolioDB(String portfolioID)
    {
        EnergyConsumerPortfolioDB db = new EnergyConsumerPortfolioDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[1], portfolioID));
        return db.deleteDataDB(liste);
    }
}
