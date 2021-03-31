package tool.sql;

import java.sql.Date;
import java.util.ArrayList;

/**
 * classe qui s'occupe de la table CONTRACTS
 */
public class ContractDB extends DB
{
    private static String[] COLUMNS = {"ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};

    private ContractDB()
    {
        super("CONTRACTS", COLUMNS);
    }


    /**
     * fonction qui permet de récupérer les données dans la table CONTRACTS grâce à l'id du contract
     * @param id l'id du contract
     * @return liste des données trouvées dans la table ("ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START")
     */
    public static ArrayList<String> getContractDB(String id)
    {
        ContractDB db = new ContractDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        return db.getDataDB(liste);
    }

    /**
     * fonction qui permet de récupérer les données dans la table CONTRACTS grâce à l'id du fournisseur
     * @param id l'id du fournisseur
     * @return liste des données trouvées dans la table ("ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START")
     */

    public static ArrayList<String> getContractBySupplierIDDB(String id)
    {
        ContractDB db = new ContractDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[1], id));
        return db.getDataDB(liste);
    }

    /**
     * fonction qui permet d'ajouter des données dans la table CONTRACTS
     * @param supplierID l'id du fournisseur
     * @param consumerID l'id du consommateur
     * @param type le type de contract (voir MeterType)
     * @param dateStart le jour d'ouverture du compteur
     * @return true si l'ajout à réussis, false sinon
     */
    public static boolean addContractDB(String supplierID, String consumerID, String type, Date dateStart)
    {
        ContractDB db = new ContractDB();
        String id = String.valueOf(db.getNewID());
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], id));
        liste2.add(new DataTable(COLUMNS[1], supplierID));
        liste2.add(new DataTable(COLUMNS[2], consumerID));
        liste2.add(new DataTable(COLUMNS[3], type));
        liste2.add(new DataTable(COLUMNS[4], dateStart.toString()));
        return db.addDataDB(liste, liste2);
    }

    /**
     * fonction qui permet d'ajouter des données dans la table CONTRACTS
     * @param supplierID l'id du fournisseur
     * @param consumerID l'id du consommateur
     * @param type le type de contract (voir MeterType)
     * @param dateStart le jour d'ouverture du compteur
     * @return true si l'ajout à réussis, false sinon
     */
    public static boolean addContractDB(String supplierID, String consumerID, String type, Date dateStart, String cId)
    {
        ContractDB db = new ContractDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], cId));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], cId));
        liste2.add(new DataTable(COLUMNS[1], supplierID));
        liste2.add(new DataTable(COLUMNS[2], consumerID));
        liste2.add(new DataTable(COLUMNS[3], type));
        liste2.add(new DataTable(COLUMNS[4], dateStart.toString()));
        return db.addDataDB(liste, liste2);
    }

    /**
     * fonction qui permet de modifier des données dans la table CONTRACTS
     * @param LastID l'ancien id du contract
     * @param newID le nouvel id du contract
     * @param supplierID l'id du fournisseur
     * @param consumerID l'id du consommateur
     * @param type le type de contract (voir MeterType)
     * @param dateStart le jour d'ouverture du compteur
     * @return true si la modification a réussi, false sinon
     */
    public static boolean modifyContractDB(String LastID, String newID, String supplierID, String consumerID, String type, Date dateStart)
    {
        ContractDB db = new ContractDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], LastID));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newID));
        liste2.add(new DataTable(COLUMNS[1], supplierID));
        liste2.add(new DataTable(COLUMNS[2], consumerID));
        liste2.add(new DataTable(COLUMNS[3], type));
        liste2.add(new DataTable(COLUMNS[4], dateStart.toString()));
        return db.modifyDataDB(liste, liste2);
    }


    /**
     * fonction qui permet de générer un nouvel ID
     * @return Le nouvel id
     */
    public static String getNewID()
    {
        ContractDB db = new ContractDB();
        return db.getNewID(COLUMNS[0]);
    }


    /**
     * fonction qui permet de supprimer un contract
     * @param id l'id du contract
     * @return true si la supression a réussi, false sinon
     */
    public static boolean deleteContractDB(String id){
        ContractDB db = new ContractDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], id));
        return db.deleteDataDB(liste);
    }
}
