package gui.app1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import user.EnergyPortfolio;

/**
 * La classe CreatePController est une classe utilitaire qui contrôle tous les éléments graphiques
 * de la page de création de portefeuille.
 */
public class CreatePController {

    @FXML
    private TextField portfolioField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    @FXML
    void cancelClicked(MouseEvent event) {
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void confirmClicked(MouseEvent event) {
        String name = portfolioField.getText();
        if(name.length() != 0)
        {
            GuiHandler.getCp().setDisable(false);
            GuiHandler.setCp(null);
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
            GuiHandler.createPortfolio(name);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("veuillez entrer un nom !");
            alert.showAndWait();
        }
    }

}
