/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;
import org.mywms.model.ItemData;

@Entity
@Table(name="los_fixassgn")
@NamedQueries({
	@NamedQuery(name="LOSFixedLocationAssignment.queryByLocation", query="FROM LOSFixedLocationAssignment fa WHERE fa.assignedLocation=:location"),
	@NamedQuery(name="LOSFixedLocationAssignment.queryByItem", query="FROM LOSFixedLocationAssignment fa WHERE fa.itemData=:item"),
	@NamedQuery(name="LOSFixedLocationAssignment.existsByLocation", query="SELECT fa.id FROM LOSFixedLocationAssignment fa WHERE fa.assignedLocation=:location"),
	@NamedQuery(name="LOSFixedLocationAssignment.existsByItem", query="SELECT fa.id FROM LOSFixedLocationAssignment fa WHERE fa.itemData=:item")
})
public class LOSFixedLocationAssignment extends BasicEntity {

	private static final long serialVersionUID = 1L;

	private LOSStorageLocation assignedLocation;
	
	private ItemData itemData;

	private BigDecimal desiredAmount = BigDecimal.ZERO;
	
	@OneToOne(optional=false)
	public LOSStorageLocation getAssignedLocation() {
		return assignedLocation;
	}

	public void setAssignedLocation(LOSStorageLocation assignedLocation) {
		this.assignedLocation = assignedLocation;
	}

	@ManyToOne(optional=false)
	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	@Column(nullable = false, precision=17, scale=4)
	public BigDecimal getDesiredAmount() {
		if (itemData != null){
			try{
				return this.desiredAmount.setScale(itemData.getScale());
			}
			catch(Throwable t){}
		}
		return desiredAmount;
	}

	public void setDesiredAmount(BigDecimal desiredAmount) {
		this.desiredAmount = desiredAmount;
	}

}
