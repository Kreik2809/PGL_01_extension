package tool.sql;

import user.ProductionType;

import java.util.ArrayList;

public class SolarPanelInfoDB extends DB {

    public static final String[] COLUMNS = {"EAN", "PUISSANCE", "SUPERFICIE", "NOMBRE"};

    protected SolarPanelInfoDB() {
        super("SOLARPANELINFO", COLUMNS);
    }

    public static ArrayList<String> getSolarPanelInfoDB(String EAN18)
    {
        SolarPanelInfoDB db = new SolarPanelInfoDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        return db.getDataDB(liste);
    }

    public static boolean addSolarPanelInfoDB(String EAN18, String puissance, String superficie, String nombre)
    {
        SolarPanelInfoDB db = new SolarPanelInfoDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], EAN18));
        liste2.add(new DataTable(COLUMNS[1], puissance));
        liste2.add(new DataTable(COLUMNS[2], superficie));
        liste2.add(new DataTable(COLUMNS[3], nombre));
        return db.addDataDB(liste, liste2);
    }

    public static boolean deleteSolarPanelInfoDB(String ean){
        ProductionInfoDB db = new ProductionInfoDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], ean));
        return db.deleteDataDB(liste);
    }
}
