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

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004BotFactory;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 *
 * @author Miguel
 */
public class AgentMain {
    
    public enum AgentType {
    REACTIVE , DELIBERATIVE, HYBRID
    }
    public static void main(String args[]) throws PogamutException {
        
        AgentType type = AgentType.REACTIVE;
        
        if(type == AgentType.REACTIVE){
                new UT2004BotRunner( // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                        ReactiveAgent.class, // which UT2004BotController it should instantiate
                        "ReactiveAgentRed" // what name the runner should be using
                        ).setMain(true) // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
                        .startAgents(new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED)
                                );
              
        } else if(type == AgentType.DELIBERATIVE){
              new UT2004BotRunner( // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                        DeliberativeAgent.class, // which UT2004BotController it should instantiate
                        "DeliberativeAgent" // what name the runner should be using
                        ).setMain(true) // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
                        .startAgents(new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED)
                                );
        } else if (type == AgentType.HYBRID){
               new UT2004BotRunner( // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                        HybridAgent.class, // which UT2004BotController it should instantiate
                        "HybridAgent" // what name the runner should be using
                        ).setMain(true) // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
                        .startAgents(new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED)
                                );
        } else{
            System.out.println("I DONT HAVE AN AGENT TYPE");
        }
              

        
        
    }
    
}
