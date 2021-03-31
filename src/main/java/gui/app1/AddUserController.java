package gui.app1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tool.AccessLevelType;
import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;
import tool.sql.NotificationDB;
import tool.sql.UserDB;
import user.EnergyConsumer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre d'ajout d'un utilisateur à un portefeuille
 */
public class AddUserController implements Initializable {


    @FXML
    private TextField nameField;

    @FXML
    private Text errorText;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ChoiceBox<String> access;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        nameField.setPromptText("Veuillez entrer l'adresse mail de l'utilisateur");
        errorText.setVisible(false);
        access.getItems().addAll("Lecture", "Lecture/Ecriture");
        access.setValue("Lecture");
    }

    /**
     * Cette méthode est appelée lorsqu'un utilisateur désire ajouter un utilisateur à son portefeuille
     */
    @FXML
    void addButtonClicked(MouseEvent event) {
        String userName = nameField.getText();
        if(userName.length() == 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("veuillez introduire un nom d'utilisateur !");
            alert.showAndWait();
        }
        else{
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
            ArrayList<String> userInfo = new ArrayList<>();
            try{
                userInfo = HttpRequest.Get(URLDB.URL + "User/MAIL=" +userName, new ParseJson(UserDB.COLUMNS, "NO USER", "PLEASE USE THIS SYNTAX : /RestAPI/User/MAIL=exemple@adresse.x"){});
            } catch (IOException ex){
                ex.printStackTrace();
            }

            if(userInfo.size()!=0 && !userInfo.get(0).equals("NO USER")) // si le user existe
            {
                ArrayList<String> notifications = new ArrayList<>();
                String[] tab = {"USERNAME", "ACCESS", "PORTFOLIO_ID", "PNAME", "MANAGER"};
                try{
                    notifications = HttpRequest.Get(URLDB.URL + "Notifications/USERNAME=" + userInfo.get(0), new ParseJson(tab, "NO NOTIFICATIONS FOR THIS USER", "PLEASE USE THIS SYNTAX : /RestAPI/Notifications/USERNAME=username") {});
                }catch (IOException exc){
                    exc.printStackTrace();
                }
                boolean alreadyInvited = false;
                for (int i=0; i<notifications.size()-1; i+=5) { //Il y a toujours un élément dans notifications
                    if (notifications.get(i+2).equals(GuiHandler.getGui().getCurrentPortfolio().getID()))
                        alreadyInvited = true;
                }
                if (!alreadyInvited) {

                    try{
                        HttpRequest.Post(URLDB.URL + "Notifications/?USERNAME="+userInfo.get(0)+"&PORTFOLIO_ID="+GuiHandler.getGui().getCurrentPortfolio().getID()+"&ACCESS="+alt.toString()+"&ACTION=add");
                    } catch (IOException exception){
                        exception.printStackTrace();
                    }
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("OK");
                    alert.setHeaderText(null);
                    alert.setContentText("invitation envoyée !");
                    alert.showAndWait();

                }

                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR !");
                    alert.setHeaderText(null);
                    alert.setContentText("Vous avez déjà envoyé une demande à l'utilisateur");
                    alert.showAndWait();
                }
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR !");
                alert.setHeaderText(null);
                alert.setContentText("l'utilisateur n'existe pas");
                alert.showAndWait();
            }
            GuiHandler.getSecondCp().setDisable(false);
            GuiHandler.setSecondCp(null);
            ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        }
    }

    @FXML
    void cancelButtonClicked(MouseEvent event) {
        GuiHandler.getSecondCp().setDisable(false);
        GuiHandler.setSecondCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }
}
