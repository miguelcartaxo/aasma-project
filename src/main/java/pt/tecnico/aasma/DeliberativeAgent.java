/*
 * Copyright (C) 2015 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pt.tecnico.aasma;

import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.utils.exception.PogamutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import pt.tecnico.aasma.beliefs.Belief;
import pt.tecnico.aasma.beliefs.SeeingWeapon;
import pt.tecnico.aasma.desires.Desire;
import pt.tecnico.aasma.desires.GrabWeapon;
import pt.tecnico.aasma.desires.Intention;

/**
 *
 * @author Miguel
 */
public class DeliberativeAgent extends UT2004BotModuleController<UT2004Bot> {
    
    private boolean executingPlan = false;
    
     // Has info about CTF flags and bases been initiaized?
    private boolean initialized = false;
    /**
     * Agent's Beliefs
     */
    protected ArrayList<Belief> currentBeliefs;
    /**
     * Agent's Desires 
     */
    protected SortedSet<Desire> currentDesires;
    /**
     * Agent's Intention
     */
    protected Intention currentIntention;
    
    // Info about both flags
    protected FlagInfo ourFlag, enemyFlag;
    // Location of both bases
    protected NavPoint ourBase, enemyBase;
    
    @Override
    public void prepareBot(UT2004Bot bot) {
    }

    /**
     * Initialization
     */
    @Override
    public Initialize getInitializeCommand() {
        return new Initialize().setName("DeliberativeAgent");
    }
    
    
    /**
    * BDI ALGORITHM
    */
    private void BDIAlgorithm() {
        
        currentBeliefs = beliefRevision();        
        currentDesires = options(currentBeliefs, currentIntention);
        currentIntention = filter(currentBeliefs, currentDesires, currentIntention);
        createAndExecutePlan(currentBeliefs, currentIntention);
    }
    
    private ArrayList<Belief> beliefRevision() {
        ArrayList<Belief> newBeliefs = new ArrayList();
        
        
        Collection<NavPoint> visiblePoints = DistanceUtils.getDistanceSorted(this.navPoints.getVisibleNavPoints().values(), this.info.getLocation());
        NavPoint nearestHealth = null;
       // NavPoint nearestAmmo = null;
        if (visiblePoints != null) {
            for (NavPoint navp : visiblePoints) {
               // if (navp.isInvSpot() && navp.isItemSpawned()) {
                 //   if (nearestHealth == null && navp.getItemClass().getCategory().equals(ItemType.Category.HEALTH)) {
                   //     nearestHealth = navp;
//                    } else if (nearestAmmo == null && navp.getItemClass().getCategory().equals(ItemType.Category.AMMO)) {
//                        if (!weaponry.hasAmmo(navp.getItemClass())) {
//                            nearestAmmo = navp;
//                        }
                   // } else 
                        if (navp.getItemClass().getCategory().equals(ItemType.Category.WEAPON)) {
                        if (!weaponry.hasWeapon(navp.getItemClass())) {
                            newBeliefs.add(new SeeingWeapon(navp));
                           
                        }
                    }
                }
            }
        
        return newBeliefs;
    
    }
    
    /**
     * Creates the list of the agent's desires according to the current beliefs and
     * his previous intention.
     */
    private SortedSet<Desire> options(ArrayList<Belief> beliefs, Intention intention) {
        SortedSet<Desire> newDesires = new TreeSet(currentDesires.comparator());
        
        for (Belief b : beliefs) {
            switch (b.getName()) {
                
                case "SeeingWeapon":
                    newDesires.add(new GrabWeapon(((SeeingWeapon) b).getPoint(), 6));
                    break;
            }
        }
         
        return newDesires;
    }
    
    
    private Intention filter(ArrayList<Belief> beliefs, SortedSet<Desire> desires, Intention intention) {
        if (desires.size() > 0) {
            Desire d = desires.last();
            return new Intention(d.getName(), d.getTarget());
        }
        return intention;
    }
    
    private void createAndExecutePlan(ArrayList<Belief> beliefs, Intention intention) {
       executingPlan = true;
        

        switch (intention.getName()) {
        
        case "GrabWeapon":    
            navigation.navigate((NavPoint) intention.getTarget());
            break;
        }
    
        executingPlan = false;
    }
    
    /**
     * This method is called only once right before actual logic() method is
     * called for the first time.
     */
    @Override
    public void beforeFirstLogic() {
        currentBeliefs = new ArrayList<>();

        currentDesires = new TreeSet<>(new Comparator<Desire>() {
            
            //Organizes the set in an ascending order
            //Higher integer means higher priority
            @Override
            public int compare(Desire d1, Desire d2) {
                return d1.getPriority() - d2.getPriority();
            }
        });
        currentIntention = null;
    }
    
    @Override
    public void logic() throws PogamutException {
        if (!initialized) {
            log.info("Reinitializing info about flags...");
            ourFlag = ctf.getOurFlag();
            enemyFlag = ctf.getEnemyFlag();

            ourBase = ctf.getOurBase();
            enemyBase = ctf.getEnemyBase();
            initialized = true;
        }

        if (!players.canSeeEnemies()) {
            shoot.stopShooting();
        }

        if (!executingPlan) {
            BDIAlgorithm();
        }
    }
    
}
