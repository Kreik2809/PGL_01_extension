package tool.restapi;

import org.json.JSONObject;
import tool.sql.DataTable;
import tool.sql.PortfolioDB;

import java.util.ArrayList;
import java.util.Enumeration;


/**
 * PortfolioIDServlet s'occupe de la génération d'un nouveau id de portfolio
 * GET :
 *      http://URLSERVER/RestAPI/PortfolioID/
 *      retourne :
 *          Le nouvel id généré
 * POST :
 *      Aucun.
 */
public class PortfolioIDServlet extends Servlet
{

    public PortfolioIDServlet() {
        super("/RestAPI/PortfolioID/", null, "ERROR IN GENERATION", "PLEASE USE THIS SYNTAX : /RestAPI/PortfolioID/");
        String[] tab = {"ID"};
        this.columName=tab;
        this.columParam = tab;
    }

    /**
     * permet de récuperer la liste des résultats de la requête
     *
     * @param liste la liste de DataTable  afin d'obtenir le résultat
     * @return la liste de des résulats
     */
    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<DataTable> arrayList = new ArrayList<>();
        arrayList.add(new DataTable(columName[0], PortfolioDB.getNewID()));
        return arrayList;
    }

    /**
     * méthode qui a pour but de récuperer les valeurs à aller chercher dans la base (utilisée dans la méthode getResultForGet)
     *
     * @param requestURL la requête url
     * @return la liste de DataTable du résultat de la requête demandée dans l'url
     */
    @Override
    protected ArrayList<DataTable> getValuesForGet(String requestURL) {
        return null;
    }

    /**
     * ajoute les nouvelles données dans la DB
     *
     * @param liste la liste de DataTable du résultat de la requête post demandée dans l'url
     */
    @Override
    protected void postValueIntoDB(ArrayList<DataTable> liste) {

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