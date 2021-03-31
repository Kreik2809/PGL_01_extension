package tool.sql;

/**
 * classe qui représente une donnée d'une table, composée d'un nom de colonne et d'une valeur
 */
public class DataTable
{
    private String Column;
    private String valueColumn;

    public DataTable(String column, String value)
    {
        Column = column;
        this.valueColumn = value;
    }

    public String getColumn() {
        return Column;
    }

    public String getValueColumn() {
        return valueColumn;
    }

    @Override
    public String toString(){
        return "[" + Column + " = " + valueColumn + "]";
    }

}
