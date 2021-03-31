package tool.sql;

import tool.DataBase;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * classe abstraite qui permet de gérer le lien DB<=>JAVA facilement (sans s'occuper du language SQL)
 */
public abstract class DB
{
    protected String tableName;
    protected ArrayList<String> columns;

    /**
     * constructeur qui permet d'initialiser la classe
     * @param tableName le nom de la table dans la DB
     * @param columns le nom des colonnes de la table
     */
    protected DB(String tableName, String ... columns)
    {
        this.columns = new ArrayList<>();
        this.tableName = tableName;
        for(int i = 0; i < columns.length ; i++)
        {
            this.columns.add(columns[i]);
        }
    }


    /**
     * méthode qui permet de récuperer les données de la table dans la DB en fonction de la liste de DataTable donné.
     * (nom de colonne, valeur) sera transformé en "WHERE [nom de colonne] = [valeur]"
     * @param columns la liste de DataTable
     * @return la liste des résultat sous la forme (columns[0], columns[1], ... , columns[k-1], columns[0], columns[1], ...) ou columns est le nom des colonnes passé au constructeur et k est le nbr de colonne
     */
    protected ArrayList<String> getDataDB(ArrayList<DataTable> columns)
    {
        ArrayList<String> listData = new ArrayList<>();
        try
        {
            String request = "SELECT * from " + tableName + " WHERE ";
            for(int i = 0 ; i < columns.size() - 1 ; i++)
            {
                request += columns.get(i).getColumn() + " = '" + columns.get(i).getValueColumn() + "' AND ";
            }
            request += columns.get(columns.size()-1).getColumn() + " = '" + columns.get(columns.size()-1).getValueColumn() + "';";
            DataBase db = new DataBase();
            ResultSet rs = db.sendRequest(request);
            while(rs.next())
            {
                for(int i = 0; i < this.columns.size() ; i++)
                {
                    listData.add(rs.getString(this.columns.get(i)));
                }
            }
            return listData;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * méthode qui permet de récuperer toute les données de la table dans la DB
     * @return la liste des résultat sous la forme (columns[0], columns[1], ... , columns[k-1], columns[0], columns[1], ...) ou columns est le nom des colonnes passé au constructeur et k est le nbr de colonne
     */
    protected ArrayList<String> getAllDataDB()
    {
        ArrayList<String> listData = new ArrayList<>();
        try
        {
            String request = "SELECT * from " + tableName;
            DataBase db = new DataBase();
            ResultSet rs = db.sendRequest(request);
            while(rs.next())
            {
                for(int i = 0; i < this.columns.size() ; i++)
                {
                    listData.add(rs.getString(this.columns.get(i)));
                }
            }
            return listData;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * méthode qui permet d'ajouter des données dans la table de la DB
     * @param columns la liste de DataTable permettant de vérifier si les données n'existe pas déjà
     * @param newColumn la liste de DataTable contenant les données a ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    protected boolean addDataDB(ArrayList<DataTable> columns, ArrayList<DataTable> newColumn)
    {
        String request = "SELECT * from " + tableName + " where ";
        for(int i = 0 ; i < columns.size() - 1 ; i++)
        {
            request += columns.get(i).getColumn() + " = '" + columns.get(i).getValueColumn() + "' AND ";
        }
        request += columns.get(columns.size()-1).getColumn() + " = '" + columns.get(columns.size()-1).getValueColumn() + "';";
        DataBase db = new DataBase();
        ResultSet rs = db.sendRequest(request);
        try {
            if(!rs.next())
            {
                request = "INSERT INTO " + tableName +"(";
                String requestEnd = " VALUES (";
                for(int i = 0 ; i < newColumn.size()-1 ; i++)
                {
                    request += newColumn.get(i).getColumn() + ", ";
                    requestEnd += "'" + newColumn.get(i).getValueColumn() + "', ";
                }
                request += newColumn.get(newColumn.size()-1).getColumn() + ")";
                requestEnd += "'" + newColumn.get(newColumn.size()-1).getValueColumn() + "');";
                return db.sendModifyRequest(request + requestEnd);
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return false;
    }


    /**
     * méthode qui permet d'ajouter des données dans la table de la DB
     * @param columns la liste de DataTable permettant de vérifier si les données existe bien dans la DB
     * @param newColumn la liste de DataTable contenant les données a modifier
     * @return true si la modification a réussi, false sinon
     */
    protected boolean modifyDataDB(ArrayList<DataTable> columns, ArrayList<DataTable> newColumn)
    {
        String request = "SELECT * from " + tableName + " where ";
        for(int i = 0 ; i < columns.size() - 1 ; i++)
        {
            request += columns.get(i).getColumn() + " = '" + columns.get(i).getValueColumn() + "' AND ";
        }
        request += columns.get(columns.size()-1).getColumn() + " = '" + columns.get(columns.size()-1).getValueColumn() + "';";
        DataBase db = new DataBase();
        ResultSet rs = db.sendRequest(request);
        try {
            while(rs.next())
            {
                String request2 = " WHERE ";
                for(int i = 0; i < columns.size()-1 ; i++)
                {
                    request2 += columns.get(i).getColumn() + " = '" + columns.get(i).getValueColumn() +"' AND ";
                }
                request2 += columns.get(columns.size()-1).getColumn() + " = '" + columns.get(columns.size()-1).getValueColumn() + "';";
                request = "UPDATE " + tableName + " SET ";
                for(int i = 0; i < newColumn.size()-1 ; i++)
                {
                    request += newColumn.get(i).getColumn() + " = '" + newColumn.get(i).getValueColumn() +"', ";
                }
                request += newColumn.get(newColumn.size()-1).getColumn() + " = '" + newColumn.get(newColumn.size()-1).getValueColumn() + "'";
                return db.sendModifyRequest(request + request2);
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return false;
    }


    /**
     * méthode qui permet de supprimer des données dans la table de la DB
     * @param columns la liste de DataTable permettant a supprimer
     * @return true si la suppression a réussi, false sinon
     */
    protected boolean deleteDataDB(ArrayList<DataTable> columns)
    {
        String request = "SELECT * from " + tableName + " where ";
        for(int i = 0 ; i < columns.size() - 1 ; i++)
        {
            request += columns.get(i).getColumn() + " = '" + columns.get(i).getValueColumn() + "' AND ";
        }
        request += columns.get(columns.size()-1).getColumn() + " = '" + columns.get(columns.size()-1).getValueColumn() + "';";
        DataBase db = new DataBase();
        ResultSet rs = db.sendRequest(request);
        try {
            while(rs.next())
            {
                String request2 = " WHERE ";
                for(int i = 0; i < columns.size()-1 ; i++)
                {
                    request2 += columns.get(i).getColumn() + " = '" + columns.get(i).getValueColumn() +"' AND ";
                }
                request2 += columns.get(columns.size()-1).getColumn() + " = '" + columns.get(columns.size()-1).getValueColumn() + "';";
                request = "DELETE FROM " + tableName;
                return db.sendModifyRequest(request + request2);
            }
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return false;
    }


    /**
     * méthode permettant de calculer un nouveau identifiant en fonction du nom de la colonne (clée primaire, nbr entier uniquement) de la table
     * @param columnName le nom de la colonne
     * @return le nouvel ID
     */
    protected String getNewID(String columnName)
    {
        DataBase db = new DataBase();
        ResultSet rs = db.sendRequest("SELECT COUNT(*) as count FROM " + tableName +";");
        try {
            if(rs.next())
            {
                return getMaxID(columnName);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    /**
     * méthode permettant de calculer l'ID ayant le nombre max d'une table (entier)
     * @param columnName le nom de la colonne
     * @return le nouvel ID
     */
    private String getMaxID(String columnName)
    {
        DataBase db = new DataBase();
        int max = 0;
        ResultSet rs = db.sendRequest("SELECT * FROM " + tableName +";");
        try {
            while(rs.next())
            {
                int value = Integer.valueOf(rs.getString(columnName));
                if(value > max)
                    max = value;
            }
            return String.valueOf(max + 1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * méthode permettant de récuperer la date d'aujourd'hui
     * @return la date
     */
    public static Date dateToday()
    {
        DataBase db = new DataBase();
        ResultSet rs = db.sendRequest("SELECT NOW() as today");
        try {
            if(rs.next())
            {
                return rs.getDate("today");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
