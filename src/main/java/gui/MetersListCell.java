package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import user.SupplyPoint;


/**
 * La classe MeterListCell est utilisée pour personnaliser l'affichage des compteurs
 * contenus dans les listView de javaFX.
 */
public class MetersListCell extends ListCell<SupplyPoint> {

    private Color bg = Color.web("#0E2439");

    private HBox content;
    private Text name;
    private Text code;
    private Text nCounter;
    private Text type;


    public MetersListCell() {
        super();
        this.setBackground(new Background(new BackgroundFill(bg,null,null)));
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10, 0, 0, 0));
        name = new Text();
        code = new Text();
        type = new Text();
        nCounter = new Text();

        VBox vBox = new VBox(name, code, type);
        content = new HBox(nCounter, vBox);
        content.setSpacing(10);
        content.setStyle("-fx-border-color :  #F3CB83; -fx-border-width : 3px; -fx-border-radius : 5px");
        content.setMaxWidth(200);
        content.setPrefHeight(70);
        name.setFont(Font.font("Arial Black", 12));
        code.setFont(Font.font("Arial Black", 10));
        nCounter.setFont(Font.font("Arial Black", 12));
        type.setFont(Font.font("Arial Black", 12));

        name.setFill(Color.WHITE);
        code.setFill(Color.WHITE);
        nCounter.setFill(Color.WHITE);
        type.setFill(Color.WHITE);

    }

    @Override
    protected void updateItem(SupplyPoint item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) { // <== test for null item and empty parameter
            name.setText("Nom: " + item.getName());
            code.setText("Code EAN: " + item.getEAN18());
            type.setText("Type : " + item.getSupplyPointType().toString());

            nCounter.setText("");
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }

}
