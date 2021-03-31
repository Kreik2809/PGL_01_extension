package tool.restapi;

import tool.sql.DataTable;
import tool.sql.GreenCertificateDB;

import java.util.ArrayList;
import java.util.Enumeration;

/**
 * GreenCertificateByRIDServlet s'occupe de la gestion des requêtes http relatives aux certificats verts lié à une autorité régionale.
 * GET :
 *      http://URLSERVER/RestAPI/GreenCertificateByRID/RID=xx
 *      retourne un ensemble d'objet JSON contenant:
 *           l'id de l'autorité, l'id du certificat,le code ean, le montant du certificat, la date de demande et la date de validation
 *
 * POST :
 *      Aucun.
 */
public class GreenCertificateByRIDServlet extends Servlet{

    public GreenCertificateByRIDServlet() {
        super("/RestAPI/GreenCertificateByRID/RID=", null, "No certificate for this RID", "");
        String[] tab = {"RID","ID", "METERID", "VALUE", "ASKINGDATE", "VALIDATIONDATE","ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l0 = GreenCertificateDB.getGreenCertificateByRegionalIDDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i=0; i<l0.size(); i+=6){
            l2.add(new DataTable(columName[0], l0.get(i+2)));
            l2.add(new DataTable(columName[1], l0.get(i)));
            l2.add(new DataTable(columName[2], l0.get(i+1)));
            l2.add(new DataTable(columName[3], l0.get(i+3)));
            l2.add(new DataTable(columName[4], l0.get(i+4)));
            l2.add(new DataTable(columName[5], l0.get(i+5)));
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
