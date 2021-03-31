package tool.restapi;

import tool.AccessLevelType;
import tool.sql.DataTable;
import tool.sql.EnergyConsumerPortfolioDB;
import tool.sql.NotificationDB;
import tool.sql.PortfolioDB;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * NotificationsServlet s'occupe de la gestion des requêtes http relatives aux notifications d'un utilisateur
 * GET :
 *      http://URLSERVER/RestAPI/Notifications/USERNAME=xx
 *      retourne des objets JSON contenant:
 *          Le nom d'un utilisateur, le niveau d'accès, l'id du portefeuille, le nom du portefeuille, le nom du manager du portefeuille
 * POST :
 *      http://URLSERVER/RestAPI/Notifications/?USERNAME=xx&PORTFOLIO_ID=xx&ACTION=delete
 *          Supprime une notification
 *      http://URLSERVER/RestAPI/Notifications/?USERNAME=xx&PORTFOLIO_ID=xx&ACCESS=xx&ACTION=add
 *          Ajoute une notification
 *      http://URLSERVER/RestAPI/Notifications/?USERNAME=xx&PORTFOLIO_ID=xx&ACCESS=xx&ACTION=accept
 *          Accepte une notification
 */
public class NotificationsServlet extends Servlet{

    public NotificationsServlet() {
        super("/RestAPI/Notifications/USERNAME=", null, "NO NOTIFICATIONS FOR THIS USER", "PLEASE USE THIS SYNTAX : /RestAPI/Notifications/USERNAME=username");
        String[] tab = {"USERNAME", "ACCESS", "PORTFOLIO_ID", "PNAME", "MANAGER", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = NotificationDB.getNotificationDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i=0; i<l1.size(); i+= 3) {
            l2.add(new DataTable(columName[0], l1.get(i)));
            l2.add(new DataTable(columName[1], l1.get(i + 1)));
            l2.add(new DataTable(columName[2], l1.get(i + 2)));
            String pName = PortfolioDB.getPortfolioDB(l1.get(i + 2)).get(1);
            String manager = EnergyConsumerPortfolioDB.getEnergyConsumerPortfolioManagerDB(l1.get(i+2)).get(0);
            l2.add(new DataTable(columName[3], pName));
            l2.add(new DataTable(columName[4], manager));
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
        String action="", uName="", pID="", access="";
        for (DataTable d : liste){
            if (d.getColumn().equals("ACTION"))
                action = d.getValueColumn();
            if (d.getColumn().equals("USERNAME"))
                uName = d.getValueColumn();
            if (d.getColumn().equals("PORTFOLIO_ID"))
                pID = d.getValueColumn();
            if (d.getColumn().equals("ACCESS"))
                access = d.getValueColumn();
        }
        if (action.equals("delete")){
            NotificationDB.deleteNotificationDB(uName, pID);
        }
        else if (action.equals("accept")){
            EnergyConsumerPortfolioDB.addEnergyConsumerPortfolioDB(uName, pID, AccessLevelType.valueOf(access));
            NotificationDB.deleteNotificationDB(uName, pID);
        }
        else if (action.equals("add")){
            NotificationDB.addNotificationDB(uName,pID, AccessLevelType.valueOf(access));
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
