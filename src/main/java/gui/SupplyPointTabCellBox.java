package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Personnalise une colonne d'un tableau contenant des SupplyPointLine afin qu'elle y affiche une checkBox
 */
public class SupplyPointTabCellBox<T> extends TableCell<SupplyPointLine, T> {

    private Color bg = Color.web("#0E2439");

    private SupplyPointLine sp;

    public SupplyPointTabCellBox(){
        super();
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        TableRow<SupplyPointLine> currentRow = getTableRow();
        sp = currentRow.getItem();
        currentRow.setBackground(new Background(new BackgroundFill(bg,null,null)));

        if (sp != null) {
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(newValue = true){
                        sp.setSpecialIndicator("checked");
                    }
                    else{
                        sp.setSpecialIndicator("");
                    }
                }
            });

            this.setText(null);
            this.setGraphic(checkBox);
        }

    }
}
