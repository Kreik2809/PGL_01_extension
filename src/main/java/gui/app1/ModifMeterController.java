package gui.app1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tool.HttpRequest;
import tool.URLDB;
import tool.sql.DB;
import tool.sql.SupplyPointDB;
import user.EnergyType;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre de modifications de compteurs.
 */
public class ModifMeterController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private Button modifButton;

    @FXML
    private Button cancelButton;

    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    void cancelButtonClicked(MouseEvent event) {
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Cette méthode est exécutée lorsque l'utilisateur confirme le changement de nom de son compteur.
     */
    @FXML
    void modifButtonClicked(MouseEvent event) {
        String name, ean;

        name = nameField.getText();

        if(name.length() == 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("veuillez introduire un nom pour le compteur !");
            alert.showAndWait();
        }
        else {
            ean = GestionPController.getCurrentS().getEAN18();

            try {
                String state = "0";
                if (GestionPController.getCurrentS().getState())
                    state = "1";
                HttpRequest.Post(URLDB.URL + "MeterByEAN/?EAN=" + ean + "&STATE=" + state + "&NAME=" + HttpRequest.refactorString(name) + "&ACTION=modify");
            } catch (IOException e) {
                e.printStackTrace();
            }

            GestionPController.getCurrentS().setName(name);
            GestionPController.updateMeters();
            DashboardMController.updateMeters();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OK");
            alert.setHeaderText(null);
            alert.setContentText("modification effectuée !");
            alert.showAndWait();

            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        }
    }

}
