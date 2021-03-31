package tool.restapi;

import org.json.JSONObject;
import tool.sql.ContractDB;
import tool.sql.DataTable;
import tool.sql.SupplyPointDB;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * ContractsServlet s'occupe de la gestion des requêtes http relatives au contrat d'un compteur
 * GET :
 *      http://URLSERVER/RestAPI/Contracts/EAN=xx
 *      retourne :
 *          Le code ean, l'id du contrat, l'id du fournisseur, l'id du consommateur, le type de contrat, la date de départ
 * POST :
 *      Aucun.
 */
public class ContractsServlet extends Servlet{

    public ContractsServlet() {
        super("/RestAPI/Contracts/EAN=", null, "NO CONTRACT FOR THIS ID", "PLEASE USE THIS SYNTAX : /RestAPI/Contracts/CONTRACT_ID=xx");
        String[] tab = {"EAN", "ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};
        this.columName=tab;
        String[] tab2 = {"ID",  "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START", "ACTION", "NEWID"};
        this.columParam = tab2;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l0 = SupplyPointDB.getSupplyPointDB(liste.get(0).getValueColumn());
        if (l0.size() != 0) {
            ArrayList<String> l1 = ContractDB.getContractDB(l0.get(4));
            ArrayList<DataTable> l2 = new ArrayList<>();
            for (int i = 0; i < l1.size(); i += 5) {
                l2.add(new DataTable("EAN", liste.get(0).getValueColumn()));
                l2.add(new DataTable("ID", l1.get(i)));
                l2.add(new DataTable("ENERGY_SUPPLIER_ID", l1.get(i + 1)));
                l2.add(new DataTable("CONSUMER_ID", l1.get(i + 2)));
                l2.add(new DataTable("TYPE", l1.get(i + 3)));
                l2.add(new DataTable("START", l1.get(i + 4)));
            }
            return l2;
        }
        else{
            return null;
        }
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
        String action="", id = "", supplierID="", consumerID="", type="", start="", lastID="";
        for (DataTable d : liste) {
            if (d.getColumn().equals(columParam[5]))
                action= d.getValueColumn();
            else if (d.getColumn().equals(columParam[0]))
                id= d.getValueColumn();
            else if (d.getColumn().equals(columParam[1]))
                supplierID= d.getValueColumn();
            else if (d.getColumn().equals(columParam[2]))
                consumerID= d.getValueColumn();
            else if (d.getColumn().equals(columParam[3]))
                type= d.getValueColumn();
            else if (d.getColumn().equals(columParam[4]))
                start= d.getValueColumn();
            else if (d.getColumn().equals(columParam[6]))
                lastID = d.getValueColumn();
        }
        if (action.equals("delete"))
        {
            ContractDB.deleteContractDB(id);
        }
        else if(action.equals("add"))
        {
            ContractDB.addContractDB(supplierID, consumerID, type, Date.valueOf(start));
        }
        else if(action.equals("addByID"))
        {
            ContractDB.addContractDB(supplierID, consumerID, type, Date.valueOf(start), id);
        }
        else if(action.equals("modify"))
        {
            ContractDB.modifyContractDB(lastID, id, supplierID,consumerID, type, Date.valueOf(start));
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
