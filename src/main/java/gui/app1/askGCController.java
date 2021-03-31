package gui.app1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tool.HttpRequest;
import tool.URLDB;

import java.io.IOException;

/**
 * Le controller de la fenêtre de demande de certificat vert.
 */
public class askGCController {
    @FXML
    private TextField amountField;

    /**
     * Cette méthode est appelée lorsqu'un client effectue une demande de certificat vert.
     * @param event
     */
    @FXML
    void continueClicked(MouseEvent event) {
        GuiHandler.getGui().getCurrentSupplyPoint().fetchContract();
        if (amountField.getText()!=null && GuiHandler.getGui().getCurrentSupplyPoint().getContract() != null){
            String amount = amountField.getText();
            String ean = GuiHandler.getGui().getCurrentSupplyPoint().getEAN18();
            String rid = "authority";
            try{
                HttpRequest.Post(URLDB.URL + "GreenCertificateByEAN/?EAN=" + ean + "&VALUE="+amount + "&REGIONALID="+rid);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        GuiHandler.getCp().setDisable(false);
        GuiHandler.setCp(null);
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();


    }
}
