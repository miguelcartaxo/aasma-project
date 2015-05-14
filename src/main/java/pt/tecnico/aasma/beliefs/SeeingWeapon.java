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
package pt.tecnico.aasma.beliefs;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/**
 *
 * @author Miguel
 */
public class SeeingWeapon extends Belief {
    
    protected NavPoint point;

    public SeeingWeapon(NavPoint point) {
        super.name = this.getClass().getSimpleName();
        this.point = point;
    }

    public NavPoint getPoint() {
        return point;
    }

    public void setPoint(NavPoint point) {
        this.point = point;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass() || !obj.getClass().isInstance(this)) {
            return false;
        }
        final SeeingWeapon other = (SeeingWeapon) obj;
        if (!this.name.equals(other.getName()))
            return false;
        
        if (this.point == null || other.getPoint() == null || !this.point.getItemClass().equals(other.getPoint().getItemClass())) {
            return false;
        }
        return true;
    }
    
}
