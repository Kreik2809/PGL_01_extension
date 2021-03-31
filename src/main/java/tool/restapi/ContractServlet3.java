package tool.restapi;

import org.json.JSONObject;
import tool.sql.ContractDB;
import tool.sql.DataTable;
import tool.sql.SupplyPointDB;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * ContractServlet3 s'occupe de la récupération des compteurs qui y sont associés à un contract grâce à l'ID du contract
 * GET :
 *      http://URLSERVER/RestAPI/ContractsID/ID=
 *      retourne :
 *          Le code ean du compteur, le nom du compteur, le type d'énergie du compteur, l'id du contract, l'id du fournisseur, l'id du consommateur, le type de contract et la date d'ouverture du contract
 * POST :
 *      Aucun.
 */
public class ContractServlet3 extends Servlet
{

    public ContractServlet3() {
        super("/RestAPI/ContractsID/ID=", null, "NO CONTRACT FOR THIS ID", "PLEASE USE THIS SYNTAX : /RestAPI/ContractsSupplier/ID==xx");
        String[] tab = {"EAN", "NAME","ENERGYTYPE" , "ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};
        this.columName=tab;
        this.columParam = tab;
    }

    /**
     * permet de récuperer la liste des résultats de la requête
     *
     * @param liste la liste de DataTable afin d'obtenir le résultat
     * @return la liste de des résulats
     */
    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l1 = ContractDB.getContractDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i = 0; i < l1.size(); i += 5) {
            ArrayList<String> arrayList = SupplyPointDB.getSupplyPointByContractIDDB(l1.get(i));
            for(int k =0 ; k< arrayList.size() ; k+=8) {
                l2.add(new DataTable("EAN", arrayList.get(k)));
                l2.add(new DataTable("NAME", arrayList.get(k+2)));
                l2.add(new DataTable("ENERGYTYPE", arrayList.get(1)));
                l2.add(new DataTable("ID", l1.get(i)));
                l2.add(new DataTable("ENERGY_SUPPLIER_ID", l1.get(i + 1)));
                l2.add(new DataTable("CONSUMER_ID", l1.get(i + 2)));
                l2.add(new DataTable("TYPE", l1.get(i + 3)));
                l2.add(new DataTable("START", l1.get(i + 4)));
            }
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
        String id = requestURL.substring(path.length());
        liste.add(new DataTable(columName[3], id));
        return liste;
    }

    /**
     * ajoute les nouvelles données dans la DB
     *
     * @param liste la liste de DataTable du résultat de la requête post demandée dans l'url
     */
    @Override
    protected void postValueIntoDB(ArrayList<DataTable> liste) {

    }

    /**
     * méthode qui permet de verifier la syntax de la requête post (donc les nom des paramètres passés)
     *
     * @param parameters les paramètres passés dans la requête
     * @return true si la requête à la syntaxe attendue, false sinon
     */
    @Override
    protected boolean verifyRequestForPost(Enumeration<String> parameters) {
        return false;
    }

    /**
     * méthode qui permet de verifier la syntax de la requête get
     *
     * @param request la requête à vérifier
     * @return true si la requête à la syntaxe attendue, false sinon
     */
    @Override
    protected boolean verifyRequestForGet(String request) {
        return true;
    }
}
