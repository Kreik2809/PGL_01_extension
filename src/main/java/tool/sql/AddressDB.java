package tool.sql;
import java.util.ArrayList;

/**
 * classe qui s'occupe de la table ADDRESS
 */
public class AddressDB extends DB
{
    private static String[] COLUMNS = {"USERNAME", "STREET", "NUMBER", "POSTCODE", "CITY", "EXTRA"};

    private AddressDB()
    {
        super("ADDRESS", COLUMNS);
    }

    /**
     * permet de récupérer les données dans la table ADDRESS
     * @param userName le nom d'utilisateur
     * @return liste des données trouvées dans la table (USERNAME ; PASSWORD ; TYPE)
     */
    public static ArrayList<String> getAddressDB(String userName)
    {
        AddressDB db = new AddressDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], userName));
        return db.getDataDB(liste);
    }

    /**
     * fonction qui permet d'ajouter des données à la table ADDRESS
     * @param userName le nom d'utilisateur
     * @param street la rue
     * @param number le numéro de maison
     * @param postCode le code postal
     * @param city la ville
     * @param extra les informations extras
     * @return true si l'ajout a réussis, false sinon
     */
    public static boolean addAddressDB(String userName, String street, String number, String postCode, String city, String extra)
    {
        AddressDB db = new AddressDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], userName));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], userName));
        liste2.add(new DataTable(COLUMNS[1], street));
        liste2.add(new DataTable(COLUMNS[2], number));
        liste2.add(new DataTable(COLUMNS[3], postCode));
        liste2.add(new DataTable(COLUMNS[4], city));
        liste2.add(new DataTable(COLUMNS[5], extra));
        return db.addDataDB(liste, liste2);
    }

    /**
     * fonction qui permet de modifier des données à la table ADDRESS
     * @param lastUsername l'ancien nom d'utilisateur
     * @param newUserName le nouveau nom d'utilisateur
     * @param newStreet la rue
     * @param newNumber le numéro de maison
     * @param newPostCode le code postal
     * @param newCity la ville
     * @param newExtra les informations extras
     * @return
     */
    public static boolean modifyAddressDB(String lastUsername, String newUserName, String newStreet, String newNumber, String newPostCode, String newCity, String newExtra)
    {
        AddressDB db = new AddressDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], lastUsername));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], newUserName));
        liste2.add(new DataTable(COLUMNS[1], newStreet));
        liste2.add(new DataTable(COLUMNS[2], newNumber));
        liste2.add(new DataTable(COLUMNS[3], newPostCode));
        liste2.add(new DataTable(COLUMNS[4], newCity));
        liste2.add(new DataTable(COLUMNS[5], newExtra));
        return db.modifyDataDB(liste, liste2);
    }
}
