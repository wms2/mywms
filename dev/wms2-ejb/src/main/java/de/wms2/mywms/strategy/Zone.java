/* 
This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.strategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 * This class replaces myWMS:Zone
 * <p>
 * Zones are used for ABC-Classifications
 */
@Entity
@Table
public class Zone extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String name = "";

	/**
	 * If the current zone is not valid the overflow zone is used
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private Zone nextZone;

	@Override
	public String toString() {
		if (name != null) {
			return name;
		}
		return super.toString();
	}

	@Override
	public String toUniqueString() {
		if (name != null) {
			return name;
		}
		return super.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Zone getNextZone() {
		return nextZone;
	}

	public void setNextZone(Zone nextZone) {
		this.nextZone = nextZone;
	}

}
