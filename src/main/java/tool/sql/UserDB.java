package tool.sql;

import user.UserType;

import java.util.ArrayList;

public class UserDB extends DB
{
    public static final String[] COLUMNS = {"USERNAME", "PWD", "TYPE", "MAIL"};
    private UserDB()
    {
        super("USERS", COLUMNS);
    }

    /**
     * permet de récupérer les données dans la table USERS
     * @param userName le nom d'utilisateur
     * @return liste des données dans trouvée dans la table (USERNAME ; PASSWORD ; TYPE)
     */
    public static ArrayList<String> getUserDB(String userName)
    {
        UserDB db = new UserDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[0], userName));
        return db.getDataDB(liste);
    }

    /**
     * permet de récupérer les données dans la table USERS
     * @param mail l'adresse mail de l'utilisateur
     * @return liste des données dans trouvée dans la table (USERNAME ; PASSWORD ; TYPE)
     */
    public static ArrayList<String> getUserByMailDB(String mail)
    {
        UserDB db = new UserDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[3], mail));
        return db.getDataDB(liste);
    }

    /**
     * fonction qui permet d'ajouter des données à la table USERS
     * @param mail l'adresse mail
     * @param password le mot de passe
     * @param type le type d'utilisateur
     * @return true si l'ajout a réussis, false sinon
     */
    public static boolean addUserDB(String mail, String username, String password, UserType type)
    {
        UserDB db = new UserDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[3], mail));
        liste.add(new DataTable(COLUMNS[0], username));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], username));
        liste2.add(new DataTable(COLUMNS[1], password));
        liste2.add(new DataTable(COLUMNS[2], type.name()));
        liste2.add(new DataTable(COLUMNS[3], mail));
        return db.addDataDB(liste, liste2);
    }

    /**
     * fonction qui permet de modifier des données dans la table USERS
     * @param lastMail le nom d'utilisateur présent dans la table
     * @param newMail le nouveau nom d'utilisateur
     * @param newPassword le nouveau mot de passe
     * @param newType le nouveau type d'utilisateur
     * @return true si la modification a réussis, false sinon
     */
    public static boolean modifyUserDB(String lastMail, String newMail,String username, String newPassword, UserType newType)
    {
        UserDB db = new UserDB();
        ArrayList<DataTable> liste = new ArrayList<>();
        liste.add(new DataTable(COLUMNS[3], lastMail));
        liste.add(new DataTable(COLUMNS[0], username));
        ArrayList<DataTable> liste2 = new ArrayList<>();
        liste2.add(new DataTable(COLUMNS[0], username));
        liste2.add(new DataTable(COLUMNS[1], newPassword));
        liste2.add(new DataTable(COLUMNS[2], newType.name()));
        liste2.add(new DataTable(COLUMNS[3], newMail));
        return db.modifyDataDB(liste, liste2);
    }
}
