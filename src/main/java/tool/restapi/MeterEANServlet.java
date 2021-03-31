package tool.restapi;

import tool.sql.DataTable;
import tool.sql.SupplyPointDB;
import user.EnergyType;
import user.SupplyPointType;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * MeterEANServlet s'occupe de la gestion des requêtes http relatives aux informations d'un compteur
 * GET :
 *      http://URLSERVER/RestAPI/MeterByEAN/EAN=xx
 *      retourne :
 *          Le code ean, le type d'énergie, le nom, l'état(ouvert/fermé), l'id du contrat, le jour de départ du contrat,
 *          l'id du fournisseur ainsi que le type de compteur.
 *
 * POST :
 *      http://URLSERVER/RestAPI/MeterByEAN/?EAN=xx&NAME=xx&ACTION=modify
 *          Modifie le nom d'un compteur dans la base de données
 *      http://URLSERVER/RestAPI/MeterByEAN/?EAN=xx&NAME=xx&ENERGYTYPE=xx&ACTION=add
 *          Ajoute un compteur dans la DB
 */
public class MeterEANServlet extends Servlet{

    public MeterEANServlet() {
        super("/RestAPI/MeterByEAN/EAN=", null, "NO METER FOR THIS EAN", "PLEASE USE THIS SYNTAX : /RestAPI/MeterByEAN/EAN=xx");
        String[] tab = {"EAN", "ENERGYTYPE", "NAME", "STATE", "CID", "DAYSTART", "SID", "TYPE", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = SupplyPointDB.getSupplyPointDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        if (l1.size()!=0) {
            l2.add(new DataTable(columName[0], l1.get(0)));
            l2.add(new DataTable(columName[1], l1.get(1)));
            l2.add(new DataTable(columName[2], l1.get(2)));
            l2.add(new DataTable(columName[3], l1.get(3)));
            l2.add(new DataTable(columName[4], l1.get(4)));
            l2.add(new DataTable(columName[5], l1.get(5)));
            l2.add(new DataTable(columName[6], l1.get(6)));
            l2.add(new DataTable(columName[7], l1.get(7)));
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
        String ean="", sType = "", name = "", s="", cId = "", dayStart="", sId = "", action="", t="";
        for (DataTable d : liste) {
            if (d.getColumn().equals("ACTION"))
                action = d.getValueColumn();
            if (d.getColumn().equals("EAN"))
                ean = d.getValueColumn();
            if (d.getColumn().equals("STATE"))
                s = d.getValueColumn();
            if (d.getColumn().equals("NAME"))
                name = d.getValueColumn();
            if (d.getColumn().equals("ENERGYTYPE"))
                t = d.getValueColumn();
            if (d.getColumn().equals("TYPE"))
                sType = d.getValueColumn();

        }
        if (action.equals("modify")){
            ArrayList<String> l1 = SupplyPointDB.getSupplyPointDB(ean);
            String d = "-1";
            if(!l1.get(5).equals("-1"))
                d = l1.get(5);
            boolean state = false;
            if(s.equals("1"))
                state = true;
            SupplyPointDB.modifySupplyPointDB(ean,ean, EnergyType.valueOf(l1.get(1)), name, state,l1.get(4) ,d, l1.get(6), l1.get(7));
        }
        else if (action.equals("add")){
            SupplyPointDB.addSupplyPointDB(ean,EnergyType.valueOf(t), name, false ,null, SupplyPointType.valueOf(sType));
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
