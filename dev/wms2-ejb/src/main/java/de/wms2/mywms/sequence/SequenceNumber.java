/* 
Copyright 2019 Matthias Krane

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
package de.wms2.mywms.sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 * Data structure to store sequences. Master data and the current state.
 * 
 * @author krane
 *
 */
@Entity
@Table
public class SequenceNumber extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String name;

	/**
	 * The current counter
	 */
	@Column(nullable = false)
	private long counter = 0;

	/**
	 * Format instruction to generate the sequence number.
	 * <p>
	 * The number is formated with the Java String.format() operation. Have a look
	 * to the Java documentation to get more information.
	 * <p>
	 * These parameters can be used in the format instruction:<br>
	 * - %1 the current counter<br>
	 * - %2 the current date
	 */
	private String format = "%1$04d";

	/**
	 * The starting value of the counter
	 */
	@Column(nullable = false)
	private long startCounter = 0;

	/**
	 * The ending value of the counter
	 */
	@Column(nullable = false)
	private long endCounter = 9999;

	@Override
	public String toString() {
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

	public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public long getStartCounter() {
		return startCounter;
	}

	public void setStartCounter(long startCounter) {
		this.startCounter = startCounter;
	}

	public long getEndCounter() {
		return endCounter;
	}

	public void setEndCounter(long endCounter) {
		this.endCounter = endCounter;
	}

}