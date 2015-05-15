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
    REACTIVE , BRAVE_DELIBERATIVE, COWARD_DELIBERATIVE, HYBRID
    }
    public static void main(String args[]) throws PogamutException {
        
        AgentType type = AgentType. COWARD_DELIBERATIVE;
        
        if(type == AgentType.REACTIVE){
                new UT2004BotRunner( 
                        ReactiveAgent.class, 
                        "ReactiveAgentRed" 
                        ).setMain(true) 
                        .startAgents(new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                        new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                        new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                        new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                        new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE)
                              
                               
                                );
              
        } else if(type == AgentType.BRAVE_DELIBERATIVE){
              new UT2004BotRunner(
                        BraveDeliberativeAgent.class, 
                        "BraveDeliberativeAgent"
                        ).setMain(true)
                        .startAgents(new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED)
                                );
              
        } else if (type == AgentType.COWARD_DELIBERATIVE){
               new UT2004BotRunner( 
                        CowardDeliberativeAgent.class, 
                        "CowardDeliberativeAgent" 
                        ).setMain(true) 
                        .startAgents(new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                        new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_RED),
                                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                                        new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                                        new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                                        new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE)
                                
                                );
               
        } else if (type == AgentType.HYBRID){
               new UT2004BotRunner( 
                        HybridAgent.class, 
                        "HybridAgent"
                        ).setMain(true) 
                        .startAgents(new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
                                new UT2004BotParameters().setAgentId(new AgentId("CTF-BOT")).setTeam(AgentInfo.TEAM_BLUE),
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
