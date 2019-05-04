/*
 * Copyright (c) 2010 - 2013 LinogistiX GmbH
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

import org.mywms.model.BasicEntity;
import org.mywms.model.Zone;

/**
 * @author krane
 *
 */
@Entity
@Table(name="los_storagestrat")
public class LOSStorageStrategy extends BasicEntity {
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
	

	private String name;
	 
	private boolean useItemZone = false;
	private Zone zone;
	private int useStorage = UNDEFINED;
	private int usePicking = UNDEFINED;

	private int clientMode = CLIENT_MODE_IGNORE;
	private int orderByMode = ORDER_BY_YPOS;
	
	/**
	 * Allow mix items on storage location 
	 */
	private boolean mixItem = true;
	
	/**
	 * Allow mix clients on storage location 
	 */
	private boolean mixClient = false;
	
	@Column(nullable = false, unique=true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    @Column(nullable = false)
	public boolean isUseItemZone() {
		return useItemZone;
	}
	public void setUseItemZone(boolean useItemZone) {
		this.useItemZone = useItemZone;
	}
	
	@ManyToOne(optional=true, fetch=FetchType.LAZY)
	public Zone getZone() {
		return zone;
	}
	public void setZone(Zone zone) {
		this.zone = zone;
	}
	
	@Column(nullable = false)
	public int getClientMode() {
		return clientMode;
	}
	public void setClientMode(int clientMode) {
		this.clientMode = clientMode;
	}

	@Column(nullable = false)
	public int getOrderByMode() {
		return orderByMode;
	}
	public void setOrderByMode(int orderByMode) {
		this.orderByMode = orderByMode;
	}
	
	@Column(nullable = false)
	public boolean isMixItem() {
		return mixItem;
	}
	public void setMixItem(boolean mixItem) {
		this.mixItem = mixItem;
	}
	@Column(nullable = false)
	public int getUseStorage() {
		return useStorage;
	}
	public void setUseStorage(int useStorage) {
		this.useStorage = useStorage;
	}
	@Column(nullable = false)
	public int getUsePicking() {
		return usePicking;
	}
	public void setUsePicking(int usePicking) {
		this.usePicking = usePicking;
	}
	@Column(nullable = false)
	public boolean isMixClient() {
		return mixClient;
	}
	public void setMixClient(boolean mixClient) {
		this.mixClient = mixClient;
	}
	@Override
	public String toUniqueString() {
		return name;
	}

	
	
}
