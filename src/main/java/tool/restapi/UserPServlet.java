package tool.restapi;

import tool.AccessLevelType;
import tool.sql.DataTable;
import tool.sql.EnergyConsumerPortfolioDB;
import tool.sql.PortfolioDB;
import tool.sql.PortfolioSupplyPointDB;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * UserPServlet s'occupe de la gestion des requêtes http relatives aux utilisateurs d'un portefeuille
 * GET :
 *      http://URLSERVER/RestAPI/UserPortfolio/PORTFOLIO_ID=xx
 *      retourne des objets JSON contenant:
 *          L'id du portefeuille,Le nom d'un utilisateur,  le niveau d'accès
 * POST :
 *      http://URLSERVER/RestAPI/UserPortfolio/?PORTOLIO_ID=xx&USERNAME=xx&ACCESS=xx&ACTION=modify
 *          Modifie le niveau d'accès d'un utilisateur pour un portefeuille
 *      http://URLSERVER/RestAPI/UserPortfolio/?PORTOLIO_ID=xx&USERNAME=xx&ACTION=delete
 *          Retire un utilisateur du portefeuille
 *      http://URLSERVER/RestAPI/UserPortfolio/?PORTOLIO_ID=xx&ACTION=deletePortfolio
 *          Supprime l'entièreté d'un portefeuille
 */
public class UserPServlet extends Servlet{

    public UserPServlet() {
        super("/RestAPI/UserPortfolio/PORTFOLIO_ID=", null, "NO USER FOR THIS PORTFOLIO", "PLEASE USE THIS SYNTAX : /RestAPI/UserPortfolio/PORTFOLIO_ID=xx");
        String[] tab = {"PORTFOLIO_ID", "USERNAME", "ACCESS", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = EnergyConsumerPortfolioDB.getEnergyConsumerPortfolioByPortfolioIDDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i =0; i<l1.size(); i+=3){
            l2.add(new DataTable(columName[0], l1.get(i)));
            l2.add(new DataTable(columName[1], l1.get(i+1)));
            l2.add(new DataTable(columName[2], l1.get(i+2)));
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
        String uName="", pID="", access="", action="";
        for (DataTable d : liste) {
            if (d.getColumn().equals("ACTION"))
                action = d.getValueColumn();
            if (d.getColumn().equals("PORTFOLIO_ID"))
                pID = d.getValueColumn();
            if (d.getColumn().equals("USERNAME"))
                uName = d.getValueColumn();
            if (d.getColumn().equals("ACCESS"))
                access = d.getValueColumn();
        }

        if (action.equals("modify")){
            EnergyConsumerPortfolioDB.modifyEnergyConsumerPortfolioDB(uName, pID, uName, pID, AccessLevelType.valueOf(access));
        }
        else if (action.equals("delete")) {
            EnergyConsumerPortfolioDB.deleteEnergyConsumerPortfolioDB(uName, pID);
        }
        else if (action.equals("deletePortfolio")){
            EnergyConsumerPortfolioDB.deleteEnergyConsumerPortfolioDB(pID); // supression lien utilisateurs => Portfolio ID
            PortfolioDB.deletePortfolioDB(pID); // supression lien Portfolio ID => nom de portfolio
            PortfolioSupplyPointDB.deletePortfolioSupplyPointDB(pID); // supression lien Portfolio ID => SupplyPoint ID
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
