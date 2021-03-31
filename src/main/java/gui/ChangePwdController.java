package gui;

import gui.app1.GuiHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import user.UserType;

import java.util.Optional;

/**
 * La classe ChangePwdController est une classe utilitaire qui contrôle tous les éléments graphiques
 * de la page de changement de mot de passe.
 */
public class ChangePwdController {

    @FXML
    private TextField newPwdField;

    @FXML
    private TextField confirmField;

    @FXML
    private Button confirmButton;

    @FXML
    void confirmClicked(MouseEvent event) {
        //On change le MDP à partir des informations entrées par l'utilisateur
        String pwd = newPwdField.getText(), conf = confirmField.getText();
        if(pwd.length() <= 6)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("veuillez introduire un mot de passe (MIN 6 caractères) !");
            alert.showAndWait();
            newPwdField.setText("");
        }
        else{
            if(pwd.equals(conf))
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Are your sure to change your password ?");
                alert.setContentText(null);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) // ... user chose OK
                {
                    if (GuiHandler.getGui() != null)
                        GuiHandler.getGui().getCurrentUser().changePassword(pwd, UserType.CONSUMER);
                    else if (gui.app2.GuiHandler.getGui().getCurrentConsumer() != null)
                        gui.app2.GuiHandler.getGui().getCurrentConsumer().changePassword(pwd, UserType.CONSUMER);
                    else
                        gui.app2.GuiHandler.getGui().getCurrentSupplier().changePassword(pwd, UserType.SUPPLIER);
                }

                ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("WRONG INFORMATIONS");
                alert.setHeaderText("PASSWORDS NOT IDENTICALS");
                alert.setContentText(null);
                alert.showAndWait();
                confirmField.setText("");
            }
            if (GuiHandler.getCp() != null) {
                GuiHandler.getCp().setDisable(false);
                GuiHandler.setCp(null);
            }
            else{
                gui.app2.GuiHandler.getCp().setDisable(false);
                gui.app2.GuiHandler.setCp(null);
            }
        }
    }

}
