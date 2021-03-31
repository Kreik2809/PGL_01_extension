package tool.restapi;

import org.json.JSONObject;
import tool.SignIn;
import tool.sql.DataTable;
import tool.sql.UserDB;
import user.UserType;
import java.util.ArrayList;
import java.util.Enumeration;


/**
 * UsersServlet s'occupe de la gestion des requêtes http relatives à un utilisateur
 * GET :
 *      http://URLSERVER/RestAPI/User/MAIL=xx
 *      retourne :
 *          L'username, le mot de passe, le type d'utilisateur, le mail de l'utilisateur
 * POST :
 *      http://URLSERVER/RestAPI/User/?MAIL=xx&USERNAME=xx&PASSWORD=xx&TYPE=xx
 *          Crée un nouvel utilisateur
 *      http://URLSERVER/RestAPI/User/?MAIL=xx&USERNAME=xx&PASSWORD=xx&TYPE=xx&ACTION=changepwd
 *          Modifie le mot de passe d'un utilisateur
 */
public class UsersServlet extends Servlet
{
    public UsersServlet()
    {
        super("/RestAPI/User/" + UserDB.COLUMNS[3] + "=", null, "NO USER", "PLEASE USE THIS SYNTAX : /RestAPI/User/MAIL=exemple@adresse.x");
        String[] tab = {"USERNAME", "PWD", "TYPE", "MAIL", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste)
    {
        ArrayList<DataTable> l = new ArrayList<>();
        ArrayList<String> l2 = new ArrayList<>();
        if(liste.get(0).getColumn().equals(columName[0])) {
            l2 = UserDB.getUserDB(liste.get(0).getValueColumn());
        }
        else {
            l2 = UserDB.getUserByMailDB(liste.get(0).getValueColumn());
        }
        for (int i=0; i<l2.size(); i++) {
            l.add(new DataTable(columName[i], l2.get(i)));
        }
        return l;
    }

    @Override
    protected String parse(ArrayList<DataTable> results)
    {
        String json = "";
        json = "{\n";
        for (int i = 0; i < columName.length-1; i++) {
            json += "\"" + columName[i] + "\": " + JSONObject.quote(results.get(i).getValueColumn()) + ",\n";
        }
        json += "}\n";
        return json;
    }

    @Override
    protected ArrayList<DataTable> getValuesForGet(String requestURL)
    {
        ArrayList<DataTable> liste = new ArrayList<>();
        String email = requestURL.substring(path.length());
        if(requestURL.contains("USER="))
            liste.add(new DataTable(columName[0], email));
        else
            liste.add(new DataTable(columName[3], email));
        return liste;
    }

    @Override
    protected void postValueIntoDB(ArrayList<DataTable> liste)
    {
        String mail = null, username = null, password = null, action="";
        UserType type = null;
        for (DataTable d : liste) {
            if (d.getColumn().equals("ACTION"))
                action = d.getValueColumn();
            if (d.getColumn().equals("USERNAME"))
                username = d.getValueColumn();
            if (d.getColumn().equals("PWD"))
                password = d.getValueColumn();
            if (d.getColumn().equals("TYPE"))
                type = UserType.valueOf(d.getValueColumn());
            if (d.getColumn().equals("MAIL"))
                mail = d.getValueColumn();
        }
        if (action.equals("changepwd")){
            UserDB.modifyUserDB(mail, mail, username, password, type);
        }
        else{
            UserDB.addUserDB(mail, username, password, type);
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
