/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.location.StorageLocation;

/**
 * Handling of strategies for outbound orders.<br>
 * This strategy is referenced by all types of outbound orders. (CustomerOrder, PickingOrder, ...).<br>
 * The fixed flags of this entity are used by the standard services.<br>
 *  
 * 
 * 
 * @author krane
 */
@Entity
@Table(name = "los_orderstrat", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {"name","client_id" }) 
})
public class LOSOrderStrategy extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;
	
	public static final String KEY_DEFAULT_STRATEGY = "STRATEGY_ORDER_DEFAULT";
	public static final String KEY_EXTINGUISH_STRATEGY = "STRATEGY_ORDER_EXTINGUISH";
	
    private String name;

    /**
     * The destination is used as default value on order creation.
     */
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
    private StorageLocation defaultDestination;
    

    
    /**
     * If TRUE, a goods out order is created, when the customer order is picked complete
     */
	@Column(nullable = false)
    private boolean createGoodsOutOrder = true;
    
    /**
     * Handling of amount differences.<br>
     * If TRUE, on amount differences is tried to find an alternatively pick-from-stock.<br>
     * If FALSE, the order position is finished with differences.<br>
     * Default value is false. No useful follow up pick can be created for manual created orders.
     */
	@Column(nullable = false)
    private boolean createFollowUpPicks = true;
    
    /**
     * The index to be listed in manual creation selection.<br>
     * It is the order of the GUI selection field.<br>
     * Values must be >= 0. Others are not available in the selection field. 
     */
	@Column(nullable = false)
    private int manualCreationIndex = 99;
    
    /**
     * If true, locked stock will be used for picking
     */
	@Column(nullable = false)
    private boolean useLockedStock = false;

    /**
     * If true, locked lots will be used for picking
     */
	@Column(nullable = false)
    private boolean useLockedLot = false;
    
    /**
     * An unopened stock (complete unit load) will be preferred. Overrides FIFO.
     */
	@Column(nullable = false)
    private boolean preferUnopened = true;

    /**
     * A matching amount of a stock unit overwrites FIFO
     */
	@Column(nullable = false)
    private boolean preferMatchingStock= true;


    
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
	
	public boolean isCreateGoodsOutOrder() {
		return createGoodsOutOrder;
	}
	public void setCreateGoodsOutOrder(boolean createGoodsOutOrder) {
		this.createGoodsOutOrder = createGoodsOutOrder;
	}
	
	public boolean isUseLockedLot() {
		return useLockedLot;
	}
	public void setUseLockedLot(boolean useLockedLot) {
		this.useLockedLot = useLockedLot;
	}

	public boolean isPreferUnopened() {
		return preferUnopened;
	}
	public void setPreferUnopened(boolean preferUnopened) {
		this.preferUnopened = preferUnopened;
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
	
	public int getManualCreationIndex() {
		return manualCreationIndex;
	}
	public void setManualCreationIndex(int manualCreationIndex) {
		this.manualCreationIndex = manualCreationIndex;
	}

	public boolean isPreferMatchingStock() {
		return preferMatchingStock;
	}
	public void setPreferMatchingStock(boolean preferMatchingStock) {
		this.preferMatchingStock = preferMatchingStock;
	}
	
	@Override
    public String toUniqueString() {
        return getName();
    }
}
