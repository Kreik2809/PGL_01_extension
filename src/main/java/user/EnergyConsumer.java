package user;

import com.fasterxml.jackson.databind.ObjectMapper;
import gui.app1.GreenCertificateLine;
import tool.*;

import java.io.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * La classe EnergyConsumer représente un utilisateur dans l'application de gestion de portefeuilles.
 *
 * La classe EnergyConsumer représente un consommateur d'énergie dans l'application de facilitation de lien avec les
 * fournisseurs d’énergie.
 */
public class EnergyConsumer extends User {

    public static final boolean ACCEPT = true, DECLINE = false;

    private ArrayList<AccessLevel> accessLevels = new ArrayList<>();// Liste des niveaus d'accés aux portefeuilles de l'utilisateur
    private ArrayList<Invitation> invitations = new ArrayList<>();// Liste des invitations de l'utilisateur


    private String specialIndicator=""; //Cette String permet de forcer la mise à jour de l'affichage des invitations dans une listView

    public EnergyConsumer(String name,String password, String mail)
    {
        super(name, password, mail);
    }

    public EnergyConsumer(String name,String password)
    {
        super(name, password);
    }

    /**
     * Cette méthode exécute une requête http afin de récupérer et stocker tous les portefeuilles auquel l'utilisateur à accès.
     */
    public void fetchAccesLevel()
    {
        accessLevels.clear();
        ArrayList<String> liste = new ArrayList<>();
        try {
            String[] tab = {"USERNAME", "PORTFOLIO_ID", "ACCESS", "PNAME", "MNAME"};
            liste = HttpRequest.Get(URLDB.URL + "Portfolios/USERNAME=" + getName(), new ParseJson(tab, "NO PORTFOLIO FOR THIS USER", "PLEASE USE THIS SYNTAX : /RestAPI/Portfolios/USERNAME=xxx") {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (liste.size() != 0) {
            for (int i = 0; i < liste.size(); i += 5) {
                String pName = liste.get(i + 3);
                String pId = liste.get(i + 1);
                String access = liste.get(i + 2);
                String mName = liste.get(i+4);
                new AccessLevel(AccessLevelType.valueOf(access), this, new EnergyPortfolio(pName, pId, mName));
            }
        }
    }

    /**
     * Cette méthode récupère et stocke toutes les invitations d'un utilisateur.
     */
    public void fetchNotifications()
    {
        invitations.clear();
        ArrayList<String> liste = new ArrayList<>();
        try{
            String[] tab = {"USERNAME", "ACCESS", "PORTFOLIO_ID", "PNAME", "MANAGER"};
            liste = HttpRequest.Get(URLDB.URL + "Notifications/USERNAME=" + getName(), new ParseJson(tab, "NO NOTIFICATIONS FOR THIS USER", "PLEASE USE THIS SYNTAX : /RestAPI/Notifications/USERNAME=username") {});
        } catch (IOException ex){
            ex.printStackTrace();
        }
        if(liste.size() != 0) // on vérifie si il y a bien des notifications
        {
            for (int i = 0; i < liste.size(); i += 5) {
                String uName = liste.get(i);
                String access = liste.get(i+1);
                String pID = liste.get(i+2);
                String pName = liste.get(i+3);
                String manager = liste.get(i+4);
                addInvitation(new Invitation(this, new EnergyPortfolio(pName, pID, manager), AccessLevelType.valueOf(access)));
            }
        }
    }

    /**
     * Cette méthode retourne le niveau d'accès de l'energyConsumer pour un portefeuille e donné.
     * @param e Le portefeuille dont on veut trouver le niveau d'accès.
     * @return Le niveau d'accès
     */
    public AccessLevelType getAccessLevelType(EnergyPortfolio e){
        AccessLevelType accessLevelType = null;
        for (AccessLevel a : accessLevels){
            if (e.getID() == a.getEnergyPortfolio().getID()){
                accessLevelType = a.getAccessLevel();
            }
        }
        return accessLevelType;
    }


    public ArrayList<AccessLevel> getAccessLevels() {
        return accessLevels;
    }

    /**
     * Cette méthode retourne tous les portefeuilles du consommateur.
     * @return Une ArrayList de portefeuilles.
     */
    public ArrayList<EnergyPortfolio> getPortfolios(){
        ArrayList<EnergyPortfolio> list = new ArrayList<>();
        for (AccessLevel a : accessLevels){
            list.add(a.getEnergyPortfolio());
        }
        return list;
    }

    /**
     * Ajoute un portefeuille à un utilisateur
     * @param accessLevel : niveau d'accès
     */
    public void addAccessLevel(AccessLevel accessLevel) {
        boolean alreadyMember = false;
        //On vérifie que le consommateur n'a pas déjà accès au portefeuille
        for (AccessLevel a : accessLevels){
            if (a.getEnergyPortfolio().getID().equals(accessLevel.getEnergyPortfolio().getID())){
                alreadyMember = true;
            }
        }
        if (!alreadyMember)
            accessLevels.add(accessLevel);
    }

    /**
     * Ajoute une invitation déjà instanciée à la liste des invitations de l'utilisateur.
     *
     * @param invitation L'invitation à ajouter.
     */
    public void addInvitation(Invitation invitation)
    {
        invitations.add(invitation);
    }


    public ArrayList<SupplyPoint> fetchSupplyPoints()
    {
        ArrayList<SupplyPoint> liste = new ArrayList<>();
        String[] tab = {"NAME", "EAN18", "ENERGYTYPE", "DAYSTART", "STYPE"};
        ArrayList<String> res = new ArrayList<>();
        try {
            res = HttpRequest.Get(URLDB.URL + "MetersU/USERNAME=" + getName(), new ParseJson(tab, "NO METER FOR THIS USERNAME", "PLEASE USE THIS SYNTAX : /RestAPI/MetersU/USERNAME='username'") {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0 ; i < res.size() ; i+=5)
        {
            SupplyPoint supplyPoint = new SupplyPoint(res.get(i), EnergyType.valueOf(res.get(i+2)), res.get(i+1), res.get(i+3));
            boolean state = true;
            if(supplyPoint.getOpeningDate().equals("-1"))
                state = false;
            supplyPoint.setState(state);
            liste.add(supplyPoint);
        }
        return liste;
    }

    /**
     * Retourne l'historique des supplypoint
     *
     */
    public ArrayList<SupplyPoint> fetchHistoricSupplyPoint()
    {
        ArrayList<SupplyPoint> liste = new ArrayList<>();
        String[] tab = {"EAN", "USERNAME", "OPEN", "CLOSE"};
        ArrayList<String> res = new ArrayList<>();
        try {
            res = HttpRequest.Get(URLDB.URL + "HistorySupplyPoint/USERNAME=" + getName(), new ParseJson(tab, "NO DATA FOR THIS METER", "PLEASE USE THIS SYNTAX : /RestAPI/HistorySupplyPoint/") {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0 ; i < res.size() ; i+=4){
            ArrayList<String> res2 = new ArrayList<>();
            String[] tab2 = {"EAN", "ENERGYTYPE", "NAME", "STATE", "CID", "DAYSTART", "SID", "TYPE"};
            try {
                res2 = HttpRequest.Get(URLDB.URL + "MeterByEAN/EAN=" + res.get(i), new ParseJson(tab2, "NO METER FOR THIS EAN", "") {});
            } catch (IOException e) {
                e.printStackTrace();
            }
            SupplyPoint supplyPoint = new SupplyPoint(res2.get(2), EnergyType.valueOf(res2.get(1)), res.get(i), res.get(i+2), res.get(i+3));
            liste.add(supplyPoint);
        }
        return liste;
    }


    /**
     * Retourne la liste des contrats liés aux SupplyPoints d'un portefeuille énergétique.
     *
     * @param energyPortfolio Le portefeuille dont on veut les contrats.
     * @return contracts La liste des contrats lié à un portefeuille énergétique.
     */
    public ArrayList<Contract> getContracts(EnergyPortfolio energyPortfolio) {
        ArrayList<Contract> contracts = new ArrayList<>();
        ArrayList<SupplyPoint> supplyPoints = energyPortfolio.getSupplyPoints();
        int nbrOfSupplyPoint = supplyPoints.size();

        for(int i = 0;i < nbrOfSupplyPoint;i++){
            if(supplyPoints.get(i).getContract() != null){
                contracts.add(supplyPoints.get(i).getContract());
            }
        }
        return contracts;
    }

    /**
     * Retourne la liste des SupplyPoints lié à un portefeuille énergétique.
     *
     * @param energyPortfolio Le portefeuille dont on veut les SupplyPoints.
     * @return La liste des SupplyPoints lié à un portefeuille énergétique.
     */
    public ArrayList<SupplyPoint> getSupplyPoints(EnergyPortfolio energyPortfolio)
    {
        return energyPortfolio.getSupplyPoints();
    }



    /**
     * Retourne la liste des Suppliers lié à un portefeuille énergétique.
     *
     * @param energyPortfolio Le portefeuille dont on veut les Suppliers.
     * @return La liste des Suppliers lié à un portefeuille énergétique.
     */
    public ArrayList<EnergySupplier> getAssociatedSuppliers(EnergyPortfolio energyPortfolio)
    {
        ArrayList<EnergySupplier> suppliers = new ArrayList<>();
        ArrayList<Contract> contracts = this.getContracts(energyPortfolio);

        for(int i = 0;i < contracts.size();i++) {
            if(!suppliers.contains(contracts.get(i).getEnergySupplier())){
                suppliers.add(contracts.get(i).getEnergySupplier());
            }
        }
        return suppliers;
    }

    public ArrayList<Invitation> getInvitations() {
        return this.invitations;
    }

    /**
     * Gére une invitation:
     *      - Accepter l'invitation
     *      - Décliner l'invitation
     * @param invitation L'invitation à gérer.
     * @param choice ACCEPT or DECLINE
     */
    public void manageInvitation(Invitation invitation, boolean choice) {
        if(choice == DECLINE){
            try{
                HttpRequest.Post(URLDB.URL + "Notifications/?USERNAME="+getName()+"&PORTFOLIO_ID="+invitation.getEnergyPortfolio().getID()+"&ACTION=delete");
            } catch (IOException e){
                e.printStackTrace();
            }
            invitations.remove(invitation);
            invitation.getEnergyPortfolio().getInvitations().remove(invitation);

        }
        else if(choice == ACCEPT){
            try{
                HttpRequest.Post(URLDB.URL + "Notifications/?USERNAME="+getName()+"&PORTFOLIO_ID="+invitation.getEnergyPortfolio().getID()+
                        "&ACCESS="+invitation.getAccessLevelType().toString()+"&ACTION=accept");
            } catch (IOException e){
                e.printStackTrace();
            }
            invitation.getEnergyPortfolio().addEnergyConsumer(this,invitation.getAccessLevelType());
            invitations.remove(invitation);
            invitation.getEnergyPortfolio().getInvitations().remove(invitation);
        }
    }

    public String getSpecialIndicator() {
        return specialIndicator;
    }

    public void setSpecialIndicator(String specialIndicator) {
        this.specialIndicator = specialIndicator;
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
                String OpeningDateCurrentSupplyPoint = supplyPoint.getOpeningDate();
                String ClosingDateCurrentSupplyPoint = supplyPoint.getClosingDate();
                for (DataSupplyPoint dataSupplyPoint : supplyPoint.getAllDataSupplyPoints()) {
                    if (dataSupplyPoint.getDate().compareTo(OpeningDateCurrentSupplyPoint) > 0
                            && dataSupplyPoint.getDate().compareTo(ClosingDateCurrentSupplyPoint) < 0) {
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
     * Exporte les données de SupplyPoint(s) dans un fichier CSV d'une période donnée.
     *
     * @param supplyPoints  La liste de SupplyPoint(s) dont les données vont être exportés.
     * @param period La période donnée
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
                String OpeningDateCurrentSupplyPoint = supplyPoint.getOpeningDate();
                String ClosingDateCurrentSupplyPoint = supplyPoint.getClosingDate();
                for (DataSupplyPoint dataSupplyPoint : supplyPoint.getAllDataSupplyPoints())
                {
                    if (dataSupplyPoint.getDate().compareTo(OpeningDateCurrentSupplyPoint) >= 0 && dataSupplyPoint.getDate().compareTo(ClosingDateCurrentSupplyPoint) < 0)
                    {
                        if (dataSupplyPoint.getDate().compareTo(startDate) >= 0 && dataSupplyPoint.getDate().compareTo(endDate) <= 0)
                        {
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
            }
            file.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
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
            ArrayList<SupplyPoint> supplyPointArrayList = new ArrayList<>();
            for (SupplyPoint supplyPoint :  supplyPoints) {
                Period currentPeriod = new Period(stringToDateSQL(supplyPoint.getOpeningDate()),stringToDateSQL(supplyPoint.getClosingDate()));
                supplyPointArrayList.add(supplyPointPeriodChecking(supplyPoint,currentPeriod));
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, supplyPointArrayList);
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
    public void exportDataSupplyPointInJSON(ArrayList<SupplyPoint> supplyPoints, String fileName, Period period){
        FileWriter file;
        try {
            file = new FileWriter(datafileDirChecking().resolve(fileName+".json").toString());
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<SupplyPoint> supplyPointArrayList = new ArrayList<>();

            for (SupplyPoint supplyPoint :  supplyPoints) {
                Period currentPeriod = new Period(stringToDateSQL(supplyPoint.getOpeningDate()),stringToDateSQL(supplyPoint.getClosingDate()));
                supplyPointArrayList.add(supplyPointPeriodChecking(supplyPoint,currentPeriod));
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file,supplyPointsPeriodChecking(supplyPointArrayList,period));
            file.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
