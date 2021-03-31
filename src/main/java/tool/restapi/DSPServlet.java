package tool.restapi;

import org.json.JSONObject;
import tool.sql.DataSupplyPointDB;
import tool.sql.DataTable;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * DSPServlet s'occupe de la gestion des requêtes http relatives aux données de consommation d'un compteur
 * GET :
 *      http://URLSERVER/RestAPI/Dsp/EAN=xx
 *      retourne des objets JSON qui contiennent:
 *          Le code ean, la date, le montant de la consommation
 * POST :
 *      Aucun.
 */
public class DSPServlet extends Servlet{

    public DSPServlet() {
        super("/RestAPI/Dsp/EAN=", null, "NO DSP FOR THIS EAN", "PLEASE USE THIS SYNTAX : /RestAPI/DSP/EAN=xx");
        String[] tab = {"EAN", "DATE", "VALUE"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = DataSupplyPointDB.getDataSupplyPointByEAN18DB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i=0; i<l1.size(); i+=3){
            l2.add(new DataTable(columName[0], l1.get(i)));
            l2.add(new DataTable(columName[1], l1.get(i+1)));
            l2.add(new DataTable(columName[2], l1.get(i+2)));
        }
        return l2;

    }

    @Override
    protected String parse(ArrayList<DataTable> r) {
        String json="";
        for (int i =0; i<r.size();i+=columName.length){
            json += "{\n";
            for (int j = 0; j < columName.length; j++) {
                json += "\"" + r.get(i+j).getColumn() + "\": " + JSONObject.quote(r.get(i+j).getValueColumn()) + ",\n";
            }
            json += "},\n";
        }
        json = json.substring(0, json.length()-2);
        return json;
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
