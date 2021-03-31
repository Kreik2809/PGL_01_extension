package tool.restapi;

import tool.sql.DB;
import tool.sql.DataTable;
import tool.sql.GreenCertificateDB;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * GreenCertificateByIDServlet s'occupe de la gestion des requêtes http relatives à un certificat vert en particulier
 * GET :
 *      http://URLSERVER/RestAPI/GreenCertificateByID/ID=xx
 *      retourne :
 *           l'id du certificat, Le code ean,l'id de l'autorité, le montant du certificat, la date de demande et la date de validation
 *
 * POST :
 *      http://URLSERVER/RestAPI/GreenCertificateByID/?ID=xx&ACTION=accept
 *          Valide un certificat vert
 *      http://URLSERVER/RestAPI/GreenCertificateByID/?ID=xx&ACTION=del
 *  *       Supprime un certificat vert
 */
public class GreenCertificateByIDServlet extends Servlet{

    public GreenCertificateByIDServlet() {
        super("/RestAPI/GreenCertificateByID/ID=", null, "No certificate for this ID", "");
        String[] tab = {"ID", "METERID", "REGIONALID", "VALUE", "ASKINGDATE", "VALIDATIONDATE", "ACTION"};
        this.columName = tab;
        this.columParam = tab;
    }

    @Override
    protected ArrayList<DataTable> getResultForGet(ArrayList<DataTable> liste) {
        ArrayList<String> l0 = GreenCertificateDB.getGreenCertificateByidDB(liste.get(0).getValueColumn());
        ArrayList<DataTable> l2 = new ArrayList<>();
        if (l0.size() != 0){
            l2.add(new DataTable(columName[0], l0.get(0)));
            l2.add(new DataTable(columName[1], l0.get(1)));
            l2.add(new DataTable(columName[2], l0.get(2)));
            l2.add(new DataTable(columName[3], l0.get(3)));
            l2.add(new DataTable(columName[4], l0.get(4)));
            l2.add(new DataTable(columName[5], l0.get(5)));
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
        String ID="", action="";
        for (DataTable d : liste) {
            if(d.getColumn().equals("ID"))
                ID = d.getValueColumn();
            if(d.getColumn().equals("ACTION"))
                action = d.getValueColumn();
        }
        //Supprime une donnée dans la table les certificats verts
        if (action.equals("del"))
            GreenCertificateDB.deleteGreenCertificateDB(ID);
        else if (action.equals("accept")){
            ArrayList<String> infos = GreenCertificateDB.getGreenCertificateByidDB(ID);
            Date dt = DB.dateToday();
            GreenCertificateDB.modifyContractDB(ID, ID, infos.get(1), infos.get(2), infos.get(3), infos.get(4), dt.toString());
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
