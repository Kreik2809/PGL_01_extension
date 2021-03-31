package gui;

import gui.SupplyPointLine;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Personnalise l'affichage des informations relatives à un compteur dans une TableView
 */
public class SupplyPointTabCell<T> extends TableCell<SupplyPointLine, T> {

    private Color bg = Color.web("#0E2439");

    private Color selectedBG = Color.web("#6C8FB3");

    private SupplyPointLine sp;

    public SupplyPointTabCell(){
        super();
    }

    @Override
    protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            String s = "";
            if (item instanceof String)
                s = (String) item;

            this.setText(null);
            this.setGraphic(null);
            TableRow<SupplyPointLine> currentRow = getTableRow();
            sp = currentRow.getItem();
            if (sp != null && sp.getSpecialIndicator().equals("selected")) {
                currentRow.setBackground(new Background(new BackgroundFill(selectedBG, null, null)));
            } else
                currentRow.setBackground(new Background(new BackgroundFill(bg, null, null)));
            this.setTextFill(Color.WHITE);
            this.setFont(Font.font("Arial Black", 10));

            if (!empty) {
                this.setText(s.toUpperCase());
            }
    }
}
