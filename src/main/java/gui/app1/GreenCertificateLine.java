package gui.app1;

/**
 * Cette classe représente le certificat vert.
 */
public class GreenCertificateLine {

    private String id, ean, askingDate, validationDate, amount, supplier, client, lastDate;

    public GreenCertificateLine(String id, String ean, String askingDate, String validationDate, String amount){
        this.id = id;
        this.ean = ean;
        this.askingDate = askingDate;
        this.validationDate = validationDate;
        this.amount = amount;
    }

    public GreenCertificateLine(String id, String ean, String lastDate, String client, String supplier, String amount){
        this.id = id;
        this.ean = ean;
        this.lastDate = lastDate;
        this.client = client;
        this.supplier = supplier;
        this.amount = amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getEan() {
        return ean;
    }

    public String getAskingDate() {
        return askingDate;
    }

    public String getValidationDate() {
        return validationDate;
    }

    public String getAmount() {
        return amount;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getClient() {
        return client;
    }

    public String getLastDate() {
        return lastDate;
    }
}
