package user;

import gui.app1.GuiHandler;
import tool.*;
import tool.sql.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * classe qui représente un portefeuille énergétique
 */
public class EnergyPortfolio
{
	private EnergyConsumer manager;
	private String name;
	private Home home;// Le foyer correspondant au portefeuille énergétique
	private int nbMeters;

	private final String ID;

	private ArrayList<AccessLevel> accessLevels = new ArrayList<>();// Liste des niveaus d'accés d'utilisateurs au portefeuille
	private ArrayList<Invitation> invitations = new ArrayList<>();// Liste des invitations liées au portefeuille énergétique
	private ArrayList<SupplyPoint> supplyPoints = new ArrayList<>();// Liste des SupplyPoints liées au portefeuille énergétique


	/**
	 * Ce constructeur est utilisé lors de la récupération de l'ensemble des portefeuille d'un utilisateur
	 * et lors de l'ajout d'invitations. (ajout direct dans les accesLevels)
	 * @param manager le manager du portefeuille
	 * @param name le nom du portefeuille
	 * @param ID l'id du portefeuille
	 */
	public EnergyPortfolio(EnergyConsumer manager, String name, String ID) {
		this.manager = manager;
		this.name = name;
		this.ID = ID;
		accessLevels.add(new AccessLevel(AccessLevelType.MANAGING,manager,this));
	}

	/**
	 * Ce constructeur est utilisé lors de la récupération de l'ensemble des portefeuilles d'un utilisateur
	 * et lors de l'ajout d'invitations.
	 * @param manager le manager du portefeuille
	 * @param name le nom du portefeuille
	 * @param ID l'id du portefeuille
	 */
	public EnergyPortfolio(String name, String ID, String manager) {
		this.manager = new EnergyConsumer(manager, "");
		this.name = name;
		this.ID = ID;
	}

	public ArrayList<EnergyConsumer> getEnergyConsumer(){
		ArrayList<EnergyConsumer> l = new ArrayList<>();
		for (AccessLevel a : accessLevels){
			l.add(a.getEnergyConsumer());
		}
		return l;
	}

	/**
	 * Cette méthode exécute une requête http afin de récupérer tous les utilisateurs du portefeuille
	 */
	public void fetchAccesLevels()
	{
		accessLevels.clear();
		String name, access;
		ArrayList<String> liste = new ArrayList<>();
		String[] tab = {"PORTFOLIO_ID", "USERNAME", "ACCESS"};
		try{
			liste = HttpRequest.Get(URLDB.URL + "UserPortfolio/PORTFOLIO_ID=" + getID(), new ParseJson(tab, "NO USER FOR THIS PORTFOLIO", "PLEASE USE THIS SYNTAX : /RestAPI/UserPortfolio/PORTFOLIO_ID=xx") {});
		}catch (IOException e){
			e.printStackTrace();
		}
		for (int i = 0; i < liste.size() - 2; i += 3) {
			name = liste.get(i);
			access = liste.get(i + 2);
			GuiHandler.getGui().getCurrentPortfolio().addEnergyConsumer(new EnergyConsumer(name, ""), AccessLevelType.valueOf(access));
		}
	}

	/**
	 * Cette méthode exécute une requête http afin de récupérer tous les compteurs d'un portefeuille
	 */
	public void fetchSupplyPoints()
	{
		supplyPoints.clear();
		ArrayList<String> liste = new ArrayList<>();
		try {
			String[] tab = {"PORTFOLIO_ID", "EAN18", "ENERGYTYPE", "NAME", "STATE", "CONTRACTID", "DAYSTART", "SUPPLIERID", "TYPE"};
			liste = HttpRequest.Get(URLDB.URL + "Meters/PORTFOLIO_ID=" + getID(), new ParseJson(tab, "NO METER FOR THIS PORTFOLIO", "PLEASE USE THIS SYNTAX : /RestAPI/Meters/PORTFOLIO_ID=xxx") {});
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (liste.size() != 0) {
			for (int i = 0; i < liste.size(); i += 9) {
				String pID = liste.get(i);
				String EAN = liste.get(i+1);
				String eType = liste.get(i+2);
				String mName = liste.get(i+3);
				String stateStr = liste.get(i+4);
				String cID = liste.get(i+5);
				String dStart = liste.get(i+6);
				String sID = liste.get(i+7);
				boolean state = false;
				if(stateStr.equals("1"))
					state = true;
				String s = liste.get(i+8);
				SupplyPointType sType = SupplyPointType.valueOf(s);
				try{
					addSupplyPoint(mName, EnergyType.valueOf(eType), EAN, cID, sType,  dStart, state);
				}catch (SupplyPoint.EAN18Exception e){
					//
				}
			}
		}
	}

	/**
	 * Ajoute un utilisateur dans la liste des niveaux d'accés d'utilisateurs au portefeuille.
	 *
	 * @param energyConsumer L'utilisateur à ajouter.
	 * @param accessLevelType Le type d'accès accordé à l'utilisateur.
	 */
	public void addEnergyConsumer(EnergyConsumer energyConsumer,AccessLevelType accessLevelType)
	{
		accessLevels.add(new AccessLevel(accessLevelType, energyConsumer, this));
	}

	/**
	 * Ajoute un nouveau SupplyPoint à la liste des SupplyPoint du portefeuille énergétique.
	 *
	 * @param energyType Le type d'énergie fourni par le SupplyPoint.
	 * @param EAN18 Le code EAN18 (unique) du SupplyPoint.
	 * @param name le nom du compteur
	 * @param cID l'id du contrat (-1 si pas de contrat)
	 * @param dStart jour d'ouverture du contrat
	 * @param state l'état du compteur
	 */
	public void addSupplyPoint(String name, EnergyType energyType,String EAN18, String cID, SupplyPointType stype, String dStart, boolean state) throws SupplyPoint.EAN18Exception {
		//On n'ajoute pas deux fois le même compteur dans la liste des compteurs du portefeuille
		boolean alreadyIn = false;
		for (SupplyPoint meter : supplyPoints){
			if (meter.getEAN18() == EAN18)
				alreadyIn = true;
		}
		if (!alreadyIn) {
			SupplyPoint s = new SupplyPoint(name, energyType, EAN18, this);
			s.setSupplyPointType(stype);
			s.setOpeningDate(dStart);
			s.setState(state);
			supplyPoints.add(s);
			this.nbMeters++;
			if (!cID.equals("-1")) {
				s.fetchContract();
			}
		}
	}

	/**
	 * Cette méthode permet de supprimer un compteur du portefeuille.
	 * @param s L'objet SupplyPoint à supprimer.
	 */
	public void removeMeter(SupplyPoint s){
		nbMeters --;
		supplyPoints.remove(s);
	}

	/**
	 * Cette méthode permet de modifier le niveau d'accès d'un utilisateur pour le portefeuille.
	 * @param e L'utilisateur en question
	 * @param newAccess Son nouveau niveau d'accès.
	 */
	public void modifAccess(EnergyConsumer e, AccessLevelType newAccess){
		for (AccessLevel a : accessLevels){
			if (a.getEnergyConsumer().getName().equals(e.getName()))
				a.setAccessLevelType(newAccess);
		}

		for (AccessLevel al : e.getAccessLevels()){
			if (al.getEnergyPortfolio().getID() == (this.getID()))
				al.setAccessLevelType(newAccess);
		}
	}

	/**
	 * Cette méthode permet supprimer l'accès d'un utilisateur au portefeuille.
	 * @param e L'utilisateur à supprimer du portefeuille
	 */
	public void removeAccess(EnergyConsumer e){
		AccessLevel a1 = null;
		AccessLevel a2 = null;
		for (AccessLevel a : accessLevels){
			if (a.getEnergyConsumer().getName().equals(e.getName()))
				a1 = a;
		}

		for (AccessLevel al : e.getAccessLevels()){
			if (al.getEnergyPortfolio().getID() == this.getID())
				a2 = al;
		}

		accessLevels.remove(a1);
		e.getAccessLevels().remove(a2);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNbMeters() {
		return nbMeters;
	}

	public EnergyConsumer getManager() {
		return manager;
	}

	public void setManager(EnergyConsumer manager) {
		this.manager = manager;
	}

	public Home getHome() {
		return home;
	}

	public void setHome(Home home) {
		this.home = home;
	}

	public ArrayList<AccessLevel> getAccessLevels() {
		return accessLevels;
	}

	public String getID() {
		return ID;
	}

	public ArrayList<SupplyPoint> getSupplyPoints() {
		return supplyPoints;
	}

	public ArrayList<Invitation> getInvitations() {
		return invitations;
	}
}