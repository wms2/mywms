/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.User;

import de.linogistix.los.location.model.LOSStorageLocation;

/**
 * Der LOSGoodsReceipt protokolliert die Anlieferung von Waren durch einen Spediteur.
 * Pro LKW einen LOSGoodsReceipt.
 * 
 * @author Jordan
 *
 */
@Entity
@Table(name="los_goodsreceipt")
public class LOSGoodsReceipt extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;
	
	private String goodsReceiptNumber;
	
	private User operator;
	
	private String licencePlate;
	
	private String driverName;
	
	private String forwarder;
	
	private String deliveryNoteNumber;
	
	private Date receiptDate;
	
	private List<LOSGoodsReceiptPosition> positionList = new ArrayList<LOSGoodsReceiptPosition>();

	private List<LOSAdvice> assignedAdvices = new ArrayList<LOSAdvice>();
	
    private LOSStorageLocation goodsInLocation;
    
    private LOSGoodsReceiptState receiptState = LOSGoodsReceiptState.RAW;
    
    private String referenceNo;
    
	/**
	 * A unique identifier for a receipt.
	 * This cannot be changed
	 * @return
	 */
	@Column(name="gr_number", unique=true, nullable=false, updatable=false)
	public String getGoodsReceiptNumber() {
		return goodsReceiptNumber;
	}

	public void setGoodsReceiptNumber(String goodsReceiptNumber) {
		this.goodsReceiptNumber = goodsReceiptNumber;
	}

	/**
	 * Der Lagermitarbeiter der die Anlieferung angenommen hat.
	 * @return
	 */
	@ManyToOne(optional=true)
	public User getOperator() {
		return operator;
	}

	public void setOperator(User user) {
		this.operator = user;
	}

	/**
	 * Das KFZ-Kennzeichen des LKWs.
	 * @return
	 */
	public String getLicencePlate() {
		return licencePlate;
	}

	public void setLicencePlate(String licencePlate) {
		this.licencePlate = licencePlate;
	}

	/**
	 * Der Name des Fahrers.
	 * @return
	 */
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	/** 
	 * Name der Spedition.
	 * @return
	 */
	public String getForwarder() {
		return forwarder;
	}

	public void setForwarder(String forwarder) {
		this.forwarder = forwarder;
	}

	/**
	 * Nummer des Lieferscheins den die Spedition mitschickt.
	 * @return
	 */
	@Column(name="delnote")
	public String getDeliveryNoteNumber() {
		return deliveryNoteNumber;
	}

	public void setDeliveryNoteNumber(String deliveryNoteNumber) {
		this.deliveryNoteNumber = deliveryNoteNumber;
	}

	/**
	 * Datum des Wareneingangs
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}
	
	/**
	 * Auf einem LKW k�nnen Waren angeliefert werden, 
	 * die unterschiedlichen Avis, Rahmenauftr�gen, etc.
	 * zugeordnet werden m�ssen. Hierf�r ist f�r jede Zuordnung eine
	 * eigenen Wareneingangsposition n�tig.
	 * @return
	 */
	@OneToMany(mappedBy="goodsReceipt")
	@OrderBy("positionNumber ASC")
	public List<LOSGoodsReceiptPosition> getPositionList() {
		return positionList;
	}

	public void setPositionList(List<LOSGoodsReceiptPosition> positionList) {
		this.positionList = positionList;
	}

    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    public LOSStorageLocation getGoodsInLocation() {
        return goodsInLocation;
    }

    public void setGoodsInLocation(LOSStorageLocation goodsInLocation) {
        this.goodsInLocation = goodsInLocation;
    }

    @Override
    public String toUniqueString() {
        return goodsReceiptNumber;
    }

    @Enumerated(EnumType.STRING)
    public LOSGoodsReceiptState getReceiptState() {
        return receiptState;
    }

    public void setReceiptState(LOSGoodsReceiptState receiptState) {
        this.receiptState = receiptState;
    }

	/**
	 * Assigned {@link LOSAdvice}s can later be used in the reception process 
	 * @param assignedAdvices the assignedAdvices to set
	 */
	public void setAssignedAdvices(List<LOSAdvice> assignedAdvices) {
		this.assignedAdvices = assignedAdvices;
	}

	/**
	 * @return the assignedAdvices
	 */
	@ManyToMany
	@OrderBy("id ASC")
	public List<LOSAdvice> getAssignedAdvices() {
		return assignedAdvices;
	}

	public void addAssignedAdvices(LOSAdvice adv) {
		if (!getAssignedAdvices().contains(adv)){
			getAssignedAdvices().add(adv);
		}
		
	}
	
	/**
	 * 
	 * @param adv
	 * @throws IllegalArgumentException if adv has assigned a {@link LOSGoodsReceiptPosition} of this {@link LOSGoodsReceipt}
	 */
	public void removeAssignedAdvices(LOSAdvice adv) throws IllegalArgumentException{	
		if (getAssignedAdvices().contains(adv)){
			for (LOSGoodsReceiptPosition p : getPositionList()){
				if (adv.getGrPositionList().contains(p)){
					throw new IllegalArgumentException();
				}
			}
			getAssignedAdvices().remove(adv);
		
		}
	}

	public boolean containsAssignedAdvices(LOSAdvice adv) {
		return getAssignedAdvices().contains(adv);
	}

	/**
	 * A number to store external references
	 * @return
	 */
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
    
}
