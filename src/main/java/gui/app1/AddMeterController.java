package gui.app1;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tool.HttpRequest;
import tool.URLDB;
import user.EnergyType;
import user.ProductionType;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre d'ajout de compteurs à un portefeuille
 */
public class AddMeterController implements Initializable {


    @FXML
    private TextField nameField;

    @FXML
    private TextField eanField;

    @FXML
    private Button addButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField powerField;

    @FXML
    private TextField areaField;

    @FXML
    private TextField numberField;

    @FXML
    private ChoiceBox<String> energyTypeBox;

    @FXML
    private  ChoiceBox<String> supplyPointTypeBox;

    private static String ean;

    public static final String TYPE[] = {EnergyType.ELECTRICITY.name(), EnergyType.GAS.name(), EnergyType.WATER.name()};

    @Override
    public void initialize(URL url, ResourceBundle rb){

        energyTypeBox.getItems().addAll(TYPE);
        energyTypeBox.setValue(TYPE[0]);

        supplyPointTypeBox.getItems().addAll("Compteur de consommation", "Panneaux photovoltaiques", "Eoliene");
        supplyPointTypeBox.setValue(supplyPointTypeBox.getItems().get(0));

        powerField.setDisable(true);
        areaField.setDisable(true);
        numberField.setDisable(true);

        supplyPointTypeBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (supplyPointTypeBox.getItems().get((Integer) newValue).equals("Panneaux photovoltaiques")){
                    powerField.setDisable(false);
                    areaField.setDisable(false);
                    numberField.setDisable(false);
                }
                else{
                    powerField.setDisable(true);
                    areaField.setDisable(true);
                    numberField.setDisable(true);
                }
            }
        });
    }

    /**
     * Cette méthode est appelée lorsque le client désire ajouter un compteur à son portefeuille.
     */
    @FXML
    void addButtonClicked(MouseEvent event) {
        String name, etype, stype, power, area, number;
        name = nameField.getText();
        ean = eanField.getText();
        power = powerField.getText();
        area = areaField.getText();
        number = numberField.getText();
        etype = energyTypeBox.getValue();
        stype = supplyPointTypeBox.getValue();

        if(name.length() == 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("veuillez introduire un nom pour le compteur !");
            alert.showAndWait();
        }
        else if(ean.length() != 18)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR !");
            alert.setHeaderText(null);
            alert.setContentText("veuillez introduire un code EAN correcte !");
            alert.showAndWait();
            eanField.setText("");
        }
        else {
            if (stype.equals("Panneaux photovoltaiques") || stype.equals("Eoliene")){
                if (stype.equals("Panneaux photovoltaiques")) {
                    ProductionType f = ProductionType.SOLAR;
                    try{
                        HttpRequest.Post(URLDB.URL+"Productions/?EAN="+ean+"&TYPE="+f.name()+"&PUISSANCE="+power+"&SUPERFICIE="+
                                area+"&NOMBRE="+number+"&ACTION=add");
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else {
                    ProductionType f = ProductionType.WIND;
                    try{
                        HttpRequest.Post(URLDB.URL+"Productions/?EAN="+ean+"&TYPE="+f.name()+"&ACTION=add");
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                stype = "PRODUCTION";
            }
            else
                stype = "CONSOMMATION";
            GuiHandler.addSupplyPoint(name, ean, etype, stype); //On gère l'ajout du portefeuille
            GuiHandler.getSecondCp().setDisable(false);
            GuiHandler.setSecondCp(null);
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        }
    }

    @FXML
    void cancelButtonClicked(MouseEvent event) {
        GuiHandler.getSecondCp().setDisable(false);
        GuiHandler.setSecondCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    public static String getEan() {
        return ean;
    }
}
