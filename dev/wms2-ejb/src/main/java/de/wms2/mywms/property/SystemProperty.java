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
package de.wms2.mywms.property;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicEntity;
import org.mywms.model.Client;

import de.wms2.mywms.util.Wms2Constants;

/**
 * System properties.
 * <p>
 * This class replaces myWMS-LOS:LOSSystemProperty
 * <p>
 * The key of a system property table contains of 3 parts<br>
 * - propertyKey: The main key<br>
 * - client: A property may be client-specific. At least the main implementation
 * with the system client has to exist.<br>
 * - propertyContext: An optional further subkey.
 * <p>
 * 
 * @author krane
 *
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "propertykey", "propertycontext", "client_id" }) })
public class SystemProperty extends BasicEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * The key of the property.
	 * <p>
	 * It has not stringently to be unique. Some properties allow context- and
	 * client-specific values.
	 */
	@Column(nullable = false)
	private String propertyKey;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Client client;

	/**
	 * Some properties can have a context specific value.
	 * <p>
	 * Have a look to the properties documentation to check whether this feature is
	 * supported and which context values can be used.
	 */
	@Column(nullable = true)
	private String propertyContext;

	/**
	 * The value of the property
	 */
	@Column(nullable = true)
	private String propertyValue;

	@Column(length = Wms2Constants.FIELDSIZE_DESCRIPTION)
	private String description;

	/**
	 * An optional grouping criteria
	 */
	private String propertyGroup;

	@Override
	public String toString() {
		if (propertyKey != null) {
			return propertyKey;
		}
		return super.toString();
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public void setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getPropertyContext() {
		return propertyContext;
	}

	public void setPropertyContext(String propertyContext) {
		this.propertyContext = propertyContext;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPropertyGroup() {
		return propertyGroup;
	}

	public void setPropertyGroup(String propertyGroup) {
		this.propertyGroup = propertyGroup;
	}
}
