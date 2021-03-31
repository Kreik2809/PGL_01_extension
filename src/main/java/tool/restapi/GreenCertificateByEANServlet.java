package tool.restapi;

import tool.sql.DB;
import tool.sql.DataTable;
import tool.sql.GreenCertificateDB;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * GreenCertificateByEANServlet s'occupe de la gestion des requêtes http relatives aux certificats verts d'un compteur.
 * GET :
 *      http://URLSERVER/RestAPI/GreenCertificateByEAN/EAN=xx
 *      retourne un ensemble d'objet JSON contenant:
 *          Le code ean, l'id du certificat, l'id de l'autorité, le montant du certificat, la date de demande et la date de validation
 *
 * POST :
 *      http://URLSERVER/RestAPI/GreenCertificateByEAN/?EAN=xx&REGIONALID=xx&VALUE=xx
 *          Ajoute un certificat vert en attente d'être validé dans la base de donnée
 */
public class GreenCertificateByEANServlet extends Servlet{

    public GreenCertificateByEANServlet() {
        super("/RestAPI/GreenCertificateByEAN/EAN=", null, "No certificate for this ean", "");
        String[] tab = {"EAN", "ID", "REGIONALID", "VALUE","ASKINGDATE", "VALIDATIONDATE", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l0 = GreenCertificateDB.getGreenCertificateByMeterIDDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        for (int i=0; i<l0.size(); i+=6){
            l2.add(new DataTable(columName[0], l0.get(i+1)));
            l2.add(new DataTable(columName[1], l0.get(i)));
            l2.add(new DataTable(columName[2], l0.get(i+2)));
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
        String meterID="", rid="", value="";
        for (DataTable d : liste) {
            if (d.getColumn().equals("EAN"))
                meterID = d.getValueColumn();
            if(d.getColumn().equals("REGIONALID"))
                rid = d.getValueColumn();
            if(d.getColumn().equals("VALUE"))
                value = d.getValueColumn();
        }
        Date d = DB.dateToday();
        GreenCertificateDB.addGreenCertificateDB(meterID, rid, value, d.toString(), "-1");
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
