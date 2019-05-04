/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.service.ConstraintViolatedException;

/**
 * ItemData contains general informations about the goods stored in the
 * warehouse.
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name="mywms_itemdata",uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "client_id", "item_nr"
    })
})
public class ItemData
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    private String name = "";
    
    private String number = null;
    
    private String description;
    
    private int safetyStock = 0;
    
    private int residualTermOfUsageGI = 0; 
    
    private UnitLoadType defaultUnitLoadType;
    
    private Zone zone = null;

    private boolean lotMandatory = false;
    
    private boolean adviceMandatory = false;
    
    private SerialNoRecordType serialNoRecordType = SerialNoRecordType.NO_RECORD;
    
    private LotSubstitutionType lotSubstitutionType = LotSubstitutionType.NOT_ALLOWED;
    
    private ItemUnit handlingUnit;
    
    private int scale = 0;
    
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal depth;
    private BigDecimal weight;
    private BigDecimal volume;
    private String tradeGroup;
    
    private List<ItemDataNumber> numberList;
    
    /**
     * @return Returns the name.
     */
    @Column(nullable = false)
    public String getName() {
        return this.name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The number is a unique product number, like for example an EAN.
     * 
     * @return Returns the number.
     */
    @Column(nullable = false, name="item_nr")
    public String getNumber() {
        return this.number;
    }

    /**
     * @see #getNumber()
     * @param number The number to set.
     */
    public void setNumber(String number) {
        this.number = number;
    }
    
    @Column(name="descr")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
    /**
     * A lower limit, underflow, minimum inventory, ...
     * 
     * @return Returns the safetyStock.
     */
    @Column(nullable = false)
    public int getSafetyStock() {
        return this.safetyStock;
    }

    /**
     * Sets the safety stock value for the ItemData. A value of 0 means
     * no lower limit.
     * 
     * @param safetyStock The safetyStock to set.
     * @throws BusinessException if the limit is smaller than 0
     */
    public void setSafetyStock(int safetyStock) throws BusinessException {
        this.safetyStock = safetyStock;
    }

    /**
     * To be accepted within a goods receipt items must be best before
     * goods in date + residual term of usage counted in days.
     * 
     * @return number of days
     */
    @Column(name="rest_Usage_GI")
    public int getResidualTermOfUsageGI() {
		return residualTermOfUsageGI;
	}

    /**
     * To be accepted within a goods receipt items must be best before
     * goods in date + residual term of usage counted in days.
     * 
     * @param residualTermOfUsage number of days
     */
	public void setResidualTermOfUsageGI(int residualTermOfUsage) {
		this.residualTermOfUsageGI = residualTermOfUsage;
	}

	/**
	 * For convenience unit load type is initialized with default
	 * unit load type of item data during the goods in process.
	 * 
	 * @return default unit load type for the item data
	 */
	@ManyToOne(optional=true)
	@JoinColumn(name="defULType_id")
	public UnitLoadType getDefaultUnitLoadType() {
		return defaultUnitLoadType;
	}

	/**
	 * For convenience unit load type is initialized with default
	 * unit load type of item data during the goods in process.
	 * 
	 * @param defaultUnitLoadType default unit load type for the item data
	 */
	public void setDefaultUnitLoadType(UnitLoadType defaultUnitLoadType) {
		this.defaultUnitLoadType = defaultUnitLoadType;
	}

    /**
     * @return Returns the zone.
     */
    @ManyToOne(optional=true)
    public Zone getZone() {
        return this.zone;
    }

    /**
     * @param zone The zone to set.
     */
    public void setZone(Zone zone) {
        this.zone = zone;
    }

    @Override
    public String toUniqueString() {
        return getNumber();
    }

    /**
     * Flag for the decision if lot must be recorded 
     * during the goods in process.
     * 
     * @return true if lot must be recorded false otherwise 
     */
	public boolean isLotMandatory() {
		return lotMandatory;
	}

	public void setLotMandatory(boolean lotMandatory) {
		this.lotMandatory = lotMandatory;
	}

	@Enumerated(EnumType.STRING)
	public LotSubstitutionType getLotSubstitutionType() {
		return lotSubstitutionType;
	}

	public void setLotSubstitutionType(LotSubstitutionType lotSubstitutionType) {
		this.lotSubstitutionType = lotSubstitutionType;
	}

	public void setAdviceMandatory(boolean adviceMandatory) {
		this.adviceMandatory = adviceMandatory;
	}

	/**
     * Flag for the decision if there must be an advice 
     * before accepting goods in the goods in process.
     * 
     * @return true if there must be an advice false otherwise 
     */
	public boolean isAdviceMandatory() {
		return adviceMandatory;
	}

	/**
	 * Flag for the decision if serial numbers of stocks 
	 * must be recorded for the item data during the various processes.
	 * 
	 * @see SerialNoRecordType
	 * 
	 * @return record type
	 */
	@Enumerated(EnumType.STRING)
	@Column(name="serialRecType", nullable=false)
	public SerialNoRecordType getSerialNoRecordType() {
		return serialNoRecordType;
	}

	public void setSerialNoRecordType(SerialNoRecordType serialNoRecordtype) {
		this.serialNoRecordType = serialNoRecordtype;
	}
	
	/**
	 * The way the amount of stocks is counted. 
	 * Possible kinds of unit are "Each", "Meter", "Inch"......
	 * 
	 * @return
	 */
	@ManyToOne(optional=false)
	public ItemUnit getHandlingUnit() {
		return handlingUnit;
	}

	public void setHandlingUnit(ItemUnit pickUnit) {
		this.handlingUnit = pickUnit;
	}

	/**
	 * The number of digits of any amount of this {@link ItemData}
	 * @return
	 */
	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
	
	
	/**
	 * Checks, if constraints are kept during the previous operations.
	 * 
	 * @throws ConstraintViolatedException
	 */
	@PreUpdate
	@PrePersist
	public void sanityCheck() throws FacadeException {

		if( number != null ) {
			number = number.trim();
		}
		
        if( number != null && number.startsWith("* ") ) {
        	number = number.substring(2);
        }
        
		if( number == null || number.length() == 0 ) {
			throw new BusinessException("number must be set");
		}
		
		if( getAdditionalContent() != null && getAdditionalContent().length() > 255)  {
			setAdditionalContent(getAdditionalContent().substring(0,255));
		}
	}

	@Override
	public String toShortString() {
		return super.toShortString() + "[number=" + number + "]";
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

	@Column(precision=15, scale=2)
	public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	@Column(precision=16, scale=3)
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	@Column(precision=19, scale=6)
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public String getTradeGroup() {
		return tradeGroup;
	}

	public void setTradeGroup(String tradeGroup) {
		this.tradeGroup = tradeGroup;
	}

    @OneToMany(mappedBy="itemData")
    @OrderBy("index ASC")
	public List<ItemDataNumber> getNumberList() {
		return numberList;
	}

	public void setNumberList(List<ItemDataNumber> numberList) {
		this.numberList = numberList;
	}
	
	
}
