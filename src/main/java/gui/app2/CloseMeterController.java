package gui.app2;

import gui.SupplyPointLine;
import gui.SupplyPointTabCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import user.SupplyPoint;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre de fermeture de compteur
 */
public class CloseMeterController implements Initializable {

    @FXML
    private TableView<SupplyPointLine> meterTable;

    @FXML
    private TableColumn<SupplyPointLine, String> nameCol;

    @FXML
    private TableColumn<SupplyPointLine, String> eanCol;

    @FXML
    private TableColumn<SupplyPointLine, String> clientCol;

    private static ObservableList<SupplyPointLine> meterLine = FXCollections.observableArrayList();

    private SupplyPointLine currentLine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentLine = DashBoardFController.getCurrentLine();
        meterLine.clear();
        meterLine.addAll(DashBoardFController.getOpenedMeters());
        nameCol.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("name"));
        eanCol.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("ID"));
        clientCol.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("clientName"));

        meterTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMeterClicked(meterTable, meterLine);
            }
        });

        setCellF(nameCol);
        setCellF(eanCol);
        setCellF(clientCol);

        meterTable.setItems(meterLine);


    }

    public void handleMeterClicked(TableView<SupplyPointLine> t, ObservableList<SupplyPointLine> l){
        if ((currentLine = t.getSelectionModel().getSelectedItem()) != null) {
            int i = t.getSelectionModel().getSelectedIndex();
            for (SupplyPointLine sp : t.getItems()) {
                sp.setSpecialIndicator("");
            }
            t.getItems().get(i).setSpecialIndicator("selected"); //On force le changement de l'affichage
            ObservableList<SupplyPointLine> copy = FXCollections.observableArrayList();
            copy.addAll(l);
            l.removeAll(l);
            l.addAll(copy);
        }
    }

    @FXML
    void cancelClicked(MouseEvent event) {
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void confirmClicked(MouseEvent event) {
        if (currentLine != null) {
            SupplyPoint s = currentLine.getMeter();
            if (s != null) {
                GuiHandler.getGui().getCurrentSupplier().closeCounter(s);
                DashBoardFController.getOpenedMeters().remove(currentLine);
                DashBoardFController.getClosedMeters().add(currentLine);
                GuiHandler.getCp().setDisable(false);
                GuiHandler.setCp(null);
                ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR ! ");
                alert.setHeaderText(null);
                alert.setContentText("veuillez sélectionner un compteur avant de le supprimer");
                alert.showAndWait();
            }
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
