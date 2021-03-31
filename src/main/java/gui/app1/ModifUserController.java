package gui.app1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tool.AccessLevelType;
import tool.HttpRequest;
import tool.URLDB;
import tool.sql.EnergyConsumerPortfolioDB;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre de modification d'utilisateurs.
 */
public class ModifUserController implements Initializable {

    @FXML
    private Button modifButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ChoiceBox<String> access;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        access.getItems().addAll("Lecture", "Lecture/Ecriture");
        access.setValue("Lecture");
    }

    @FXML
    void cancelButtonClicked(MouseEvent event) {
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Cette méthode est exécutée lorsque l'utilisateur confirme le changement de niveaux d'accès pour un utilisateur de son portefeuille
     */
    @FXML
    void modifButtonClicked(MouseEvent event) {
        AccessLevelType alt = AccessLevelType.READING; //De base on octroie l'accès le plus faible.
        String a = access.getValue();
        switch(a){
            case "Lecture":
                alt = AccessLevelType.READING;
                break;
            case "Lecture/Ecriture":
                alt = AccessLevelType.WRITING;
                break;
        }
        try{
            HttpRequest.Post(URLDB.URL + "UserPortfolio/?PORTFOLIO_ID="+GuiHandler.getGui().getCurrentPortfolio().getID()+"&USERNAME="+GestionPController.getCurrentE().getName()+"&ACCESS="+alt.toString()+"&ACTION=modify");
        }catch (IOException e){
            e.printStackTrace();
        }
        GuiHandler.getGui().getCurrentPortfolio().modifAccess(GestionPController.getCurrentE(), alt);
        GestionPController.updateUsers();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("OK");
        alert.setHeaderText(null);
        alert.setContentText("modification effectuée !");
        alert.showAndWait();

        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();

    }
}
