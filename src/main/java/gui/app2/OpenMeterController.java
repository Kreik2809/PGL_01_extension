package gui.app2;

import gui.SupplyPointLine;
import gui.SupplyPointTabCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.MeterType;
import tool.ParseJson;
import tool.URLDB;
import user.Contract;
import user.EnergyConsumer;
import user.EnergyType;
import user.SupplyPoint;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre d'ouverture de compteur.
 */
public class OpenMeterController implements Initializable {

    @FXML
    private TableView<SupplyPointLine> meterTable;

    @FXML
    private TableColumn<SupplyPointLine, String> nameCol;

    @FXML
    private TableColumn<SupplyPointLine, String> eanCol;

    @FXML
    private TableColumn<SupplyPointLine, String> typeCol;

    @FXML
    private TextField clientField;

    @FXML
    private TextField contractID;

    @FXML
    private Text errorText;

    @FXML
    private ChoiceBox<String> contractBox;

    private static ObservableList<SupplyPointLine> meterLine = FXCollections.observableArrayList();

    private SupplyPointLine currentLine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentLine = DashBoardFController.getCurrentLine();
        meterLine.clear();
        meterLine.addAll(DashBoardFController.getClosedMeters());
        errorText.setVisible(false);
        contractBox.getItems().addAll(MeterType.MONO.name(), MeterType.BI.name());
        contractBox.setValue(MeterType.MONO.name());

        nameCol.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("name"));
        eanCol.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("ID"));
        typeCol.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("type"));

        setCellF(nameCol);
        setCellF(eanCol);
        setCellF(typeCol);

        meterTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMeterClicked(meterTable, meterLine);
            }
        });



        meterTable.setItems(meterLine);
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
    void cancelButtonClicked(MouseEvent event) {
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void confirmButtonClicked(MouseEvent event) {
        if(currentLine != null) {
            SupplyPoint s = currentLine.getMeter();
            String client, type, cID;
            client = clientField.getText();
            type = contractBox.getValue();
            cID = contractID.getText();
            if (client.length() == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR !");
                alert.setHeaderText(null);
                alert.setContentText("veuillez entrez un nom d'utilisateur !");
                alert.showAndWait();
            }
            boolean conf = true;
            if (cID.length() == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("CONFIRMATION");
                alert.setHeaderText(null);
                alert.setContentText("ÊTES-VOUS SÛR DE VOULOIR GENERER UN NOUVEAU CONTRACT ?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    conf = true;
                } else {
                    conf = false;
                }
            }
            if (conf) {
                boolean verificationUser = false;
                boolean verificationCid = true;
                String[] tab = {"USERNAME", "PWD", "TYPE", "MAIL"};
                try {
                    verificationUser = (0 != HttpRequest.Get(URLDB.URL + "User/USER=" + client, new ParseJson(tab, "NO USER", "PLEASE USE THIS SYNTAX : /RestAPI/User/MAIL=exemple@adresse.x") {
                    }).size());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] tab4 = {"EAN", "NAME", "ENERGYTYPE", "ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};
                ArrayList<String> l2 = new ArrayList<>();
                if (verificationUser) {
                    if (s != null && client != "" && !cID.equals("") && contractBox.getValue() != null) {
                        try {
                            verificationCid = (0 != HttpRequest.Get(URLDB.URL + "ContractsID/ID=" + cID, new ParseJson(tab4, "NO CONTRACT FOR THIS ID", "PLEASE USE THIS SYNTAX : /RestAPI/ContractsID/ID=\"") {
                            }).size());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (verificationCid)
                            GuiHandler.getGui().getCurrentSupplier().openCounter(s, client, type, cID, false);
                    } else if (s != null && client != "" && cID.equals("") && contractBox.getValue() != null) {
                        GuiHandler.getGui().getCurrentSupplier().openCounter(s, client, type, null, true);
                    }
                    if (verificationCid) {
                        ArrayList<String> arrayList = new ArrayList<>();
                        String[] tab2 = {"EAN", "ENERGYTYPE", "NAME", "STATE", "CID", "DAYSTART", "SID", "TYPE"};
                        try {
                            arrayList = HttpRequest.Get(URLDB.URL + "MeterByEAN/EAN=" + currentLine.getID(), new ParseJson(tab2, "NO METER FOR THIS EAN", "PLEASE USE THIS SYNTAX : /RestAPI/MeterByEAN/EAN=xx") {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Date date = GuiHandler.getDate();
                        GuiHandler.setDate(null);
                        DashBoardFController.getClosedMeters().remove(currentLine);
                        currentLine = new SupplyPointLine(new Contract(GuiHandler.getContractID(), GuiHandler.getGui().getCurrentSupplier(), new EnergyConsumer(client, ""), type, date), new SupplyPoint(arrayList.get(2), EnergyType.valueOf(arrayList.get(1)), arrayList.get(0), arrayList.get(5)));
                        GuiHandler.setContractID(null);
                        DashBoardFController.getOpenedMeters().add(currentLine);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("ERROR");
                        alert.setHeaderText(null);
                        alert.setContentText("ERROR Contract DON'T EXIST !");
                        alert.showAndWait();
                    }
                    GuiHandler.getCp().setDisable(false);
                    GuiHandler.setCp(null);
                    ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("ERROR");
                    alert.setHeaderText(null);
                    alert.setContentText("ERROR USER DON'T EXIST !");
                    alert.showAndWait();
                }
            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("VEUILLEZ SELECTIONNER UN COMPTEUR !");
        }
    }

    public void setCellF(TableColumn t){
        t.setCellFactory(new Callback<TableColumn<SupplyPointLine, String>, TableCell<SupplyPointLine, String>>() {
            @Override
            public TableCell<SupplyPointLine, String> call(TableColumn<SupplyPointLine, String> param) {
                return new SupplyPointTabCell<>();
            }
        });
    }
}
