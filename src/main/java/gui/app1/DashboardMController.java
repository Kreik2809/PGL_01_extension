package gui.app1;

import gui.DataSupplyPointTabCell;
import gui.Language;
import gui.MetersListCell;
import gui.SupplyPointLine;
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
import javafx.scene.text.Text;
import javafx.util.Callback;
import tool.AccessLevelType;
import user.*;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * Le controller de la fenêtre de visualisation de données, appelée dashboard meter.
 */
public class DashboardMController implements Initializable {

    @FXML
    private AnchorPane mainPane;


    @FXML
    private Menu gestionMenu;

    @FXML
    private MenuItem returnMenu;

    @FXML
    private MenuItem infoPMenu;

    @FXML
    private MenuItem infoCMenu;

    @FXML
    private MenuItem gestionPMenu;

    @FXML
    private MenuItem globalElecMenu;

    @FXML
    private MenuItem globalGazMenu;

    @FXML
    private MenuItem globalWaterMenu;

    @FXML
    private MenuItem globalProdConso;

    @FXML
    private MenuItem globalProd;

    @FXML
    private MenuItem rendement;

    @FXML
    private ListView<SupplyPoint> meters;

    @FXML
    private TableView<DataSupplyPoint> tableConso;

    @FXML
    private TableColumn<DataSupplyPoint, String> dates;

    @FXML
    private TableColumn<DataSupplyPoint, Double> consommation;

    @FXML
    private Text scaleLabel;

    @FXML
    private Text periodLabel;

    @FXML
    private Button scaleButton;

    private static StringProperty scaleString = new SimpleStringProperty();

    private static StringProperty periodString = new SimpleStringProperty();

    @FXML
    private LineChart<String, Double> graph;

    private static ObservableList<SupplyPoint> observableMeters = FXCollections.observableArrayList();

    private static ObservableList<DataSupplyPoint> tableLines = FXCollections.observableArrayList();

    private static ObservableList<XYChart.Series<String, Double>> graphData = FXCollections.observableArrayList();

    private SupplyPoint currentM;

    private static String dataText;

    @Override
    public void initialize(URL url, ResourceBundle rb){
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



        //On configure la personnalisation de l'affichage es données dans le tableView.
        dates.setCellValueFactory(new PropertyValueFactory<DataSupplyPoint, String>("date"));
        consommation.setCellValueFactory(new PropertyValueFactory<DataSupplyPoint, Double>("value"));
        setCellF(dates);
        setCellF(consommation);

        // On configure les accès à la fenêtre
        setWindow(GuiHandler.getGui().getCurrentUser());

        observableMeters.clear();

        meters.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMeterClick();
            }
        });

        EnergyPortfolio e = GuiHandler.getGui().getCurrentPortfolio();
        e.fetchSupplyPoints();
        ArrayList<SupplyPoint> list = e.getSupplyPoints();
        showMeters(list);
        for (SupplyPoint meter : e.getSupplyPoints()){
            meter.fetchData();
        }
    }

    /**
     * Cette méthode modifie la fenêtre en fonction du niveau d'accès au portefeuille courant de l'utilisateur.
     * @param user L'utilisateur dont on doit vérifier le niveau d'accès
     */
    public void setWindow(EnergyConsumer user){
        AccessLevelType accessLevelType = user.getAccessLevelType(GuiHandler.getGui().getCurrentPortfolio());
        if (accessLevelType == AccessLevelType.READING){
            gestionMenu.setDisable(true);
            gestionMenu.setVisible(false);
        }

        else if (accessLevelType == AccessLevelType.WRITING){
            gestionPMenu.setDisable(true);
            gestionPMenu.setVisible(false);
        }
    }

    /**
     * Cette méthode personnalise et affiche les Compteurs dans la listView de la fenêtre.
     * @param l La liste des portefeuilles à afficher
     */
    public void showMeters(ArrayList<SupplyPoint> l){
        observableMeters.addAll(l);
        meters.setItems(observableMeters);
        meters.setCellFactory(new Callback<ListView<SupplyPoint>, ListCell<SupplyPoint>>() {
            @Override
            public ListCell<SupplyPoint> call(ListView<SupplyPoint> listView) {
                return new MetersListCell();
            }
        });
    }

    /**
     * Cette méthode est appelée lorsqu'un utilisateur clique sur compteur du portefeuille.
     * Elle se charge d'exécuter les méthodes relatives à l'affichage des données du compteur.
     */
    public void handleMeterClick(){
        if ((currentM=meters.getSelectionModel().getSelectedItem())!=null && currentM.getDataSupplyPoints() != null)
        {
            scaleString.setValue("Echelle : ");
            periodString.setValue("Periode : ");
            displayData(currentM.getDataSupplyPoints(), currentM.getEnergyType());
            GuiHandler.getGui().setCurrentSupplyPoint(currentM);
            if (currentM.getSupplyPointType() == SupplyPointType.PRODUCTION){
                consommation.setText("Production (kwh)");
            }
        }
    }

    /**
     * Cette méthode prend en paramètre une ArrayList de DataSupplyPoint et en affiche le contenu dans le tableau
     * et sur le graphique
     * @param list La list contenant les données
     * @param t Le type d'énergie à afficher
     */
    public void displayData(ArrayList<DataSupplyPoint> list, EnergyType t){
        graphData.clear();
        tableLines.clear();
        tableLines.addAll(list);

        tableConso.setItems(tableLines);



        XYChart.Series<String, Double> consoSerie = new XYChart.Series<String, Double>();


        for (int i=0; i<list.size(); i++){
            consoSerie.getData().add(new XYChart.Data<String, Double>(list.get(i).getDate(), list.get(i).getValue()));
        }

        graphData.addAll(consoSerie);

        graph.setAnimated(false);
        graph.setData(graphData);

        switch(t){
            case ELECTRICITY:
                dataText = "Consommation (kwh)";
                consommation.setText("Consommation (kwh)");
                consoSerie.setName("Consommation (kwh)");
                break;
            case GAS:
                dataText = "Consommation (m3)";
                consommation.setText("Consommation (m3)");
                consoSerie.setName("Consommation (m3)");
                break;
            case WATER:
                dataText = "Consommation (m3)";
                consommation.setText("Consommation (m3)");
                consoSerie.setName("Consommation (m3)");
                break;
        }
        scaleString.setValue("Echelle : ");

        periodString.setValue("Periode : ");
    }

    @FXML
    void changePwdClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/change_passwd.fxml", "Modifier mot de passe", 300, 200, this);
    }

    @FXML
    void chooseEnglish(ActionEvent event) {
        Language en = Language.English;
        GuiHandler.changeLanguage(en);
    }

    @FXML
    void chooseFrench(ActionEvent event) {
        Language fr = Language.French;
        GuiHandler.changeLanguage(fr);
    }

    @FXML
    void gestionPClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/gestionPortfolios.fxml", "Gestion Portefeuille", 800, 600, this);
    }

    /**
     * Cette méthode superpose les données d'un compteur à une liste qui contient les données superposées d'autres compteurs.
     */
    public ArrayList<DataSupplyPoint> superimposeData(ArrayList<DataSupplyPoint> list, ArrayList<DataSupplyPoint> secondList){

        ArrayList<DataSupplyPoint> copyList = new ArrayList<>(list);
        ArrayList<DataSupplyPoint> copySecondList = new ArrayList<>(secondList);
        ArrayList<DataSupplyPoint> finalList = new ArrayList<>();
        if (copyList.size() == 0) //C'est les premières données que l'on ajoute.
            finalList.addAll(copySecondList);
        else if (copySecondList.size()==0){
            finalList.addAll(copyList);
        }
        else {// Il y a déjà des données dans la liste

            Date date = new Date(System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, 1); // On met la date de demain dans la sentinelle
            date = new Date(c.getTimeInMillis());
            copyList.add(copyList.size(),new DataSupplyPoint(date.toString(), 0, null)); //On ajoute une sentinelle
            copySecondList.add(copySecondList.size(),new DataSupplyPoint(date.toString(), 0, null)); //On ajoute une sentinelle

            int i=0,k=0;
            while(!copyList.get(i).getDate().equals(date.toString())|| !copySecondList.get(k).getDate().equals(date.toString())){
                if (Date.valueOf(copyList.get(i).getDate()).compareTo(Date.valueOf(copySecondList.get(k).getDate()))>0){

                    finalList.add(copySecondList.get(k));
                    k++;
                }
                else if(Date.valueOf(copyList.get(i).getDate()).compareTo(Date.valueOf(copySecondList.get(k).getDate()))<0){

                    finalList.add(copyList.get(i));
                    i++;
                }
                else{

                    double value = copySecondList.get(k).getValue() + copyList.get(i).getValue();
                    DataSupplyPoint dt = new DataSupplyPoint();
                    dt.setValue(value);
                    dt.setDate(copySecondList.get(k).getDate());
                    finalList.add(dt);
                    i++;k++;
                }
            }
        }
        return finalList;
    }

    /**
     * Cette méthode récupère les données de consommation de l'entièreté des compteurs d'un même type et les additionne.
     * @param t le type d'énergie
     */
    public ArrayList<DataSupplyPoint> gatherConsommation(EnergyType t){
        ArrayList<DataSupplyPoint> list = new ArrayList<>();
        for (SupplyPoint s : GuiHandler.getGui().getCurrentPortfolio().getSupplyPoints()){
            if (s.getSupplyPointType() == SupplyPointType.CONSOMMATION) {
                if (s.getEnergyType() == t) {

                    list = superimposeData(list, s.getDataSupplyPoints());
                }
            }
        }
        return list;

    }

    /**
     * Cette méthode récupère les données de production de l'ensemble des compteur de production du portefeuille.
     * On prend comme précondition que les compteur de type production seront uniquement des compteurs électriques.
     */
    public ArrayList<DataSupplyPoint> gatherProduction(){
        ArrayList<DataSupplyPoint> list = new ArrayList<>();
        for (SupplyPoint s : GuiHandler.getGui().getCurrentPortfolio().getSupplyPoints()) {
            if (s.getSupplyPointType() == SupplyPointType.PRODUCTION) {
                list = superimposeData(list, s.getDataSupplyPoints());
            }
        }
        return  list;
    }

    @FXML
    void globalElecClicked(ActionEvent event) {
        displayData(gatherConsommation(EnergyType.ELECTRICITY), EnergyType.ELECTRICITY);
        SupplyPoint s = new SupplyPoint("",EnergyType.ELECTRICITY,"", "");
        s.setAllDataSupplyPoints(gatherConsommation(EnergyType.ELECTRICITY));
        GuiHandler.getGui().setCurrentSupplyPoint(s);
    }

    @FXML
    void globalGazClicked(ActionEvent event) {
        displayData(gatherConsommation(EnergyType.GAS), EnergyType.GAS);
        SupplyPoint s = new SupplyPoint("",EnergyType.GAS,"", "");
        s.setAllDataSupplyPoints(gatherConsommation(EnergyType.GAS));
        GuiHandler.getGui().setCurrentSupplyPoint(s);
    }

    @FXML
    void globalWaterClicked(ActionEvent event) {
        displayData(gatherConsommation(EnergyType.WATER), EnergyType.WATER);
        SupplyPoint s = new SupplyPoint("",EnergyType.WATER,"", "");
        s.setAllDataSupplyPoints(gatherConsommation(EnergyType.WATER));
        GuiHandler.getGui().setCurrentSupplyPoint(s);
    }

    @FXML
    void globalProdClicked(ActionEvent event) {
        displayData(gatherProduction(), EnergyType.ELECTRICITY);
        SupplyPoint s = new SupplyPoint("",EnergyType.ELECTRICITY,"", "");
        s.setAllDataSupplyPoints(gatherProduction());
        GuiHandler.getGui().setCurrentSupplyPoint(s);
    }

    @FXML
    void globalProdConsoClicked(ActionEvent event) {
        ArrayList<DataSupplyPoint> productionData = gatherProduction();
        ArrayList<DataSupplyPoint> consommationData = gatherConsommation(EnergyType.ELECTRICITY);
        ArrayList<DataSupplyPoint> tempConso = new ArrayList<>();
        for (DataSupplyPoint d : consommationData){
            double data = d.getValue();
            data = -data;
            DataSupplyPoint dt = new DataSupplyPoint();
            dt.setDate(d.getDate());
            dt.setValue(data);
            tempConso.add(dt);
        }
        ArrayList<DataSupplyPoint> globalData = new ArrayList<>();
        globalData = superimposeData(productionData, tempConso);
        SupplyPoint s = new SupplyPoint("",EnergyType.ELECTRICITY,"", "");
        s.setAllDataSupplyPoints(globalData);
        GuiHandler.getGui().setCurrentSupplyPoint(s);
        displayData(globalData, EnergyType.ELECTRICITY);
    }

    @FXML
    void rendementClicked(ActionEvent event) {
        if (currentM != null && currentM.getSupplyPointType() == SupplyPointType.PRODUCTION){
            GuiHandler.setCp(mainPane.getScene().getRoot());
            GuiHandler.getCp().setDisable(true);
            GuiHandler.createScene("app1/rendements.fxml","Information Compteurs", 1000, 500, this);
        }


    }

    @FXML
    void askGCClicked(ActionEvent event) {
        if (currentM != null && currentM.getSupplyPointType() == SupplyPointType.PRODUCTION){
            GuiHandler.setCp(mainPane.getScene().getRoot());
            GuiHandler.getCp().setDisable(true);
            GuiHandler.createScene("app1/ask_greenCertificate.fxml","Demande Certificat vert", 320, 100, this);
        }
    }

    @FXML
    void infoGreenCClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/info_greenCertificate.fxml","Information Certificat vert", 600, 400, this);

    }

    @FXML
    void infoCClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/info_counters.fxml","Information Compteurs", 600, 400, this);
    }

    @FXML
    void infoPClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/infoPortfolio.fxml","Information Portefeuille", 600, 400, this);
    }

    @FXML
    void returnClicked(ActionEvent event) {
        GuiHandler.loadScene("app1/dashboard_portefeuille.fxml", 800, 550, this);
    }

    @FXML
    void scaleButtonClicked(MouseEvent event) {
        if(GuiHandler.getGui().getCurrentSupplyPoint() != null){
            GuiHandler.setCp(((((Button)event.getSource()).getScene().getRoot())));
            GuiHandler.getCp().setDisable(true);
            GuiHandler.createScene("app1/changePeriod.fxml", "Choix de la periode", 300, 250, this);
        }

    }

    @FXML
    void addDataClicked(ActionEvent event) {
        GuiHandler.setCp(mainPane.getScene().getRoot());
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/addDataToCounter.fxml", "Ajout de donnee", 300, 100, this);
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

    public static ObservableList<SupplyPoint> getObservableMeters()
    {
        return observableMeters;
    }

    /**
     * Cette méthode met à jour l'entièreté de la liste de compteurs disponible sur la page.
     */
    public static void updateMeters(){
        observableMeters.clear();
        observableMeters.addAll(GuiHandler.getGui().getCurrentPortfolio().getSupplyPoints());
    }

    public static ObservableList<DataSupplyPoint> getTableLines() {
        return tableLines;
    }

    public static ObservableList<XYChart.Series<String, Double>> getGraphData() {
        return graphData;
    }

    public static String getDataText() {
        return dataText;
    }

    public static StringProperty getScaleStringProperty() {
        return scaleString;
    }

    public static StringProperty getPeriodStringProperty() {
        return periodString;
    }

}
