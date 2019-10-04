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

import org.mywms.model.BasicEntity;

import de.wms2.mywms.location.StorageLocation;

/**
 * Handling of strategies for outgoing orders.<br>
 * This strategy is referenced by all types of outgoing orders. (DeliveryOrder,
 * PickingOrder, ...).<br>
 * The fixed flags of this entity are used by the standard services.<br>
 * <p>
 * This class is based on myWMS-LOS:LOSOrderStrategy
 * 
 * @author krane
 */
@Entity
public class OrderStrategy extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(nullable = false, unique = true)
	private String name;

	/**
	 * The destination is used as default value on order creation.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private StorageLocation defaultDestination;

	/**
	 * Send to packing order after picking order is finished
	 */
	@Column(nullable = false)
	private boolean createPackingOrder = false;

	/**
	 * Send to shipping after all other sub-orders are finished
	 */
	@Column(nullable = false)
	private boolean createShippingOrder = true;

	/**
	 * Automatically try to generate new picks, if a there are missing amounts on
	 * the pick-from stock.
	 */
	@Column(nullable = false)
	private boolean createFollowUpPicks = true;

	/**
	 * Allow usage of locked material
	 */
	@Column(nullable = false)
	private boolean useLockedStock = false;

	/**
	 * Allow usage of locked lots
	 */
	@Column(nullable = false)
	private boolean useLockedLot = false;

	/**
	 * Prefer material on not opened unit loads. Overrides FIFO.
	 * <p>
	 * A unit load is marked as opened after the first change of the amount is done.
	 */
	@Column(nullable = false)
	private boolean preferComplete = false;

	/**
	 * A matching amount of a stock unit overwrites FIFO
	 */
	@Column(nullable = false)
	private boolean preferMatching = false;

	/**
	 * Take only not opened unit loads. Overrides FIFO.
	 * <p>
	 * This strategy may cause differences to the requested amount. It will take as
	 * much unit loads until the requested amount is fulfilled.<br>
	 * <p>
	 * A unit load is marked as opened after the first change of the amount is done.
	 */
	private boolean completeOnly = false;

	/**
	 * Generate separated picking orders for every type of picking (partial amount /
	 * complete)
	 */
	@Column(nullable = false)
	private boolean createTypeOrders = true;

	@Column(nullable = false)
	private int manualCreationIndex = 99;

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
		return super.toUniqueString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StorageLocation getDefaultDestination() {
		return defaultDestination;
	}

	public void setDefaultDestination(StorageLocation defaultDestination) {
		this.defaultDestination = defaultDestination;
	}

	public boolean isCreatePackingOrder() {
		return createPackingOrder;
	}

	public void setCreatePackingOrder(boolean createPackingOrder) {
		this.createPackingOrder = createPackingOrder;
	}

	public boolean isCreateShippingOrder() {
		return createShippingOrder;
	}

	public void setCreateShippingOrder(boolean createShippingOrder) {
		this.createShippingOrder = createShippingOrder;
	}

	public boolean isCreateFollowUpPicks() {
		return createFollowUpPicks;
	}

	public void setCreateFollowUpPicks(boolean createFollowUpPicks) {
		this.createFollowUpPicks = createFollowUpPicks;
	}

	public boolean isUseLockedStock() {
		return useLockedStock;
	}

	public void setUseLockedStock(boolean useLockedStock) {
		this.useLockedStock = useLockedStock;
	}

	public boolean isUseLockedLot() {
		return useLockedLot;
	}

	public void setUseLockedLot(boolean useLockedLot) {
		this.useLockedLot = useLockedLot;
	}

	public boolean isPreferComplete() {
		return preferComplete;
	}

	public void setPreferComplete(boolean preferComplete) {
		this.preferComplete = preferComplete;
	}

	public boolean isPreferMatching() {
		return preferMatching;
	}

	public void setPreferMatching(boolean preferMatching) {
		this.preferMatching = preferMatching;
	}

	public boolean isCompleteOnly() {
		return completeOnly;
	}

	public void setCompleteOnly(boolean completeOnly) {
		this.completeOnly = completeOnly;
	}

	public boolean isCreateTypeOrders() {
		return createTypeOrders;
	}

	public void setCreateTypeOrders(boolean createTypeOrders) {
		this.createTypeOrders = createTypeOrders;
	}

	public int getManualCreationIndex() {
		return manualCreationIndex;
	}

	public void setManualCreationIndex(int manualCreationIndex) {
		this.manualCreationIndex = manualCreationIndex;
	}
}
