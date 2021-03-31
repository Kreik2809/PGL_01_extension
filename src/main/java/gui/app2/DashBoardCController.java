package gui.app2;

import gui.SupplyPointLine;
import gui.SupplyPointTabCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.util.Callback;
import user.EnergyType;
import user.SupplyPoint;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Cette classe est le contrôleur de la fenêtre de gestion du consommateur de l'application 2
 */
public class DashBoardCController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableView<SupplyPointLine> tableOpenedCounter;

    @FXML
    private TableColumn<SupplyPointLine, String> openName;

    @FXML
    private TableColumn<SupplyPointLine, String> codeEan;

    @FXML
    private TableColumn<SupplyPointLine, String> openedDate;

    @FXML
    private TableView<SupplyPointLine> tableClosedCounter;

    @FXML
    private TableColumn<SupplyPointLine, String> closedName;

    @FXML
    private TableColumn<SupplyPointLine, String> closedEan;

    @FXML
    private TableColumn<SupplyPointLine, String> closedStartCounterDate;

    @FXML
    private TableColumn<SupplyPointLine, String> closedEndCounterDate;

    private static ObservableList<SupplyPointLine> openedmetersLine = FXCollections.observableArrayList();

    private static ObservableList<SupplyPointLine> closedmetersLine = FXCollections.observableArrayList();

    private SupplyPointLine currentS;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // On personnalise l'affichage dans les deux tableaux de la page.
        openName.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("name"));
        codeEan.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("ID"));
        openedDate.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("openingDate"));

        closedName.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("Name"));
        closedEan.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("ID"));
        closedStartCounterDate.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("openingDate"));
        closedEndCounterDate.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("closingDate"));

        setCellF(openName);
        setCellF(codeEan);
        setCellF(openedDate);

        setCellF(closedName);
        setCellF(closedEan);
        setCellF(closedStartCounterDate);
        setCellF(closedEndCounterDate);

        //
        tableOpenedCounter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMeterClicked(tableOpenedCounter, openedmetersLine);
                if (currentS != null){
                    GuiHandler.getGui().setCurrentSupplyPoint(currentS.getMeter());
                }
            }
        });

        //On récupère les données  à afficher
        ArrayList<SupplyPoint> l1 = GuiHandler.getGui().getCurrentConsumer().fetchSupplyPoints();
        ArrayList<SupplyPoint> l2 = GuiHandler.getGui().getCurrentConsumer().fetchHistoricSupplyPoint();
        ArrayList<SupplyPointLine> openedLine = new ArrayList<>();
        ArrayList<SupplyPointLine> closedLine = new ArrayList<>();

        for(int i = 0; i < l1.size() ; i++)
        {
            if (l1.get(i).getState())
                openedLine.add(new SupplyPointLine(l1.get(i)));
        }

        for(int j=0; j<l2.size(); j++){
            closedLine.add(new SupplyPointLine(l2.get(j)));
        }

        showMeters(openedLine, closedLine);
    }

    @FXML
    void addDataClicked(ActionEvent event) {
        if (currentS != null){
            GuiHandler.setCp(mainPane.getScene().getRoot());
            GuiHandler.getCp().setDisable(true);
            GuiHandler.createScene("app2/data_writing.fxml", "Saisie de donnees", 500, 450, this);        }
    }

    @FXML
    void changePwdClicked(ActionEvent event) {
        GuiHandler.createScene("app1/change_passwd.fxml", "Modifier mot de passe", 300, 200, this);    }

    @FXML
    void chooseEnglish(ActionEvent event) {

    }

    @FXML
    void chooseFrench(ActionEvent event) {

    }

    @FXML
    void getDataClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app2/data_export.fxml", "Saisie de donnees", 600, 550, this);    }

    public void handleMeterClicked(TableView<SupplyPointLine> t, ObservableList<SupplyPointLine> l){
        if ((currentS = t.getSelectionModel().getSelectedItem()) != null) {
            int i = t.getSelectionModel().getSelectedIndex();
            for (SupplyPointLine sp : t.getItems()) {
                sp.setSpecialIndicator("");
            }
            t.getItems().get(i).setSpecialIndicator("selected");
            ObservableList<SupplyPointLine> copy = FXCollections.observableArrayList();
            copy.addAll(l);
            l.removeAll(l);
            l.addAll(copy);
        }
    }

    public void showMeters(ArrayList<SupplyPointLine> openedMeters, ArrayList<SupplyPointLine> closedMeters){
        openedmetersLine.clear();
        closedmetersLine.clear();
        openedmetersLine.addAll(openedMeters);
        closedmetersLine.addAll(closedMeters);
        tableOpenedCounter.setItems(openedmetersLine);
        tableClosedCounter.setItems(closedmetersLine);
    }

    /**
     * On paramètre le cellFactory d'une colonne t
     */
    public void setCellF(TableColumn t){
        t.setCellFactory(new Callback<TableColumn<SupplyPointLine, String>, TableCell<SupplyPointLine, String>>() {
            @Override
            public TableCell<SupplyPointLine, String> call(TableColumn<SupplyPointLine, String> param) {
                return new SupplyPointTabCell<>();
            }
        });
    }

    public static ObservableList<SupplyPointLine> getOpenedmetersLine() {
        return openedmetersLine;
    }

    public static ObservableList<SupplyPointLine> getClosedmetersLine() {
        return closedmetersLine;
    }
}
