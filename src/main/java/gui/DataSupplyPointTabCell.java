package gui;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import user.DataSupplyPoint;

/**
 * Personnalise l'affichage des données dans le tableau reprenant les données de consommation du dashboard.
 */
public class DataSupplyPointTabCell<T> extends TableCell<DataSupplyPoint, T> {

    private Color bg = Color.web("#0E2439");

    public DataSupplyPointTabCell(){
        super();
    }

    @Override
    protected void updateItem(T item, boolean empty){
        super.updateItem(item, empty);

        String s;
        if (item instanceof  String)
            s = (String) item;
        else
            s = String.valueOf(item);
        this.setText(null);
        this.setGraphic(null);
        TableRow<DataSupplyPoint> currentRow = getTableRow();
        currentRow.setBackground(new Background(new BackgroundFill(bg,null,null)));
        this.setTextFill(Color.WHITE);
        this.setFont(Font.font("Arial Black", 10));

        if(!empty){
            this.setText(s.toUpperCase());
        }
    }
}
