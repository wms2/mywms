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
 * This class is based on myWMS-LOS:LOSStorageStrategy
 * <p>
 * Definition of rules to search a storage location
 */
@Entity
@Table
public class StorageStrategy extends BasicEntity {
	private static final long serialVersionUID = 1L;

	public final static String PROPERY_KEY_DEFAULT_STRATEGY = "STRATEGY_STORAGE_DEFAULT";

	public final static int CLIENT_MODE_IGNORE = 0;
	public final static int CLIENT_MODE_PREFER_OWN = 1;
	public final static int CLIENT_MODE_ONLY_OWN = 2;

	public final static int ORDER_BY_YPOS = 0;
	public final static int ORDER_BY_XPOS = 1;

	public static final int UNDEFINED = -1;
	public static final int FALSE = 0;
	public static final int TRUE = 1;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private boolean useItemZone = false;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private Zone zone;
	@Column(nullable = false)
	private int useStorage = UNDEFINED;
	@Column(nullable = false)
	private int usePicking = UNDEFINED;

	@Column(nullable = false)
	private int clientMode = CLIENT_MODE_IGNORE;
	@Column(nullable = false)
	private int orderByMode = ORDER_BY_YPOS;

	/**
	 * Allow mix items on storage location
	 */
	@Column(nullable = false)
	private boolean mixItem = true;

	/**
	 * Allow mix clients on storage location
	 */
	@Column(nullable = false)
	private boolean mixClient = false;

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

	public boolean isUseItemZone() {
		return useItemZone;
	}

	public void setUseItemZone(boolean useItemZone) {
		this.useItemZone = useItemZone;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public int getClientMode() {
		return clientMode;
	}

	public void setClientMode(int clientMode) {
		this.clientMode = clientMode;
	}

	public int getOrderByMode() {
		return orderByMode;
	}

	public void setOrderByMode(int orderByMode) {
		this.orderByMode = orderByMode;
	}

	public boolean isMixItem() {
		return mixItem;
	}

	public void setMixItem(boolean mixItem) {
		this.mixItem = mixItem;
	}

	public int getUseStorage() {
		return useStorage;
	}

	public void setUseStorage(int useStorage) {
		this.useStorage = useStorage;
	}

	public int getUsePicking() {
		return usePicking;
	}

	public void setUsePicking(int usePicking) {
		this.usePicking = usePicking;
	}

	public boolean isMixClient() {
		return mixClient;
	}

	public void setMixClient(boolean mixClient) {
		this.mixClient = mixClient;
	}

}
