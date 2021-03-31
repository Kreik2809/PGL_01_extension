package tool.restapi;

import org.json.JSONObject;
import tool.AccessLevelType;
import tool.sql.DataTable;
import tool.sql.EnergyConsumerPortfolioDB;
import tool.sql.PortfolioDB;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * PortfoliosServlet s'occupe de la gestion des requêtes http relatives aux portefeuilles d'un utilisateur
 * GET :
 *      http://URLSERVER/RestAPI/Portfolios/USERNAME=xx
 *      retourne des objets JSON contenant:
 *          Le nom d'un utilisateur, l'id du portefeuille, le niveau d'accès,  le nom du portefeuille, le nom du manager du portefeuille
 * POST :
 *      http://URLSERVER/RestAPI/Portfolios/?USERNAME=xx&PORTFOLIO_ID=xx&ACCESS=xx&PNAME=xx
 *          Crée et ajoute un portefeuille à un utilisateur
 */
public class PortfoliosServlet extends Servlet{

    public PortfoliosServlet() {
        super("/RestAPI/Portfolios/USERNAME=", null, "NO PORTFOLIO FOR THIS USER", "PLEASE USE THIS SYNTAX : /RestAPI/Portfolio/USERNAME=username");
        String[] tab = {"USERNAME", "PORTFOLIO_ID", "ACCESS", "PNAME", "MNAME"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = EnergyConsumerPortfolioDB.getEnergyConsumerPortfolioByUsernameDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i=0; i<l1.size(); i+= 3){
            l2.add(new DataTable(columName[0], l1.get(i)));
            l2.add(new DataTable(columName[1], l1.get(i+1)));
            l2.add(new DataTable(columName[2], l1.get(i+2)));
            String pName = PortfolioDB.getPortfolioDB(l1.get(i+1)).get(1);
            String mName = EnergyConsumerPortfolioDB.getEnergyConsumerPortfolioManagerDB(l1.get(i+1)).get(0);
            l2.add(new DataTable(columName[3], pName));
            l2.add(new DataTable(columName[4], mName));
        }
        return l2;
    }

    @Override
    protected String parse(ArrayList<DataTable> r) {
        String json="";
        for (int i =0; i<r.size();i+=columName.length){
            json += "{\n";
            for (int j = 0; j < columName.length; j++) {
                json += "\"" + r.get(i+j).getColumn() + "\": " + JSONObject.quote(r.get(i+j).getValueColumn()) + ",\n";
            }
            json += "},\n";
        }
        json = json.substring(0, json.length()-2);
        return json;
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
        String id = liste.get(1).getValueColumn();
        String pName = liste.get(3).getValueColumn();
        String uName = liste.get(0).getValueColumn();
        String access = liste.get(2).getValueColumn();
        AccessLevelType accessLevelType = AccessLevelType.valueOf(access);
        EnergyConsumerPortfolioDB.addEnergyConsumerPortfolioDB(uName, id , accessLevelType);
        PortfolioDB.addPortfolioDB(id, pName);
    }

    @Override
    protected boolean verifyRequestForPost(Enumeration<String> parameters) {
        return true;
    }

    @Override
    protected boolean verifyRequestForGet(String request) {
        if(!request.contains(columName[0] + "="))
            return false;
        return true;
    }
}
