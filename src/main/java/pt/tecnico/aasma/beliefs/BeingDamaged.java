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
public class BeingDamaged extends Belief{
    
    protected boolean byEnemy;
    protected boolean byWorld;
    protected Player enemy;

    public BeingDamaged() {
        super.name = this.getClass().getSimpleName();
        this.byEnemy = false;
        this.byWorld = true;
        this.enemy = null;
    }

    public BeingDamaged(Player enemy) {
        super.name = this.getClass().getSimpleName();
        this.byEnemy = true;
        this.byWorld = false;
        this.enemy = enemy;
    }

    public boolean byEnemy() {
        return byEnemy;
    }

    public void byEnemy(boolean byEnemy) {
        this.byEnemy = byEnemy;
    }

    public boolean byWorld() {
        return byWorld;
    }

    public void byWorld(boolean byWorld) {
        this.byWorld = byWorld;
    }

    public Player getEnemy() {
        return enemy;
    }

    public void setEnemy(Player enemy) {
        this.enemy = enemy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass() || !obj.getClass().isInstance(this)) {
            return false;
        }
        final BeingDamaged other = (BeingDamaged) obj;
        if (!this.name.equals(other.getName()))
            return false;
        
        if (this.byEnemy != other.byEnemy()) {
            return false;
        }
        if (this.byWorld != other.byWorld()) {
            return false;
        }
        if (this.enemy == null || other.getEnemy() == null || !this.enemy.equals(other.getEnemy())) {
            return false;
        }
        return true;
    }
}
