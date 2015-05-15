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
package pt.tecnico.aasma.desires;

import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 *
 * @author Miguel
 */
public class Intention {
    
    String name = "";
    
    /* Target to which the intention applies */
    IWorldObject target = null;
    Location l;
     public Intention(String name, IWorldObject target) {
       this.name = name;
       this.target = target;
    }
     
     public Intention(String name, Location target) {
       this.name = name;
       this.l = target;
    }
     
     public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IWorldObject getTarget() {
        return target;
    }

    public void setTarget(IWorldObject target) {
        this.target = target;
    }
    
     public Location getLocation() {
        return l;
    }

    public void setTarget(Location l) {
        this.l = l;
    }
    
}
