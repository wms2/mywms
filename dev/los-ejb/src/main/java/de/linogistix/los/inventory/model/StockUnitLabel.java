/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mywms.model.Document;

/**
 *
 * @author trautm
 */
@Entity
@Table(name = "los_suLabel"
//	, uniqueConstraints = {
//	    @UniqueConstraint(columnNames = {
//	            "client_id","labelID"
//	        })}
) 
public class StockUnitLabel extends Document{

	private static final long serialVersionUID = 1L;

	private String labelID;
    
    private String clientRef;
    
    private String dateRef;
    
    private String itemdataRef;
    
    private String itemNameRef;
    
    private String lotRef;
    
    private BigDecimal amount;

    private int scale;
    
    private String itemUnit;
    
    @Column(nullable=false)
    public String getLabelID() {
        return labelID;
    }

    public void setLabelID(String labelID) {
        this.labelID = labelID;
    }

    @Column(nullable=false)
    public String getClientRef() {
        return clientRef;
    }

    public void setClientRef(String clientRef) {
        this.clientRef = clientRef;
    }

    @Column(nullable=false)
    public String getDateRef() {
        return dateRef;
    }

    public void setDateRef(String dateRef) {
        this.dateRef = dateRef;
    }

    @Column(nullable=false)
    public String getItemdataRef() {
        return itemdataRef;
    }

    public void setItemdataRef(String itemdataRef) {
        this.itemdataRef = itemdataRef;
    }

    public String getLotRef() {
        return lotRef;
    }

    public void setLotRef(String lotRef) {
        this.lotRef = lotRef;
    }

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(scale=4, nullable=false)
	public BigDecimal getAmount() {
		return (amount != null)? amount.setScale(scale):new BigDecimal(0);
	}

	@Column(nullable=false)
	public int getScale() {
		return scale;
	}
	
	public void setScale(int scale) {
		this.scale = scale;
	}

	@Column(nullable=false)
	public String getItemUnit() {
		return itemUnit;
	}

	public void setItemUnit(String itemUnit) {
		this.itemUnit = itemUnit;
	}

	@Transient
	public String getItemNameRef() {
		return itemNameRef;
	}
	public void setItemNameRef(String itemNameRef) {
		this.itemNameRef = itemNameRef;
	}
    
    
}
