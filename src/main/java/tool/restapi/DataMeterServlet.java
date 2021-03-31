package tool.restapi;

import org.json.JSONObject;
import tool.sql.DataSupplyPointDB;
import tool.sql.DataTable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;


/**
 * DataMeterServlet s'occupe de la récupération des données d'un compteur à partir de son code EAN
 * GET :
 *      http://URLSERVER/RestAPI/MeterData/EAN=
 *      retourne :
 *          la liste de toutes les données (Date, Valeur) du compteur
 * POST :
 *      http://URLSERVER/RestAPI/MeterData/?EAN=x&DATE=x&OLDDATE=x&VALUE=x&ACTION=modify (modifier une donnée déjà existante)
 *      http://URLSERVER/RestAPI/MeterData/?EAN=x&DATE=x&VALUE=x&ACTION=add (ajout de donnée)
 *      http://URLSERVER/RestAPI/MeterData/?EAN=x&DATE=x&ACTION=delete (supression de donnée)
 */
public class DataMeterServlet extends Servlet
{

    public DataMeterServlet() {
        super("/RestAPI/MeterData/EAN=", null , "NO DATA FOR THIS METER", "PLEASE USE THIS SYNTAX : /RestAPI/MeterData/EAN=ean18");
        String[] tab = {"DATE", "VALUE"};
        this.columName = tab;
        String[] tab2= {"EAN", "DATE", "OLDDATE", "VALUE", "ACTION"};
        this.columParam = tab2;
    }

    /**
     * permet de récuperer la liste des résultats de la requête
     *
     * @param liste la liste de DataTable afin d'obtenir le résultat
     * @return la liste de des résulats
     */
    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<DataTable> l1 = new ArrayList<>();
        ArrayList<String> l2 = DataSupplyPointDB.getDataSupplyPointByEAN18DB(liste.get(0).getValueColumn());
        for(int i = 0 ; i < l2.size() ; i+= 3)
        {
            l1.add(new DataTable(columName[0], l2.get(i+1)));
            l1.add(new DataTable(columName[1], l2.get(i+2)));
        }
        return l1;
    }

    /**
     * méthode qui a pour but de récuperer les valeurs à aller chercher dans la base (utilisée dans la méthode getResultForGet)
     *
     * @param requestURL la requête url
     * @return la liste de DataTable du résultat de la requête demandée dans l'url
     */
    @Override
    protected ArrayList<DataTable> getValuesForGet(String requestURL) {
        ArrayList<DataTable> liste = new ArrayList<>();
        String ean = requestURL.substring(path.length());
        liste.add(new DataTable(DataSupplyPointDB.COLUMNS[0], ean));
        return liste;
    }

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

    /**
     * ajoute les nouvelles données dans la DB
     *
     * @param liste la liste de DataTable du résultat de la requête post demandée dans l'url
     */
    @Override
    protected void postValueIntoDB(ArrayList<DataTable> liste) {
        String ean="", date = "", value = "",action="", oldDate="";
        for (DataTable d : liste) {
            if (d.getColumn().equals(columParam[4]))
                action = d.getValueColumn();
            if (d.getColumn().equals(columParam[0]))
                ean = d.getValueColumn();
            if (d.getColumn().equals(columParam[1]))
                date = d.getValueColumn();
            if (d.getColumn().equals(columParam[3]))
                value = d.getValueColumn();
            if (d.getColumn().equals(columParam[2]))
                oldDate = d.getValueColumn();

        }
        if (action.equals("modify")){
            DataSupplyPointDB.modifyDataSupplyPointDB(ean, Date.valueOf(oldDate), ean, Date.valueOf(date), Double.valueOf(value));
        }
        else if (action.equals("add")){
            DataSupplyPointDB.addDataSupplyPointDB(ean, Date.valueOf(date), Double.valueOf(value));
        }
        else if (action.equals("delete"))
        {
            DataSupplyPointDB.deleteDataSupplyPointDB(ean, Date.valueOf(date));
        }
    }

    /**
     * méthode qui permet de verifier la syntax de la requête post (donc les noms des paramètres passés)
     *
     * @param parameters les paramètres passés dans la requête
     * @return true si la requête à la syntaxe attendue, false sinon
     */
    @Override
    protected boolean verifyRequestForPost(Enumeration<String> parameters) {
        return true;
    }

    /**
     * méthode qui permet de verifier la syntax de la requête get
     *
     * @param request la requête à vérifier
     * @return true si la requête à la syntaxe attendue, false sinon
     */
    @Override
    protected boolean verifyRequestForGet(String request) {
        return true;
    }
}
