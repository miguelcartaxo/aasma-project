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


import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;


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
import org.jcp.xml.dsig.internal.dom.Utils;



/**
 *
 * @author Miguel
 */

public class ReactiveAgent extends UT2004BotModuleController{
    
    protected TabooSet<Item> tabooItems = null;
    
    private static String[] teamColors = new String[] {"Red", "Blue", "Green", "Gold"};
    
    protected Item item = null;
    
    public boolean shouldTakeFlag = true;
    
    public boolean shouldReturnBaseFlag = true;
    
    private boolean takeBack = false;
    
    private FlagInfo ourFlag;
    private FlagInfo enemyFlag;
    
    private Location home;
    
    
    
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
        body.getCommunication().sendGlobalTextMessage("F U " + event.getObject().getName() + "!");
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
    
    /**
     * Initialize all necessary variables here, before the bot actually receives
     * anything from the environment.
     */
    @Override
    public void prepareBot(UT2004Bot bot) {
        
        
    }
    
    
    @Override
    public Initialize getInitializeCommand() {
        int maxTeams = this.game.getMaxTeams();
        System.out.println("teams: " + maxTeams);
        int team = random.nextInt(maxTeams + 1);
        home = getTeamBase(team).getLocation();
        return new Initialize().setName("ReactiveAgent").setTeam(
                team);
        //ADICIONAR AQUI BOTS PARA CADA EQUIPA???
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
        
       if ((shouldTakeFlag && !ctf.isOurTeamCarryingEnemyFlag() && !ctf.isBotCarryingEnemyFlag()
                || (shouldTakeFlag && ctf.isOurFlagDropped()) || (shouldTakeFlag && ctf.isEnemyFlagHome())
                || (shouldTakeFlag && ctf.isEnemyFlagDropped())) && enemyFlag != null && !senses.isBeingDamaged()) {
            this.stateTakeFlag();
        }
       
       
       if (shouldReturnBaseFlag && ctf.isBotCarryingEnemyFlag() && (enemyFlag != null || ourFlag != null)) {
            this.stateReturnBaseFlag();
        }
    }
    
    @Override
    public void botKilled(BotKilled event) {
        
        body.getCommunication().sendGlobalTextMessage("I was KILLED!");

    }

//flag info
    public FlagInfo getEnemyFlag() {
        return enemyFlag;
    }

    public FlagInfo getOurFlag() {
        return ourFlag;
    }
    
    protected void stateTakeFlag() {
        

        if(takeBack) {
            if (ourFlag != null) {
                if (ourFlag.isVisible()) {
                    move.moveTo(ourFlag.getLocation());
                }
            }
        } else if (enemyFlag != null) {
            if (enemyFlag.isVisible()) {
                move.moveTo(enemyFlag.getLocation());
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
    
    
}
    