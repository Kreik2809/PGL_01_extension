package gui.app2;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tool.HttpRequest;
import tool.URLDB;
import user.DataSupplyPoint;
import user.SupplyPoint;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre d'importation de données depuis un fichier.
 */
public class ImportDataController implements Initializable {

    @FXML
    private TextField fileField;

    @FXML
    private Text errorText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorText.setVisible(false);
    }

    @FXML
    void cancelClicked(MouseEvent event) {
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    /**
     * Cette méthode est appelée lorsqu'un utilisateur confirme l'importation de données depuis un fichier
     */
    @FXML
    void confirmClicked(MouseEvent event) {
        String file = fileField.getText();
        if (file.length() == 0){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("VEUILLEZ CHOISIR UN FICHIER !");
            alert.showAndWait();
        }
        else {
            ArrayList<SupplyPoint> arrayList = new ArrayList<>();
            if (file.contains("JSON") || file.contains("json")) {
                String substring = file.substring(0, file.length() - 5);
                arrayList = GuiHandler.getGui().getCurrentSupplier().importDataSupplyPointInJSON(substring);
            } else if (file.contains("CSV") || file.contains("csv"))
                arrayList = GuiHandler.getGui().getCurrentSupplier().importDataSupplyPointInCSV(file.substring(0, file.length() - 4));
            for (SupplyPoint supplyPoint : arrayList) {
                for (DataSupplyPoint dataSupplyPoint : supplyPoint.getAllDataSupplyPoints()) {
                    try {
                        String request = URLDB.URL + "MeterData/?EAN=" + supplyPoint.getEAN18() + "&DATE=" + dataSupplyPoint.getDate() + "&VALUE=" + dataSupplyPoint.getValue() + "&ACTION=add";
                        HttpRequest.Post(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            GuiHandler.getCp().setDisable(false);
            GuiHandler.setCp(null);
            ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
        }
    }

    @FXML
    void openFileClicked(MouseEvent event) {
        Stage s= new Stage();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(new File("").getAbsolutePath()));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.CSV ; *.csv)", "*.CSV", "*.csv"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.JSON ; *.json)", "*.JSON", "*.json"));
        File file = fileChooser.showOpenDialog(s);
        if (file != null){
            fileField.setText(file.getAbsolutePath());
        }
    }
}
