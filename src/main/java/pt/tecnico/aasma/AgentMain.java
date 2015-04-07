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

import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.exception.PogamutException;
import java.util.List;

/**
 *
 * @author Miguel
 */
public class AgentMain {
    
    public static void main(String args[]) throws PogamutException {
        new UT2004BotRunner( // class that wrapps logic for bots executions, suitable to run single bot in single JVM
                ReactiveAgent.class, // which UT2004BotController it should instantiate
                "ReactiveAgent" // what name the runner should be using
                ).setMain(true) // tells runner that is is executed inside MAIN method, thus it may block the thread and watch whether agent/s are correctly executed
                .startAgents(4);
    }
    
}
