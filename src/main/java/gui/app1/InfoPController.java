package gui.app1;

import gui.CustomListCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;
import user.EnergyConsumer;
import user.EnergyPortfolio;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre contenant les informations concernant les membres d'un portefeuille
 */
public class InfoPController implements Initializable {

    @FXML
    private Text nbMeterText;

    @FXML
    private ListView<EnergyConsumer> users;

    private static ObservableList<EnergyConsumer> observableUsers = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb){
        nbMeterText.setText("Nombre de compteurs : " + GuiHandler.getGui().getCurrentPortfolio().getNbMeters());
        EnergyPortfolio e = GuiHandler.getGui().getCurrentPortfolio();
        e.fetchAccesLevels();
        ArrayList<EnergyConsumer> l = e.getEnergyConsumer();
        observableUsers.clear();
        showUser(l);
    }

    /**
     * Cette méthode personnalise et affiche les utilisateurs dans la listView de la fenêtre.
     * @param list La liste des utilisateurs à afficher
     */
    public void showUser(List<EnergyConsumer> list){
        users.setCellFactory(new Callback<ListView<EnergyConsumer>, ListCell<EnergyConsumer>>() {
            @Override
            public ListCell<EnergyConsumer> call(ListView<EnergyConsumer> param) {
                return new CustomListCell<>(GuiHandler.getGui().getCurrentUser());
            }
        });
        observableUsers.addAll(list);
        users.setItems(observableUsers);
    }
}
