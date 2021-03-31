package tool;

import user.EnergyConsumer;
import user.EnergyPortfolio;

/**
 * La classe AccessLevel représente l'association entre utilisateur et portefeuille énergétique avec un certain niveau
 * d'accès.
 */
public class AccessLevel
{
	private AccessLevelType accessLevelType;// Le niveau d'accès entre un utilisateur et un portefeuille énergétique
	private EnergyConsumer energyConsumer;// L'utilisateur
	private EnergyPortfolio energyPortfolio;// Le portefeuille énergétique

	public AccessLevel(AccessLevelType accessLevel,EnergyConsumer energyConsumer,EnergyPortfolio energyPortfolio)
	{
		this.accessLevelType = accessLevel;
		this.energyConsumer = energyConsumer;
		this.energyPortfolio = energyPortfolio;
		energyConsumer.addAccessLevel(this);
	}

	public AccessLevelType getAccessLevel() {
		return accessLevelType;
	}

	public EnergyConsumer getEnergyConsumer() {
		return energyConsumer;
	}

	public EnergyPortfolio getEnergyPortfolio() {
		return energyPortfolio;
	}

	public void setAccessLevelType(AccessLevelType accessLevelType){
		this.accessLevelType = accessLevelType;
	}
}