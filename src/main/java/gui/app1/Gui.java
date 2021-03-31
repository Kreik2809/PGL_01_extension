package gui.app1;

import javafx.stage.Stage;
import tool.AccessLevelType;
import user.*;

import java.io.IOException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Random;


/**
 * La classe Gui instancie et regroupe les informations relatives à la session en cours.
 * Elle permet de lancer l'interface graphique de l'application de gestion de portefeuilles énergétiques.
 */
public class Gui {

    private Stage primaryStage;

    private EnergyConsumer currentUser;

    private EnergyPortfolio currentPortfolio;

    private SupplyPoint currentSupplyPoint;

    /**
     * A la créaton d'un objet de type Gui, la classe utilitaire GuiHandler reçoit une référence vers
     * la GUI nouvellement créée.
     */
    public Gui(){

        GuiHandler.setGui(this);

    }



    /**
     * La méthode start charge la première page de l'application.
     * @param primaryStage
     */
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        GuiHandler.loadScene("app1/login_signup.fxml", 500, 450, this);
        primaryStage.setTitle("Application 1");
        primaryStage.show();
    }

    public Stage getPrimaryStage(){
        return primaryStage;
    }

    public EnergyConsumer getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(EnergyConsumer currentUser) {
        this.currentUser = currentUser;
    }

    public void setCurrentPortfolio(EnergyPortfolio currentPortfolio) {
        this.currentPortfolio = currentPortfolio;
    }

    public EnergyPortfolio getCurrentPortfolio() {
        return currentPortfolio;
    }

    public SupplyPoint getCurrentSupplyPoint() {
        return currentSupplyPoint;
    }

    public void setCurrentSupplyPoint(SupplyPoint currentSupplyPoint) {
        this.currentSupplyPoint = currentSupplyPoint;
    }
}
