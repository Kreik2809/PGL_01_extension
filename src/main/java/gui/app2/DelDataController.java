package gui.app2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;
import user.DataSupplyPoint;
import user.Period;
import user.SupplyPoint;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre de suppression de données.
 */
public class DelDataController implements Initializable {


    @FXML
    private CheckBox supprCheck;

    @FXML
    private DatePicker startDataPicker;
    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Text errorText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        errorText.setVisible(false);
        startDataPicker.setEditable(false);
        endDatePicker.setEditable(false);
        supprCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(supprCheck.isSelected())
                {
                    startDataPicker.setDisable(true);
                    endDatePicker.setDisable(true);
                }
                else
                {
                    startDataPicker.setDisable(false);
                    endDatePicker.setDisable(false);
                }
            }
        });
    }

    @FXML
    void cancelClicked(MouseEvent event) {
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void confirmClicked(MouseEvent event) {
        boolean end = false;
        if (supprCheck.isSelected())
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("êtes-vous sur ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                deleteData(null);
                end = true;
            }
        } else {
            if(startDataPicker.getValue() == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("VEUILLEZ SELECTIONNER UNE DATE DE DEBUT !");
                alert.showAndWait();
            }
            else if(endDatePicker.getValue() == null)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText(null);
                alert.setContentText("VEUILLEZ SELECTIONNER UNE DATE DE FIN !");
                alert.showAndWait();
            }
            else {
                Date start = Date.valueOf(startDataPicker.getValue());
                Date ending = Date.valueOf(endDatePicker.getValue());
                deleteData(new Period(start, ending));
                end = true;
            }
        }
        if (end){
            GuiHandler.getCp().setDisable(false);
            GuiHandler.setCp(null);
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        }
    }

    /**
     * Cette méthode supprime les données d'un compteur en fonction d'une période
     */
    private void deleteData(Period period)
    {
        SupplyPoint supplyPoint = GuiHandler.getGui().getCurrentSupplyPoint();
        String[] tab = {"DATE", "VALUE"};
        ArrayList<String> liste = new ArrayList<>();
        supplyPoint.fetchContract();
        if (supplyPoint.getContract()!=null){
            try{
                HttpRequest.Post(URLDB.URL + "DelNotif/?USERNAME="+supplyPoint.getContract().getEnergyConsumer().getName()+"&EAN="+supplyPoint.getEAN18()+"&ACTION=add");
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        try{
            liste = HttpRequest.Get(URLDB.URL+"MeterData/EAN="+supplyPoint.getEAN18(), new ParseJson(tab, "NO DATA FOR THIS METER", "PLEASE USE THIS SYNTAX : /RestAPI/MeterData/EAN=ean18") {});

        }catch (IOException e){
            e.printStackTrace();
        }
        if (liste.size() != 0) {
            for (int i = 0; i < liste.size(); i += 2) {
                supplyPoint.getAllDataSupplyPoints().add(new DataSupplyPoint(liste.get(i), Double.parseDouble(liste.get(i+1))));
            }
        }
        for(DataSupplyPoint dataSupplyPoint : supplyPoint.getAllDataSupplyPoints()) {
            if (period == null) {
                try {
                    HttpRequest.Post(URLDB.URL + "MeterData/?EAN=" + supplyPoint.getEAN18() + "&DATE=" + dataSupplyPoint.getDate() + "&ACTION=delete");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if ((Date.valueOf(dataSupplyPoint.getDate()).compareTo(period.getStartDate()) >= 0) && ((Date.valueOf(dataSupplyPoint.getDate()).compareTo(period.getEndDate()) <= 0))) {
                try {
                    HttpRequest.Post(URLDB.URL + "MeterData/?EAN=" + supplyPoint.getEAN18() + "&DATE=" + dataSupplyPoint.getDate() + "&ACTION=delete");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
