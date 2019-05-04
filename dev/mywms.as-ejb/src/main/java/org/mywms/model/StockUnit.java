/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.mywms.service.ConstraintViolatedException;

/**
 * The stock unit defines a concrete entity of an item (ItemData). A stock unit
 * is bound to a unit load.
 * 
 * @see ItemData
 * @see UnitLoad
 * @author <a href="http://community.mywms.de/developer.jsp">Olaf Krause</a>
 * @version $Revision$ provided by $Author$
 */
@Entity
//dgrys portierung wildfly 8.2, PrePersist doesn't work because id always null, labelId is not needed
//@Table(name = "mywms_stockunit",
//		uniqueConstraints = { 
//		@UniqueConstraint(columnNames = {
//		"labelId","itemdata_id" }) })
@Table(name = "mywms_stockunit")
public class StockUnit extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	private ItemData itemData;

	private BigDecimal amount = new BigDecimal(0);

	private BigDecimal reservedAmount = new BigDecimal(0);

	private UnitLoad unitLoad;

	//dgrys portierung auf wildfly 8.2
	//private String labelId;

	private Lot lot;

	private String serialNumber;

	private Date strategyDate = new Date();

	/**
	 * @return Returns the amount.
	 */
	@Column(nullable = false, precision=17, scale=4)
	public BigDecimal getAmount() {
		if (itemData != null){
			try{
				return this.amount.setScale(itemData.getScale());
			}catch(ArithmeticException ae){
				System.out.println("------- LE "+getId()+" : expected scale = "+itemData.getScale()+" but was "+amount);
				return this.amount;
			}
		} else{
			return this.amount;
		}
	}

	/**
	 * @param amount
	 *            The amount to set.
	 * @throws ConstraintViolatedException 
	 * @throws ConstraintViolatedException 
	 * @throws ConstraintViolatedException 
	 */
	public void setAmount(BigDecimal amount) {
		
		if (amount == null ) {
			throw new NullPointerException();
		}
		
		if (amount.compareTo(new BigDecimal(0)) < 0) {
			throw new BusinessException(
					"the amount of a stock unit must not be smaller than 0");
		}
	
		this.amount = amount;
	}
	
	@Transient
	public ItemUnit getItemUnit(){
		if( itemData == null )
			return null;
		
		return itemData.getHandlingUnit();
	}

	/**
	 * @return Returns the itemData.
	 */
	@ManyToOne(optional = false)
	public ItemData getItemData() {
		return this.itemData;
	}

	/**
	 * @param itemData
	 *            The itemData to set.
	 */
	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	/**
	 * @return Returns the unitLoad.
	 */
	@ManyToOne(optional = false)
	public UnitLoad getUnitLoad() {
		return this.unitLoad;
	}

	/**
	 * @param unitLoad
	 *            The unitLoad to set.
	 */
	public void setUnitLoad(UnitLoad unitLoad) {
		this.unitLoad = unitLoad;
	}

	//dgrys portierung auf wildfly 8.2
//	/**
//	 * Returns the label id of the stock unit, if applicable. The
//	 * label id could be a real label id (e.g. printed as barcode). 
//	 * Otherwise the database id is used as default.
//	 * 
//	 * @return Returns the labelId.
//	 */
//	@Column(nullable=false)
//	public String getLabelId() {
//		return this.labelId;
//	}
//
//	/**
//	 * @see #getLabelId()
//	 * @param labelId
//	 *            The labelId to set.
//	 */
//	public void setLabelId(String labelId) {
//		this.labelId = labelId;
//	}

	/**
	 * Checks, if following constraints are kept during the previous operations.
	 * <ul>
	 * <li>ItemData must be assigned to the same client as the StockUnit.
	 * </ul>
	 * @throws ConstraintViolatedException 
	 */
	@PreUpdate
	@PrePersist
	public void sanityCheck() throws BusinessException, ConstraintViolatedException {

//dgrys portierung auf wildfly 8.2
//		if (getId() != null){
//			if (( getLabelId() == null || getLabelId().length() == 0 )){
//				setLabelId(getId().toString());
//			} else{
//				//ok
//			}
//        } else{
//        	throw new RuntimeException("Id cannot be retrieved yet - hence labelId cannot be set");
//        }
		
		if (!itemData.getClient().equals(getClient()) && !itemData.getClient().isSystemClient() ) {
			throw new BusinessException("ItemData " + itemData.toUniqueString()
					+ " is assigned to a different client ("
					+ itemData.getClient().toUniqueString() + ")"
					+ " than this StockUnit (" + getClient().toUniqueString()
					+ ")");
		}
		
		if (lot != null && ( ! lot.getItemData().equals(itemData))){
			throw new BusinessException("ItemData " + lot.getItemData().toUniqueString()
					+ " of lot ("
					+ lot.toUniqueString() + ")"
					+ " does  not match this item data (" + itemData.toUniqueString()
					+ ")");
		}
			
		if (amount.scale() > itemData.getScale()){
			amount.setScale(itemData.getScale());
		}
		
		if (reservedAmount.scale() > itemData.getScale()){
			reservedAmount.setScale(itemData.getScale());
		}
		
		if (getAvailableAmount().compareTo(new BigDecimal(0)) < 0 ){
			throw new BusinessException("Available amount must be positiv but is " + getAvailableAmount());
		}
		
	}

	/**
	 * @return the reservedAmount
	 */
	@Column(precision=17, scale=4)
	public BigDecimal getReservedAmount() {
		if (itemData != null){
			try{
				return this.reservedAmount.setScale(itemData.getScale());
			}catch(ArithmeticException ae){
				System.out.println("------- LE "+getId()+" : expected scale = "+itemData.getScale()+" but was "+reservedAmount);
				return this.reservedAmount;
			}
		} else{
			return this.reservedAmount;
		}
	}

	/**
	 * @param reservedAmount
	 *            the reservedAmount to set
	 * @throws ConstraintViolatedException 
	 */
	public void setReservedAmount(BigDecimal reservedAmount) {
		this.reservedAmount = reservedAmount;
	}

	/**
	 * Adds an amount to the reserved amount, if available.
	 * 
	 * @param amount
	 *            the additional amount to reserve
	 * @throws BusinessException
	 *             if the new reserved amount would exceed the amount of items
	 */
	@Transient
	public void addReservedAmount(BigDecimal amount) throws BusinessException {
		// check constraint
		if (amount.compareTo(new BigDecimal(0)) < 0) {
			throw new BusinessException("cannot reserve negative amount");
		} else if (this.reservedAmount.add(amount).compareTo(this.amount) > 0) {
			throw new BusinessException(
					"cannot reserve more items than available (item="+getItemData().getNumber()+", on stock="+this.amount+", already reserved="+this.reservedAmount+", new reservation="+amount+")");
		}

		System.out.println("reserve add="+amount);
		this.reservedAmount = this.reservedAmount.add(amount);
	}

	/**
	 * Releases an amount of the reserved amount.
	 * 
	 * @param amount
	 *            the amount of reservations to release
	 * @throws BusinessException
	 *             if the new reserved amount would be less than 0 or if amount
	 *             is negative
	 */
	@Transient
	public void releaseReservedAmount(BigDecimal amount) throws BusinessException {
		// check constraint
		if (amount.compareTo(new BigDecimal(0)) < 0) {
			throw new BusinessException("cannot release negative reservation");
		}

		BigDecimal reservedAmountNew = this.reservedAmount.subtract(amount);
		if( reservedAmountNew.compareTo(BigDecimal.ZERO) < 0 ) {
//			throw new BusinessException(
//					"cannot release less than reserved items");
			System.out.println("------- LE "+getId()+" : want to release less than reserved items" + amount + " but was "+reservedAmount);
			reservedAmountNew = BigDecimal.ZERO;
		}

		this.reservedAmount = reservedAmountNew;
	}

	@Transient
	public BigDecimal getAvailableAmount() {
		if (itemData != null){
			try{
				return amount.subtract(reservedAmount).setScale(itemData.getScale());
			}catch(ArithmeticException ae){
				System.out.println("------- LE "+getId()+" : expected scale = "+itemData.getScale()+" but was "+amount.subtract(reservedAmount));
				return amount.subtract(reservedAmount);
			}
		} else{
			return amount.subtract(reservedAmount);
		}
	}

	/**
	 * @return the lot
	 */
	@ManyToOne(optional=true)
	public Lot getLot() {
		return lot;
	}

	/**
	 * @param lot
	 *            the lot to set
	 */
	public void setLot(Lot lot) {
		this.lot = lot;
	}

	//dgrys portierung  auf wildfly 8.2
//	@Override
//	public String toUniqueString() {
//		if (getLabelId() != null) {
//			return getLabelId();
//		} else {
//			return getId().toString();
//		}
//	}

	/**
	 * A transient getter for the {@link ItemMeasure}  containing a value, a {@link ItemUnit} and a format String
	 * @return
	 */
	@Transient
	public ItemMeasure getDisplayAmount() {
		if( getItemData() == null ) 
			return null;
		
		BigDecimal i = getAmount();
		
		try{
			i = i.setScale(getItemData().getScale());
		}catch(ArithmeticException ae){
			System.out.println("------- LE "+getId()+" : expected scale = "+itemData.getScale()+" but was "+i);
		}
		
		ItemMeasure m  = new ItemMeasure(
				i, 
				getItemData().getHandlingUnit()
				);
		
		return m;
		
	}

	/**
	 * 
	 * @return
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	
	public void setSerialNumber(String serialNumber){
		this.serialNumber = serialNumber;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	public Date getStrategyDate() {
		return strategyDate;
	}
	public void setStrategyDate(Date strategyDate) {
		this.strategyDate = strategyDate;
	}

	
	@Override
	public String toShortString() {
		StringBuffer sb = new StringBuffer(super.toShortString());
		sb.append("[itemDataName=" + (itemData == null ? "" : itemData.getName()) + "]");
		sb.append("[lotName=" + (lot == null ? "" : lot.getName()) + "]");
		sb.append("[amount=" + amount + "]");
		sb.append("[unitLoadLabel=" + (unitLoad == null ? "" : unitLoad.getLabelId()) + "]");

		return sb.toString();
	}
}
