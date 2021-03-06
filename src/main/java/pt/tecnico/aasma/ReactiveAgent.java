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


import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorState;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathAutoFixer;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.ASSAULT_RIFLE;
import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.BIO_RIFLE;
import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.FLAK_CANNON;
import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.LIGHTNING_GUN;
import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.LINK_GUN;
import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.MINIGUN;
import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.ROCKET_LAUNCHER;
import static cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType.SHOCK_RIFLE;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Rotate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Stop;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StopShooting;


import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Bumped;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FlagInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;

import cz.cuni.amis.utils.exception.PogamutException;

//flag info
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
//import org.jcp.xml.dsig.internal.dom.Utils;



/**
 *
 * @author Miguel
 */

public class ReactiveAgent extends UT2004BotModuleController<UT2004Bot>{
    
    protected TabooSet<Item> tabooItems = null;
    
    private static String[] teamColors = new String[] {"Red", "Blue", "Green", "Gold"};
    
    protected Item item = null;
    
    public boolean shouldTakeFlag = true;
    
    public boolean shouldFight = true;
    public boolean shouldReturnBaseFlag = true;
    protected boolean runningToPlayer = false;
    private boolean takeBack = false;
    public boolean shouldGrabItems = true;
    private FlagInfo ourFlag;
    private FlagInfo enemyFlag;
    protected Player enemy = null;
    private Location home;
    
    private UT2004PathAutoFixer autoFixer;
    
    protected int notMoving;
    protected State previousState = State.OTHER;
    protected static enum State {

        FIGHT, RETURN_BASE_FLAG, TAKE_FLAG, GRAB, OTHER
    }
    
    private void randomStrafe() {
        switch (random.nextInt(3)) {
            case 0:
                body.getLocomotion().strafeLeft(300.0);
                break;
            case 1:
                body.getLocomotion().strafeRight(300.0);
                break;
           /* case 2:
                body.getLocomotion().moveContinuos();
                break;*/
            case 2:
                body.getLocomotion().turnHorizontal(random.nextInt(100));
                
       
        }
        
    }
    /**
     * Listener called when someone/something bumps into the bot. The bot
     * responds by moving in the opposite direction than the bump come from.
     * 
     */
    @EventListener(eventClass = Bumped.class)
    protected void bumped(Bumped event) {
        // schema of the vector computations
        //
        //  e<->a<------>t
        //  |   |   v    |
        //  |   |        target - bot will be heading there
        //  |   getLocation()
        //  event.getLocation()

        Location v = event.getLocation().sub(bot.getLocation()).scale(5);
        Location target = bot.getLocation().sub(v);

        // make the bot to go to the computed location while facing the bump source
        move.strafeTo(target, event.getLocation());
    }
    
    /**
     * Listener called when a player appears. 
     * 
     */
    @ObjectClassEventListener(eventClass = WorldObjectAppearedEvent.class, objectClass = Player.class)
    protected void playerAppeared(WorldObjectAppearedEvent<Player> event) {
        // greet player when he appears
        body.getCommunication().sendGlobalTextMessage("Hello " + event.getObject().getName() + "!");
    }
    
    /**
     * Flag indicating whether the player was also close to the bot last time it
     * was updated.
     */
    protected boolean wasCloseBefore = false;
    
    /**
     * Listener called each time a player is updated. 
     */
    @ObjectClassEventListener(eventClass = WorldObjectUpdatedEvent.class, objectClass = Player.class)
    protected void playerUpdated(WorldObjectUpdatedEvent<Player> event) {
        // Check whether the player is closer than 5 bot diameters.
        // Notice the use of the UnrealUtils class.
        // It contains many auxiliary constants and methods.
        Player player = event.getObject();
        // First player objects are received in HandShake - at that time we don't have Self message yet or players location!!
        if (player.getLocation() == null || info.getLocation() == null) {
            return;
        }
        if (player.getLocation().getDistance(info.getLocation()) < (UnrealUtils.CHARACTER_COLLISION_RADIUS * 10)) {
            // If the player wasn't close enough the last time this listener was called,
            // then ask him what does he want.
            if (!wasCloseBefore) {
                body.getCommunication().sendGlobalTextMessage("What do you want " + player.getName() + "?");
                // Set proximity flag to true.
                wasCloseBefore = true;
            }
        } else {
            // Otherwise set the proximity flag to false.
            wasCloseBefore = false;
        }
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
								navigation.navigate(ctf.getOurBase());
                                                                 body.getCommunication().sendGlobalTextMessage("IM STUCK IN PREPARE BOT!");
								break;

							default:
								System.out.println("WHATS THIS!!");
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

    
    @Override
    public Initialize getInitializeCommand() {
        int maxTeams = this.game.getMaxTeams();

        System.out.println("teams: " + maxTeams);
        int team = random.nextInt(maxTeams);
        home = getTeamBase(team).getLocation();
         return new Initialize().setName("ReactiveAgent");
    }
    
   
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        // notify the world (i.e., send message to UT2004) that the bot is up and running
        body.getCommunication().sendGlobalTextMessage("I am alive!");
    }
    
    /**
     * This method is called only once right before actual logic() method is
     * called for the first time.
     *
     */
    @Override
    public void beforeFirstLogic() {
    }
    
   
    @Override
    @SuppressWarnings("empty-statement")
    public void logic() throws PogamutException {
        
      
        if (!info.isMoving()) {
            ++notMoving;
            if (notMoving > 25) {
                
                navigation.navigate(ctf.getEnemyBase());
                body.getCommunication().sendGlobalTextMessage("IM STUCK IN LOGIC!");
                return;
            }
        }
      
       if ((shouldTakeFlag && !ctf.isOurTeamCarryingEnemyFlag() && !ctf.isBotCarryingEnemyFlag()
                || (shouldTakeFlag && ctf.isOurFlagDropped()) || (shouldTakeFlag && ctf.isEnemyFlagHome())
                || (shouldTakeFlag && ctf.isEnemyFlagDropped())) && enemyFlag != null && !senses.isBeingDamaged()) {
            this.stateTakeFlag();
        }
       
         
        if (shouldReturnBaseFlag && ctf.isBotCarryingEnemyFlag() && (enemyFlag != null || ourFlag != null) && !senses.isBeingDamaged()) {
            this.stateReturnBaseFlag();
            return;
        }
       
        if (shouldFight && players.canSeeEnemies() && weaponry.hasLoadedWeapon()) {
            this.stateFight();
            return;
        }
        
        if (senses.isBeingDamaged()) {
            this.stateHit();
            return;
        }
        
        if (shouldGrabItems && !items.getVisibleItems().isEmpty()) {
            stateSeeItem();
            previousState = State.GRAB;
            return;
        }
    }
    
    @Override
    public void botKilled(BotKilled event) {
        
        body.getCommunication().sendGlobalTextMessage("I was KILLED!");

    }


    public FlagInfo getEnemyFlag() {
        return enemyFlag;
    }

    public FlagInfo getOurFlag() {
        return ourFlag;
    }
    
    protected void stateTakeFlag() {
         previousState = State.TAKE_FLAG;

        if(takeBack) {
            if (ourFlag != null) {
                if (ourFlag.isVisible()) {
                    navigation.navigate(ourFlag.getLocation());
                }
            }
        } else if (enemyFlag != null) {
            if (enemyFlag.isVisible()) {
               navigation.navigate(enemyFlag.getLocation());
            }
        }

    }
    
    @ObjectClassEventListener(eventClass = WorldObjectAppearedEvent.class, objectClass = FlagInfo.class)
    protected void flagEncountered(WorldObjectAppearedEvent<FlagInfo> event) {

        if (event.getObject().getTeam() != info.getTeam()) {
            enemyFlag = event.getObject();

            takeBack = false;

        }
        if (event.getObject().getTeam() == info.getTeam()) {
            ourFlag = event.getObject();
            if (ctf.isBotCarryingEnemyFlag()) {
                takeBack = true;
            }

        }
       
        body.getCommunication().sendGlobalTextMessage("Found the flag of team " + event.getObject().getTeam());
    }
    
    protected void stateReturnBaseFlag() throws IllegalStateException {
   
        previousState = State.RETURN_BASE_FLAG;
        
        if (ourFlag != null) {
            //log.log(Level.INFO, "OUR FLAG:{0}", ourFlag);
            move.moveTo(home);
        }
    }
    
    public NavPoint getTeamBase(int team) {
        String flagBaseStr = "x" + teamColors[team] + "FlagBase0";

        for (NavPoint navpoint : bot.getWorldView().getAll(NavPoint.class).values()) {
            if (navpoint.getId().getStringId().contains(flagBaseStr)) {
                return navpoint;
            }
        }
        throw new IllegalStateException("Unable to find base for " + teamColors[team] + " team.");
    }
    
    protected void stateFight() {
       

        boolean shooting = false;
        double distance = Double.MAX_VALUE;

        
        if ( previousState != State.FIGHT || enemy == null || !enemy.isVisible()) {
           
            enemy = players.getNearestVisiblePlayer(players.getVisibleEnemies()
                    .values());
            if (enemy == null) {
                log.info("Can't see any enemies... ???");
                return;
            }
            if (info.isShooting()){
                getAct().act(new StopShooting());
            }
            runningToPlayer = false;
        }

        if (enemy != null) {
            
            distance = info.getLocation().getDistance(enemy.getLocation());

            if (shoot.shoot(weaponPrefs, enemy) != null) {
                log.info("Shooting at enemy!!!");
                shooting = true;
            }
            
        }

        int decentDistance = Math.round(random.nextFloat() * 800) + 200;
        
        if (!enemy.isVisible() || !shooting || decentDistance < distance) {
            if (!runningToPlayer) {
                navigation.navigate(players.getNearestVisibleEnemy());
                runningToPlayer = true;
            }
        } else {
            runningToPlayer = false;
            navigation.getPathExecutor().stop();
            getAct().act(new Stop());
        }
        
        previousState = State.FIGHT;
    }
    
     protected void stateHit() {

        getAct().act(new Rotate().setAmount(32000));
        previousState = State.OTHER;
    }
     
     protected void stateSeeItem() {
        
         body.getCommunication().sendGlobalTextMessage("I got to get that item");
        log.info("I got to get that item");
        if (previousState != State.GRAB) {
            item = DistanceUtils.getNearest(items.getVisibleItems().values(),
                    info.getLocation());
           if (item.getLocation().getDistance(info.getLocation()) < 300) {
                getAct().act(new Move().setFirstLocation(item.getLocation()));
            } else {
                navigation.navigate(item);
               
            }
           
        }

    }
}