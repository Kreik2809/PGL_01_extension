package tool.restapi;

import org.json.JSONObject;
import tool.sql.DataTable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * classe abstraite servlet qui permet de créer facilement des servlets
 */
public abstract class Servlet extends HttpServlet
{
    protected String path;
    protected String[] columName;
    protected String[] columParam;
    protected String errorString;
    protected String errorSyntax;

    /**
     * constructeur de la classe Servlet
     * @param path le chemin que doit avoir la requête (exemple : "/RestAPI/User/MAIL=)"
     * @param columName la liste des nom des collonnes à récuperer
     * @param errorString l'erreur envoyée si la recherche dans la db n'a pas aboutie
     * @param errorSyntax l'erreur envoyée si la syntaxe n'est pas respectée dans la requête http
     */
    public Servlet(String path, String[] columName, String errorString, String errorSyntax)
    {
        this.path = path;
        this.columName = columName;
        this.errorString = errorString;
        this.errorSyntax = errorSyntax;
    }

    public Servlet(String path, String[] columName, String errorString, String errorSyntax, String[] columParam)
    {
        this.path = path;
        this.columName = columName;
        this.errorString = errorString;
        this.errorSyntax = errorSyntax;
        this.columParam = columParam;
    }

    /**
     * permet de récuperer la liste des résultats de la requête
     * @param liste la liste de DataTable afin d'obtenir le résultat
     * @return la liste de des résulats
     */
    protected abstract ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste);

    protected String parse(ArrayList<DataTable> r) {
        String json="";
        for (int i =0; i<r.size();i+=columName.length-1){
            json += "{\n";
            for (int j = 0; j < columName.length-1; j++) {
                json +=  JSONObject.quote(r.get(i+j).getColumn()) + ": " + JSONObject.quote(r.get(i+j).getValueColumn()) + ",\n";
            }
            json += "},\n";
        }
        json = json.substring(0, json.length()-2);
        return json;
    }

    /**
     * méthode qui a pour but de récuperer les valeurs à aller chercher dans la base (utilisée dans la méthode getResultForGet)
     * @param requestURL la requête url
     * @return la liste de DataTable du résultat de la requête demandée dans l'url
     */
    protected abstract ArrayList<DataTable> getValuesForGet(String requestURL);

    /**
     *
     * @param requestURL la requête url reçue
     * @return la liste de DataTable (colonne => valeur) du résultat de la requête post demandée dans l'url
     */
    protected ArrayList<DataTable> getValuesForPost(HttpServletRequest requestURL)
    {
        ArrayList<DataTable> liste = new ArrayList<>();
        for(int i = 0 ; i < columParam.length ; i++)
        {
            if (requestURL.getParameter(columParam[i]) != null)
                liste.add(new DataTable(columParam[i], requestURL.getParameter(columParam[i])));
        }
        return liste;
    }

    /**
     * ajoute les nouvelles données dans la DB
     * @param liste la liste de DataTable du résultat de la requête post demandée dans l'url
     */
    protected abstract void postValueIntoDB(ArrayList<DataTable> liste);

    /**
     * méthode qui permet de verifier la syntax de la requête post (donc les nom des paramètres passés)
     * @param parameters les paramètres passés dans la requête
     * @return true si la requête à la syntaxe attendue, false sinon
     */
    protected abstract boolean verifyRequestForPost(Enumeration<String> parameters);

    /**
     * méthode qui permet de verifier la syntax de la requête get
     * @param request la requête à vérifier
     * @return true si la requête à la syntaxe attendue, false sinon
     */
    protected abstract boolean verifyRequestForGet(String request);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestUrl = request.getRequestURI();

        if(verifyRequestForGet(requestUrl))
        {
            ArrayList<DataTable> listValues = getValuesForGet(requestUrl);

            ArrayList<DataTable> results = getResultForGet(listValues);

            if (results.size() != 0)
            {
                response.getOutputStream().println(parse(results));

            } else {
                response.getOutputStream().println("{\n" + errorString + "\n}");
            }
        }
        else
        {
            response.getOutputStream().println("{\n" + errorSyntax + "\n}");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        if(verifyRequestForPost(request.getParameterNames()))
        {
            ArrayList<DataTable> liste = getValuesForPost(request);

            if (liste != null)
                postValueIntoDB(liste);
        }
    }
}
