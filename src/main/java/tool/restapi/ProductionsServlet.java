package tool.restapi;


import tool.sql.DataTable;
import tool.sql.ProductionInfoDB;
import tool.sql.SolarPanelInfoDB;
import tool.sql.SupplyPointDB;
import user.ProductionType;


import java.util.ArrayList;
import java.util.Enumeration;

public class ProductionsServlet extends Servlet{

    public ProductionsServlet() {
        super("/RestAPI/Productions/EAN=", null, "No production supply point for this EAN", "");
        String tab[] = {"EAN", "TYPE", "PUISSANCE", "SUPERFICIE", "NOMBRE", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = ProductionInfoDB.getProductionInfoDB(liste.get(0).getValueColumn());
        ArrayList<String> l2 = SolarPanelInfoDB.getSolarPanelInfoDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l3 = new ArrayList<>();
        if (l1.size()!=0) {
            l3.add(new DataTable(columName[0], l1.get(0)));
            l3.add(new DataTable(columName[1], l1.get(1)));
        }
        if (l2.size()!=0){
            l3.add(new DataTable(columName[2], l2.get(1)));
            l3.add(new DataTable(columName[3], l2.get(2)));
            l3.add(new DataTable(columName[4], l2.get(3)));
        }
        return l3;
    }

    @Override
    protected ArrayList<DataTable> getValuesForGet(String requestURL) {
        ArrayList<DataTable> liste = new ArrayList<>();
        String username = requestURL.substring(path.length());
        liste.add(new DataTable(columName[0], username));
        return liste;
    }

    @Override
    protected void postValueIntoDB(ArrayList<DataTable> liste) {
        String ean="", t="", puissance="", superficie="", nombre="", action="";
        for (DataTable d : liste) {
            if (d.getColumn().equals("EAN"))
                ean=d.getValueColumn();
            if (d.getColumn().equals("TYPE"))
                t=d.getValueColumn();
            if (d.getColumn().equals("PUISSANCE"))
                puissance=d.getValueColumn();
            if (d.getColumn().equals("SUPERFICIE"))
                superficie=d.getValueColumn();
            if (d.getColumn().equals("NOMBRE"))
                nombre=d.getValueColumn();
            if (d.getColumn().equals("ACTION"))
                action=d.getValueColumn();
        }
        if (action.equals("add")){
            ProductionInfoDB.addProductionInfoDB(ean, ProductionType.valueOf(t));
            if (t.equals("SOLAR")){
                SolarPanelInfoDB.addSolarPanelInfoDB(ean, puissance, superficie, nombre);
            }

        }
    }

    @Override
    protected boolean verifyRequestForPost(Enumeration<String> parameters) {
        return true;
    }

    @Override
    protected boolean verifyRequestForGet(String request) {
        return true;
    }
}
