package gui.app1;

import gui.SupplyPointLine;
import gui.SupplyPointTabCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import user.Contract;
import user.SupplyPoint;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre d'informations concernant les compteurs d'un portefeuille.
 */
public class InfoCController implements Initializable {

    @FXML
    private TableView<SupplyPointLine> tableCounter;

    @FXML
    private TableColumn<SupplyPointLine, String> ids;

    @FXML
    private TableColumn<SupplyPointLine, String> types;

    @FXML
    private TableColumn<SupplyPointLine, String> nContracts;

    @FXML
    private TableColumn<SupplyPointLine, String> energySuppliers;

    @FXML
    private TableColumn<SupplyPointLine, String> tContracts;

    public ObservableList<SupplyPointLine> metersLine = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb){
        // On initialise la tableView

        setCellF(ids);
        setCellF(types);
        setCellF(nContracts);
        setCellF(energySuppliers);
        setCellF(tContracts);

        ids.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("ID"));
        types.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("type"));
        nContracts.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("contractNumber"));
        energySuppliers.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("energySupplier"));
        tContracts.setCellValueFactory(new PropertyValueFactory<SupplyPointLine, String>("contractType"));
        metersLine.clear();

        //On récupère les infos à afficher
        ArrayList<SupplyPoint> l = GuiHandler.getGui().getCurrentPortfolio().getSupplyPoints();
        ArrayList<SupplyPointLine> lines = new ArrayList<>();
        for (SupplyPoint s : l){
            s.fetchContract();
            if (s.getContract() != null)
                lines.add(new SupplyPointLine(s.getContract(), s));
            else{
                ArrayList<SupplyPoint> liste = new ArrayList<>();
                liste.add(s);
                lines.add(new SupplyPointLine(s));
            }
        }
        showUser(lines);
    }

    /**
     * Cette méthode personnalise et affiche les compteurs dans la tableView de la fenêtre.
     * @param list La liste des compteurs à afficher
     */
    public void showUser(List<SupplyPointLine> list){
        metersLine.addAll(list);
        tableCounter.setItems(metersLine);
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


}
