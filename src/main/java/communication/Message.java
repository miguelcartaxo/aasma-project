/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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
package communication;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotName;
import java.util.EventObject;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author march_000
 */
public class Message{

    private String team;
    private Location location;
    private String botName;
    private String msg;

    public String  getBotName() {
        return botName;
    }

    public void setBotName(String  botName) {
        this.botName = botName;
    }

        public Message() {
        }
    
        public Message(String s, AgentInfo info) {
        this.location = new Location(info.getLocation());
        this.botName = info.getBotName().toString();
        setTeam(info.getTeam());
        this.msg = s;
        }
       
 
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getTeam() {
     return this.team;
    }
    
    public void setTeam(int team) {
        if(team==1)
            this.team="Red";
        else this.team="Blue";
    }
    
    @Override
    public String toString() {
        //ex: HELP;BotName:1,2,3
        return msg + ";" + botName + ":" + location.x + "," +location.y + "," + location.z;
    }
}
