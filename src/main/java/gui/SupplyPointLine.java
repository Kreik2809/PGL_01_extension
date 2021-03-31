package gui;

import user.Contract;
import user.SupplyPoint;

/**
 * La classe SupplyPointLine représente une ligne d'informations dans une tableView représentant un ensemble de SupplyPoint.
 */
public class SupplyPointLine {

    private Contract c;

    private SupplyPoint meter;

    private String ID, type, contractNumber, energySupplier, contractType, name, openingDate, closingDate, clientName;

    private String specialIndicator="";

    public SupplyPointLine(Contract c, SupplyPoint meter){

        this.c = c;
        this.meter = meter;
        this.ID = meter.getEAN18();
        this.type = meter.getEnergyType().toString();
        this.contractNumber = c.getId();
        this.name = meter.getName();
        this.openingDate = meter.getOpeningDate();
        this.closingDate = meter.getClosingDate();
        if (c.getEnergySupplier() != null)
            this.energySupplier = c.getEnergySupplier().getName();
        else
            this.energySupplier = null;
        this.contractType = c.getType();
        this.clientName = c.getEnergyConsumer().getName();
    }

    public SupplyPointLine(SupplyPoint meter){
        this.meter = meter;
        this.ID = meter.getEAN18();
        this.type = meter.getEnergyType().toString();
        this.name = meter.getName();
        this.openingDate = meter.getOpeningDate();
        this.closingDate = meter.getClosingDate();

    }



    public Contract getC() {
        return c;
    }

    public SupplyPoint getMeter() {
        return meter;
    }

    public String getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public String getEnergySupplier() {
        return energySupplier;
    }

    public String getContractType() {
        return contractType;
    }

    public String getName() {
        return name;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public String getClosingDate() {
        return closingDate;
    }

    public String getClientName() {
        return clientName;
    }

    public String getSpecialIndicator() {
        return specialIndicator;
    }

    public void setSpecialIndicator(String specialIndicator) {
        this.specialIndicator = specialIndicator;
    }
}
