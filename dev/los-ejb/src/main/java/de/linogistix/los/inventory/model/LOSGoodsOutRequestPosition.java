/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

import de.wms2.mywms.inventory.UnitLoad;

@Entity
@Table(name = "los_outpos")
public class LOSGoodsOutRequestPosition extends BasicEntity {
	
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional=false)
	private UnitLoad source;
	
	@ManyToOne(optional=false)
	private LOSGoodsOutRequest goodsOutRequest;
	
	@Enumerated(EnumType.STRING)
	private LOSGoodsOutRequestPositionState outState;
	
	public void setSource(UnitLoad source) {
		this.source = source;
	}

	public UnitLoad getSource() {
		return source;
	}


	public void setGoodsOutRequest(LOSGoodsOutRequest goodsOutRequest) {
		this.goodsOutRequest = goodsOutRequest;
	}

	public LOSGoodsOutRequest getGoodsOutRequest() {
		return goodsOutRequest;
	}

	public void setOutState(LOSGoodsOutRequestPositionState outState) {
		this.outState = outState;
	}

	public LOSGoodsOutRequestPositionState getOutState() {
		return outState;
	}
	
	@Override
	public String toUniqueString() {
		return source == null ? "ID-"+getId() : source.getLabelId();
	}
}
