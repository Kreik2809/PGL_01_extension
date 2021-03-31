package gui.app1;

import gui.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;
import user.DataSupplyPoint;
import user.EnergyType;
import user.SupplyPoint;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * La Controller de la fenêtre de visualisation de donnée pour l'autorité régionale
 */
public class DashBoardAController implements Initializable {

    @FXML
    private Text scaleLabel;

    @FXML
    private Text periodLabel;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ListView<GreenCertificateLine> greenCertifs;

    @FXML
    private TableView<DataSupplyPoint> tableProd;

    @FXML
    private TableColumn<DataSupplyPoint, String> dates;

    @FXML
    private TableColumn<DataSupplyPoint, Double> production;

    @FXML
    private LineChart<String, Double> graph;

    private ObservableList<GreenCertificateLine> greenCertificateLines= FXCollections.observableArrayList();

    private static ObservableList<DataSupplyPoint> tableLines = FXCollections.observableArrayList();

    private static ObservableList<XYChart.Series<String, Double>> graphData = FXCollections.observableArrayList();

    private static GreenCertificateLine currentGC = null;

    private static StringProperty scaleString = new SimpleStringProperty();

    private static StringProperty periodString = new SimpleStringProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //On paramètre les Text javafx pour la gestion de l'échelle et de la période.
        scaleString.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                scaleLabel.setText(scaleString.getValue());
            }
        });

        periodString.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                periodLabel.setText(periodString.getValue());
            }
        });

        scaleString.setValue("Echelle : ");

        periodString.setValue("Periode : ");

        scaleLabel.setText(scaleString.getValue());

        periodLabel.setText(periodString.getValue());

        //On récupère et personnalise l'affichage des certificats verts pour l'authorité
        greenCertificateLines.clear();
        greenCertifs.setItems(greenCertificateLines);
        greenCertifs.setCellFactory(new Callback<ListView<GreenCertificateLine>, ListCell<GreenCertificateLine>>() {
            @Override
            public ListCell<GreenCertificateLine> call(ListView<GreenCertificateLine> param) {
                return new GreenCertificatListCell();
            }
        });

        //On récupère l'ensemble des certificats verts.
        fetchGreenCertificate();

        dates.setCellValueFactory(new PropertyValueFactory<DataSupplyPoint, String>("date"));
        production.setCellValueFactory(new PropertyValueFactory<DataSupplyPoint, Double>("value"));

        setCellF(dates);
        setCellF(production);

        greenCertifs.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleGreenCertifClick();
            }
        });
    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur clique sur un certificat vert.
     * Elle a pour rôle d'affocher la production de celui-ci.
     */
    public void handleGreenCertifClick(){
        if ((currentGC=greenCertifs.getSelectionModel().getSelectedItem())!=null){
            String[] tab = {"EAN", "ENERGYTYPE", "NAME", "STATE", "CID", "DAYSTART", "SID", "TYPE"};
            ArrayList<String> l = new ArrayList<>();
            try{
                l =HttpRequest.Get(URLDB.URL+"MeterByEAN/EAN="+currentGC.getEan(), new ParseJson(tab, "NO METER FOR THIS EAN", "PLEASE USE THIS SYNTAX : /RestAPI/MeterByEAN/EAN=xx") {});
            } catch (IOException e){
                e.printStackTrace();
            }
            SupplyPoint s = new SupplyPoint("", EnergyType.ELECTRICITY, currentGC.getEan(), l.get(5));
            GuiHandler.getGui().setCurrentSupplyPoint(s);
            s.fetchData();
            displayData(s.getDataSupplyPoints());
        }
    }

    /**
     * Cette méthode prend en paramètre une ArrayList de DataSupplyPoint et en affiche le contenu dans le tableau
     * et sur le graphique
     * @param list La list contenant les données
     */
    public void displayData(ArrayList<DataSupplyPoint> list){
        graphData.clear();
        tableLines.clear();
        tableLines.addAll(list);

        tableProd.setItems(tableLines);



        XYChart.Series<String, Double> consoSerie = new XYChart.Series<String, Double>();

        consoSerie.setName("Production (kwh)");


        for (int i=0; i<list.size(); i++){
            consoSerie.getData().add(new XYChart.Data<String, Double>(list.get(i).getDate(), list.get(i).getValue()));
        }

        graphData.addAll(consoSerie);

        graph.setAnimated(false);
        graph.setData(graphData);

        scaleString.setValue("Echelle : ");

        periodString.setValue("Periode : ");

    }

    /**
     * Cette méthode va exécuter l'ensemble des requêtes http nécessaires afin de récupérer l'ensemble des certificats vert que l'authorité doit analyser.
     */
    public void fetchGreenCertificate(){
        ArrayList<String> l = new ArrayList<>();
        String[] tab = {"RID","ID", "METERID", "VALUE", "ASKINGDATE", "VALIDATIONDATE"};
        String[] tab2 = {"EAN", "ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};
        try{
            l = HttpRequest.Get(URLDB.URL+"GreenCertificateByRID/RID="+GuiHandler.getGui().getCurrentUser().getName(), new ParseJson(tab, "No certificate for this RID", ""){});
        } catch (IOException e){
            e.printStackTrace();
        }
        ArrayList<String> l2 = new ArrayList<>();
        for (int i=0;i<l.size(); i+=6){
            if (l.get(i+5).equals("-1")) {
                String id = l.get(i + 1);
                String ean = l.get(i + 2);
                String value = l.get(i + 3);

                try {
                    l2 = HttpRequest.Get(URLDB.URL + "Contracts/EAN=" + ean, new ParseJson(tab2, "NO CONTRACT FOR THIS ID", "") {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String username = l2.get(3);
                String supplier = l2.get(2);
                String lastCertif = GuiHandler.getLastCertifDate(ean);

                greenCertificateLines.add(new GreenCertificateLine(id, ean, lastCertif, username, supplier, value));
            }

        }
    }

    @FXML
    void changePwdClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/change_passwd.fxml", "Modifier mot de passe", 300, 200, this);
    }

    @FXML
    void scaleButtonClicked(MouseEvent event) {
        if(GuiHandler.getGui().getCurrentSupplyPoint() != null){
            GuiHandler.setLastController(Controller.DashBoardAuthority);
            GuiHandler.setCp(((((Button)event.getSource()).getScene().getRoot())));
            GuiHandler.getCp().setDisable(true);
            GuiHandler.createScene("app1/changePeriod.fxml", "Choix de la periode", 300, 250, this);
        }
    }


    /**
     * Cette méthode est appelée lorsque l'authorité désire valider un certificat
     * @param event
     */
    @FXML
    void confirmClicked(MouseEvent event) {
        if (currentGC != null){
            try{
                HttpRequest.Post(URLDB.URL + "GreenCertificateByID/?ID="+currentGC.getId()+"&ACTION=accept");
            } catch (IOException e){
                e.printStackTrace();
            }
            greenCertificateLines.remove(currentGC);
            tableLines.clear();
            graphData.clear();
            currentGC = null;
        }

    }

    /**
     * Cette méthode est appelée lorsque l'authortié désire supprimer un certificat
     * @param event
     */
    @FXML
    void supprClicked(MouseEvent event) {
        if (currentGC != null){
            try{
                HttpRequest.Post(URLDB.URL + "GreenCertificateByID/?ID="+currentGC.getId()+"&ACTION=del");
            } catch (IOException e){
                e.printStackTrace();
            }
            greenCertificateLines.remove(currentGC);
            tableLines.clear();
            graphData.clear();
            currentGC = null;
        }
    }

    /**
     * On paramètre le cellFactory d'une colonne t
     */
    public void setCellF(TableColumn t){
        t.setCellFactory(new Callback<TableColumn<SupplyPointLine, String>, TableCell<SupplyPointLine, String>>() {
            @Override
            public TableCell<SupplyPointLine, String> call(TableColumn<SupplyPointLine, String> param) {
                return new DataSupplyPointTabCell();
            }
        });
    }

    public Text getScaleLabel() {
        return scaleLabel;
    }

    public void setScaleLabel(Text scaleLabel) {
        this.scaleLabel = scaleLabel;
    }

    public Text getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(Text periodLabel) {
        this.periodLabel = periodLabel;
    }

    public static ObservableList<DataSupplyPoint> getTableLines() {
        return tableLines;
    }

    public static void setTableLines(ObservableList<DataSupplyPoint> tableLines) {
        DashBoardAController.tableLines = tableLines;
    }

    public static ObservableList<XYChart.Series<String, Double>> getGraphData() {
        return graphData;
    }

    public static void setGraphData(ObservableList<XYChart.Series<String, Double>> graphData) {
        DashBoardAController.graphData = graphData;
    }

    public static String getScaleString() {
        return scaleString.get();
    }

    public static StringProperty scaleStringProperty() {
        return scaleString;
    }

    public static void setScaleString(String scaleString) {
        DashBoardAController.scaleString.set(scaleString);
    }

    public static String getPeriodString() {
        return periodString.get();
    }

    public static StringProperty periodStringProperty() {
        return periodString;
    }

    public static void setPeriodString(String periodString) {
        DashBoardAController.periodString.set(periodString);
    }
}
