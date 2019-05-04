/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.mywms.facade.FacadeException;
import org.mywms.service.ConstraintViolatedException;

/**
 * A lot (German: <i>Charge</i>) collects several instances of StockUnits,
 * produced in one batch.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name = "mywms_lot", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {
		"name","itemdata_id" }) })
public class Lot extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	private String name;

	private Date date;

	private ItemData itemData;

	private Date useNotBefore;

	private Date bestBeforeEnd;
	
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal depth;
    private BigDecimal volume;
    private BigDecimal weight;
    private String code;
    private String age;

	@Temporal(TemporalType.DATE)
	public Date getUseNotBefore() {
		return useNotBefore;
	}

	public void setUseNotBefore(Date useNotBefore) {
		this.useNotBefore = useNotBefore;
	}

	@Temporal(TemporalType.DATE)
	public Date getBestBeforeEnd() {
		return bestBeforeEnd;
	}

	public void setBestBeforeEnd(Date bestBeforeEnd) {
		this.bestBeforeEnd = bestBeforeEnd;
	}

	/**
	 * @return the date of the lot
	 */
	@Column(nullable = false, name = "lot_date")
	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date of the lot to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the name of the lot
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name of the lot to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the itemData
	 */
	@ManyToOne(optional = false)
	public ItemData getItemData() {
		return itemData;
	}

	/**
	 * @param itemData
	 *            the itemData to set
	 */
	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	
	
	
	@Column(precision=15, scale=2)
	public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	@Column(precision=15, scale=2)
	public BigDecimal getWidth() {
		return width;
	}
	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	@Column(precision=15, scale=2)
	public BigDecimal getDepth() {
		return depth;
	}
	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

	@Column(precision=19, scale=6)
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}
	
	@Column(precision=16, scale=3)
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public String toUniqueString() {
		return getName() + " (" + getItemData().toUniqueString() + ")";
	}

	
	
	/**
	 * Checks, if following constraints are kept during the previous operations.
	 * 
	 * @throws ConstraintViolatedException
	 */
	@PreUpdate
	@PrePersist
	public void sanityCheck() throws FacadeException {

		if( name != null ) {
			name = name.trim();
		}
		
        if( name != null && name.startsWith("* ") ) {
        	name = name.substring(2);
        }
        
		if( name == null || name.length() == 0 ) {
			throw new BusinessException("Lot name must be set");
		}
		
		if( getAdditionalContent() != null && getAdditionalContent().length() > 255)  {
			setAdditionalContent(getAdditionalContent().substring(0,255));
		}

	}

}
