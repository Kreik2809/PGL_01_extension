package user;

import tool.AccessLevelType;

/**
 * La classe Invitation représente une invitation dans l'application de gestion de portefeuilles.
 */
public class Invitation
{
	private EnergyConsumer energyConsumer;
	private EnergyPortfolio energyPortfolio;
	private AccessLevelType accessLevelType;

	private static int count = 0;

	private final int ID;

	private String specialIndicator=""; //Cette String permet de forcer la mise à jour de l'affichage des invitations dans une listView

	/**
	 * constructeur
	 * @param energyConsumer le consommateur
	 * @param energyPortfolio le portefeuille
	 * @param accessLevelType le type d'accès
	 */
	public Invitation(EnergyConsumer energyConsumer,EnergyPortfolio energyPortfolio,AccessLevelType accessLevelType)
	{
		this.energyConsumer = energyConsumer;
		this.energyPortfolio = energyPortfolio;
		this.accessLevelType = accessLevelType;
		this.ID = count;
		count ++;
	}

	public EnergyConsumer getEnergyConsumer() {
		return energyConsumer;
	}

	public EnergyPortfolio getEnergyPortfolio() {
		return energyPortfolio;
	}

	public AccessLevelType getAccessLevelType() {
		return accessLevelType;
	}

	public void setSpecialIndicator(String s){
		this.specialIndicator = s;
	}

	public String getSpecialIndicator(){
		return specialIndicator;
	}

	public int getID() {
		return ID;
	}
}