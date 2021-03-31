package gui;

import gui.app1.GreenCertificateLine;
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
 * Cette classe personnalise l'affichage des certificats verts dans les list view
 */
public class GreenCertificatListCell extends ListCell<GreenCertificateLine> {

    private Color bg = Color.web("#0E2439");

    private VBox content;
    private Text id;
    private Text supplier;
    private Text client;
    private Text lastCertif;
    private Text amount;
    private Text ean;

    public GreenCertificatListCell() {
        super();
        this.setBackground(new Background(new BackgroundFill(bg,null,null)));
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10, 0, 0, 0));
        id = new Text();
        supplier = new Text();
        client = new Text();
        lastCertif = new Text();
        amount = new Text();
        ean = new Text();

        content = new VBox(id, supplier, client, ean, lastCertif, amount);
        content.setSpacing(10);
        content.setStyle("-fx-border-color :  #F3CB83; -fx-border-width : 3px; -fx-border-radius : 5px");
        content.setMinWidth(230);
        content.setMaxWidth(230);
        content.setPrefHeight(185);
        id.setFont(Font.font("Arial Black", 12));
        supplier.setFont(Font.font("Arial Black", 12));
        lastCertif.setFont(Font.font("Arial Black", 9));
        amount.setFont(Font.font("Arial Black", 12));
        client.setFont(Font.font("Arial Black", 12));
        ean.setFont(Font.font("Arial Black", 12));

        id.setFill(Color.WHITE);
        supplier.setFill(Color.WHITE);
        lastCertif.setFill(Color.WHITE);
        amount.setFill(Color.WHITE);
        client.setFill(Color.WHITE);
        ean.setFill(Color.WHITE);

    }

    @Override
    protected void updateItem(GreenCertificateLine item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) { // <== test for null item and empty parameter
            id.setText("ID: " + item.getId());
            supplier.setText("Fournisseur : " + item.getSupplier());
            client.setText("Client : " + item.getClient());
            ean.setText("EAN : " + item.getEan());

            lastCertif.setText("Dernier certificat : " + item.getLastDate());
            amount.setText("Montant (kwh) : " + item.getAmount());
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}
