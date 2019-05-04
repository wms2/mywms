/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.mywms.model.Area;

/**
 *
 * @author trautm
 */
@Entity
public class LOSArea extends Area {
    
	private static final long serialVersionUID = 1L;
	
	private boolean useForGoodsIn = false;
	private boolean useForGoodsOut = false;
	private boolean useForStorage = false;
	private boolean useForPicking = false;
    private boolean useForTransfer = false;
    private boolean useForReplenish = false;


    @Column(nullable=false)
	public boolean isUseForGoodsIn() {
		return useForGoodsIn;
	}
	public void setUseForGoodsIn(boolean useForGoodsIn) {
		this.useForGoodsIn = useForGoodsIn;
	}

    @Column(nullable=false)
	public boolean isUseForGoodsOut() {
		return useForGoodsOut;
	}
	public void setUseForGoodsOut(boolean useForGoodsOut) {
		this.useForGoodsOut = useForGoodsOut;
	}

    @Column(nullable=false)
	public boolean isUseForStorage() {
		return useForStorage;
	}
	public void setUseForStorage(boolean useForStorage) {
		this.useForStorage = useForStorage;
	}

    @Column(nullable=false)
	public boolean isUseForPicking() {
		return useForPicking;
	}
	public void setUseForPicking(boolean useForPicking) {
		this.useForPicking = useForPicking;
	}   

    @Column(nullable=false)
	public boolean isUseForTransfer() {
		return useForTransfer;
	}
	public void setUseForTransfer(boolean useForTransfer) {
		this.useForTransfer = useForTransfer;
	}
	
    @Column(nullable=false)
	public boolean isUseForReplenish() {
		return useForReplenish;
	}
	public void setUseForReplenish(boolean useForReplenish) {
		this.useForReplenish = useForReplenish;
	}
}
