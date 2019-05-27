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

import de.wms2.mywms.location.StorageLocation;

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
	
	/**
	 * A unique identifier for a receipt.
	 * This cannot be changed
	 */
	@Column(name="gr_number", unique=true, nullable=false, updatable=false)
	private String goodsReceiptNumber;
	
	/**
	 * Der Lagermitarbeiter der die Anlieferung angenommen hat.
	 */
	@ManyToOne(optional=true)
	private User operator;
	
	/**
	 * Das KFZ-Kennzeichen des LKWs.
	 */
	private String licencePlate;
	
	/**
	 * Der Name des Fahrers.
	 */
	private String driverName;
	
	/** 
	 * Name der Spedition.
	 */
	private String forwarder;
	
	/**
	 * Nummer des Lieferscheins den die Spedition mitschickt.
	 */
	@Column(name="delnote")
	private String deliveryNoteNumber;
	
	/**
	 * Datum des Wareneingangs
	 */
	@Temporal(TemporalType.DATE)
	private Date receiptDate;
	
	/**
	 * Auf einem LKW k�nnen Waren angeliefert werden, 
	 * die unterschiedlichen Avis, Rahmenauftr�gen, etc.
	 * zugeordnet werden m�ssen. Hierf�r ist f�r jede Zuordnung eine
	 * eigenen Wareneingangsposition n�tig.
	 * @return
	 */
	@OneToMany(mappedBy="goodsReceipt")
	@OrderBy("positionNumber ASC")
	private List<LOSGoodsReceiptPosition> positionList = new ArrayList<LOSGoodsReceiptPosition>();

	/**
	 * Assigned {@link LOSAdvice}s can later be used in the reception process 
	 * @param assignedAdvices the assignedAdvices to set
	 */
	@ManyToMany
	@OrderBy("id ASC")
	private List<LOSAdvice> assignedAdvices = new ArrayList<LOSAdvice>();
	
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    private StorageLocation goodsInLocation;
    
    @Enumerated(EnumType.STRING)
    private LOSGoodsReceiptState receiptState = LOSGoodsReceiptState.RAW;
    
	/**
	 * A number to store external references
	 */
    private String referenceNo;
    
	/**
	 * 
	 * @param adv
	 * @throws IllegalArgumentException if adv has assigned a {@link LOSGoodsReceiptPosition} of this {@link LOSGoodsReceipt}
	 */
	public void removeAssignedAdvices(LOSAdvice adv) throws IllegalArgumentException {
		if (getAssignedAdvices().contains(adv)) {
			for (LOSGoodsReceiptPosition p : getPositionList()) {
				if (adv.getGrPositionList().contains(p)) {
					throw new IllegalArgumentException();
				}
			}
			getAssignedAdvices().remove(adv);

		}
	}

    @Override
    public String toUniqueString() {
        return goodsReceiptNumber;
    }

	public void addAssignedAdvices(LOSAdvice adv) {
		if (!getAssignedAdvices().contains(adv)) {
			getAssignedAdvices().add(adv);
		}

	}

	public String getGoodsReceiptNumber() {
		return goodsReceiptNumber;
	}

	public void setGoodsReceiptNumber(String goodsReceiptNumber) {
		this.goodsReceiptNumber = goodsReceiptNumber;
	}

	public User getOperator() {
		return operator;
	}

	public void setOperator(User user) {
		this.operator = user;
	}

	public String getLicencePlate() {
		return licencePlate;
	}

	public void setLicencePlate(String licencePlate) {
		this.licencePlate = licencePlate;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getForwarder() {
		return forwarder;
	}

	public void setForwarder(String forwarder) {
		this.forwarder = forwarder;
	}

	public String getDeliveryNoteNumber() {
		return deliveryNoteNumber;
	}

	public void setDeliveryNoteNumber(String deliveryNoteNumber) {
		this.deliveryNoteNumber = deliveryNoteNumber;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}
	
	public List<LOSGoodsReceiptPosition> getPositionList() {
		return positionList;
	}

	public void setPositionList(List<LOSGoodsReceiptPosition> positionList) {
		this.positionList = positionList;
	}

    public StorageLocation getGoodsInLocation() {
        return goodsInLocation;
    }

    public void setGoodsInLocation(StorageLocation goodsInLocation) {
        this.goodsInLocation = goodsInLocation;
    }


    public LOSGoodsReceiptState getReceiptState() {
        return receiptState;
    }

    public void setReceiptState(LOSGoodsReceiptState receiptState) {
        this.receiptState = receiptState;
    }

	public void setAssignedAdvices(List<LOSAdvice> assignedAdvices) {
		this.assignedAdvices = assignedAdvices;
	}

	public List<LOSAdvice> getAssignedAdvices() {
		return assignedAdvices;
	}



	public boolean containsAssignedAdvices(LOSAdvice adv) {
		return getAssignedAdvices().contains(adv);
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}
    
}
