package gui.app2;

import gui.DataSupplyPointTabCell;
import gui.SupplyPointLine;
import gui.SupplyPointTabCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import user.DataSupplyPoint;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Ce controller gère la fenêtre de saisie de données du consommateur ainsi que la fenêtre de modification de données du fournisseur.
 */
public class WriteDataController implements Initializable {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TableView<DataSupplyPoint> tableData;

    @FXML
    private TableColumn<DataSupplyPoint, String> date;

    @FXML
    private TableColumn<DataSupplyPoint, Double> conso;

    @FXML
    private Text errorText;

    @FXML
    private TextField valueField;

    @FXML
    private Button confBtn;

    private ObservableList<DataSupplyPoint> datalist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        confBtn.setDisable(true);

        datePicker.setEditable(false);

        date.setCellValueFactory(new PropertyValueFactory<DataSupplyPoint, String>("date"));
        conso.setCellValueFactory(new PropertyValueFactory<DataSupplyPoint, Double>("value"));

        setCellF(date);
        setCellF(conso);


        errorText.setVisible(false);

        tableData.setItems(datalist);
    }

    @FXML
    void addButtonClicked(MouseEvent event) {
        double value;

        try {
            value = Double.parseDouble(valueField.getText());
            if (datePicker.getValue() != null) {
                DataSupplyPoint dt = new DataSupplyPoint(Date.valueOf(datePicker.getValue()).toString(), value, GuiHandler.getGui().getCurrentSupplyPoint());
                datalist.add(dt);
                confBtn.setDisable(false);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR !");
                alert.setHeaderText(null);
                alert.setContentText("veuillez choisir une date !");
                alert.showAndWait();
            }
        }catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("mauvais entrée de valeur (valeur exemple : x.y) !");
            alert.showAndWait();
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
        ArrayList<DataSupplyPoint> l = new ArrayList<>();
        l.addAll(datalist);
        datalist.clear();
        if (l.size() != 0) {
            GuiHandler.getGui().getCurrentSupplyPoint().addDataSupplyPoints(l);
            GuiHandler.getCp().setDisable(false);
            GuiHandler.setCp(null);
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        }
    }

    /**
     * On paramètre le cellFactory d'une colonne t
     */
    public void setCellF(TableColumn t){
        t.setCellFactory(new Callback<TableColumn<DataSupplyPoint, String>, TableCell<DataSupplyPoint, String>>() {
            @Override
            public TableCell<DataSupplyPoint, String> call(TableColumn<DataSupplyPoint, String> param) {
                return new DataSupplyPointTabCell<>();
            }
        });
    }
}
