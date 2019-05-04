package org.mywms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

/**
 * Some units of measure used in a warehouse. 
 *  
 * @author trautm
 *
 */
@Entity
@Table(name="mywms_itemunit")
public class ItemUnit extends BasicEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private ItemUnitType unitType;
	
	private String unitName;
	
	private ItemUnit baseUnit = null;
	
	private int baseFactor = 1;

	
	@Enumerated(EnumType.STRING)
	public ItemUnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(ItemUnitType type) {
		this.unitType = type;
	}

	@Column(nullable=false, unique=false)
	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	@ManyToOne(optional=true)
	public ItemUnit getBaseUnit() {
		return baseUnit;
	}

	public void setBaseUnit(ItemUnit baseUnit) {
		this.baseUnit = baseUnit;
	}

	public int getBaseFactor() {
		return baseFactor;
	}

	public void setBaseFactor(int factor) {
		this.baseFactor = factor;
	}
	
	@PrePersist
	void checkUnit(){
		
//		if (!(baseUnit.unitType.equals(pickUnit.unitType))){
//			throw new IllegalArgumentException("base unit must be of same unitType");
//		}
	}
	
	
	@Override
	public String toUniqueString() {
		return unitName;
	}
	

	
	
}
