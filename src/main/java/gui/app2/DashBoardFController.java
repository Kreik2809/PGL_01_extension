package gui.app2;

import gui.SupplyPointLine;
import gui.SupplyPointTabCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;
import user.Contract;
import user.EnergyConsumer;
import user.EnergyType;
import user.SupplyPoint;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Cette classe est le controlleur de la fenêtre de gestion du fournisseur de l'application 2.
 */
public class DashBoardFController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TextField researchField;

    @FXML
    private TableView<SupplyPointLine> tableOpenedCounter;

    @FXML
    private TableColumn<SupplyPointLine, String> openName;

    @FXML
    private TableColumn<SupplyPointLine, String> openEan;

    @FXML
    private TableColumn<SupplyPointLine, String> openDate;

    @FXML
    private TableColumn<SupplyPointLine, String> openClient;

    @FXML
    private TableColumn<SupplyPointLine, String> openType;

    @FXML
    private TableColumn<SupplyPointLine, String> openContract;

    @FXML
    private TableView<SupplyPointLine> tableClosedCounter;

    @FXML
    private TableColumn<SupplyPointLine, String> closeName;

    @FXML
    private TableColumn<SupplyPointLine, String> closeEan;

    @FXML
    private TableColumn<SupplyPointLine, String> closeType;

    private static ObservableList<SupplyPointLine> openedMeters = FXCollections.observableArrayList();

    private static ObservableList<SupplyPointLine> closedMeters = FXCollections.observableArrayList();

    private static SupplyPointLine currentLine;

    private SupplyPoint currentOpenS;

    private SupplyPoint currentCloseS;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //On récupère les compteurs du fournisseur

        String[] tab = {"EAN", "NAME","ENERGYTYPE" , "ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};
        ArrayList<String> res = new ArrayList<>();
        try {
            res = HttpRequest.Get(URLDB.URL + "ContractsSupplier/ID=" + GuiHandler.getGui().getCurrentSupplier().getName(), new ParseJson(tab, "NO CONTRACT FOR THIS ID", "PLEASE USE THIS SYNTAX : /RestAPI/ContractsSupplier/ID==xx") {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> l2 = new ArrayList<>();
        String[] tab3 = {"EAN", "ENERGYTYPE", "NAME", "STATE", "CID", "DAYSTART", "SID", "TYPE"};
        String dayStart="";
        ArrayList<SupplyPointLine> supplyPointsOpened = new ArrayList<>();
        for(int i = 0 ; i < res.size() ; i+=8)
        {
            try{
                dayStart = HttpRequest.Get(URLDB.URL + "MeterByEAN/EAN=" + res.get(i), new ParseJson(tab3, "NO METER FOR THIS EAN", "PLEASE USE THIS SYNTAX : /RestAPI/MeterByEAN/EAN=xx") {}).get(5);
            }catch (IOException e){
                e.printStackTrace();
            }
            SupplyPoint supplyPoint = new SupplyPoint(res.get(i+1), EnergyType.valueOf(res.get(i+2)), res.get(i), dayStart);
            Contract contract = new Contract(res.get(i+3),GuiHandler.getGui().getCurrentSupplier(),new EnergyConsumer(res.get(i+5), ""),null,res.get(i+6) );
            supplyPoint.setContract(contract);
            supplyPointsOpened.add(new SupplyPointLine(contract,supplyPoint));
        }

        //On récupère l'ensemble des compteurs disponibles
        ArrayList<SupplyPointLine> supplyPointsClosed = new ArrayList<>();
        String[] tab2 = {"EAN18", "ENERGYTYPE", "NAME", "STATE", "CONTRACTID", "DAYSTART", "SUPPLIERID"};
        res = new ArrayList<>();
        try {
            res = HttpRequest.Get(URLDB.URL + "AllMeters/", new ParseJson(tab2, "NO METERS", "PLEASE USE THIS SYNTAX : /RestAPI/AllMeters/") {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0 ; i < res.size() ; i+=7)
        {
            if(res.get(i+3).equals("0"))
                supplyPointsClosed.add(new SupplyPointLine(new SupplyPoint(res.get(i+2), EnergyType.valueOf(res.get(i+1)), res.get(i), res.get(i+5))));
        }


        //On remplit les listes avec les compteurs obtenus
        for(SupplyPointLine supplyPointLine : supplyPointsOpened)
        {
            openedMeters.add(supplyPointLine);
        }
        for(SupplyPointLine supplyPointLine : supplyPointsClosed)
        {
            closedMeters.add(supplyPointLine);
        }

        tableClosedCounter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMeterClicked(tableClosedCounter, closedMeters);
                if(currentLine != null) {
                    currentCloseS = currentLine.getMeter();
                }
            }
        });
        tableOpenedCounter.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMeterClicked(tableOpenedCounter, openedMeters);
                if(currentLine != null) {
                    currentOpenS = currentLine.getMeter();
                    GuiHandler.getGui().setCurrentSupplyPoint(currentOpenS);
                }
            }
        });
        tableOpenedCounter.setItems(openedMeters);
        tableClosedCounter.setItems(closedMeters);
        openName.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("name"));
        openEan.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("ID"));
        openDate.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("openingDate"));
        openClient.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("clientName"));
        openType.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("type"));
        openContract.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("contractNumber"));
        closeName.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("name"));
        closeEan.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("ID"));
        closeType.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("type"));
        setCellF(openName);
        setCellF(openEan);
        setCellF(openDate);
        setCellF(openClient);
        setCellF(openType);
        setCellF(openContract);
        setCellF(closeName);
        setCellF(closeEan);
        setCellF(closeType);
    }


    public void handleMeterClicked(TableView<SupplyPointLine> t, ObservableList<SupplyPointLine> l){
        if ((currentLine = t.getSelectionModel().getSelectedItem()) != null) {
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

    @FXML
    void addDataClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app2/data_importation.fxml", "Importation de donnees", 350, 180, this);
    }

    @FXML
    void changePwdClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/change_passwd.fxml", "Modifier mot de passe", 300, 200, this);
    }

    @FXML
    void chooseEnglish(ActionEvent event) {

    }

    @FXML
    void chooseFrench(ActionEvent event) {

    }

    @FXML
    void closeMeterClicked(MouseEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app2/counter_close.fxml", "Fermeture d'un compteur", 350, 400, this);
    }

    @FXML
    void delDataClicked(ActionEvent event) {
        if (currentOpenS != null) {
            GuiHandler.setCp(mainPane.getScene().getRoot());
            GuiHandler.getCp().setDisable(true);
            GuiHandler.createScene("app2/data_suppression.fxml", "Suppression de donnees", 600, 300, this);
        }
    }


    @FXML
    void getDataClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app2/data_export.fxml", "Exportation de donnees", 600, 550, this);
    }

    @FXML
    void modifDataClicked(ActionEvent event) {
        if (currentOpenS != null){
            GuiHandler.setCp(mainPane.getScene().getRoot());
            GuiHandler.getCp().setDisable(true);
            GuiHandler.createScene("app2/data_writing.fxml", "Modification/Saisie de donnees", 500, 450, this);
        }
    }

    @FXML
    void openMeterClicked(MouseEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app2/counter_open.fxml", "Ouverture d'un compteur", 450, 550, this);
    }

    @FXML
    void researchClicked(MouseEvent event) {
        String eanCode = researchField.getText();
        researchEan(openedMeters, eanCode);
        researchEan(closedMeters, eanCode);
    }

    /**
     * Cette méthode parcourt une liste de SupplyPointLine et déplace en première place le compteur recherché si elle le trouve.
     */
    public void researchEan(ObservableList<SupplyPointLine> supplyPoints, String ean){
        ArrayList<SupplyPointLine> supplyPointLines = new ArrayList<>(supplyPoints);
        for (SupplyPointLine s : supplyPointLines){
            if (s.getMeter().getEAN18().contains(ean)){
                supplyPoints.remove(s);
                supplyPoints.add(0, s);
            }
        }
    }

    public static ObservableList<SupplyPointLine> getOpenedMeters() {
        return openedMeters;
    }

    public static ObservableList<SupplyPointLine> getClosedMeters() {
        return closedMeters;
    }

    public void setCellF(TableColumn t){
        t.setCellFactory(new Callback<TableColumn<SupplyPointLine, String>, TableCell<SupplyPointLine, String>>() {
            @Override
            public TableCell<SupplyPointLine, String> call(TableColumn<SupplyPointLine, String> param) {
                return new SupplyPointTabCell<>();
            }
        });
    }

    public static SupplyPointLine getCurrentLine() {
        return currentLine;
    }
}
