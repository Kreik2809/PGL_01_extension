package tool.restapi;

import org.json.JSONObject;
import tool.sql.DataTable;
import tool.sql.SupplyPointDB;
import user.EnergyType;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * MeterServlet3 s'occupe de la gestion des requêtes http relatives à la récupération des infos de tout les compteurs
 * GET :
 *      http://URLSERVER/RestAPI/AllMeters/
 *      retourne :
 *          Le code ean du compteur, le type d'énergie du compteur, le nom du compteur, l'état du compteur, l'id du contract associé, le jour de départ du compteur et l'id du fournisseur asscoié
 *
 * POST :
 *      rien
 */
public class MetersServlet3 extends Servlet{

    public MetersServlet3() {
        super("/RestAPI/AllMeters/", null, "NO METERS", "PLEASE USE THIS SYNTAX : /RestAPI/AllMeters/");
        String[] tab = {"EAN18", "ENERGYTYPE", "NAME", "STATE", "CONTRACTID", "DAYSTART", "SUPPLIERID"};
        this.columName = tab;
        String[] tab2= {"EAN18", "ENERGYTYPE", "NAME", "STATE", "CONTRACTID", "DAYSTART", "SUPPLIERID", "ACTION"};
        this.columParam = tab2;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = SupplyPointDB.getAllSupplyPointDB();
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i = 0; i < l1.size(); i+= 8)
        {
            l2.add(new DataTable(columName[0], l1.get(i)));
            l2.add(new DataTable(columName[1], l1.get(i + 1)));
            l2.add(new DataTable(columName[2], l1.get(i + 2)));
            l2.add(new DataTable(columName[3], l1.get(i+3)));
            l2.add(new DataTable(columName[4], l1.get(i+4)));
            l2.add(new DataTable(columName[5], l1.get(i+5)));
            l2.add(new DataTable(columName[6], l1.get(i+6)));
        }
        return l2;

    }

    @Override
    protected String parse(ArrayList<DataTable> r) {
        String json="";
        for (int i =0; i<r.size();i+=columName.length){
            json += "{\n";
            for (int j = 0; j < columName.length; j++) {
                json +=  JSONObject.quote(r.get(i+j).getColumn()) + ": " + JSONObject.quote(r.get(i+j).getValueColumn()) + ",\n";
            }
            json += "},\n";
        }
        json = json.substring(0, json.length()-2);
        return json;
    }


    @Override
    protected ArrayList<DataTable> getValuesForGet(String requestURL) {
        return null;
    }

    @Override
    protected void postValueIntoDB(ArrayList<DataTable> liste) {
        String ean="", energyType = "", name = "",action="", state="", contractID="",dayStart="",supplierID = "";
        for (DataTable d : liste) {
            if (d.getColumn().equals(columParam[0]))
                ean= d.getValueColumn();
            else if (d.getColumn().equals(columParam[1]))
                energyType= d.getValueColumn();
            else if (d.getColumn().equals(columParam[2]))
                name= d.getValueColumn();
            else if (d.getColumn().equals(columParam[3]))
                state= d.getValueColumn();
            else if (d.getColumn().equals(columParam[4]))
                contractID= d.getValueColumn();
            else if (d.getColumn().equals(columParam[5]))
                dayStart= d.getValueColumn();
            else if (d.getColumn().equals(columParam[6]))
                supplierID= d.getValueColumn();
            else if (d.getColumn().equals(columParam[7]))
                action= d.getValueColumn();
        }
        boolean stateBool = false;
        if(state.equals("1"))
            stateBool = true;
        if (action.equals("modify"))
        {
            ArrayList<String> l1 = SupplyPointDB.getSupplyPointDB(ean);
            SupplyPointDB.modifySupplyPointDB(ean, ean, EnergyType.valueOf(energyType), name, stateBool, contractID, dayStart, supplierID, l1.get(7));
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