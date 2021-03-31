package gui;

import gui.app1.GuiHandler;
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
import tool.AccessLevel;
import user.EnergyConsumer;
import user.EnergyPortfolio;
import user.Invitation;

/**
 * La classe CustomListCell est utilisée pour personnaliser l'affichage de certains types d'éléments
 * contenus dans les listView de javaFX.
 */
public class CustomListCell<T> extends ListCell<T> {
    private HBox content;
    private Text t1;
    private Text t2;
    private Text t3;
    private Text t4;
    private Text t5;

    private EnergyConsumer u;

    private Color bg = Color.web("#0E2439");

    public CustomListCell(EnergyConsumer u){
        super();
        this.setBackground(new Background(new BackgroundFill(bg,null,null)));
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20, 0, 0, 0));
        this.u = u;
        t1 = new Text();
        t2 = new Text();
        t3 = new Text();
        t4 = new Text();
        t5 = new Text();
        VBox vBox = new VBox(t1, t2, t4, t3);
        content = new HBox(t5, vBox);
        content.setSpacing(20);
        content.setStyle("-fx-border-color :  #F3CB83; -fx-border-width : 3px; -fx-border-radius : 10px");
        content.setMaxWidth(400);
        content.setPrefHeight(150);
        t1.setFont(Font.font("Arial Black", 16));
        t2.setFont(Font.font("Arial Black", 16));
        t4.setFont(Font.font("Arial Black", 16));
        t3.setFont(Font.font("Arial Black", 16));
        t5.setFont(Font.font("Arial Black", 16));
        t1.setFill(Color.WHITE);
        t2.setFill(Color.WHITE);
        t4.setFill(Color.WHITE);
        t3.setFill(Color.WHITE);
        t5.setFill(Color.WHITE);

    }

    public void updateItem(T item, boolean empty){
        super.updateItem(item, empty);
        if (item != null && !empty && item instanceof EnergyPortfolio) {

            EnergyPortfolio e = (EnergyPortfolio) item;

            //On cherche l'accessLevel
            String accessLevel="";
            for (AccessLevel a : u.getAccessLevels()){
                if (a.getEnergyPortfolio().getID() == e.getID())
                    accessLevel = a.getAccessLevel().toString();
            }
            // <== test for null item and empty parameter
            t1.setText("Nom: " + e.getName());
            t2.setText("Manager: " + e.getManager().getName());
            t4.setText("Acces: " + accessLevel);
            t5.setText("ID: " + e.getID());
            setGraphic(content);
        }
        else if (item != null && !empty && item instanceof Invitation){
            Invitation i = (Invitation) item;

            if (i.getSpecialIndicator().equalsIgnoreCase("selected")){
                content.setStyle("-fx-border-color :  #FFFFFF; -fx-border-width : 3px; -fx-border-radius : 10px");
            }
            else{
                content.setStyle("-fx-border-color :  #F3CB83; -fx-border-width : 3px; -fx-border-radius : 10px");
            }

            t1.setText("Proprietaire: " + i.getEnergyPortfolio().getManager().getName());
            t2.setText("Portefeuille: " + i.getEnergyPortfolio().getName());
            t4.setText("Acces: " + i.getAccessLevelType().toString());
            t3.setText("");
            t5.setText("ID: " + i.getID());
            setGraphic(content);
        }
        else if (item != null && !empty && item instanceof  EnergyConsumer){
            EnergyConsumer e = (EnergyConsumer) item;
            t1.setText("Nom : " + e.getName());
            t2.setText("Acces: " + e.getAccessLevelType(GuiHandler.getGui().getCurrentPortfolio()));
            this.setPadding(new Insets(-20, 0, 0, 0));
            content.setSpacing(30);
            content.setMaxWidth(300);
            content.setMaxHeight(50);
            setGraphic(content);
        }
        else {
            setGraphic(null);
        }

    }

}
