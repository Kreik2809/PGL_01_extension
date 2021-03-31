package tool.restapi;

import tool.sql.DataTable;
import tool.sql.DelNotifDB;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * DelNotif s'occupe de la gestion de requête http concernant les notifications de suppression de données d'un utilisateur
 * GET :
 *      http://URLSERVER/RestAPI/DelNotif/USERNAME=
 *      retourne une liste d'objet JSON contenant:
 *          l'username de l'utilisateur, le code ean du compteur
 * POST :
 *      http://URLSERVER/RestAPI/DelNotif/?USERNAME=xx&EAN=xx&ACTION=add (ajoute une notification)
 *      http://URLSERVER/RestAPI/DelNotif/?USERNAME=xx&EAN=xx&ACTION=delete (supprime une notification)
 */
public class DelNotifServlet extends Servlet{

    public DelNotifServlet() {
        super("/RestAPI/DelNotif/USERNAME=", null, "NO del Notif FOR THIS parameters", "");
        String[] tab = {"USERNAME", "EAN", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = DelNotifDB.getDelNotifDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i=0; i<l1.size(); i+=2){
            l2.add(new DataTable(columName[0], l1.get(i)));
            l2.add(new DataTable(columName[1], l1.get(i+1)));
        }
        return l2;
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
        String username="",ean="", action="";
        for (DataTable d : liste) {
            if (d.getColumn().equals("USERNAME"))
                username = d.getValueColumn();
            if(d.getColumn().equals("EAN"))
                ean = d.getValueColumn();
            if(d.getColumn().equals("ACTION"))
                action = d.getValueColumn();
        }
        if (action.equals("add")){
            DelNotifDB.addDelNotifDB(username, ean);
        }
        else if (action.equals("delete"))
            DelNotifDB.deleteDelNotifDB(username, ean);
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
