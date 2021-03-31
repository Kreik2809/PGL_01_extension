package gui.app1;

import gui.GestionCustomListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.URLDB;
import tool.sql.EnergyConsumerPortfolioDB;
import tool.sql.PortfolioDB;
import tool.sql.PortfolioSupplyPointDB;
import user.EnergyConsumer;
import user.SupplyPoint;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre de gestion d'un portefeuille
 */
public class GestionPController implements Initializable {

    @FXML
    private ListView<SupplyPoint> meters;

    @FXML
    private ListView<EnergyConsumer> users;

    @FXML
    private Button delC;

    @FXML
    private Button addC;

    @FXML
    private Button delU;

    @FXML
    private Button addU;

    @FXML
    private Button modifU;

    @FXML
    private Button ModifC;

    @FXML
    private Button delP;

    private static ObservableList<SupplyPoint> observableMeters = FXCollections.observableArrayList();

    private static ObservableList<EnergyConsumer> observableEnergyConsumer = FXCollections.observableArrayList();

    private static EnergyConsumer currentE;

    private static SupplyPoint currentS;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        //On affiche les compteurs et les utilisateurs du portefeuille
        users.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleUserClicked();
            }
        });

        meters.setOnMouseClicked((new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMeterClicked();
            }
        }));

        observableEnergyConsumer.clear();
        observableMeters.clear();
        GuiHandler.getGui().getCurrentPortfolio().fetchAccesLevels();
        ArrayList<SupplyPoint> l1 = GuiHandler.getGui().getCurrentPortfolio().getSupplyPoints();
        ArrayList<EnergyConsumer> l2 = GuiHandler.getGui().getCurrentPortfolio().getEnergyConsumer();
        showData(l1, l2);


    }

    /**
     * Cette méthode est appelée lorsque l'on clique sur un utilisateur, elle permet de mettre à jour l'affichage
     * de l'utilisateur dans la liste.
     */
    public void handleUserClicked(){
        if ((currentE = users.getSelectionModel().getSelectedItem()) != null) {
            int i = users.getSelectionModel().getSelectedIndex();
            for (EnergyConsumer ec : users.getItems()) {
                ec.setSpecialIndicator("");
            }
            users.getItems().get(i).setSpecialIndicator("selected");
            users.setItems(null);
            users.setItems(observableEnergyConsumer);
        }
    }

    /**
     * Cette méthode est appelée lorsque l'on clique sur un compteur, elle permet de mettre à jour l'affichage
     * du compteur dans la liste.
     */
    public void handleMeterClicked(){
            if ((currentS = meters.getSelectionModel().getSelectedItem()) != null) {
                int i = meters.getSelectionModel().getSelectedIndex();
                for (SupplyPoint sp : meters.getItems()) {
                    sp.setSpecialIndicator("");
                }
                meters.getItems().get(i).setSpecialIndicator("selected");
                meters.setItems(null);
                meters.setItems(observableMeters);
            }
    }


    /**
     * La méthode showData prend en paramètre une liste de compteurs et une liste d'utilisateur.
     * Son rôle est d'afficher de manière personnalisée ces informations dans les deux listView de la fenêtre.
     */
    public void showData(List<SupplyPoint> l1, List<EnergyConsumer> l2){
        observableMeters.addAll(l1);
        observableEnergyConsumer.addAll(l2);
        meters.setItems(observableMeters);
        users.setItems(observableEnergyConsumer);
        meters.setCellFactory(new Callback<ListView<SupplyPoint>, ListCell<SupplyPoint>>() {
            @Override
            public ListCell<SupplyPoint> call(ListView<SupplyPoint> param) {
                return new GestionCustomListCell<>();
            }
        });
        users.setCellFactory(new Callback<ListView<EnergyConsumer>, ListCell<EnergyConsumer>>() {
            @Override
            public ListCell<EnergyConsumer> call(ListView<EnergyConsumer> param) {
                return new GestionCustomListCell<>();
            }
        });
    }

    /**
     * Cette méthode met à jour l'entièreté de la liste de compteurs disponibles sur la page.
     */
    public static void updateMeters(){
        observableMeters.clear();
        observableMeters.addAll(GuiHandler.getGui().getCurrentPortfolio().getSupplyPoints());
    }

    /**
     * Cette méthode met à jour l'entièreté de la liste d'utilisateurs disponibles sur la page.
     */
    public static void updateUsers(){
        observableEnergyConsumer.clear();
        observableEnergyConsumer.addAll(GuiHandler.getGui().getCurrentPortfolio().getEnergyConsumer());
    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton d'ajout de compteur
     */
    @FXML
    void addCClicked(MouseEvent event) {
        GuiHandler.setSecondCp(((Button)event.getSource()).getScene().getRoot());
        GuiHandler.getSecondCp().setDisable(true);
        GuiHandler.createScene("app1/addCounter.fxml", "Ajout d'un compteur", 300, 450, this);
    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton d'ajout d'utilisateur
     */
    @FXML
    void addUClicked(MouseEvent event) {
        GuiHandler.setSecondCp(((Button)event.getSource()).getScene().getRoot());
        GuiHandler.getSecondCp().setDisable(true);
        GuiHandler.createScene("app1/addUser.fxml", "Ajout d'un utilisateur", 300, 200, this);
    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton de supression de compteurs.
     */
    @FXML
    void delCClicked(MouseEvent event) {
        if (currentS != null){
            try{
                HttpRequest.Post(URLDB.URL + "Meters/?PORTFOLIO_ID="+GuiHandler.getGui().getCurrentPortfolio().getID()+"&EAN18="+currentS.getEAN18()+"&ACTION=delete");
            }catch (IOException e){
                e.printStackTrace();
            }

            GuiHandler.getGui().getCurrentPortfolio().removeMeter(currentS);
            currentS = null;
            GuiHandler.getGui().setCurrentSupplyPoint(null);
            DashboardMController.updateMeters();
            updateMeters();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OK");
            alert.setHeaderText(null);
            alert.setContentText("compteur supprimé !");
            alert.showAndWait();

        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un compteur !");
            alert.showAndWait();
        }
    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton de suppression de portefeuille
     */
    @FXML
    void delPClicked(MouseEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMATIONS");
        alert.setHeaderText(null);
        alert.setContentText("êtes-vous sûr de vouloir supprimer ce portefeuille ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
        {
            try{
                HttpRequest.Post(URLDB.URL + "UserPortfolio/?PORTFOLIO_ID="+GuiHandler.getGui().getCurrentPortfolio().getID()+"&ACTION=deletePortfolio");
            } catch (IOException e){
                e.printStackTrace();
            }
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OK");
            alert.setHeaderText(null);
            alert.setContentText("Portefeuille supprimé !");
            alert.showAndWait();
            GuiHandler.deletePortfolio(GuiHandler.getGui().getCurrentPortfolio());
            GuiHandler.loadScene("app1/dashboard_portefeuille.fxml", 800, 550, this);
        }
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton de suppression d'utilisateurs.
     */
    @FXML
    void delUClicked(MouseEvent event) {
        if (currentE != null )
        {
            if(!currentE.getName().equals(GuiHandler.getGui().getCurrentUser().getName()))
            {
                try{
                    HttpRequest.Post(URLDB.URL + "UserPortfolio/?PORTFOLIO_ID="+GuiHandler.getGui().getCurrentPortfolio().getID()+"&USERNAME="+getCurrentE().getName()+"&ACTION=delete");
                }catch (IOException e){
                    e.printStackTrace();
                }
                GuiHandler.getGui().getCurrentPortfolio().removeAccess(currentE);
                updateUsers();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("OK");
                alert.setHeaderText(null);
                alert.setContentText("utilisateur supprimé !");
                alert.showAndWait();

            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR !");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner un autre utilisateur que vous");
                alert.showAndWait();
            }

        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un user (à part vous) !");
            alert.showAndWait();
        }
    }

    /**
     * Cette méthode est appelée lorque l'utilisateur clique sur le bouton de modification de compteurs.
     */
    @FXML
    void modifCClicked(MouseEvent event) {
        if (currentS != null)
            GuiHandler.createScene("app1/modifCounter.fxml", "Modification d'un compteur", 300, 150, this);
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un compteur !");
            alert.showAndWait();
        }

    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton de modification d'utilisateurs.
     */
    @FXML
    void modifUClicked(MouseEvent event) {
        if (currentE != null)
        {
            if(!currentE.getName().equals(GuiHandler.getGui().getCurrentUser().getName()))
                GuiHandler.createScene("app1/modifUser.fxml", "Modification d'un utilisateur", 300, 100, this);
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR !");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner un autre utilisateur que vous");
                alert.showAndWait();
            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un user !");
            alert.showAndWait();
        }
    }

    public static EnergyConsumer getCurrentE() {
        return currentE;
    }

    public static SupplyPoint getCurrentS() {
        return currentS;
    }

}
