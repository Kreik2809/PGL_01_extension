package tool.restapi;

import org.json.JSONObject;
import tool.sql.DataTable;
import tool.sql.SupplyPointDB;
import tool.sql.SupplyPointHistoryDB;
import tool.sql.UserDB;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * HistorySupplyPointServlet s'occupe de la gestion des requêtes http relatives aux historiques aux historiques de compteur
 * GET :
 *  *      http://URLSERVER/RestAPI/HystorySupplyPoint/USERNAME=xxx
 *  *      retourne des objets JSON qui contiennent:
 *  *          Le code ean, l'username de l'utilisateur, la date d'ouverture, la date de fermeture du compteur
 *  * POST :
 *  *      http://URLSERVER/RestAPI/HistorySupplyPoint/?EAN=xx&USERNAME=xx&OPEN=xx&CLOSE=xx&ACTION=add"
 *          Ajoute un élément dans la table SUPPLY_POINT_HISTORY
 */
public class HistorySupplyPointServlet extends Servlet
{

    public HistorySupplyPointServlet()
    {
        super("/RestAPI/HistorySupplyPoint/", null , "NO DATA FOR THIS METER", "PLEASE USE THIS SYNTAX : /RestAPI/HistorySupplyPoint/");
        String[] tab2 = {"EAN", "USERNAME", "OPEN", "CLOSE","ACTION"};
        String[] tab = {"EAN", "USERNAME", "OPEN", "CLOSE"};
        this.columName = tab;
        this.columParam = tab2;
    }

    /**
     * permet de récuperer la liste des résultat de la requête
     *
     * @param liste la liste de DataTable afin d'obtenir le résultat
     * @return la liste de des résulats
     */
    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        String username = liste.get(0).getValueColumn();
        ArrayList<String> l1 = new ArrayList<>();
        ArrayList<DataTable> l2 = new ArrayList<>();
        l1 = SupplyPointHistoryDB.getSupplyPointHistoryByUsernameDB(username);
        for(int i = 0 ; i < l1.size() ; i+=4)
        {
            l2.add(new DataTable(columName[0], l1.get(i)));
            l2.add(new DataTable(columName[1], l1.get(i+1)));
            l2.add(new DataTable(columName[2], l1.get(i+2)));
            l2.add(new DataTable(columName[3], l1.get(i+3)));
        }
        return l2;
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
     * méthode qui a pour but de récuperer les valeurs à aller chercher dans la base (utilisée dans la méthode getResultForGet)
     *
     * @param requestURL la requête url
     * @return la liste de DataTable du résultat de la requête demandée dans l'url
     */
    @Override
    protected ArrayList<DataTable> getValuesForGet(String requestURL) {
        ArrayList<DataTable> liste = new ArrayList<>();
        String username = requestURL.substring(path.length()+9);
        liste.add(new DataTable(UserDB.COLUMNS[1], username));
        return liste;
    }

    /**
     * ajoute les nouvelles données dans la DB
     *
     * @param liste la liste de DataTable du résultat de la requête post demandée dans l'url
     */
    @Override
    protected void postValueIntoDB(ArrayList<DataTable> liste)
    {
        String ean="", dayStart="", dayClose= "", action="", username="";
        for (DataTable d : liste) {
            if (d.getColumn().equals("ACTION"))
                action = d.getValueColumn();
            if (d.getColumn().equals("EAN"))
                ean = d.getValueColumn();
            if (d.getColumn().equals("USERNAME"))
                username = d.getValueColumn();
            if (d.getColumn().equals("OPEN"))
                dayStart = d.getValueColumn();
            if (d.getColumn().equals("CLOSE"))
                dayClose = d.getValueColumn();

        }
        if(action.equals("add"))
            SupplyPointHistoryDB.addSupplyPointHistoryDB(ean, username, Date.valueOf(dayStart), Date.valueOf(dayClose));
    }

    /**
     * méthode qui permet de verifier la syntax de la requête post (donc les nom des paramètres passés)
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