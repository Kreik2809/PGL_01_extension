package user;

import tool.HttpRequest;
import tool.SignIn;
import tool.URLDB;
import tool.sql.UserDB;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe abstraite est une classe parent de EnergyConsumer et EnergySypplier, elle représente n'importe quel type d'utilisateur
 * notre système.
 */
public abstract class User
{
    //Délimiteurs qui doivent être dans le fichier CSV
    protected static final String DELIMITER = ",";
    protected static final String SEPARATOR = "\n";

    //En-tête de fichier csv
    protected static final String HEADER = "SupplyPoint,EnergyType,Date,Value";

    protected String name;
    protected String password;
    protected String mail;

    protected User(String name, String password, String mail)
    {
        this.name = name;
        this.password = password;
        this.mail = mail;
    }

    protected User(String name, String password)
    {
        this.name = name;
        this.password = password;
    }

    /**
     * Vérifie l'existence du répertoire "datafile", création de celui-ci si besoin
     * et retourne son path.
     *
     * @return Le path du répertoire "datafile".
     */
    public static Path datafileDirChecking() throws IOException {
        Path dataFileDirectory = Paths.get("datafile");
        if(! Files.exists(dataFileDirectory)) Files.createDirectories(dataFileDirectory);
        return dataFileDirectory;
    }

    /**
     * Convertir un String date ("yyyy-mm-dd") en une instance de java.sql.Date
     *
     * @param date un String date ("yyyy-mm-dd") à convertir
     * @return une instance de java.sql.Date correspondant au String date
     */
    public static Date stringToDateSQL(String date) {
        int year = Integer.parseInt(date.substring(0,4));
        int month = Integer.parseInt(date.substring(5,7));
        int day = Integer.parseInt(date.substring(8,10));
        return new Date(year - 1900, month - 1, day);
    }

    public abstract void exportDataSupplyPointInCSV(ArrayList<SupplyPoint> supplyPoints, String fileName);

    public abstract void exportDataSupplyPointInJSON(ArrayList<SupplyPoint> supplyPoints, String fileName);

    public abstract void exportDataSupplyPointInCSV(ArrayList<SupplyPoint> supplyPoints, String fileName, Period period);

    public abstract void exportDataSupplyPointInJSON(ArrayList<SupplyPoint> supplyPoints, String fileName, Period period);

    /**
     * Retourne une ArrayList de SupplyPoint dont les données appartiennent à une certaine période.
     *
     * @param supplyPoints L'ArrayList de SupplyPoint dont on veut les données.
     * @param period La période dont on veut récupérer les données.
     * @return supplyPointsPeriodChecked Une ArrayList de SupplyPoint dont les données appartiennent à une certaine période.
     */
    public static ArrayList<SupplyPoint> supplyPointsPeriodChecking(ArrayList<SupplyPoint> supplyPoints, Period period) {
        String startDate = period.getStartDate().toString();
        String endDate = period.getEndDate().toString();
        ArrayList<SupplyPoint> supplyPointsPeriodChecked = new ArrayList<>();

        for (SupplyPoint supplyPoint : supplyPoints) {
            ArrayList<DataSupplyPoint> dataSupplyPointsPeriodChecked = new ArrayList<>();

            for (DataSupplyPoint dataSupplyPoint : supplyPoint.getAllDataSupplyPoints()) {
                if (dataSupplyPoint.getDate().compareTo(startDate) >= 0
                        && dataSupplyPoint.getDate().compareTo(endDate) < 0) {
                    dataSupplyPointsPeriodChecked.add(new DataSupplyPoint(dataSupplyPoint.getDate(),dataSupplyPoint.getValue(),dataSupplyPoint.getSupplyPoint()));
                }
            }
            supplyPointsPeriodChecked.add(new SupplyPoint(supplyPoint.getEAN18(),supplyPoint.getEnergyType(),dataSupplyPointsPeriodChecked));
        }
        return supplyPointsPeriodChecked;
    }

    /**
     * Retourne un SupplyPoint dont les données appartiennent à une certaine période.
     *
     * @param supplyPoint Le SupplyPoint dont on veut les données.
     * @param period La période dont on veut récupérer les données.
     * @return un SupplyPoint dont les données appartiennent à une certaine période.
     */
    public static SupplyPoint supplyPointPeriodChecking(SupplyPoint supplyPoint, Period period) {
        String startDate = period.getStartDate().toString();
        String endDate = period.getEndDate().toString();

        ArrayList<DataSupplyPoint> dataSupplyPointsPeriodChecked = new ArrayList<>();

        for (DataSupplyPoint dataSupplyPoint : supplyPoint.getAllDataSupplyPoints()) {
            if (dataSupplyPoint.getDate().compareTo(startDate) >= 0
                    && dataSupplyPoint.getDate().compareTo(endDate) < 0) {
                dataSupplyPointsPeriodChecked.add(new DataSupplyPoint(dataSupplyPoint.getDate(),dataSupplyPoint.getValue(),dataSupplyPoint.getSupplyPoint()));
            }
        }
        return new SupplyPoint(supplyPoint.getEAN18(),supplyPoint.getEnergyType(),dataSupplyPointsPeriodChecked);
    }

    /**
     * méthode qui retourne le nom d'utilisateur
     * @return le nom d'utilisateur
     */
    public String getName()
    {
        return name;
    }

    /**
     * méthode qui retourne le mot de passe de l'utilisateur
     * @return le mot de passe de l'utilisateur
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * méthode qui permet de changer le nom de l'utilisateur
     * @param newName le nom de l'utilisateur
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * méthode qui permet de changer le mot de passe de l'utilisateur
     * @param newPassword le mot de passe de l'utilisateur
     */
    public void changePassword(String newPassword, UserType type)
    {
        password = newPassword;
        try{
            String request = URLDB.URL + "User/?MAIL=" + getMail() + "&PWD=" + SignIn.encryption(password) + "&TYPE=" + type.name() + "&USERNAME=" + getName() + "&ACTION=changepwd";
            HttpRequest.Post(request);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }


}
