package gui.app2;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Date;


/**
 * La classe GuiHandler est une classe utilitaire qui fait le lien entre les classes Controller et les objets Gui.
 * Elle permet de transporter les informations de l'une à l'autre.
 */
public class GuiHandler {

    private static Date date;
    private static Gui gui;
    private static String ContractID;

    public static Parent cp;

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

    public static Gui getGui() {
        return gui;
    }

    public static void setGui(Gui gui) {
        GuiHandler.gui = gui;
    }

    public static Parent getCp() {
        return cp;
    }

    public static void setCp(Parent cp) {
        GuiHandler.cp = cp;
    }

    public static Date getDate() {
        return date;
    }

    public static void setDate(Date date) {
        GuiHandler.date = date;
    }

    public static String getContractID() {
        return ContractID;
    }

    public static void setContractID(String contractID) {
        ContractID = contractID;
    }
}
