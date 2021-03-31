package tool;

import java.sql.*;

/**
 * classe qui s'occupe de la connexion à la DB
 */
public class DataBase
{
    private static Statement stmt;

    /**
     * constructeur qui permet de se connecter a la DB
     */
    public DataBase()
    {
        try
        {
            try{
                Class cls = Class.forName("Driver");
            } catch(Exception e){
                Class cls = Class.forName("com.mysql.cj.jdbc.Driver");
            }
            if(stmt == null) {
                if (!connexionToDB())
                {
                    throw new SQLException();
                }
            }
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Impossible to find com.mysql.cj.jdbc.Driver\n");
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            System.out.println("ERROR WITH DB CONNEXION");
            System.exit(0);
        }
    }

    /**
     * Méthode qui permet de se connecter à la base de donnée
     * @return true si la connexion est établie, false sinon
     */
    private boolean connexionToDB()
    {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://mysql-pgl-01-autoprod.alwaysdata.net/pgl-01-autoprod_db",
                    "229136",
                    "Rugby2809");
            stmt=con.createStatement();

            return true;
            //con.close();
        }
        catch (SQLException e)
        {
            System.out.println("Impossible to connect with sql\n");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * méthode qui permet d'envoyer une requête et de recevoir le résultat
     * @param requestSQL Le String représentant la requête SQL
     * @return le résultat de la requête
     */
    public ResultSet sendRequest(String requestSQL)
    {
        try {
            ResultSet rs = stmt.executeQuery(requestSQL);
            return rs;
        }
        catch (SQLException e)
        {
            System.out.println("Impossible to connect with sql\n");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * méthode qui permet d'envoyer une requête ayant pour but de modifier la BD
     * @param requestSQL Le String représentant la requête SQL
     * @return true si la modification a été effectuée dans la base, false sinon
     */
    public boolean sendModifyRequest(String requestSQL)
    {
        try {
            int rs = stmt.executeUpdate(requestSQL);
            if(rs == 1)
                return true;
            else
                return false;
        }
        catch (SQLException e)
        {
            System.out.println("Impossible to connect with sql\n");
            e.printStackTrace();
            return false;
        }
    }

}
