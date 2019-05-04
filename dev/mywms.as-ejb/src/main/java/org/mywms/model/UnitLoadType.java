/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The unit load type marks a unit load as being for example of type
 * pallet, box, bin or just wrapping.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Olaf Krause</a>
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name = "mywms_unitloadtype", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name"
    })
})
public class UnitLoadType extends BasicEntity
{
    private static final long serialVersionUID = 1L;

    private String name = null;

    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal depth;
    private BigDecimal volume;
    private BigDecimal weight;
    private BigDecimal liftingCapacity;

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
     * Returns the depth of entities of this type.
     * 
     * @return Returns the depth.
     */
    @Column(nullable = true, precision=15, scale=2)
    public BigDecimal getDepth() {
        return this.depth;
    }

    /**
     * @see #getDepth()
     * @param depth The depth to set.
     */
    public void setDepth(BigDecimal depth) {
        this.depth = depth;
    }

    /**
     * Returns the height of entities of this type.
     * 
     * @return Returns the height.
     */
    @Column(nullable = true	, precision=15, scale=2)
    public BigDecimal getHeight() {
        return this.height;
    }

    /**
     * @see #getHeight()
     * @param height The height to set.
     */
    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    /**
     * Returns the mass of entities of this type.
     * 
     * @return Returns the weight.
     */
    @Column(nullable = true, precision=16, scale=3)
    public BigDecimal getWeight() {
        return this.weight;
    }

    /**
     * @see #getWeight()
     * @param weight The weight to set.
     */
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    /**
     * Returns the width of entities of this type
     * 
     * @return Returns the width.
     */
    @Column(nullable = true, precision=15, scale=2)
    public BigDecimal getWidth() {
        return this.width;
    }

    /**
     * @see #getWidth()
     * @param width The width to set.
     */
    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    @Column(nullable = true, precision=16, scale=3)
    public BigDecimal getLiftingCapacity() {
		return liftingCapacity;
	}
	public void setLiftingCapacity(BigDecimal liftingCapacity) {
		this.liftingCapacity = liftingCapacity;
	}

	
    @Column(nullable = true, precision=19, scale=6)
	public BigDecimal getVolume() {
		return volume;
	}
	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	@Override
    public String toUniqueString() {
        return getName();
    }
    
    
}
