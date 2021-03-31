package user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

/**
 * La classe SupplyPoint représente un point d'approvisionnement/un compteur dans l'application de gestion de portefeuilles.
 */
@JsonIgnoreProperties({ "supplyPointName", "energyPortfolio", "contract", "dataSupplyPoints", "state", "name",
        "openingDate", "closingDate", "id", "count", "specialIndicator"})
@JsonPropertyOrder({ "EAN18", "EnergyType", "DataSupplyPoints" })

public class SupplyPoint
{
    public static final boolean OPEN = true, CLOSE = false;

    private EnergyType energyType;// Type d'énergie fourni
    private String EAN18;// Code EAN18 (unique)
    private String supplyPointName;// Le nom du compteur/point d'approvisionnement
    private EnergyPortfolio energyPortfolio;// Le portefeuille énergétique lié
    private Contract contract;// Le contrat lié à ce SupplyPoint
    private ArrayList<DataSupplyPoint> dataSupplyPoints = new ArrayList<>();// Liste des DataSupplyPoints liées au SupplyPoint (à afficher)
    private ArrayList<DataSupplyPoint> allDataSupplyPoints = new ArrayList<>();// Liste des DataSupplyPoints liées au SupplyPoint
    private boolean state;// Etat du compteur
    private String name;
    private String openingDate="";
    private String closingDate="";
    private int ID;
    private static int count = 0;
    private SupplyPointType supplyPointType;


    private String specialIndicator=""; //Cette String permet de forcer la mise à jour de l'affichage des invitations dans une listView

    /**
     * Ce constructeur est utilisé pour représenter les compteurs dans l'application de gestion de portefeuilles électriques
     */
    public SupplyPoint(String name, EnergyType energyType,String EAN18,EnergyPortfolio energyPortfolio) throws EAN18Exception
    {
        if(EAN18.length() != 18)
            throw new EAN18Exception("WRONG EAN18 FORMAT : max 8 numbers (" + EAN18.length() + " numbers)");
        this.energyType = energyType;
        this.EAN18 = EAN18;
        this.energyPortfolio = energyPortfolio;
        this.name = name;
        this.ID= count;
        state = OPEN;
        count ++;
    }

    /**
     * Ce constructeur est utilisé pour représenter les compteurs dans l'application de facilitation de liens avec
     * le fournisseur
     */
    public SupplyPoint(String name, EnergyType energyType, String EAN18, String openingDate){
        this.name = name;
        this.energyType = energyType;
        this.EAN18 = EAN18;
        this.openingDate = openingDate;
        this.ID = count;
        count ++;
    }

    /**
     * Ce constructeur est utilisé pour représenter les compteurs dans l'application de facilitation de liens avec
     * le fournisseur
     */
    public SupplyPoint(String name, EnergyType energyType, String EAN18, String openingDate, String closingDate){
        this.name = name;
        this.energyType = energyType;
        this.EAN18 = EAN18;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        if(closingDate.equals(""))
            state = true;
        this.ID = count;
        count ++;
    }

    @JsonCreator
    public SupplyPoint(@JsonProperty("EAN18") String EAN18, @JsonProperty("EnergyType") EnergyType energyType, @JsonProperty("DataSupplyPoints") ArrayList<DataSupplyPoint> allDataSupplyPoints) {
        // no argument constructor required by Jackson
        this.EAN18 = EAN18;
        this.energyType = energyType;
        this.allDataSupplyPoints = allDataSupplyPoints;
    }

    public static class EAN18Exception extends Exception
    {
        /**
         * Constructs a new exception with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public EAN18Exception(String message)
        {
            super(message);
        }
    }

    /**
     * Cette méthode exécute une requête http au serveur en vue de récupérer les données de consommation du compteur
     */
    public void fetchData()
    {
        fetchContract();
        allDataSupplyPoints.clear();
        dataSupplyPoints.clear();
        String[] tab = {"DATE", "VALUE"};
        ArrayList<String> liste = new ArrayList<>();
        try{
            liste = HttpRequest.Get(URLDB.URL+"MeterData/EAN="+getEAN18(), new ParseJson(tab, "NO DATA FOR THIS METER", "PLEASE USE THIS SYNTAX : /RestAPI/MeterData/EAN=ean18") {});
        }catch (IOException e){
            e.printStackTrace();
        }
        if (liste.size() != 0) {
            for (int i = 0; i < liste.size(); i += 2) {
                if(contract != null) {
                    boolean dateSup = (Date.valueOf(liste.get(i)).compareTo(Date.valueOf(this.getOpeningDate())) >= 0); // vérifie si date d'ouverture < date de la donnée
                    if (dateSup) {
                        dataSupplyPoints.add(new DataSupplyPoint(liste.get(i ), Double.parseDouble(liste.get(i + 1)), this));
                        allDataSupplyPoints.add(new DataSupplyPoint(liste.get(i ), Double.parseDouble(liste.get(i + 1)), this));
                    }
                }
            }
        }
    }

    /**
     * Cette méthode exécute une requête http en vue de récupérer les données du contrat de ce compteur
     */
    public void fetchContract()
    {
        String[] tab = {"EAN", "ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};
        ArrayList<String> liste = new ArrayList<>();
        try{
            liste = HttpRequest.Get(URLDB.URL + "Contracts/EAN="+getEAN18(), new ParseJson(tab, "NO CONTRACT FOR THIS ID", "PLEASE USE THIS SYNTAX : /RestAPI/Contracts/CONTRACT_ID=xx") {});

        }catch (IOException e){
            e.printStackTrace();
        }
        if(liste.size() != 0 && !liste.get(0).equals("NO CONTRACT FOR THIS ID")) {

            String cID = liste.get(1);
            String energySupplierID = liste.get(2);
            String energyConsumerID = liste.get(3);
            String type = liste.get(4);
            Date date = Date.valueOf(liste.get(5));

            setContract(new Contract(cID, new EnergySupplier(energySupplierID, ""), new EnergyConsumer(energyConsumerID, ""), type, date));
        }
    }

    public String getSupplyPointName()
    {
        return supplyPointName;
    }

    public void setSupplyPointName(String supplyPointName)
    {
        this.supplyPointName = supplyPointName;
    }

    @JsonProperty("EAN18")
    public String getEAN18()
    {
        return EAN18;
    }

    @JsonProperty("EnergyType")
    public EnergyType getEnergyType()
    {
        return energyType;
    }

    @JsonProperty("DataSupplyPoints")
    public ArrayList<DataSupplyPoint> getAllDataSupplyPoints() {
        return allDataSupplyPoints;
    }

    public EnergyPortfolio getEnergyPortfolio()
    {
        return energyPortfolio;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public ArrayList<DataSupplyPoint> getDataSupplyPoints() {
        return dataSupplyPoints;
    }

    public void addDataSupplyPoints(ArrayList<DataSupplyPoint> dataSupplyPoints){
        String[] tab = {"DATE", "VALUE"};
        ArrayList<String> res = new ArrayList<>();
        try {
            res = HttpRequest.Get(URLDB.URL + "MeterData/EAN=" + getEAN18(), new ParseJson(tab, "NO DATA FOR THIS METER", "PLEASE USE THIS SYNTAX : /RestAPI/MeterData/EAN=ean18") {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<DataSupplyPoint> l1 = new ArrayList<>();
        ArrayList<DataSupplyPoint> toAdd = new ArrayList<>();
        ArrayList<DataSupplyPoint> toModify = new ArrayList<>();
        for(int i = 0 ; i < res.size() ; i+=2)
        {
            l1.add(new DataSupplyPoint(res.get(i), Double.valueOf(res.get(i+1))));
        }
        for(int i = 0 ; i < dataSupplyPoints.size(); i++)
        {
            String request = "";
            if(l1.contains(dataSupplyPoints.get(i))) {
                request = URLDB.URL + "MeterData/?EAN=" +getEAN18() + "&VALUE=" + dataSupplyPoints.get(i).getValue() + "&OLDDATE=" + l1.get(l1.indexOf(dataSupplyPoints.get(i))).getDate() + "&DATE=" + dataSupplyPoints.get(i).getDate() + "&ACTION=modify";
                try {
                    HttpRequest.Post(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                request = URLDB.URL + "MeterData/?EAN=" +getEAN18() + "&VALUE=" + dataSupplyPoints.get(i).getValue() + "&DATE=" + dataSupplyPoints.get(i).getDate() + "&ACTION=add";
                try {
                    HttpRequest.Post(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setDataSupplyPoints(ArrayList<DataSupplyPoint> dataSupplyPoints) {

        this.dataSupplyPoints = dataSupplyPoints;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public String getSpecialIndicator() {
        return specialIndicator;
    }

    public void setSpecialIndicator(String specialIndicator) {
        this.specialIndicator = specialIndicator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(String closingDate) {
        this.closingDate = closingDate;
    }

    public void setAllDataSupplyPoints(ArrayList<DataSupplyPoint> allDataSupplyPoints) {
        this.allDataSupplyPoints = allDataSupplyPoints;
    }

    public void setSupplyPointType(SupplyPointType supplyPointType) {
        this.supplyPointType = supplyPointType;
    }

    public SupplyPointType getSupplyPointType() {
        return supplyPointType;
    }
}
