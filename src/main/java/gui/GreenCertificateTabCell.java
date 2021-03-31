package gui;

import gui.app1.GreenCertificateLine;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import user.DataSupplyPoint;

/**
 * Cette classe personnalise l'affichage des certificats verts dans les table view
 */
public class GreenCertificateTabCell<T> extends TableCell<GreenCertificateLine, T> {

    private Color bg = Color.web("#0E2439");

    public GreenCertificateTabCell(){
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
        TableRow<GreenCertificateLine> currentRow = getTableRow();
        currentRow.setBackground(new Background(new BackgroundFill(bg,null,null)));
        this.setTextFill(Color.WHITE);
        this.setFont(Font.font("Arial Black", 10));

        if(!empty){
            this.setText(s.toUpperCase());
        }
    }
}
