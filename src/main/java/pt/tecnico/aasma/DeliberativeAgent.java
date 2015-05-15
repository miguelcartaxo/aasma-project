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

import cz.cuni.amis.pogamut.base.agent.module.comm.PogamutJVMComm;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import pt.tecnico.aasma.beliefs.BeingDamaged;
import pt.tecnico.aasma.beliefs.Belief;
import pt.tecnico.aasma.beliefs.CarryingFlag;
import pt.tecnico.aasma.beliefs.FlagDropped;
import pt.tecnico.aasma.beliefs.FlagInBase;
import pt.tecnico.aasma.beliefs.LowAmmo;
import pt.tecnico.aasma.beliefs.LowHealth;
import pt.tecnico.aasma.beliefs.SeeingAmmoPack;
import pt.tecnico.aasma.beliefs.SeeingEnemy;
import pt.tecnico.aasma.beliefs.SeeingHealthPack;
import pt.tecnico.aasma.beliefs.SeeingWeapon;
import pt.tecnico.aasma.desires.CaptureEnemyFlag;
import pt.tecnico.aasma.desires.CaptureOwnFlag;
import pt.tecnico.aasma.desires.ChangeWeapon;
import pt.tecnico.aasma.desires.Desire;
import pt.tecnico.aasma.desires.GoToBase;
import pt.tecnico.aasma.desires.GrabAmmo;
import pt.tecnico.aasma.desires.GrabHealth;
import pt.tecnico.aasma.desires.GrabWeapon;
import pt.tecnico.aasma.desires.Intention;
import pt.tecnico.aasma.desires.KillEnemy;




/**
 *
 * @author Miguel
 */
public class DeliberativeAgent extends UT2004BotModuleController<UT2004Bot> {
    
    private boolean executingPlan = false;
    
    protected GameInfo gameInfo;
    
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
    
    private UT2004PathAutoFixer autoFixer;
    
    // Last known health packet location
    private NavPoint lastHealthItem = null;
    
    protected TabooSet<Item> tabooItems = null;
    
    @Override
	public void botFirstSpawn(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init, Self self) {
		PogamutJVMComm.getInstance().registerAgent(bot, self.getTeam());
	}
	
	@Override
	public void botShutdown() {
		PogamutJVMComm.getInstance().unregisterAgent(bot);
	}
    
    
    @Override
    public void prepareBot(UT2004Bot bot) {
        tabooItems = new TabooSet<Item>(bot);

	    autoFixer = new UT2004PathAutoFixer(bot, navigation.getPathExecutor(), fwMap, aStar, navBuilder); // auto-removes wrong navigation links between navpoints

		// listeners
		navigation.getPathExecutor().getState().addListener(
				new FlagListener<IPathExecutorState>() {
					@Override
					public void flagChanged(IPathExecutorState changedValue) {
						switch (changedValue.getState()) {
							case STUCK:
								currentDesires.remove(currentDesires.last());
                                                                filter(currentBeliefs, currentDesires, currentIntention);
                                                                createAndExecutePlan(currentBeliefs, currentIntention);
								break;

						}
					}
				});
        // DEFINE WEAPON PREFERENCES
		weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.MINIGUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.LIGHTNING_GUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.SHOCK_RIFLE, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.ROCKET_LAUNCHER, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.LINK_GUN, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.ASSAULT_RIFLE, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, false);
		weaponPrefs.addGeneralPref(UT2004ItemType.FLAK_CANNON, true);
		weaponPrefs.addGeneralPref(UT2004ItemType.BIO_RIFLE, true);
    }

    /**
     * Initialization
     */
    @Override
    public Initialize getInitializeCommand() {
        return new Initialize().setName("DeliberativeAgent");
    }
    
    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
       this.gameInfo = gameInfo;
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
        ArrayList<Belief> newBeliefs = new ArrayList<>();
        
        if (senses.isBeingDamaged() && !senses.getLastDamage().isCausedByWorld()) {
            Player p = (Player) getWorldView().get(senses.getLastDamage().getInstigator());
            newBeliefs.add(new BeingDamaged(p));
        }
        if (ctf.isOurFlagHome()) 
            newBeliefs.add(new FlagInBase(ourBase, false));
        
        
        if (ctf.isEnemyFlagHome()) 
            newBeliefs.add(new FlagInBase(enemyBase, true));
        
        
        if (ctf.isOurFlagHeld()) {
            UnrealId holderId = ctf.getOurFlag().getHolder();
            if (holderId != null) {
                Player pl = (Player) getWorldView().get(holderId);
                newBeliefs.add(new CarryingFlag(pl, true));
            } else {
                newBeliefs.add(new CarryingFlag(null, true));
            }
        }
        
        if (ctf.isEnemyFlagHeld() && !ctf.isBotCarryingEnemyFlag()) 
            newBeliefs.add(new CarryingFlag(null, false));
        
        
          if (ctf.isOurFlagDropped()) {
            log.info("Our flag is dropped!");
            ourFlag = ctf.getOurFlag();
            newBeliefs.add(new FlagDropped(ourFlag, false));
        }

        if (ctf.isEnemyFlagDropped()) {
            log.info("Enemy's flag is dropped!");
            enemyFlag = ctf.getEnemyFlag();
            newBeliefs.add(new FlagDropped(enemyFlag, true));
        }

        if (ctf.isBotCarryingEnemyFlag()) 
            newBeliefs.add(new CarryingFlag());
        

        
       
        if (players.canSeeEnemies()) 
            newBeliefs.add(new SeeingEnemy(players.getNearestVisibleEnemy())); 
        
        Collection<NavPoint> visiblePoints = DistanceUtils.getDistanceSorted(this.navPoints.getVisibleNavPoints().values(), this.info.getLocation());
        NavPoint nearestHealth = null;
        NavPoint nearestAmmo = null;
        if (visiblePoints != null) {
            for (NavPoint navp : visiblePoints) {
                if (navp.isInvSpot() && navp.isItemSpawned()) {
                    if (nearestHealth == null && navp.getItemClass().getCategory().equals(ItemType.Category.HEALTH)) {
                        nearestHealth = navp;
                    } else if (nearestAmmo == null && navp.getItemClass().getCategory().equals(ItemType.Category.AMMO)) {
                        if (!weaponry.hasAmmo(navp.getItemClass())) {
                            nearestAmmo = navp;
                        }
                    } else if (navp.getItemClass().getCategory().equals(ItemType.Category.WEAPON)) {
                        if (!weaponry.hasWeapon(navp.getItemClass())) {
                            newBeliefs.add(new SeeingWeapon(navp));
                           
                        }
                    }
                }
            }  
        }   
        
        if(nearestHealth != null)
            newBeliefs.add(new SeeingHealthPack(nearestHealth));
        
        if(nearestAmmo != null)
            newBeliefs.add(new SeeingAmmoPack(nearestAmmo));
        
        if (info.getHealth() < 70) 
            newBeliefs.add(new LowHealth());
        

        if (info.getCurrentAmmo() < weaponry.getMaxAmmo(weaponry.getCurrentWeapon().getDescriptor().getPriAmmoItemType()) * 0.3)
            newBeliefs.add(new LowAmmo());
        
        
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
                 case "BeingDamaged":
                    if (((BeingDamaged) b).byEnemy()) {
                        newDesires.add(new KillEnemy(((BeingDamaged) b).getEnemy(), 10));
                    }
                    break;
                
                case "SeeingEnemy":
                    newDesires.add(new KillEnemy(((SeeingEnemy) b).getEnemy(), 5));
                    break;
                    
//                case "Bored":
//                    newDesires.add(new GoToBase(enemyBase, true, 4));
//                    break;
                case "CarryingFlag":
                    newDesires.add(new CaptureEnemyFlag(enemyFlag, 10));
                    break;
                
                case "EnemyCarryingFlag":
                    if (((CarryingFlag) b).getCarrier() == null) {
                        newDesires.add(new GoToBase(enemyBase, true, 5));
                    } else {
                        log.info("I see him with my flag!!");
                        newDesires.add(new GoToBase(ourBase, true, 1));
                        //newDesires.add(new KillEnemyDesire(((CarryingFlagBelief) b).getCarrier(), 9));
                    }
                    break;
                
                case "FriendCarryingFlag":
                    newDesires.add(new GoToBase(ourBase, true, 6));
                    break;
                
                case "OurFlagDropped":
                    if (((FlagDropped) b).getFlag().getLocation() == null) {
                        if (info.getLocation().getDistance(enemyBase.getLocation()) >= info.getLocation().getDistance(ourBase.getLocation())) {
                            newDesires.add(new GoToBase(enemyBase, true, 4));
                        } else {
                            newDesires.add(new GoToBase(ourBase, false, 4));
                        }
                    } else {
                        
                        newDesires.add(new CaptureOwnFlag(((FlagDropped) b).getFlag(), 8));
                    }
                    break;
                    
                case "EnemyFlagDropped":
                    if (((FlagDropped) b).getFlag().getLocation() == null) {
                        if (info.getLocation().getDistance(enemyBase.getLocation()) >= info.getLocation().getDistance(ourBase.getLocation())) {
                            newDesires.add(new GoToBase(enemyBase, false, 1));
                        } else {
                            newDesires.add(new GoToBase(ourBase, true, 1));
                        }
                    } else {
                        log.info("Belief: Enemy flag dropped and I see it!!");
                        newDesires.add(new CaptureEnemyFlag(((FlagDropped) b).getFlag(), 7));
                    }
                    break;
                case "EnemyFlagInBase":
                    if (!beliefs.contains(new CarryingFlag())) {
                        newDesires.add(new CaptureEnemyFlag(enemyFlag, 2));
                    }
                    break;
                    
                case "FlagInBase":
                    newDesires.add(new GoToBase(ourBase, true, 10));
                    break;
                
                case "SeeingWeapon":
                    newDesires.add(new GrabWeapon(((SeeingWeapon) b).getPoint(), 3));
                    break;
                
                case "SeeingAmmoPack":
                    int priority = 0;
                    if (beliefs.contains(new LowAmmo())) {
                        priority = 11;
                    } else {
                        priority = 6;
                    }
                    NavPoint firstPacket = ((SeeingAmmoPack) b).getPoint();
                    newDesires.add(new GrabAmmo(firstPacket, priority));
                    break;
                
                case "LowHealth":

                    if (beliefs.contains(new SeeingHealthPack(null))) {
                        SeeingHealthPack bel = (SeeingHealthPack) beliefs.get(beliefs.indexOf(new SeeingHealthPack(null)));
                        newDesires.add(new GrabHealth(bel.getPoint(), 12));
                        lastHealthItem = bel.getPoint();
                    } else if (lastHealthItem != null) {
                        newDesires.add(new GrabHealth(lastHealthItem, 12));
                    }
                    break;
                case "LowAmmoBelief":
                    newDesires.add(new ChangeWeapon(11));
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
            
            case "KillEnemy":
                Player p = (Player) intention.getTarget();
    
                if (p != null) {
                    navigation.navigate(p);
                    move.turnTo(p);
                    shoot.shoot(weaponPrefs, p);         
                }
                break;
                
            case "CaptureEnemyFlag":
                if (beliefs.contains(new CarryingFlag())) {
                    log.info("Executing plan: I have the flag and I'm returning to base!");
                    navigation.navigate(ourBase);
                } else if (beliefs.contains(new FlagInBase(enemyBase, true))) {
                    log.info("Executing plan: Flag at the base, going to get it");
                    navigation.navigate(enemyBase);
                } else if (beliefs.contains(new FlagDropped(enemyFlag, true))) {
                    log.info("Executing plan: Enemy flag dropped, going to get it");
                    navigation.navigate(((FlagInfo) intention.getTarget()).getLocation());
                }
                break;
            
            case "CaptureOurFlag":
                Location loc = ((FlagInfo) intention.getTarget()).getLocation();
                if (loc != null) {
                    log.info("Executing plan: Capturing our flag");
                    navigation.navigate(loc);
                }
                break;
            case "GoToEnemyBase":
                log.info("Executing plan: Going to enemy base");
                NavPoint target = (NavPoint) intention.getTarget();
              
                navigation.navigate(target);
                break;
            case "GoToOurBase":
                
                log.info("Executing plan: Going to my base");
                navigation.navigate((NavPoint) intention.getTarget());
                break;
        
            case "GrabWeapon": 
                navigation.navigate((NavPoint) intention.getTarget());
                break;
            
            case "GrabHealth":
                navigation.navigate((NavPoint) intention.getTarget());
                break;
            
            case "GrabAmmo":
                navigation.navigate((NavPoint) intention.getTarget());
                break;
            
            case "ChangeWeapon":
                if (intention.getTarget() == null) {
                    Object[] loadedWeapons = weaponry.getLoadedWeapons().keySet().toArray();
                    weaponry.changeWeapon((ItemType) loadedWeapons[random.nextInt(loadedWeapons.length)]);
                }
            
        }
    
        executingPlan = false;
    }
    
    
    
}
