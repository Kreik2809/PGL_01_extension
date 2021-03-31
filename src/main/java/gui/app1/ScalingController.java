package gui.app1;

import gui.Controller;
import  javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tool.Scale;
import user.DataSupplyPoint;

import java.net.URL;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Le controller de la fenêtre de modification d'échelle et de période pour la visualisation de données.
 */
public class ScalingController implements Initializable {

    @FXML
    private ChoiceBox<String> echelles;

    @FXML
    private DatePicker datePickerStart;
    @FXML
    private DatePicker datePickerEnd;

    @FXML
    private Text errorText;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;


    private static DecimalFormat df = new DecimalFormat("0.00");


    private static final String[] SCALE = {"Journaliere", "Hebdomadaire", "Mensuelle", "Annuelle"};

    private static final String[] SCALERENDEMENT = {"Journaliere"};

    private static String period, echelleString;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        datePickerStart.setEditable(false);
        datePickerEnd.setEditable(false);
        errorText.setVisible(false);

        if (GuiHandler.getLastController() == Controller.EfficiencyDashBoardWind || GuiHandler.getLastController() == Controller.EfficiencyDashboardSolar)
            echelles.getItems().addAll(SCALERENDEMENT);
        else
            echelles.getItems().addAll(SCALE);

        echelles.setValue(SCALE[0]);
    }

    @FXML
    void cancelButtonClicked(MouseEvent event) {
        if (GuiHandler.getSecondCp()!= null){
            GuiHandler.getSecondCp().setDisable(false);
            GuiHandler.setSecondCp(null);
        }else{
            GuiHandler.getCp().setDisable(false);
            GuiHandler.setCp(null);
        }
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    void confirmButtonClicked(MouseEvent event) {

        Scale scale = null;
        if(echelles.getValue().equals(SCALE[0])) {
            scale = Scale.DAILY;
        }
        else if(echelles.getValue().equals(SCALE[1])) {
            scale = Scale.WEEKLY;
        }
        else if(echelles.getValue().equals(SCALE[2])) {
            scale = Scale.MONTHLY;
        }
        else if(echelles.getValue().equals(SCALE[3])) {
            scale = Scale.YEARLY;
        }
        if (scale != null && datePickerEnd.getValue() != null && datePickerStart.getValue()!=null) {
            scalling(scale);
            if (GuiHandler.getSecondCp() != null) {
                GuiHandler.getSecondCp().setDisable(false);
                GuiHandler.setSecondCp(null);
            } else {
                GuiHandler.getCp().setDisable(false);
                GuiHandler.setCp(null);
            }
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        }
    }

    /**
     * Cette méthode permet de filtrer l'ensemble des données d'un compteur afin d'en afficher uniquement celles choisies par
     * l'utilisateur.
     * @param scale
     */
    private void scalling(Scale scale)
    {
        ArrayList<DataSupplyPoint> liste;

        liste = new ArrayList<>(GuiHandler.getGui().getCurrentSupplyPoint().getAllDataSupplyPoints());


        Collections.sort(liste, new SortDataByDate());
        ArrayList<DataSupplyPoint> liste2 = new ArrayList<>();
        for(int i = 0; i < liste.size(); i++)
        {
            boolean dateBefore = (Date.valueOf(liste.get(i).getDate()).compareTo(Date.valueOf(datePickerStart.getValue())) < 0 );
            boolean dateAfter = (Date.valueOf(liste.get(i).getDate()).compareTo(Date.valueOf(datePickerEnd.getValue())) > 0 );
            if(!(dateBefore || dateAfter))
            {
                liste2.add(liste.get(i));
            }
        }
        if(scale.name().equals(Scale.WEEKLY.toString()))
        {
            ArrayList<DataSupplyPoint> liste3 = new ArrayList<>();
            liste3.addAll(liste2);
            liste2.clear();
            DataSupplyPoint newDT = new DataSupplyPoint(liste3.get(0).getDate(), 0, GuiHandler.getGui().getCurrentSupplyPoint());
            Date startDate = Date.valueOf(liste3.get(0).getDate());
            Date endDate = addDays(startDate, 7);
            for(DataSupplyPoint dt : liste3){
                if (Date.valueOf(dt.getDate()).compareTo(endDate) < 0){
                    newDT.setValue(newDT.getValue() + dt.getValue());
                }
                else{
                    newDT.setDate(startDate.toString());
                    liste2.add(newDT);
                    startDate = Date.valueOf(dt.getDate());
                    endDate = addDays(startDate, 7);
                    newDT = new DataSupplyPoint(startDate.toString(), 0, GuiHandler.getGui().getCurrentSupplyPoint());
                }
            }
        }
        else if(scale.name().equals(Scale.MONTHLY.toString()))
        {
            ArrayList<DataSupplyPoint> liste3 = new ArrayList<>();
            liste3.addAll(liste2);
            liste2.clear();
            DataSupplyPoint newDT = new DataSupplyPoint(liste3.get(0).getDate(), 0, GuiHandler.getGui().getCurrentSupplyPoint());
            Date startDate = Date.valueOf(liste3.get(0).getDate());
            Date endDate = getLastDayMonth(startDate);
            for(DataSupplyPoint dt : liste3){
                if (Date.valueOf(dt.getDate()).compareTo(endDate) < 0){
                    newDT.setValue(newDT.getValue() + dt.getValue());
                }
                else{
                    newDT.setDate(startDate.toString());
                    liste2.add(newDT);
                    startDate = Date.valueOf(dt.getDate());
                    startDate = addDays(startDate, 1);
                    endDate = getLastDayMonth(startDate);
                    newDT = new DataSupplyPoint(startDate.toString(), 0, GuiHandler.getGui().getCurrentSupplyPoint());
                }
            }

        }
        else if(scale.name().equals(Scale.YEARLY.toString()))
        {
            ArrayList<DataSupplyPoint> liste3 = new ArrayList<>();
            liste3.addAll(liste2);
            liste2.clear();
            DataSupplyPoint newDT = new DataSupplyPoint(liste3.get(0).getDate(), 0, GuiHandler.getGui().getCurrentSupplyPoint());
            Date startDate = Date.valueOf(liste3.get(0).getDate());
            Date endDate = getLastDayYear(startDate);
            for(DataSupplyPoint dt : liste3){
                if (Date.valueOf(dt.getDate()).compareTo(endDate) < 0){
                    newDT.setValue(newDT.getValue() + dt.getValue());
                }
                else{
                    newDT.setDate(startDate.toString());
                    liste2.add(newDT);
                    startDate = Date.valueOf(dt.getDate());
                    startDate = addDays(startDate, 1);
                    endDate = getLastDayYear(startDate);
                    newDT = new DataSupplyPoint(startDate.toString(), 0, GuiHandler.getGui().getCurrentSupplyPoint());
                }
            }
        }
        period = Date.valueOf(datePickerStart.getValue()).toString() + " - " +
                Date.valueOf(datePickerEnd.getValue()).toString();
        echelleString = echelles.getValue();
        GuiHandler.getGui().getCurrentSupplyPoint().setDataSupplyPoints(liste2);
        if (GuiHandler.getLastController() == Controller.EfficiencyDashboardSolar) {
            updateEfficiencyDataSolar(liste2);
            GuiHandler.setLastController(null);
        }
        else if (GuiHandler.getLastController() == Controller.EfficiencyDashBoardWind) {
            updateEfficiencyDataWind(liste2);
            GuiHandler.setLastController(null);
        }
        else if (GuiHandler.getLastController() == Controller.DashBoardAuthority) {
            updateDashBoardAuthorityData(liste2);
            GuiHandler.setLastController(null);
        }
        else
            updateDashBoardData(liste2);
    }

    /**
     * Cette méthode met à jour le graphique et le tableau de la page principale.
     * @param liste La liste de données à afficher.
     */
    private static void updateDashBoardData(ArrayList<DataSupplyPoint> liste){
        DashboardMController.getPeriodStringProperty().setValue("Periode : "+ period);
        DashboardMController.getScaleStringProperty().setValue("Echelle : " + echelleString);
        DashboardMController.getTableLines().clear();
        DashboardMController.getTableLines().addAll(liste);
        XYChart.Series<String, Double> consoSerie = new XYChart.Series<String, Double>();
        for (int i=0; i<liste.size(); i++){
            consoSerie.getData().add(new XYChart.Data<String, Double>(liste.get(i).getDate(), liste.get(i).getValue()));
        }
        consoSerie.setName(DashboardMController.getDataText());
        DashboardMController.getGraphData().clear();
        DashboardMController.getGraphData().addAll(consoSerie);

    }

    /**
     * Cette méthode met à jour le graphique et le tableau de la page principale de l'autorité régionale.
     * @param liste La liste de données à afficher.
     */
    private static void updateDashBoardAuthorityData(ArrayList<DataSupplyPoint> liste){
        DashBoardAController.periodStringProperty().setValue("Periode : "+ period);
        DashBoardAController.scaleStringProperty().setValue("Echelle : " + echelleString);
        DashBoardAController.getTableLines().clear();
        DashBoardAController.getTableLines().addAll(liste);
        XYChart.Series<String, Double> consoSerie = new XYChart.Series<String, Double>();
        for (int i=0; i<liste.size(); i++){
            consoSerie.getData().add(new XYChart.Data<String, Double>(liste.get(i).getDate(), liste.get(i).getValue()));
        }
        consoSerie.setName("Production (kwh)");
        DashBoardAController.getGraphData().clear();
        DashBoardAController.getGraphData().addAll(consoSerie);

    }

    /**
     * Cette méthode met à jour le graphique et le tableau de la page des rendements si on calcul le rendement d'une installation solaire.
     * @param liste La liste de donnée à afficher.
     */
    private static void updateEfficiencyDataSolar(ArrayList<DataSupplyPoint> liste){
        EfficiencyController.getTableLines().clear();
        EfficiencyController.getTableLines().addAll(liste);
        XYChart.Series<String, Double> prodSerie = new XYChart.Series<String, Double>();
        prodSerie.setName("Production (kwh)");
        for (int i=0; i<liste.size(); i++){
            prodSerie.getData().add(new XYChart.Data<String, Double>(liste.get(i).getDate(), liste.get(i).getValue()));
        }
        EfficiencyController.getGraphData().clear();
        EfficiencyController.getGraphData().addAll(prodSerie);
        EfficiencyController.getPeriodStringProperty().setValue("Periode : "+ period);
        EfficiencyController.getScaleStringProperty().setValue("Echelle : " + echelleString);
        EfficiencyController.getActualEfficiencyStringProperty().setValue("Rendement reel : " + df.format(EfficiencyController.computeActualSolarEfficiency()*100) + "%");
        double delta = (EfficiencyController.computeActualSolarEfficiency()*100) - (EfficiencyController.getTheoricalEfficieny()*100);
        EfficiencyController.getDiffStringProperty().setValue("Difference de rendement : " + df.format(delta) + "%");
    }

    /**
     * Cette méthode met à jour le graphique et le tableau de la page des rendements si on calcul le rendement d'une installation éolienne
     * @param liste La liste de données à afficher
     */
    private static void updateEfficiencyDataWind(ArrayList<DataSupplyPoint> liste){
        EfficiencyController.getTableLines().clear();
        EfficiencyController.getTableLines().addAll(liste);
        XYChart.Series<String, Double> prodSerie = new XYChart.Series<String, Double>();
        prodSerie.setName("Production (kwh)");
        for (int i=0; i<liste.size(); i++){
            prodSerie.getData().add(new XYChart.Data<String, Double>(liste.get(i).getDate(), liste.get(i).getValue()));
        }
        EfficiencyController.getPeriodStringProperty().setValue("Periode : "+ period);
        EfficiencyController.getScaleStringProperty().setValue("Echelle : " + echelleString);
        EfficiencyController.getGraphData().clear();
        EfficiencyController.getGraphData().addAll(prodSerie);
        EfficiencyController.getActualEfficiencyStringProperty().setValue("Rendement reel : " + df.format(EfficiencyController.computeActualWindEfficiency())+" kwh/jour");

    }

    /**
     * Cette méthode permet de rajouter un certain nombre de jour à une date
     * @return La date mofifiée avec les jours en plus
     */
    public static Date addDays(Date date, int days){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return new Date(c.getTimeInMillis());
    }

    /**
     * Cette méthode retourne le dernier jour d'un mois donné en fonction d'une date.
     */
    public static Date getLastDayMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int d = cal.getActualMaximum(Calendar.DATE);
        int m = cal.get(Calendar.MONTH);
        int y = cal.get(Calendar.YEAR);
        cal.set(y, m, d, 0, 0);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * Cette méthode retourne le dernier jour d'une année en fonction d'une date.
     */
    public static Date getLastDayYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int y = cal.get(Calendar.YEAR);
        cal.set(y, Calendar.DECEMBER, 31, 0, 0);
        return new Date(cal.getTimeInMillis());
    }

    /**
     * classe comparteur qui permet de comparer afin de trier les éléments dans une liste de DataSupplyPoint en fonction de la date
     */
    private class SortDataByDate implements Comparator<DataSupplyPoint>
    {
        @Override
        public int compare(DataSupplyPoint o1, DataSupplyPoint o2) {
            return Date.valueOf(o1.getDate()).compareTo(Date.valueOf(o2.getDate()));
        }
    }

}
