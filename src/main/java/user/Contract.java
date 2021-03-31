package user;

import java.sql.Date;
import java.util.ArrayList;


/**
 * classe contrat qui représente un contrat
 */
public class Contract
{
    private String id;
    private Date date;
    private EnergySupplier energySupplier;
    private EnergyConsumer energyConsumer;
    private ArrayList<SupplyPoint> meters;
    private String type;

    /**
     * constructeur
     * @param id l'id du contrat
     * @param energySupplier le fournisseur d'énergie lié au contrat
     * @param energyConsumer le consommateur d'énergie lié au contrat
     * @param meters les compteurs
     * @param type le type de contrat
     */
    public Contract(String id,  EnergySupplier energySupplier, EnergyConsumer energyConsumer, ArrayList<SupplyPoint> meters, String type)
    {
        this.id = id;
        this.energySupplier = energySupplier;
        this.energyConsumer = energyConsumer;
        this.meters = meters;
        this.type = type;
    }

    /**
     * constructeur
     * @param id l'id du contrat
     * @param energySupplier le fournisseur d'énergie lié au contrat
     * @param energyConsumer le consommateur d'énergie lié au contrat
     * @param date la date d'ouverture du contrat
     * @param type le type de contrat
     */
    public Contract(String id,  EnergySupplier energySupplier, EnergyConsumer energyConsumer, String type, Date date)
    {
        this.id = id;
        this.energySupplier = energySupplier;
        this.energyConsumer = energyConsumer;
        this.type = type;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public EnergySupplier getEnergySupplier() {
        return energySupplier;
    }

    public void setEnergySupplier(EnergySupplier energySupplier) {
        this.energySupplier = energySupplier;
    }

    public EnergyConsumer getEnergyConsumer() {
        return energyConsumer;
    }

    public void setEnergyConsumer(EnergyConsumer energyConsumer) {
        this.energyConsumer = energyConsumer;
    }

    public ArrayList<SupplyPoint> getCounters() {
        return meters;
    }

    public String getType() {
        return type;
    }
}
