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

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;

/**
 *
 * @author Miguel
 */
public class CarryingFlag extends Belief {
    
    protected boolean byMe;
    protected Player carrier;

    public CarryingFlag(Player carrier, boolean isEnemy) {
        if(isEnemy) {
            super.name = "Enemy" + this.getClass().getSimpleName();
            this.byMe = false;
        } else {
            super.name = "Friend" + this.getClass().getSimpleName();
            this.byMe = false;
        }
        this.carrier = carrier;
    }

    public CarryingFlag() {
        super.name = this.getClass().getSimpleName();
        this.byMe = true;
        this.carrier = null;
    }

    public Player getCarrier() {
        return carrier;
    }

    public void setCarrier(Player carrier) {
        this.carrier = carrier;
    }

    public boolean isCarriedByMe() {
        return byMe;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass() || !obj.getClass().isInstance(this)) {
            return false;
        }
        final CarryingFlag other = (CarryingFlag) obj;

        if (!this.name.equals(other.getName()))
            return false;
        
        if (this.byMe != other.isCarriedByMe()) {
            return false;
        }
        if(this.carrier != null && other.getCarrier() != null && !this.carrier.equals(other.getCarrier())) {
            return false;
        }
        if (this.carrier != other.getCarrier()) {
            return false;
        }
        return true;
    }
}
