package gui.app2;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import tool.SignIn;
import user.EnergyConsumer;
import user.EnergySupplier;
import user.UserType;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Le Controller de la fenêtre de connexion.
 */
public class LoginController implements Initializable {

    @FXML
    private TextField emailField;

    @FXML
    private TextField pwdField;

    @FXML
    private Button continueButton;

    @FXML
    private Text errorText;

    @FXML
    private Button forgotButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorText.setVisible(false);
    }

    @FXML
    void continueButtonClicked(MouseEvent event) {
        String mail = emailField.getText();
        String pwd = pwdField.getText();

        if(SignIn.log(mail, pwd))
        {
            UserType userType = UserType.valueOf(SignIn.getInfo().get(2));
            GuiHandler.getGui().setCurrentUserType(userType);
            if (userType == UserType.CONSUMER){ //On charge l'application pour un consommateur
                GuiHandler.getGui().setCurrentConsumer(new EnergyConsumer(SignIn.getInfo().get(0), "", mail));
                GuiHandler.loadScene("app2/consommateur_dashboard.fxml", 800, 500, this);
            }
            else if(userType == UserType.SUPPLIER){ //On charge l'application pour un fournisseur
                GuiHandler.getGui().setCurrentSupplier(new EnergySupplier(SignIn.getInfo().get(0), "", mail));
                GuiHandler.loadScene("app2/fournisseur_dashboard.fxml", 800, 500, this);
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("IMPOSSIBLE TO CONNECT");
            alert.setHeaderText("Please verify your informations");
            alert.setContentText(null);
            alert.showAndWait();
        }

    }

    @FXML
    void forgotButtonClicked(MouseEvent event) {
        GuiHandler.setCp(((Button)event.getSource()).getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/forgot_passwd.fxml", "mot de passe oublie", 300, 150, this);
    }
}
