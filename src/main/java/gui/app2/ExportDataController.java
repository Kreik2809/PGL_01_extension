package gui.app2;

import gui.SupplyPointLine;
import gui.SupplyPointTabCell;
import gui.SupplyPointTabCellBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;
import user.DataSupplyPoint;
import user.Period;
import user.SupplyPoint;
import user.UserType;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre d'exportation de données vers un fichier
 */
public class ExportDataController implements Initializable {
    @FXML
    private ChoiceBox<String> echelles;

    @FXML
    private BorderPane mainPane;

    @FXML
    private DatePicker datePicker1;

    @FXML
    private DatePicker datePicker2;

    @FXML
    private TableView<SupplyPointLine> tableCounter;

    @FXML
    private TableColumn<SupplyPointLine, Boolean> checkCol;

    @FXML
    private TableColumn<SupplyPointLine, String> meterID;

    @FXML
    private TextField fileField;

    @FXML
    private TextField destDir;

    @FXML
    private ChoiceBox<String> fileType;

    private ArrayList<SupplyPoint> selectedSupplyPoint = new ArrayList<>();

    private final String[] FORMAT = {"CSV", "JSON"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        echelles.getItems().addAll("Journalier");

        echelles.setValue("Journalier");

        datePicker1.setEditable(false);
        datePicker2.setEditable(false);

        fileType.getItems().addAll(FORMAT);
        fileType.setValue(FORMAT[0]);

        meterID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        meterID.setCellFactory(new Callback<>() {
            @Override
            public TableCell<SupplyPointLine, String> call(TableColumn<SupplyPointLine, String> param) {
                return new SupplyPointTabCell<>();
            }
        });
        checkCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<SupplyPointLine, Boolean> call(TableColumn<SupplyPointLine, Boolean> param) {
                return new SupplyPointTabCellBox<>();
            }
        });

        if (GuiHandler.getGui().getCurrentUserType() == UserType.CONSUMER)
            tableCounter.setItems(DashBoardCController.getOpenedmetersLine());
        else
            tableCounter.setItems(DashBoardFController.getOpenedMeters());


    }

    @FXML
    void cancelClicked(MouseEvent event) {
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void choiseDirButtonClicked(MouseEvent event) {
        Stage s = new Stage();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(new File("").getAbsolutePath()));

        File selectedDirectory = directoryChooser.showDialog(s);
        if (selectedDirectory != null){
            destDir.setText(selectedDirectory.getAbsolutePath());
        }

    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur confirme l'exportation de données vers un fichier
     */
    @FXML
    void confirmClicked(MouseEvent event) {
        selectedSupplyPoint.clear();
        for (SupplyPointLine line : tableCounter.getItems()) {
            if (line.getSpecialIndicator().equals("checked"))
                selectedSupplyPoint.add(line.getMeter());
        }
        if(fileField.getText().length() == 0)
        {
            fileField.setText("EXPORT_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss_dd-MM-yyyy")));
        }
        if(destDir.getText().length() ==0)
        {
            destDir.setText(new File("").getAbsolutePath());
        }
        String format, directory;
        directory = destDir.getText() + "/" + fileField.getText();
        format = fileType.getValue();

        String[] tab = {"DATE", "VALUE"};
        ArrayList<SupplyPoint> supplyPoints = new ArrayList<>();
        for (int i = 0; i < selectedSupplyPoint.size(); i++) {

            ArrayList<String> res = new ArrayList<>();
            ArrayList<DataSupplyPoint> dataSupplyPoints = new ArrayList<>();
            try {
                res = HttpRequest.Get(URLDB.URL + "MeterData/EAN=" + selectedSupplyPoint.get(i).getEAN18(), new ParseJson(tab, "NO DATA FOR THIS METER", "PLEASE USE THIS SYNTAX : /RestAPI/MeterData/EAN=ean18") {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int k = 0; k < res.size(); k += 2) {
                DataSupplyPoint dataSupplyPoint = new DataSupplyPoint(res.get(k), Double.valueOf(res.get(k + 1)));
                dataSupplyPoints.add(dataSupplyPoint);
            }
            SupplyPoint supplyPoint = selectedSupplyPoint.get(i);
            supplyPoint.setAllDataSupplyPoints(dataSupplyPoints);

            if(GuiHandler.getGui().getCurrentUserType() == UserType.CONSUMER)
            {
                ArrayList<String> res2 = new ArrayList<>();
                try {
                    String[] tab1 = {"EAN", "ENERGYTYPE", "NAME", "STATE", "CID", "DAYSTART", "SID"};
                    res2 = HttpRequest.Get(URLDB.URL + "MeterByEAN/EAN=" + supplyPoint.getEAN18(), new ParseJson(tab1, "NO METER FOR THIS EAN", "PLEASE USE THIS SYNTAX : /RestAPI/MeterByEAN/EAN=xx") {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                supplyPoint.setOpeningDate(Date.valueOf(res2.get(5)).toString());            }
            else {

                supplyPoint.setOpeningDate(null);
            }

            try { //On récupère la date d'auourd'hui
                String[] tab2 = {"DATE"};
                Date date = Date.valueOf(HttpRequest.Get(URLDB.URL + "Date/today", new ParseJson(tab2, "ERROR DATE", "PLEASE USE THIS SYNTAX : /RestAPI/Date/today") {}).get(0));
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                c.add(Calendar.DATE, 1);
                date = new Date(c.getTime().getTime());
                supplyPoint.setClosingDate(date.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            supplyPoints.add(supplyPoint);
        }
        if(supplyPoints.size() > 0)
            export(directory,format, supplyPoints, new Period(Date.valueOf(datePicker1.getValue()), Date.valueOf(datePicker2.getValue())));
        if(selectedSupplyPoint.size() == 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR ! ");
            alert.setHeaderText(null);
            alert.setContentText("VEUILLEZ CHOISIR DES COMPTEURS !");
            alert.showAndWait();
        }
        else {
            GuiHandler.getCp().setDisable(false);
            GuiHandler.setCp(null);
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        }
    }


    private void export(String path, String format, ArrayList<SupplyPoint> liste, Period period)
    {
        if(format.equals(FORMAT[0])) // CSV
        {
            if(GuiHandler.getGui().getCurrentUserType() == UserType.CONSUMER)
                GuiHandler.getGui().getCurrentConsumer().exportDataSupplyPointInCSV(liste, path, period);
            else
                GuiHandler.getGui().getCurrentSupplier().exportDataSupplyPointInCSV(liste, path, period);
        }
        else // XML
        {
            if(GuiHandler.getGui().getCurrentUserType() == UserType.CONSUMER)
                GuiHandler.getGui().getCurrentConsumer().exportDataSupplyPointInJSON(liste, path, period);
            else
                GuiHandler.getGui().getCurrentSupplier().exportDataSupplyPointInJSON(liste, path, period);
        }
    }

}
