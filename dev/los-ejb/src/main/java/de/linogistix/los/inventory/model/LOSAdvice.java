/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

@Entity
@Table(name = "los_avisreq")
public class LOSAdvice extends BasicClientAssignedEntity {
	
	private static final long serialVersionUID = 1L;

	private String adviceNumber;
	
	private String externalAdviceNumber;
	
	private String externalId;
	
	private ItemData itemData;
	
	private Lot lot;
	
	private Date expectedDelivery;
	
	private boolean expireBatch;

	private BigDecimal receiptAmount = new BigDecimal(0);
	
    private BigDecimal notifiedAmount = new BigDecimal(0);
        
    private List<LOSGoodsReceiptPosition> grPositionList = new ArrayList<LOSGoodsReceiptPosition>();
    
    private LOSAdviceState adviceState = LOSAdviceState.RAW;
	
    private Date processDate;
    
    private Date finishDate;
    
	@Column(unique=true, nullable=false)
	public String getAdviceNumber() {
		return adviceNumber;
	}

	public void setAdviceNumber(String requestId) {
		this.adviceNumber = requestId;
	}

	@Column(name="externalNo")
	public String getExternalAdviceNumber() {
		return externalAdviceNumber;
	}

	public void setExternalAdviceNumber(String externalAdviceNumber) {
		this.externalAdviceNumber = externalAdviceNumber;
	}

	@ManyToOne(optional=false)
	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	/** The already arrived (Goods receipt) amount of goods*/
	@Column(precision=17, scale=4)
	public BigDecimal getReceiptAmount() {
		if (itemData != null){
			try{
				return this.receiptAmount.setScale(itemData.getScale());
			}catch(ArithmeticException ae){
				System.out.println("LOSAdvice "+getId()+" : expected scale = "+itemData.getScale()+" but was "+receiptAmount);
			}
		}

		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal amount) {
        this.receiptAmount = amount;
	}

	@ManyToOne(optional=true)
	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot batch) {
		this.lot = batch;
	}

	@Temporal(TemporalType.DATE)
	public Date getExpectedDelivery() {
		return expectedDelivery;
	}

	/**
	 * When is the delivery due to expect?
	 * @param expectedDelivery
	 */
	public void setExpectedDelivery(Date expectedDelivery) {
		this.expectedDelivery = expectedDelivery;
	}

	/**
	 * Should old batches be extinguished?
	 * @return
	 */
	public boolean isExpireBatch() {
		return expireBatch;
	}

	public void setExpireBatch(boolean expireBatch) {
		this.expireBatch = expireBatch;
	}

    /**
     * 
     * @return
     */
    @OneToMany(mappedBy="relatedAdvice")
    public List<LOSGoodsReceiptPosition> getGrPositionList() {
        return grPositionList;
    }

    public void setGrPositionList(List<LOSGoodsReceiptPosition> grPositionList) {
        this.grPositionList = grPositionList;
    }
    
    public void addGrPos(LOSGoodsReceiptPosition pos){
    	pos.setRelatedAdvice(this);
        this.grPositionList.add(pos);
        this.receiptAmount = this.receiptAmount.add(pos.getAmount());
    }
    
    public void removeGrPos(LOSGoodsReceiptPosition pos){    
        pos.setRelatedAdvice(null);
    	this.grPositionList.remove(pos);
        this.receiptAmount = this.receiptAmount.subtract(pos.getAmount());
    }
    
    

    /** The amount that has been adviced */
    @Column(precision=17, scale=4)
    public BigDecimal getNotifiedAmount() {
		if (itemData != null){
			try{
				return this.notifiedAmount.setScale(itemData.getScale());
			}catch(ArithmeticException ae){
				System.out.println("LOSAdvice "+getId()+" : expected scale = "+itemData.getScale()+" but was "+notifiedAmount);
			}
		}
        return notifiedAmount;
    }
    
    public void setNotifiedAmount(BigDecimal notifiedAmount) {
        this.notifiedAmount = notifiedAmount;
    }

    @Enumerated(EnumType.STRING)
    public LOSAdviceState getAdviceState() {
        return adviceState;
    }

    public void setAdviceState(LOSAdviceState adviceType) {
        this.adviceState = adviceType;
    }

    @Override
    public String toUniqueString() {
        return adviceNumber;
    }
	
    /** More amount has been advised than has come up to now.
     *  The system expects more goods to come.
     */
	@Transient
	public boolean isOverload(){
		return receiptAmount.compareTo( notifiedAmount ) > 0;
	}
	
	/** More amount has been advised than has come up to now.
	 * The system expects more goods to come.
	 * */
	@Transient
	public boolean isGoodsToCome(){
		return receiptAmount.compareTo( notifiedAmount ) < 0;
	}
	
	@Transient
	public BigDecimal diffAmount(){
		return receiptAmount.subtract(notifiedAmount==null?new BigDecimal(0):notifiedAmount).negate();
	}
	
	@Transient
	public BigDecimal getDiffAmount() {
		BigDecimal diffAmount = receiptAmount;
		if( receiptAmount != null && notifiedAmount != null ) {
			diffAmount = notifiedAmount.subtract(receiptAmount);
		}
		
		if( diffAmount != null && itemData != null){
			try{
				return diffAmount.setScale(itemData.getScale());
			}catch(ArithmeticException ae){
				System.out.println("LOSAdvice "+getId()+" : expected scale = "+itemData.getScale()+" but was "+diffAmount);
			}
		}

		return diffAmount;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	
	
}
