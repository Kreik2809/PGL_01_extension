package tool;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;

public abstract class ParseJson
{
    protected String[] rows;
    protected String errorString;
    protected String errorSyntax;

    /**
     * Parse un fichier Json et retourne une liste contenant chaque valeur que celui-ci contient..
     * @param rowName Le nom de chaque ligne du pattern du fichier json
     * @param errorString l'erreur renvoyée par le serveur si pas de résultat
     * @param errorSyntax l'erreur renvoyée par le serveur si faute de syntax
     */
    protected ParseJson(String[] rowName, String errorString, String errorSyntax)
    {
        rows = rowName;
        this.errorString = errorString;
        this.errorSyntax = errorSyntax;
    }

    /**
     * méthode qui permet de transformer le résultat d'une requête GET en liste de données
     * @param result le résultat de la requête GET
     * @return la liste des données récupérées
     */
    public ArrayList<String> parseGetResult(String result)
    {
        ArrayList<String> results = new ArrayList<>();
        if(result.equals("{"+ errorString +"}")) {
            return results;
        }
        else if(result.equals("{"+ errorSyntax +"}")) {
            return results;
        }


        ArrayList<String> objects = new ArrayList<>();
        int start = 0;
        int end;
        for(int i=0; i<result.length(); i++){
            if (result.charAt(i) == '}'){
                i ++;
                end = i;
                String o = result.substring(start, end);
                objects.add(o);
                i++;
                start = i;
            }
        }

        try{
            for(String o : objects) {
                JSONObject jsonObject = new JSONObject(o);
                for (int j = 0; j < rows.length; j++)
                    results.add((String) jsonObject.get(rows[j]));
            }
        } catch(JSONException e){
            
        }
        

        return results;
    }
}
