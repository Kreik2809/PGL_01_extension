package user;

import java.util.ArrayList;

/**
 * La classe Home représente un foyer dans l'application de gestion de portefeuilles.
 */
public class Home
{
    private Address address;// Adresse du foyer
    private EnergyPortfolio energyPortfolio;
    private ArrayList<SupplyPoint> supplyPoints = new ArrayList<>();// Liste des SupplyPoints du foyer

    public Home(Address address)
    {
        this.address = address;
    }

    public Home(Address address, ArrayList<SupplyPoint> supplyPoints)
    {
        this.address = address;
        this.supplyPoints = supplyPoints;
    }

    public Address getAddress() {
        return address;
    }

    public ArrayList<SupplyPoint> getSupplyPoints() {
        return supplyPoints;
    }


}
