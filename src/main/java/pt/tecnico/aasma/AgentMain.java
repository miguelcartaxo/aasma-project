package pt.tecnico.aasma;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
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
        
        AgentType type = AgentType.DELIBERATIVE;
        
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
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE)
//                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
//                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
//                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
//                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
//                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED)
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
