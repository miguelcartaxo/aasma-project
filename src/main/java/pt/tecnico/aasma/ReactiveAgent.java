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
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;


import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Bumped;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;

import cz.cuni.amis.utils.exception.PogamutException;

/**
 *
 * @author Miguel
 */
public class ReactiveAgent extends UT2004BotModuleController{
    
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
        return new Initialize().setName("ReactiveAgent").setTeam(
                random.nextInt(maxTeams + 1));
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
    }
    
    @Override
    public void botKilled(BotKilled event) {
        
        body.getCommunication().sendGlobalTextMessage("I was KILLED!");

    }
    
}
