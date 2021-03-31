package tool.restapi;

import org.json.JSONObject;
import tool.AccessLevelType;
import tool.sql.*;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * MeterServlet2 s'occupe de la gestion des requêtes http relatives à la récupération des infos sur les compteurs possédé par un utilisateur
 * GET :
 *      http://URLSERVER/RestAPI/MetersU/USERNAME=
 *      retourne :
 *          le nom, Le code ean, le type d'énergie et le jour de départ du compteur
 *
 * POST :
 *      rien
 */
public class MeterServlet2 extends Servlet
{

    public MeterServlet2() {
        super("/RestAPI/MetersU/USERNAME=", null , "NO METER FOR THIS USERNAME", "PLEASE USE THIS SYNTAX : /RestAPI/MetersU/USERNAME='username'");
        String[] tab = {"NAME", "EAN18", "ENERGYTYPE", "DAYSTART", "STYPE"};
        this.columName = tab;
        this.columParam = tab;
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
        ArrayList<String> l1 = EnergyConsumerPortfolioDB.getEnergyConsumerPortfolioByUsernameDB(username);
        ArrayList<DataTable> l2 = new ArrayList<>();
        for(int i = 0 ; i < l1.size() ; i+=3)
        {
            if(l1.get(i+2).equals(AccessLevelType.MANAGING.name()) || l1.get(i+2).equals(AccessLevelType.WRITING.name()))
            {
                ArrayList<String> l3 = PortfolioSupplyPointDB.getPortfolioSupplyPointByIDDB(l1.get(i+1));
                for(int k = 0 ; k < l3.size() ; k+=2)
                {
                    ArrayList<String> l4 = SupplyPointDB.getSupplyPointDB(l3.get(k+1));
                    l2.add(new DataTable(columName[0], l4.get(2)));
                    l2.add(new DataTable(columName[1], l4.get(0)));
                    l2.add(new DataTable(columName[2], l4.get(1)));
                    l2.add(new DataTable(columName[3], l4.get(5)));
                    l2.add(new DataTable(columName[4], l4.get(7)));
                }
            }
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
        String username = requestURL.substring(path.length());
        liste.add(new DataTable(UserDB.COLUMNS[0], username));
        return liste;
    }

    /**
     * ajoute les nouvelles données dans la DB
     *
     * @param liste la liste de DataTable du résultat de la requête post demandée dans l'url
     */
    @Override
    protected void postValueIntoDB(ArrayList<DataTable> liste) {

    }

    /**
     * méthode qui permet de verifier la syntax de la requête post (donc les nom des paramètres passés)
     *
     * @param parameters les paramètres passés dans la requête
     * @return true si la requête à la syntaxe attendue, false sinon
     */
    @Override
    protected boolean verifyRequestForPost(Enumeration<String> parameters) {
        return false;
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
