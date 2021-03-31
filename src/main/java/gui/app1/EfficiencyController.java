package gui.app1;

import gui.Controller;
import gui.DataSupplyPointTabCell;
import gui.SupplyPointLine;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;
import user.DataSupplyPoint;
import user.EnergyType;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre de visualistation de rendements
 */
public class EfficiencyController implements Initializable {



    @FXML
    private GridPane meterGrid;

    @FXML
    private Text theoryEfficiency;

    @FXML
    private Text actualEfficiency;

    @FXML
    private Text diff;

    @FXML
    private TableView<DataSupplyPoint> tableConso;

    @FXML
    private TableColumn<DataSupplyPoint, String> dates;

    @FXML
    private TableColumn<DataSupplyPoint, Double> production;

    @FXML
    private Text scaleLabel;

    @FXML
    private Text PeriodLabel;

    @FXML
    private LineChart<String, Double> graph;

    private static String installationType;

    private static DecimalFormat df = new DecimalFormat("0.00");

    private static ObservableList<DataSupplyPoint> tableLines = FXCollections.observableArrayList();

    private static ObservableList<XYChart.Series<String, Double>> graphData = FXCollections.observableArrayList();

    private static double theoricalEfficieny, panelNumber, area;

    private static StringProperty scaleString = new SimpleStringProperty();

    private static StringProperty periodString = new SimpleStringProperty();

    private static StringProperty actualEfficiencyString = new SimpleStringProperty();

    private static StringProperty diffString = new SimpleStringProperty();

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
                PeriodLabel.setText(periodString.getValue());
            }
        });

        actualEfficiencyString.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                actualEfficiency.setText(actualEfficiencyString.getValue());
            }
        });

        diffString.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                diff.setText(diffString.getValue());
            }
        });

        scaleString.setValue("Echelle : ");

        periodString.setValue("Periode : ");

        scaleLabel.setText(scaleString.getValue());

        PeriodLabel.setText(periodString.getValue());

        //On personnalise l'affichage des infos de la page.
        dates.setCellValueFactory(new PropertyValueFactory<DataSupplyPoint, String>("date"));
        production.setCellValueFactory(new PropertyValueFactory<DataSupplyPoint, Double>("value"));
        setCellF(dates);
        setCellF(production);

        ArrayList<DataSupplyPoint> data = new ArrayList<>(DashboardMController.getTableLines());
        displayData(data);

        String[] tab = {"EAN", "TYPE", "PUISSANCE", "SUPERFICIE", "NOMBRE"};
        ArrayList<String> l = new ArrayList<>();
        try{

            l = HttpRequest.Get(URLDB.URL+"Productions/EAN="+GuiHandler.getGui().getCurrentSupplyPoint().getEAN18(),
                    new ParseJson(tab, "No production supply point for this EAN", "") {});

        } catch (IOException e){
            e.printStackTrace();
        }
        if (l.size() > 1){ //On est dans le cas ou le supply point est un panneau solaire
            installationType = "SOLAR";
            double puissance = Double.parseDouble(l.get(2));
            double superficie = Double.parseDouble(l.get(3));
            area = superficie;
            panelNumber = Integer.parseInt(l.get(4));

            theoricalEfficieny = puissance/(superficie*1000);
            theoryEfficiency.setText("Rendement theorique : " + df.format(theoricalEfficieny*100) + "%");
            actualEfficiency.setText("Rendement reel : " + df.format(computeActualSolarEfficiency()*100) + "%");
            double delta = (computeActualSolarEfficiency()*100) - (theoricalEfficieny*100);
            diff.setText("Difference de rendement : " + df.format(delta) + "%");
        }
        else{
            installationType = "WIND";
            theoryEfficiency.setText("");
            diff.setText("");
            actualEfficiency.setText("Rendement : " + df.format(computeActualWindEfficiency()) + " kwh/jour");
        }

    }

    /**
     * Calcul le rendement réel de l'installation solaire en fonction de la période affichée.
     * De base on calcule le rendement réel sur l'entièreté des données disponibles.
     */
    public static Double computeActualSolarEfficiency(){
        Double res=0.0;

        Double total = 0.0;
        for (DataSupplyPoint dt : tableLines){
            total += dt.getValue();
        }
        Double avg = total/tableLines.size();
        Double actualPower = (avg *365)/panelNumber;

        res = (actualPower*0.9)/(area*1000);

        return res;
    }

    public static Double computeActualWindEfficiency(){
        Double res=0.0;
        Double total = 0.0;
        for (DataSupplyPoint dt : tableLines){
            total += dt.getValue();
        }
        res = total/tableLines.size();
        return res;
    }

    public void displayData(ArrayList<DataSupplyPoint> list){
        graphData.clear();
        tableLines.clear();
        tableLines.addAll(list);

        tableConso.setItems(tableLines);



        XYChart.Series<String, Double> consoSerie = new XYChart.Series<String, Double>();


        for (int i=0; i<list.size(); i++){
            consoSerie.getData().add(new XYChart.Data<String, Double>(list.get(i).getDate(), list.get(i).getValue()));
        }

        graphData.addAll(consoSerie);

        consoSerie.setName("Production (kwh)");

        graph.setAnimated(false);
        graph.setData(graphData);


        scaleString.setValue("Echelle : ");

        periodString.setValue("Periode : ");
    }

    @FXML
    void changePeriodClicked(MouseEvent event) {
        GuiHandler.setSecondCp(((((Button)event.getSource()).getScene().getRoot())));
        GuiHandler.getSecondCp().setDisable(true);
        if (installationType.equals("SOLAR"))
            GuiHandler.setLastController(Controller.EfficiencyDashboardSolar);
        else
            GuiHandler.setLastController(Controller.EfficiencyDashBoardWind);
        GuiHandler.createScene("app1/changePeriod.fxml", "Choix de la periode", 300, 250, this);
    }

    public void setCellF(TableColumn t){
        t.setCellFactory(new Callback<TableColumn<SupplyPointLine, String>, TableCell<SupplyPointLine, String>>() {
            @Override
            public TableCell<SupplyPointLine, String> call(TableColumn<SupplyPointLine, String> param) {
                return new DataSupplyPointTabCell();
            }
        });
    }

    public static ObservableList<DataSupplyPoint> getTableLines() {
        return tableLines;
    }

    public static ObservableList<XYChart.Series<String, Double>> getGraphData() {
        return graphData;
    }

    public static String getScaleString() {
        return scaleString.get();
    }

    public static StringProperty getScaleStringProperty() {
        return scaleString;
    }

    public static String getPeriodString() {
        return periodString.get();
    }

    public static StringProperty getPeriodStringProperty() {
        return periodString;
    }

    public static String getActualEfficiencyString() {
        return actualEfficiencyString.get();
    }

    public static StringProperty getActualEfficiencyStringProperty() {
        return actualEfficiencyString;
    }

    public static String getDiffString() {
        return diffString.get();
    }

    public static StringProperty getDiffStringProperty() {
        return diffString;
    }

    public static double getTheoricalEfficieny() {
        return theoricalEfficieny;
    }
}
