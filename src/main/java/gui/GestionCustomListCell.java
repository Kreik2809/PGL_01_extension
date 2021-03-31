package gui;

import gui.app1.GuiHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import user.EnergyConsumer;
import user.SupplyPoint;

/**
 * La classe GestionCustomListCell est utilisée pour personnaliser l'affichage des Utilisateurs et des Compteurs
 * contenus dans les listView de la fenêtre de gestion du portefeuille.
 */
public class GestionCustomListCell<T> extends ListCell<T> {

    private VBox content;
    private Text T1;
    private Text T2;

    private Color bg = Color.web("#0E2439");

    public GestionCustomListCell(){
        super();
        this.setBackground(new Background(new BackgroundFill(bg,null,null)));
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10, 0, 10, 0));
        T1 = new Text();
        T2 = new Text();
        content = new VBox(T1, T2);
        content.setSpacing(10);
        content.setPadding(new Insets(5, 5, 5, 5));
        content.setStyle("-fx-border-color :  #F3CB83; -fx-border-width : 1px; -fx-border-radius : 10px");
        content.setMaxWidth(300);
        content.setMaxHeight(70);
        T1.setFont(Font.font("Arial Black", 14));
        T2.setFont(Font.font("Arial Black", 12));
        T1.setFill(Color.WHITE);
        T2.setFill(Color.WHITE);
    }


    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item instanceof EnergyConsumer && item != null && !empty){
            EnergyConsumer e = (EnergyConsumer) item;
            if (e.getSpecialIndicator().equalsIgnoreCase("selected")){
                content.setStyle("-fx-border-color :  #FFFFFF; -fx-border-width : 3px; -fx-border-radius : 10px");
            }
            else{
                content.setStyle("-fx-border-color :  #F3CB83; -fx-border-width : 3px; -fx-border-radius : 10px");
            }
            T1.setText("Nom : " + e.getName());
            T2.setText("Acces : " + e.getAccessLevelType(GuiHandler.getGui().getCurrentPortfolio()));
            setGraphic(content);
        }
        else if (item instanceof SupplyPoint && item != null && !empty){
            SupplyPoint s = (SupplyPoint) item;
            if (s.getSpecialIndicator().equalsIgnoreCase("selected")){
                content.setStyle("-fx-border-color :  #FFFFFF; -fx-border-width : 3px; -fx-border-radius : 10px");
            }
            else{
                content.setStyle("-fx-border-color :  #F3CB83; -fx-border-width : 3px; -fx-border-radius : 10px");
            }
            T1.setText("Nom : " + s.getName());
            T2.setText("Code EAN : " + s.getEAN18());
            setGraphic(content);
        }
        else{
            setGraphic(null);
        }
    }

}
