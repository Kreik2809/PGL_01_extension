package gui.app1;

import gui.CustomListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.URLDB;
import tool.sql.EnergyConsumerPortfolioDB;
import tool.sql.NotificationDB;
import user.EnergyConsumer;
import user.EnergyPortfolio;
import user.Invitation;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * La classe NotifController est une classe utilitaire qui contrôle tous les éléments graphiques
 * de la page de gestion des notifications.
 */
public class NotifController implements Initializable  {

    private static ObservableList<Invitation> items = FXCollections.observableArrayList();

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button deleteButton;

    @FXML
    private Button acceptButton;

    @FXML
    private ListView<Invitation> invitations;

    private Invitation currentI;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        invitations.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleInvitationClick();
            }
        });
        EnergyConsumer u = GuiHandler.getGui().getCurrentUser();
        u.fetchNotifications();
        items.clear();
        ArrayList<Invitation> list = u.getInvitations();
        showInvitation(list);
    }



    /**
     * Cette méthode est appelée lorsque l'on clique sur une invitation, elle permet de mettre à jour l'affichage
     * de l'invitation.
     */
    public void handleInvitationClick(){
        if ((currentI = invitations.getSelectionModel().getSelectedItem()) != null){
            int i = invitations.getSelectionModel().getSelectedIndex();
            for (Invitation inv : invitations.getItems()){
                inv.setSpecialIndicator("");
            }
            invitations.getItems().get(i).setSpecialIndicator("selected");
            invitations.setItems(null);
            invitations.setItems(items);

        }
    }


    /**
     * Cette méthode personnalise et affiche les invitations d'un utilisateur à l'écran.
     * @param l La liste des invitations à afficher
     */
    public void showInvitation(ArrayList<Invitation> l ){
        items.addAll(l);
        invitations.setItems(items);
        invitations.setCellFactory(new Callback<ListView<Invitation>, ListCell<Invitation>>() {
            @Override
            public ListCell<Invitation> call(ListView<Invitation> listView) {
                return new CustomListCell<Invitation>(GuiHandler.getGui().getCurrentUser());
            }
        });
    }

    /**
     * Cette méthode est executée lorsque l'utilisateur clique sur le bouton accepter.
     * La méthode s'occupe d'accepter l'invitation et donc de créer correctement les associations portfeuille-utilisateur
     * et utilisateur-portfeuille.
     * La méthode met également à jour la liste d'invitations.
     * @param event
     */
    @FXML
    void acceptClicked(MouseEvent event) {
        if (currentI != null){
            EnergyPortfolio p = currentI.getEnergyPortfolio();
            GuiHandler.updatePortfolios(p);
            currentI.getEnergyConsumer().manageInvitation(currentI, true);
            items.remove(currentI);
            currentI = null;
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR ! ");
            alert.setHeaderText(null);
            alert.setContentText("veuillez sélectionner une invitation avant de l'accepter");
            alert.showAndWait();
        }
    }

    @FXML
    void deleteClicked(MouseEvent event) {
        if (currentI != null){
            currentI.getEnergyConsumer().manageInvitation(currentI, false);
            items.remove(currentI);
            currentI = null;
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR ! ");
            alert.setHeaderText(null);
            alert.setContentText("veuillez sélectionner une invitation avant de la refuser");
            alert.showAndWait();
        }
    }

    public static ObservableList<Invitation> getItems() {
        return items;
    }

}
