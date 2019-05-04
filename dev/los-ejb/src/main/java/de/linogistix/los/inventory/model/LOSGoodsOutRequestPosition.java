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

import de.linogistix.los.location.model.LOSUnitLoad;

@Entity
@Table(name = "los_outpos")
public class LOSGoodsOutRequestPosition extends BasicEntity {
	
	private static final long serialVersionUID = 1L;

	private LOSUnitLoad source;
	
	private LOSGoodsOutRequest goodsOutRequest;
	
	private LOSGoodsOutRequestPositionState outState;
	
	public void setSource(LOSUnitLoad source) {
		this.source = source;
	}

	@ManyToOne(optional=false)
	public LOSUnitLoad getSource() {
		return source;
	}


	public void setGoodsOutRequest(LOSGoodsOutRequest goodsOutRequest) {
		this.goodsOutRequest = goodsOutRequest;
	}

	@ManyToOne(optional=false)
	public LOSGoodsOutRequest getGoodsOutRequest() {
		return goodsOutRequest;
	}

	public void setOutState(LOSGoodsOutRequestPositionState outState) {
		this.outState = outState;
	}

	@Enumerated(EnumType.STRING)
	public LOSGoodsOutRequestPositionState getOutState() {
		return outState;
	}
	
	@Override
	public String toUniqueString() {
		return source == null ? "ID-"+getId() : source.getLabelId();
	}
}
