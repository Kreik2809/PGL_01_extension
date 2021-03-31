package gui.app1;

import gui.Controller;
import gui.Language;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import tool.*;
import tool.sql.*;
import user.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Date;
import java.util.ArrayList;

/**
 * La classe GuiHandler est une classe utilitaire qui fait le lien entre les classes Controller et les objets Gui.
 * Elle permet de transporter les informations de l'une à l'autre.
 */
public class GuiHandler {

    private static Gui gui;

    public static Parent cp, secondCp; //Ces deux objet Parent sont utilisés pour freeze les fenêtres.

    public static Controller lastController;

    /**
     * La méthode loadWindow permet de charger une nouvelle fenêtre et de remplacer la fenêtre courante.
     * @param fxmlFile Le chemin vers le fichier fxml de la nouvelle fenêtre
     * @param width La largeur de la nouvelle fenêtre
     * @param height La hauteur de la nouvelle fenêtre
     * @param main mettre "this", objet à partir duquel on va aller chercher les ressources
     */
    public static void loadScene(String fxmlFile, int width, int height, Object main) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(new File("src/main/resources/" + fxmlFile).toURI().toURL());
            Parent root = fxmlLoader.load();
            gui.getPrimaryStage().setScene(new Scene(root, width, height));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(main.getClass().getClassLoader().getResource(fxmlFile));
                Parent root = fxmlLoader.load();
                gui.getPrimaryStage().setScene(new Scene(root, width, height));
            } catch (IOException ioException) {
                System.out.println("Erreur IO lors du chargement d'une page");
                ioException.printStackTrace();
            }


        }
    }

    /**
     * La méthode createScene crée une nouvelle fenêtre.
     * @param fxmlFile Le chemin vers le fichier fxml de la nouvelle fenêtre
     * @param name Le nom de la nouvelle fenêtre
     * @param width La largeur de la nouvelle fenêtre
     * @param height La hauteur de la nouvelle fenêtre
     * @param main mettre "this", objet à partir duquel on va aller chercher les ressources
     */
    public static void createScene(String fxmlFile, String name, int width, int height, Object main){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader((new File("src/main/resources/" + fxmlFile).toURI().toURL()));
            Parent root = fxmlLoader.load();
            Stage popupWindow = new Stage();
            popupWindow.setOnCloseRequest(event -> {
                if (getCp() != null){
                    getCp().setDisable(false);
                    setCp(null);
                }
                if (getSecondCp() != null) {
                    getSecondCp().setDisable(false);
                    setSecondCp(null);
                }
            });;
            popupWindow.setTitle(name);
            popupWindow.setScene(new Scene(root, width, height));
            popupWindow.show();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(main.getClass().getClassLoader().getResource(fxmlFile));
                Parent root = fxmlLoader.load();
                Stage popupWindow = new Stage();
                popupWindow.setOnCloseRequest(event -> {
                    if (getCp() != null){
                        getCp().setDisable(false);
                        setCp(null);
                    }
                    if (getSecondCp() != null) {
                        getSecondCp().setDisable(false);
                        setSecondCp(null);
                    }

                });;
                popupWindow.setTitle(name);
                popupWindow.setScene(new Scene(root, width, height));
                popupWindow.show();
            } catch (IOException ioException) {
                System.out.println("Erreur IO lors du chargement d'une page");
                ioException.printStackTrace();
            }
        }
    }

    /**
     * La méthode changeLanguage s'occupe de modifier la langue de l'interface graphique
     * @param l La nouvelle langue souhaitée par l'utilisateur
     */
    public static void changeLanguage(Language l){
        //On change la langue de la GUI
    }

    /**
     * Cette méthode permet d'ajout à la liste des portefeuille présents sur la fenêtre DBPortefeuille un nouveau portefeuille
     * @param e Le portefeuille à ajouter.
     */
    public static void updatePortfolios(EnergyPortfolio e){
        boolean alreadyMember = false;
        for (AccessLevel a : getGui().getCurrentUser().getAccessLevels()){
            if (a.getEnergyPortfolio().getID().equals(e.getID())){
                alreadyMember = true;
            }

        }
        if (!alreadyMember){
            DashboardPController.getItems().add(e);
        }
    }

    /**
     * Cette méthode est appelée lorsque l'utilisateur désire créer un portefeuille.
     * Elle s'occupe de la création ainsi que de la mise à jour de la liste des portefeuilles dans l'interface graphique.
     * @param Pname Le nom du portefeuille
     */
    public static void createPortfolio(String Pname){
        String username = GuiHandler.getGui().getCurrentUser().getName();
        String id = null;
        String[] tab = {"ID"};
        try {
            id = HttpRequest.Get(URLDB.URL + "PortfolioID/", new ParseJson(tab, "ERROR IN GENERATION", "PLEASE USE THIS SYNTAX : /RestAPI/PortfolioID/") {}).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EnergyPortfolio e = new EnergyPortfolio(GuiHandler.getGui().getCurrentUser(),Pname, id);
        DashboardPController.getItems().add(e);
        try{
            HttpRequest.Post(URLDB.URL + "Portfolios/?USERNAME="+username+"&PORTFOLIO_ID="+id+"&ACCESS=MANAGING&PNAME="+Pname);

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Cette méthode est appelée lorsque qu'un utilisateur désire ajouter un compteur à son portfeuille.
     */
    public static void addSupplyPoint(String name, String ean, String type, String supplyPointType){
        boolean alreadyIn = false;
        EnergyType t = EnergyType.valueOf(type);
        try{
            for( SupplyPoint supplyPoint : GuiHandler.getGui().getCurrentPortfolio().getSupplyPoints())
            {
                if(supplyPoint.getEAN18().equals(ean))
                {
                    alreadyIn = true;
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR !");
                    alert.setHeaderText(null);
                    alert.setContentText("YOUR SUPPLYPOINT IS ALREADY CREATED AND IN YOUR PORTFOLIO");
                    alert.showAndWait();
                }
            }
            if (!alreadyIn) {
                if (ean.length() > 18)
                    throw new SupplyPoint.EAN18Exception("");
                Date date = DB.dateToday();
                String[] tab = {"EAN", "ENERGYTYPE", "NAME", "STATE", "CID", "DAYSTART", "SID", "TYPE"};
                ArrayList<String> infos = new ArrayList<>();
                try {
                    infos = HttpRequest.Get(URLDB.URL + "MeterByEAN/EAN=" + ean, new ParseJson(tab, "NO METER FOR THIS EAN", "PLEASE USE THIS SYNTAX : /RestAPI/MeterByEAN/EAN=xx") {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (infos.size() != 0) // le compteur existe dans la DB
                {
                    SupplyPointType sType = SupplyPointType.valueOf(infos.get(7));
                    String contractID = infos.get(4);
                    String stateStr = infos.get(3);
                    boolean state = false;
                    if (stateStr.equals("1"))
                        state = true;
                    String dStart = infos.get(5);
                    if (contractID.equals("-1")) // pas encore de contract
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("ERROR CONTRACT !");
                        alert.setHeaderText(null);
                        alert.setContentText("YOUR SUPPLYPOINT IS ALREADY CREATED BUT PLEASE WAIT FOR YOUR SUPPLIER");
                        alert.showAndWait();
                    } else {
                        try {
                            String[] row = {"EAN", "ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};
                            infos = HttpRequest.Get(URLDB.URL + "Contracts/EAN=" + ean, new ParseJson(row, "NO CONTRACT FOR THIS ID", "PLEASE USE THIS SYNTAX : /RestAPI/Contracts/CONTRACT_ID=xx") {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (infos.get(3).equals(GuiHandler.getGui().getCurrentUser().getName()))// vérifie si le contract est bien au nom du USER courant
                        {
                            try {
                                //On met à jour la DB avec le dernier nom associé
                                HttpRequest.Post(URLDB.URL + "MeterByEAN/?EAN=" + ean + "&ENERGYTYPE=" + t.toString() + "&NAME=" + HttpRequest.refactorString(name) + "&STATE=1" + "&ACTION=modify");
                                //On ajoute le compteur au portefeuille
                                HttpRequest.Post(URLDB.URL + "Meters/?PORTFOLIO_ID=" + GuiHandler.getGui().getCurrentPortfolio().getID() + "&EAN18=" + ean + "&ACTION=add");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            gui.getCurrentPortfolio().addSupplyPoint(name, t, ean, infos.get(1), sType, dStart, state);
                            GestionPController.updateMeters();
                            DashboardMController.updateMeters();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("OK");
                            alert.setHeaderText(null);
                            alert.setContentText("YOUR SUPPLYPOINT IS CREATED");
                            alert.showAndWait();
                        } else // le contract n'est pas au bon nom d'utilisateur
                        {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR CONTRACT !");
                            alert.setHeaderText(null);
                            alert.setContentText("PLEASE ADD YOUR OWN METER !");
                            alert.showAndWait();
                        }
                    }
                } else // le compteur n'existe pas
                {
                    try {
                        HttpRequest.Post(URLDB.URL + "MeterByEAN/?EAN=" + ean + "&NAME=" + HttpRequest.refactorString(name) + "&ENERGYTYPE=" + t.toString() + "&TYPE=" + supplyPointType + "&ACTION=add");
                        HttpRequest.Post(URLDB.URL + "Meters/?PORTFOLIO_ID=" + GuiHandler.getGui().getCurrentPortfolio().getID() + "&EAN18=" + ean + "&ACTION=add");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    SupplyPointType sType = SupplyPointType.valueOf(supplyPointType);
                    gui.getCurrentPortfolio().addSupplyPoint(name, t, ean, "-1", sType, "-1", false);
                    GestionPController.updateMeters();
                    DashboardMController.updateMeters();
                }
            }
        } catch (SupplyPoint.EAN18Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR EAN !");
            alert.setHeaderText(null);
            alert.setContentText("PLEASE VERIFY YOUR EAN18 CODE !");
            alert.showAndWait();
        }
    }

    /**
     * Cette méthode se charge d'effectuer toutes les modifications dans la structure d'objet afin de supprimer le portefeuille
     * @param e Le portefeuille à supprimer
     */
    public static void deletePortfolio(EnergyPortfolio e){
        // Pour chaque utilisateur du portefeuille, on lui retire l'accès au portefeuille.
        for (EnergyConsumer user : e.getEnergyConsumer()){
            e.removeAccess(user);
        }
    }

    /**
     * Cette méthode retourne la date du dernier certificat vert confirmé pour un certain compteur
     */
    public static String getLastCertifDate(String ean){
        String str="";
        String[] tab3 = {"EAN", "ID", "REGIONALID", "VALUE","ASKINGDATE", "VALIDATIONDATE"};
        ArrayList<String> l3 = new ArrayList<>();
        try {
            l3 = HttpRequest.Get(URLDB.URL + "GreenCertificateByEAN/EAN=" + ean, new ParseJson(tab3, "No certificate for this ean", "") {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<GreenCertificateLine> previousGC = new ArrayList<>();
        for (int j = 0; j < l3.size(); j += 6) {
            if (!l3.get(j+5).equals("-1"))  //On vérifie que le certificat a été validé.
                previousGC.add(new GreenCertificateLine(l3.get(j), l3.get(j + 1), l3.get(j + 4), l3.get(j + 5), l3.get(j + 3)));
        }
        if (previousGC.size() > 0) {
            GreenCertificateLine closestGC = previousGC.get(0);

            for (GreenCertificateLine g : previousGC) {
                if (Date.valueOf(g.getAskingDate()).compareTo(Date.valueOf(closestGC.getAskingDate())) > 0) {
                    closestGC = g;
                }
            }
            str = closestGC.getAskingDate();
        } else {
            str = "Pas de certificat";
        }
        return str;
    }

    public static void setGui(Gui gui){
        GuiHandler.gui = gui;
    }

    public static Gui getGui(){return gui;}

    public static Parent getCp() {
        return cp;
    }

    public static void setCp(Parent cp) {
        GuiHandler.cp = cp;
    }

    public static Parent getSecondCp() {
        return secondCp;
    }

    public static void setSecondCp(Parent secondCp) {
        GuiHandler.secondCp = secondCp;
    }

    public static Controller getLastController() {
        return lastController;
    }

    public static void setLastController(Controller lastController) {
        GuiHandler.lastController = lastController;
    }
}
