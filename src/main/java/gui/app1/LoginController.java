package gui.app1;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import tool.SignIn;
import tool.sql.UserDB;
import user.EnergyConsumer;
import user.UserType;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * La classe LoginController est une classe utilitaire qui contrôle tous les éléments graphiques
 * de la page de connexion.
 */
public class LoginController implements Initializable {
    private boolean isTryToConnect;

    @FXML
    private Text confirmPwdLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    @FXML
    private Text usernameText;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwdField;

    @FXML
    private TextField confirmPasswdField;

    @FXML
    private Button forgetButton;

    @FXML
    private Button continueButton;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        usernameField.setVisible(false);
        usernameText.setVisible(false);
        confirmPwdLabel.setVisible(false);
        confirmPasswdField.setEditable(false);
        confirmPasswdField.setVisible(false);
        isTryToConnect = true;
    }

    /**
     * La méthode ContinueClicked s'effectue lorsque l'utilisateur clique sur le bouton continuer
     * A cette étape, l'application va vérifier que les informations de connexions entrées par l'utilisateur sont correctes
     * et lancer la page suivante
     * @param event
     */
    @FXML
    void continueClicked(MouseEvent event) {
        String username, email, passwd, confirmpasswd;
        email = emailField.getText();
        passwd = passwdField.getText();
        if(passwd.length() <= 6) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("veuillez introduire un mot de passe (MIN 6 caractères) !");
            alert.showAndWait();
            passwdField.setText("");
        }
        else if(email.contains("@") && email.contains(".") && email.length() >= 5) {
            if (isTryToConnect) // si user se connecte
            {
                if (SignIn.log(email, passwd)) {
                    username = SignIn.getInfo().get(0);
                    GuiHandler.getGui().setCurrentUser(new EnergyConsumer(username, passwd, email));
                    if (SignIn.getInfo().get(2).equals("CONSUMER"))
                        GuiHandler.loadScene("app1/dashboard_portefeuille.fxml", 800, 550, this);
                    else if (SignIn.getInfo().get(2).equals("AUTHORITY"))
                        GuiHandler.loadScene("app1/dashboard_authority.fxml", 1100, 650, this);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("IMPOSSIBLE TO CONNECT");
                    alert.setHeaderText("Please verify your informations");
                    alert.setContentText(null);
                    alert.showAndWait();
                }
            } else // si user s'inscrit
            {
                confirmpasswd = confirmPasswdField.getText();
                username = usernameField.getText();
                if (username.length() == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR !");
                    alert.setHeaderText(null);
                    alert.setContentText("veuillez introduire un nom d'utilisateur !");
                    alert.showAndWait();
                } else if (confirmpasswd.equals(passwd)) {
                    if (SignIn.signUp(email, username, passwd, UserType.CONSUMER)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("CONGRALUATION");
                        alert.setHeaderText("PLEASE CONNECT !");
                        alert.setContentText(null);
                        alert.showAndWait();
                        loginClicked(event);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("WRONG INFORMATIONS");
                        alert.setHeaderText("THE MAIL OR THE USERNAME ARE ALREADY EXIST, PLEASE CHANGE OR RETRY!");
                        alert.setContentText(null);
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("WRONG INFORMATIONS");
                    alert.setHeaderText("PASSWORDS NOT IDENTICALS");
                    alert.setContentText(null);
                    alert.showAndWait();
                }
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

    /**
     * Cette méthode s'effectue lorsque l'utilisateur clique sur le bouton "Mot de passe oublié".
     * @param event
     */
    @FXML
    void forgotClicked(MouseEvent event) {
        GuiHandler.setCp(((Button)event.getSource()).getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/forgot_passwd.fxml", "mot de passe oublié", 300, 150, this);    }

    /**
     * Cette méthode s'effectue lorsque l'utilisateur clique sur le bouton "Se connecter"
     * Elle modifie l'interface afin de n'afficher que les éléments utiles à la connexion.
     * @param event
     */
    @FXML
    void loginClicked(MouseEvent event) {
        usernameField.setVisible(false);
        usernameText.setVisible(false);
        confirmPwdLabel.setVisible(false);
        confirmPasswdField.setEditable(false);
        confirmPasswdField.setVisible(false);
        isTryToConnect = true;
    }

    /**
     * Cette méthode s'effectue lorsque l'utilisateur clique sur le bouton "S'inscrire"
     * Elle modifie l'interface afin de n'afficher que les éléments utiles à l'inscription.
     * @param event
     */
    @FXML
    void signUpClicked(MouseEvent event) {
        usernameField.setVisible(true);
        usernameText.setVisible(true);
        confirmPwdLabel.setVisible(true);
        confirmPasswdField.setEditable(true);
        confirmPasswdField.setVisible(true);
        isTryToConnect = false;
    }



}
