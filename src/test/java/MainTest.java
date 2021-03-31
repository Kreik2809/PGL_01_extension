import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import tool.AccessLevel;
import tool.AccessLevelType;
import user.EnergyConsumer;
import user.EnergyPortfolio;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MainTest {

    private Stage primaryStage;
    public EnergyConsumer u1, u2, u3;
    public EnergyPortfolio e1, e2, e3, e4, e5;

    @Before
    public void init(){


        u1 = new EnergyConsumer("Nicolas", "azerty");
        u2 = new EnergyConsumer("Romeo", "azerty");
        u3 = new EnergyConsumer("Clement", "azerty");


        e1 = new EnergyPortfolio(u1, "p1", "1");
        e4 = new EnergyPortfolio(u1, "p2", "2");
        e5 = new EnergyPortfolio(u1, "p3", "3");
        e2 = new EnergyPortfolio(u2, "Mon portefeuille", "4");
        e3 = new EnergyPortfolio(u3, "Maison vacance", "5");

    }


    /**
     * Ce test vérifie qu'un EnergyConsumer e dispose bien des portefeuilles dont il est propriétaire dans
     * sa liste de portefeuille.
     */
    @Test
    public void testSelfPortfolio(){
        ArrayList<EnergyPortfolio> l = u1.getPortfolios();
        assertTrue(l.contains(e1));
        assertTrue(l.contains(e4));
        assertTrue(l.contains(e5));
        assertTrue(l.size() == 3);
    }

    /**
     * Ce test vérifie que l'ajout d'un portefeuille à un utilisateur fonctionne correctement.
     * C'est à dire que le portefeuille est correctement ajouté à l'utilisateur et que l'utilisateur est correctement ajouté
     * au portefeuille.
     */
    @Test
    public void testNewPortfolio(){
        EnergyConsumer fakeUser = new EnergyConsumer("fake", "fake");
        EnergyPortfolio newE = new EnergyPortfolio(fakeUser, "NouveauP", "6");
        newE.addEnergyConsumer(u1, AccessLevelType.WRITING);
        assertTrue(u1.getPortfolios().contains(newE)); // On vérifie que l'utilisateur dispose du portefeuille
        assertTrue(newE.getEnergyConsumer().contains(u1)); // On vérifie que le portefeuille dispose de l'utilisateur
        assertTrue(u1.getPortfolios().size() == 4); // On vérifie la taille pour s'assurer qu'il n'y ait pas de doublons
        assertTrue((newE.getEnergyConsumer().size()==2)); // On vérifie la taille pour s'assurer qu'il n'y ait pas de doublons
    }

    /**
     * Ce test vérifie que lorsque l'on ajoute un nouveau portefeuille, le niveau d'accès dont l'utilisateur dispose pour ce
     * portefeuille est cohérent par rapport à celui qu'on a voulu lui donner.
     */
    @Test
    public void testNewPortfolioAccess(){
        e2.addEnergyConsumer(u1, AccessLevelType.WRITING);
        ArrayList<AccessLevel> l = u1.getAccessLevels();
        for (AccessLevel accessl : l){
            if(e2.getID() == accessl.getEnergyPortfolio().getID())
                assertTrue(AccessLevelType.WRITING == accessl.getAccessLevel());
        }
    }

    /**
     * Ce test vérifie que l'on ne peut pas ajouter deux fois le même portefeuille dans la liste des portfeuilles
     * d'un même EnergyConsumer.
     * Pour ce faire, l'ajout d'un portefeuille dans la liste des portefeuille d'un utilisateur se fait via l'ajout de
     * cet utilisateur dans la liste des utilisateur du portefeuille
     */
    @Test
    public void testSamePortfolio(){
        e1.addEnergyConsumer(u1, AccessLevelType.WRITING);
        assertTrue(! (u1.getPortfolios().size() == 4));
    }

    /**
     * Ce test vérifie que lors de la création d'un portefeuille, celui-ci dispose bien de son manager dans sa liste d'utilisateur
     */
    @Test
    public void testContainManager(){
        assertTrue(e1.getEnergyConsumer().contains(u1));
        assertTrue(e1.getEnergyConsumer().size() == 1);
    }




}
