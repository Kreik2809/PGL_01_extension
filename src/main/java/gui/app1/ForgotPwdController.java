package gui.app1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tool.*;
import tool.sql.UserDB;

import java.io.IOException;
import java.util.ArrayList;

/**
 * La classe ForgotPwdController est une classe utilitaire qui contrôle tous les éléments graphiques
 * de la page d'oubli de mot de passe.
 */
public class ForgotPwdController {

    @FXML
    private TextField emailField;

    @FXML
    private Button confirmButton;

    @FXML
    void confirmClicked(MouseEvent event)
    {
        String email = emailField.getText();
        if(email.contains("@") && email.contains(".") && email.length() >= 5) {
            ArrayList<String> info = new ArrayList<>();
            try {
                info = HttpRequest.Get(URLDB.URL + "User/MAIL=" + email, new ParseJson(UserDB.COLUMNS, "NO USER", "PLEASE USE THIS SYNTAX : /RestAPI/User/MAIL=exemple@adresse.x") {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (info.size() != 0) {
                String password = SignIn.generatePassword();
                try {
                    HttpRequest.Post(URLDB.URL + "User/?MAIL=" + email + "&PWD=" + SignIn.encryption(password) + "&TYPE=" + info.get(2) + "&USERNAME=" + info.get(0) + "&ACTION=changepwd");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Mail.sendMail("NEW PASSWORD FOR YOUR ACCOUNT !", "YOUR NEW PASSWORD : " + password, email);
                GuiHandler.getCp().setDisable(false);
                GuiHandler.setCp(null);
                ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR !");
                alert.setHeaderText(null);
                alert.setContentText("Adresse mail incorrecte !");
                alert.showAndWait();
            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("veuillez entrer une adresse mail correcte (exemple@exemple.com) !");
            alert.showAndWait();
        }
    }

}
