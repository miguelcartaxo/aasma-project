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
import java.util.EventObject;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author march_000
 */
public class Message{
   
    private String msg;
    private Location location;
    private String id;
    private boolean direct;
    private String botName;

    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public Message() {
    }
    
        public Message(String s) {
   
        s=s.replace(",", "");

        StringTokenizer st = new StringTokenizer(s, " ");

        this.location = new Location(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken()));
        this.id = st.nextToken();
        this.direct = Boolean.parseBoolean(st.nextToken());
        this.botName = st.nextToken();
        }
       
 
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDirect() {
        return direct;
    }

    public void setDirect(boolean Direct) {
        this.direct = Direct;
    }

    @Override
    public String toString() {
        return msg + " from " + botName + " in: " + location.x + ", " +location.y + ", " + location.z+ ", " + id + ", " + direct + ", " + botName;
    }



}
