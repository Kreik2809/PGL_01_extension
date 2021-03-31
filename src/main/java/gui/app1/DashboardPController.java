package gui.app1;

import gui.CustomListCell;
import gui.Language;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import tool.HttpRequest;
import tool.Mail;
import tool.ParseJson;
import tool.URLDB;
import user.EnergyConsumer;
import user.EnergyPortfolio;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * La classe DashboardPController est une classe utilitaire qui contrôle tous les éléments graphiques
 * de la page de gestion des portefeuilles.
 */
public class DashboardPController implements Initializable {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button createButton;

    @FXML
    private Button notifButton;

    @FXML
    private ListView<EnergyPortfolio> portfolios;

    //Arraylist contenant les portefeuilles à afficher dans la listview
    private static ObservableList<EnergyPortfolio> items = FXCollections.observableArrayList();

    private EnergyPortfolio currentP;


    @Override
    public void initialize(URL url, ResourceBundle rb){
        checkProduction(); // On vérifie si il n'a pas produit suffisament que pour demander un certificat vert
        //On vérifie si il n'a pas de notification de supression.
        ArrayList<String> l= new ArrayList<>();
        String[] tab = {"USERNAME", "EAN"};
        try{
            l= HttpRequest.Get(URLDB.URL + "DelNotif/USERNAME=" + GuiHandler.getGui().getCurrentUser().getName(), new ParseJson(tab, "NO del Notif FOR THIS parameters", "") {});
        }catch (IOException e){
            e.printStackTrace();
        }
        if (l.size()>0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data Suppression");
            alert.setHeaderText("Votre fournisseur a supprimé des données pour certains de vos compteurs");
            alert.setContentText(null);
            alert.showAndWait();
            String ean ="";
            for (int i=0; i<l.size(); i+=2){
                try{
                    ean = l.get(i+1);
                    HttpRequest.Post(URLDB.URL + "DelNotif/?USERNAME="+GuiHandler.getGui().getCurrentUser().getName()+"&EAN="+ean+"&ACTION=delete");
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        items.clear();
        portfolios.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handlePortfolioClick();
            }
        });
        //On charge les portefeuilles de l'utilisateur
        EnergyConsumer u = GuiHandler.getGui().getCurrentUser();
        u.fetchAccesLevel();
        ArrayList<EnergyPortfolio> list = GuiHandler.getGui().getCurrentUser().getPortfolios();
        showPortfolios(list);
    }



    /**
     * Cette méthode va vérifier si l'un des compteurs de production de l'utilisateur à dépasser un seuil de production.
     * Ici 1000 kwh.
     */
    public void checkProduction(){
        String[] tab = {"NAME", "EAN18", "ENERGYTYPE", "DAYSTART", "STYPE"};
        ArrayList<String> l = new ArrayList<>();
        try {
            l = HttpRequest.Get(URLDB.URL + "MetersU/USERNAME=" + GuiHandler.getGui().getCurrentUser().getName(), new ParseJson(tab, "NO METER FOR THIS USERNAME", "PLEASE USE THIS SYNTAX : /RestAPI/MetersU/USERNAME='username'") {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0 ; i < l.size() ; i+=5)
        {
            String[] tab2 = {"DATE", "VALUE"};
            ArrayList<String> l2 = new ArrayList<>();
            if (l.get(i+4).equals("PRODUCTION")){
                String ean = l.get(i+1);
                try{
                    l2 = HttpRequest.Get(URLDB.URL+"MeterData/EAN="+ean, new ParseJson(tab2, "NO DATA FOR THIS METER", "PLEASE USE THIS SYNTAX : /RestAPI/MeterData/EAN=ean18") {});
                }catch (IOException e){
                    e.printStackTrace();
                }
                String lastCertif = GuiHandler.getLastCertifDate(ean);
                Double prod = 0.0;
                if (!lastCertif.equals("Pas de certificat")){
                    for (int j=0; j<l2.size(); j+=2){
                        if(Date.valueOf(l2.get(j)).compareTo(Date.valueOf(lastCertif)) > 0 && Date.valueOf(l2.get(j)).compareTo(Date.valueOf(l.get(i+3))) >= 0){
                            prod = prod + Double.parseDouble(l2.get(j+1));
                        }
                    }
                }
                else{
                    for (int j=0; j<l2.size(); j+=2){
                        if(Date.valueOf(l2.get(j)).compareTo(Date.valueOf(l.get(i+3))) >= 0){
                            prod = prod + Double.parseDouble(l2.get(j+1));
                        }
                    }
                }
                if (prod >= 1000){
                    String subject = "Certificat vert";
                    String content = "Bonjour,\nVotre compteur dont le code ean est : " + ean + " a produit " + prod.toString() + " kwh.\nVous pouvez donc effectuer une demande de certificat vert.";
                    String adressMailTo = GuiHandler.getGui().getCurrentUser().getMail();
                    Mail.sendMail(subject, content, adressMailTo);
                }

            }


        }
    }

    public void handlePortfolioClick(){
        if ((currentP=portfolios.getSelectionModel().getSelectedItem())!=null){
            GuiHandler.getGui().setCurrentPortfolio(currentP);
            GuiHandler.loadScene("app1/dashboard_compteurs.fxml", 1100, 650, this);
        }
    }

    /**
     * Cette méthode personnalise et affiche les portefeuilles dans la liste view de la fenêtre.
     * @param l La liste des portefeuilles à afficher
     */
    public void showPortfolios(ArrayList<EnergyPortfolio> l){
        items.addAll(l);
        portfolios.setItems(items);
        portfolios.setCellFactory(new Callback<ListView<EnergyPortfolio>, ListCell<EnergyPortfolio>>() {
            @Override
            public ListCell<EnergyPortfolio> call(ListView<EnergyPortfolio> listView) {
                return new CustomListCell<>(GuiHandler.getGui().getCurrentUser());
            }
        });
    }

    /**
     * Cette méthode s'exécute lorsque le bouton "créer un portefeuille" est cliqué.
     * Elle lance la fenêtre de création d'un portfeuille.
     * @param event
     */
    @FXML
    void createClicked(MouseEvent event) {
        GuiHandler.setCp(((((Button)event.getSource()).getScene().getRoot())));
        GuiHandler.getCp().setDisable(true);
        GuiHandler.createScene("app1/createPortfolios.fxml",
                "Nouveau porteffeuille", 300, 150, this);
    }

    @FXML
    void notifClicked(MouseEvent event) {

        GuiHandler.setCp(((((Button)event.getSource()).getScene().getRoot())));
        GuiHandler.getCp().setDisable(true);


        GuiHandler.createScene("app1/notifications.fxml", "Vos notifications", 600, 500, this);
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

    public static ObservableList<EnergyPortfolio> getItems() {
        return items;
    }
}
