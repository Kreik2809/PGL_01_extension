package tool.sql;

import user.ProductionType;
import java.util.ArrayList;

public class ProductionInfoDB extends DB{

    public static final String[] COLUMNS = {"EAN", "TYPE"};

    protected ProductionInfoDB() {
        super("PRODUCTIONTYPE", COLUMNS);
    }

    public static ArrayList<String> getProductionInfoDB(String EAN18)
    {
        ProductionInfoDB db = new ProductionInfoDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        return db.getDataDB(liste);
    }

    public static boolean addProductionInfoDB(String EAN18, ProductionType type)
    {
        ProductionInfoDB db = new ProductionInfoDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], EAN18));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], EAN18));
        liste2.add(new DataTable(COLUMNS[1], type.name()));
        return db.addDataDB(liste, liste2);
    }

    public static boolean deleteProductionInfoDB(String ean){
        ProductionInfoDB db = new ProductionInfoDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], ean));
        return db.deleteDataDB(liste);
    }
}
