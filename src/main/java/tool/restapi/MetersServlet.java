package tool.restapi;

import tool.sql.DataTable;
import tool.sql.PortfolioSupplyPointDB;
import tool.sql.SupplyPointDB;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * MetersServlet s'occupe de la gestion des requêtes http relatives aux compteur d'un portefeuille
 * GET :
 *      http://URLSERVER/RestAPI/MetersM/PORTFOLIO_ID=xx
 *      retourne des objets JSON contenant :
 *         L'id du portefeuille, le code EAN, le type d'énergie, le nom, l'état, l'id de contrat, le jour de départ, l'id du fournisseur
 *         d'un compteur.
 * POST :
 *      http://URLSERVER/RestAPI/MetersM/?PORTFOLIO_ID=xx&EAN18=xx&ACTION=add
 *          Ajoute un compteur au portefeuille
 *      http://URLSERVER/RestAPI/MetersM/?PORTFOLIO_ID=xx&EAN18=xx&ACTION=delete
 *          Supprimer un compteur du portefeuille
 */
public class MetersServlet extends Servlet{

    public MetersServlet() {
        super("/RestAPI/Meters/PORTFOLIO_ID=", null, "NO METER FOR THIS PORTFOLIO", "PLEASE USE THIS SYNTAX : /RestAPI/MetersM/PORTOFOLIO_ID=id");
        String[] tab = {"PORTFOLIO_ID", "EAN18", "ENERGYTYPE", "NAME", "STATE", "CONTRACTID", "DAYSTART", "SUPPLIERID","TYPE", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = PortfolioSupplyPointDB.getPortfolioSupplyPointByIDDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i = 0; i < l1.size(); i+= 2){
            l2.add(new DataTable(columName[0], l1.get(i)));
            l2.add(new DataTable(columName[1], l1.get(i + 1)));
            ArrayList<String> l3 = (SupplyPointDB.getSupplyPointDB(l1.get(i+1)));
            if (l3.size() > 0){
                l2.add(new DataTable(columName[2], l3.get(1)));
                l2.add(new DataTable(columName[3], l3.get(2)));
                l2.add(new DataTable(columName[4], l3.get(3)));
                l2.add(new DataTable(columName[5], l3.get(4)));
                l2.add(new DataTable(columName[6], l3.get(5)));
                l2.add(new DataTable(columName[7], l3.get(6)));
                l2.add(new DataTable(columName[8], l3.get(7)));
            }
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
        String pId ="", ean="", action="";
        for (DataTable d : liste) {
            if (d.getColumn().equals("ACTION"))
                action = d.getValueColumn();
            if (d.getColumn().equals("EAN18"))
                ean = d.getValueColumn();
            if (d.getColumn().equals("PORTFOLIO_ID"))
                pId = d.getValueColumn();
        }
        if (action.equals("add")){
            PortfolioSupplyPointDB.addPortfolioSupplyPointDB(pId, ean);
        }
        else if (action.equals("delete")){
            PortfolioSupplyPointDB.deletePortfolioSupplyPointDB(pId, ean);
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
