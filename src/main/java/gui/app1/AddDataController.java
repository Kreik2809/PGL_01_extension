package gui.app1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Le controller de la fenêtre de confirmation d'ajout de données
 * Cette fenêtre a pour but de lancer l'application 2
 */
public class AddDataController{

    @FXML
    private Button continueButton;

    @FXML
    private Button cancelButton;

    @FXML
    void cancelButtonClicked(MouseEvent event) {
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void continueButtonClicked(MouseEvent event) {
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        GuiHandler.getGui().getPrimaryStage().close();
        gui.app2.Gui guiApp2 = new gui.app2.Gui();
        guiApp2.start(GuiHandler.getGui().getPrimaryStage());
    }

}
