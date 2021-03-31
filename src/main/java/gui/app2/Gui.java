package gui.app2;

import javafx.stage.Stage;
import user.*;

import java.io.IOException;

/**
 * La classe Gui instancie et regroupe les informations relatives à la session en cours.
 * Elle permet de lancer l'interface graphique de l'application facilitation de lien entre le fournisseur et le client.
 */
public class Gui {

    private Stage primaryStage;

    private EnergyConsumer currentConsumer;

    private EnergySupplier currentSupplier;

    private SupplyPoint currentSupplyPoint;

    private UserType currentUserType;

    public Gui(){
        GuiHandler.setGui(this);
    }

    /**
     * La méthode start charge la première page de l'application.
     * @param primaryStage
     */
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        GuiHandler.loadScene("app2/login_signup.fxml", 450, 350, this);
        primaryStage.setTitle("Application 2");
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public UserType getCurrentUserType() {
        return currentUserType;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setCurrentUserType(UserType currentUserType) {
        this.currentUserType = currentUserType;
    }

    public EnergyConsumer getCurrentConsumer() {
        return currentConsumer;
    }

    public void setCurrentConsumer(EnergyConsumer currentConsumer) {
        this.currentConsumer = currentConsumer;
    }

    public EnergySupplier getCurrentSupplier() {
        return currentSupplier;
    }

    public void setCurrentSupplier(EnergySupplier currentSupplier) {
        this.currentSupplier = currentSupplier;
    }

    public SupplyPoint getCurrentSupplyPoint() {
        return currentSupplyPoint;
    }

    public void setCurrentSupplyPoint(SupplyPoint currentSupplyPoint) {
        this.currentSupplyPoint = currentSupplyPoint;
    }
}
