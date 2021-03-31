package gui.app1;

import gui.GreenCertificateTabCell;
import gui.SupplyPointLine;
import gui.SupplyPointTabCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;
import user.SupplyPoint;
import user.SupplyPointType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre qui contient l'ensemble des informations concernant les certificats verts d'un portefeuille.
 */
public class GreenCertificateInfoController implements Initializable {

    @FXML
    private TableView<GreenCertificateLine> tableGC;

    @FXML
    private TableColumn<GreenCertificateLine, String> ids;

    @FXML
    private TableColumn<GreenCertificateLine, String> ean;

    @FXML
    private TableColumn<GreenCertificateLine, String> dateAsk;

    @FXML
    private TableColumn<GreenCertificateLine, String> dateValidation;

    @FXML
    private TableColumn<GreenCertificateLine, String> amount;

    public ObservableList<GreenCertificateLine> certificatesLine = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCellF(ids);
        setCellF(ean);
        setCellF(dateAsk);
        setCellF(dateValidation);
        setCellF(amount);

        ids.setCellValueFactory(new PropertyValueFactory<GreenCertificateLine, String>("id"));
        ean.setCellValueFactory(new PropertyValueFactory<GreenCertificateLine, String>("ean"));
        dateAsk.setCellValueFactory(new PropertyValueFactory<GreenCertificateLine, String>("askingDate"));
        dateValidation.setCellValueFactory(new PropertyValueFactory<GreenCertificateLine, String>("validationDate"));
        amount.setCellValueFactory(new PropertyValueFactory<GreenCertificateLine, String>("amount"));
        certificatesLine.clear();
        fetchGreenCertificate();
        tableGC.setItems(certificatesLine);

    }

    /**
     * Cette méthode récupère l'ensemble des certificats verts liés au portefeuille courant
     */
    public void fetchGreenCertificate(){
        ArrayList<String> l = new ArrayList<>();
        String[] tab = {"EAN", "ID", "REGIONALID", "VALUE","ASKINGDATE", "VALIDATIONDATE"};
        for (SupplyPoint s : GuiHandler.getGui().getCurrentPortfolio().getSupplyPoints()){
            if(s.getSupplyPointType()== SupplyPointType.PRODUCTION){
                try{
                    l = HttpRequest.Get(URLDB.URL+"GreenCertificateByEAN/EAN="+s.getEAN18(),  new ParseJson(tab, "No certificate for this ean", ""){});
                }catch (IOException e){
                    e.printStackTrace();
                }
                String validation="";
                if(l.size()>1){
                    if (l.get(5).equals("-1")){
                        validation = "En attente";
                    }
                    else{
                        validation = l.get(5);
                    }
                    GreenCertificateLine gc = new GreenCertificateLine(l.get(0), l.get(1), l.get(4),validation, l.get(3));
                    certificatesLine.add(gc);
                }
            }
        }
    }

    /**
     * On paramètre le cellFactory d'une colonne t
     */
    public void setCellF(TableColumn t){
        t.setCellFactory(new Callback<TableColumn<SupplyPointLine, String>, TableCell<SupplyPointLine, String>>() {
            @Override
            public TableCell<SupplyPointLine, String> call(TableColumn<SupplyPointLine, String> param) {
                return new GreenCertificateTabCell();
            }
        });
    }
}
