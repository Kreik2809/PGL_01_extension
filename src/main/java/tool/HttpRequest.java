package tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * classe qui gère l'envoie de requêtes HTTP
 */
public class HttpRequest
{

    /**
     * fonction POST
     * @param request la requête HTTP pour le POST
     * @throws IOException si une erreur survient
     */
    public static void Post(String request) throws IOException
    {
        request = refactorString(request);
        HttpURLConnection connection;
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();


        URL url = new URL(request);
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int status = connection.getResponseCode();

        if (status > 299){
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            while((line = reader.readLine()) != null){
                responseContent.append(line);
            }
            reader.close();
        } else{
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null){
                responseContent.append(line);
            }
            reader.close();
        }

    }

    /**
     * fonction GET
     * @param request la requête HTTP pour le GET
     * @param parseJson le parseJSON (traduction JSON =liste de String)
     * @return la liste des valeurs
     * @throws IOException si une erreur survient
     */
    public static ArrayList<String> Get(String request, ParseJson parseJson) throws IOException
    {
        HttpURLConnection connection;
        BufferedReader reader;
        String line;
        StringBuffer responseContent = new StringBuffer();


        URL url = new URL(request);
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int status = connection.getResponseCode();

        if (status > 299){
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            while((line = reader.readLine()) != null){
                responseContent.append(line);
            }
            reader.close();
        } else{
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null){
                responseContent.append(line);
            }
            reader.close();
        }
        String response = responseContent.toString();
        return parseJson.parseGetResult(response);
    }

    /**
     * fonction qui permet de remplacer les espaces d'une chaine de caractère pour la requête HTTP
     * @param s le requête HTTP
     * @return la chaine de caractère transformée
     */
    public static String refactorString(String s){
        return s.replaceAll("\\s+","%20");
    }

}
