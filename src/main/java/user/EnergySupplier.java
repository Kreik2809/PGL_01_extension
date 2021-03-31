package user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gui.app2.GuiHandler;
import javafx.scene.control.Alert;
import tool.AccessLevelType;

import java.io.*;
import java.nio.file.Files;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Objects;

import tool.HttpRequest;
import tool.ParseJson;
import tool.URLDB;
import tool.sql.DB;

/**
 * classe qui représente un fournisseur d'énergie
 */
public class EnergySupplier extends User
{
    /**
     * constructeur
     * @param name le nom
     * @param password le mot de passe
     */
    public EnergySupplier(String name, String password)
    {
        super(name, password);
    }

    /**
     * constructeur
     * @param name le nom
     * @param password le mot de passe
     * @param email l'adresse mail
     */
    public EnergySupplier(String name, String password, String email)
    {
        super(name, password, email);
    }

    /**
     * méthode qui permet d'ouvrir un compteur
     * @param meter le compteur
     * @param client le nom d'utilisateur du client
     * @param type le type de contrat
     * @param contractID l'id du contrat (ou null si il faut générer un contrat)
     * @param newID true si nouveau contrat, false sinon
     */
    public void openCounter(SupplyPoint meter, String client, String type, String contractID, boolean newID)
    {
        GuiHandler.setContractID(contractID);
        String date = "";
        String[] tab2 = {"DATE"};
        try {
            date = HttpRequest.Get(URLDB.URL + "Date/", new ParseJson(tab2,"ERROR DATE", "PLEASE USE THIS SYNTAX : /RestAPI/Date/today") {}).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GuiHandler.setDate(Date.valueOf(date));
        if(newID)
        {
            String[] tab = {"ID"};
            try {
                contractID = HttpRequest.Get(URLDB.URL + "ContractID/", new ParseJson(tab, "ERROR IN GENERATION", "PLEASE USE THIS SYNTAX : /RestAPI/ContractID/") {}).get(0);
                GuiHandler.setContractID(contractID);
                HttpRequest.Post(URLDB.URL + "Contracts/?ID=" + contractID + "&ENERGY_SUPPLIER_ID=" + getName() + "&CONSUMER_ID=" + client + "&TYPE=" + type + "&START=" + date + "&ACTION=addByID");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            String[] tab3 = {"EAN", "NAME","ENERGYTYPE", "ID", "ENERGY_SUPPLIER_ID", "CONSUMER_ID", "TYPE", "START"};
            ArrayList<String> liste = new ArrayList<>();
            try {
                liste = HttpRequest.Get(URLDB.URL + "ContractsID/ID=" + contractID, new ParseJson(tab3, "NO CONTRACT FOR THIS ID", "PLEASE USE THIS SYNTAX : /RestAPI/ContractsSupplier/ID==xx") {});
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(liste.size() != 0) {
                date = liste.get(7);
                try {
                    HttpRequest.Post(URLDB.URL + "Contracts/?ID=" + contractID + "&ENERGY_SUPPLIER_ID=" + getName() + "&CONSUMER_ID=" + client + "&TYPE=" + type + "&START=" + date + "&ACTION=modify&NEWID=" + contractID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (date.equals(""))
                    GuiHandler.setDate(Date.valueOf(date));
            }
        }
        String[] tab = {"DATE"};
        try{
            date = HttpRequest.Get(URLDB.URL + "Date/today", new ParseJson( tab, "ERROR DATE", "PLEASE USE THIS SYNTAX : /RestAPI/Date/today") {}).get(0);
        } catch (IOException e){
            e.printStackTrace();
        }
        String request = URLDB.URL + "AllMeters/?EAN18=" + meter.getEAN18() + "&ENERGYTYPE=" + meter.getEnergyType().name() + "&NAME=" + meter.getName() + "&STATE=1&CONTRACTID=" + contractID + "&DAYSTART=" + date + "&SUPPLIERID=" + getName() + "&ACTION=modify";
        try {
            HttpRequest.Post(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * méthode qui permet de fermer un compteur
     * @param meter le compteur
     */
    public void closeCounter(SupplyPoint meter)
    {
        meter.fetchContract();
        addHistorySupplyPoint(meter);
        String request = URLDB.URL + "AllMeters/?EAN18=" + meter.getEAN18() + "&ENERGYTYPE=" + meter.getEnergyType().name() + "&NAME=" + meter.getName() + "&STATE=0&CONTRACTID=-1&DAYSTART=-1&SUPPLIERID=-1&ACTION=modify";
        try {
            HttpRequest.Post(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> res = new ArrayList<>();
        try {
            String[] tab = {"EAN18", "ENERGYTYPE", "NAME", "STATE", "CONTRACTID", "DAYSTART", "SUPPLIERID"};
            res = HttpRequest.Get(URLDB.URL + "AllMeters/", new ParseJson(tab, "NO METERS", "PLEASE USE THIS SYNTAX : /RestAPI/AllMeters/") {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean empty = true;
        for(int i = 0 ; i < res.size() ; i+= 7)
        {
            if(res.get(i+4).equals(meter.getContract().getId()))
            {
                empty = false;
                break;
            }
        }
        if(empty)
        {
            try {
                HttpRequest.Post(URLDB.URL + "Contracts/?ID=" + meter.getContract().getId() + "&ACTION=delete");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * méthode qui permet de rajouter un compteur desormait fermé à la liste des historiques compteurs dans la DB
     * @param supplyPoint
     */
    private void addHistorySupplyPoint(SupplyPoint supplyPoint)
    {
        supplyPoint.getState();
        String date = "";
        String[] tab2 = {"DATE"};
        try {
            date = HttpRequest.Get(URLDB.URL + "Date/", new ParseJson(tab2,"ERROR DATE", "PLEASE USE THIS SYNTAX : /RestAPI/Date/today") {}).get(0);
            HttpRequest.Post(URLDB.URL + "HistorySupplyPoint/?EAN=" + supplyPoint.getEAN18() + "&USERNAME="+supplyPoint.getContract().getEnergyConsumer().getName() + "&OPEN=" + supplyPoint.getOpeningDate() + "&CLOSE=" +date + "&ACTION=add" );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * Exporte les données de SupplyPoint(s) dans un fichier CSV.
     *
     * @param supplyPoints La liste de SupplyPoint(s) dont les données vont être exportés.
     */
    public void exportDataSupplyPointInCSV(ArrayList<SupplyPoint> supplyPoints, String fileName){
        FileWriter file;
        try {
            file = new FileWriter(datafileDirChecking().resolve(fileName+".csv").toString());
            file.append(HEADER);
            file.append(SEPARATOR);
            for (SupplyPoint supplyPoint : supplyPoints) {
                for (DataSupplyPoint dataSupplyPoint : supplyPoint.getAllDataSupplyPoints()) {
                    file.append(supplyPoint.getEAN18());
                    file.append(DELIMITER);
                    file.append(supplyPoint.getEnergyType().toString());
                    file.append(DELIMITER);
                    file.append(dataSupplyPoint.getDate());
                    file.append(DELIMITER);
                    file.append(String.valueOf(dataSupplyPoint.getValue()));
                    file.append(SEPARATOR);
                }
            }
            file.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Exporte les données de SupplyPoint(s) dans un fichier CSV d'une période donnée.
     *
     * @param supplyPoints  La liste de SupplyPoint(s) dont les données vont être exportés.
     * @param period La période donnée.
     */
    public void exportDataSupplyPointInCSV(ArrayList<SupplyPoint> supplyPoints, String fileName, Period period){
        FileWriter file;
        String startDate = period.getStartDate().toString();
        String endDate = period.getEndDate().toString();
        try {
            file = new FileWriter(datafileDirChecking().resolve(fileName+".csv").toString());
            file.append(HEADER);
            file.append(SEPARATOR);
            for (SupplyPoint supplyPoint : supplyPoints) {
                for (DataSupplyPoint dataSupplyPoint : supplyPoint.getAllDataSupplyPoints()) {
                    if (dataSupplyPoint.getDate().compareTo(startDate) >= 0
                            && dataSupplyPoint.getDate().compareTo(endDate) <= 0) {
                        file.append(supplyPoint.getEAN18());
                        file.append(DELIMITER);
                        file.append(supplyPoint.getEnergyType().toString());
                        file.append(DELIMITER);
                        file.append(dataSupplyPoint.getDate());
                        file.append(DELIMITER);
                        file.append(String.valueOf(dataSupplyPoint.getValue()));
                        file.append(SEPARATOR);
                    }
                }
            }
            file.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Importe les données de SupplyPoint(s) d'un fichier CSV.
     *
     * @param fileName Nom du fichier à importer.
     */
    public ArrayList<SupplyPoint> importDataSupplyPointInCSV(String fileName) {
        ArrayList<SupplyPoint> supplyPointList = new ArrayList<>();
        try {
            for(String line : Files.readAllLines(datafileDirChecking().resolve(fileName + ".csv"))) {
                if (line.equals(HEADER)) continue;
                String[] currentLine = line.split(DELIMITER);
                if (!containsSpecifySupplyPoint(supplyPointList, currentLine[0])) {
                    ArrayList<DataSupplyPoint> newDataSupplyPoints = new ArrayList<>();
                    DataSupplyPoint newDataSupplyPoint = new DataSupplyPoint(currentLine[2], Double.parseDouble(currentLine[3]));
                    newDataSupplyPoints.add(newDataSupplyPoint);
                    SupplyPoint newSupplyPoint = new SupplyPoint(currentLine[0], EnergyType.valueOf(currentLine[1]), newDataSupplyPoints);
                    supplyPointList.add(newSupplyPoint);
                } else {
                    DataSupplyPoint newDataSupplyPoint = new DataSupplyPoint(currentLine[2], Double.parseDouble(currentLine[3]));
                    Objects.requireNonNull(getSpecifySupplyPoint(supplyPointList, currentLine[0])).getAllDataSupplyPoints().add(newDataSupplyPoint);
                }
            }
            setSupplyPointToDataSupplyPoint(supplyPointList);
            return supplyPointList;
        } catch (IOException e) {
            e.printStackTrace();
            return supplyPointList;
        }
    }

    /**
     * méthode qui permet de récuperer un compteur dans une liste de compteur
     * @param supplyPoints la liste de compteurs
     * @param EAN18 le code ean du compteur à trouver
     * @return le compteur
     */
    public static SupplyPoint getSpecifySupplyPoint(ArrayList<SupplyPoint> supplyPoints, String EAN18){
        for (SupplyPoint supplyPoint : supplyPoints){
            if (supplyPoint.getEAN18().equals(EAN18)) return supplyPoint;
        }
        return null;
    }

    /**
     * méthode qui permet de savoir si un compteur est présent dans une liste de compteurs
     * @param supplyPoints la liste des compteurs
     * @param EAN18 le code ean du compteur à trouver
     * @return true si présent, false
     */
    public static boolean containsSpecifySupplyPoint(ArrayList<SupplyPoint> supplyPoints, String EAN18){
        if (supplyPoints.size() == 0) return false;
        for (SupplyPoint supplyPoint : supplyPoints)
            if (supplyPoint.getEAN18().equals(EAN18)) return true;
        return false;
    }

    /**
     * Exporte les données de SupplyPoint(s) dans un fichier JSON.
     *
     * @param supplyPoints La liste de SupplyPoint(s) dont les données vont être exportés.
     */
    public void exportDataSupplyPointInJSON(ArrayList<SupplyPoint> supplyPoints, String fileName){
        FileWriter file;
        try {
            file = new FileWriter(datafileDirChecking().resolve(fileName+".json").toString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, supplyPoints);
            file.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Exporte les données de SupplyPoint(s) dans un fichier CSV d'une période donnée.
     *
     * @param supplyPoints  La liste de SupplyPoint(s) dont les données vont être exportées.
     * @param period La période donnée.
     * @param fileName le chemin du fichier (exemple : c://dekstop//.../test)
     */
    public void exportDataSupplyPointInJSON(ArrayList<SupplyPoint> supplyPoints, String fileName, Period period){
        FileWriter file;
        try {
            file = new FileWriter(datafileDirChecking().resolve(fileName+".json").toString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, supplyPointsPeriodChecking(supplyPoints, period));
            file.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Importe les données d'un fichier JSON dans une ArrayList.
     *
     * @param fileName Le nom du fichier à importer
     * @return supplyPointList Arraylist du contenant les données du fichier.
     */
    public ArrayList<SupplyPoint> importDataSupplyPointInJSON(String fileName) {
        ArrayList<SupplyPoint> supplyPointList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            supplyPointList = objectMapper.readValue(
                            new File(datafileDirChecking().resolve(fileName + ".json").toString()),
                            new TypeReference<ArrayList<SupplyPoint>>() {
                            });
            setSupplyPointToDataSupplyPoint(supplyPointList);
            return supplyPointList;
        } catch (IOException e) {
            e.printStackTrace();
            return supplyPointList;
        }
    }

    /**
     * Set les SupplyPoint aux DataSupplyPoint correspondant d'une ArrayList de SupplyPoint.
     *
     * Cette méthode est uniquement utilisé lors de l'importation de données à partir d'un fichier
     * afin que les liens entre SupplyPoint et DataSupplyPoint correspondent.
     *
     * @param supplyPoints L'Arraylist de SupplyPoint
     */
    public static void setSupplyPointToDataSupplyPoint(ArrayList<SupplyPoint> supplyPoints){
        for (SupplyPoint supplyPoint:supplyPoints) {
            for (DataSupplyPoint dataSupplyPoint:supplyPoint.getAllDataSupplyPoints()) {
                dataSupplyPoint.setSupplyPoint(supplyPoint);
            }
        }
    }

}