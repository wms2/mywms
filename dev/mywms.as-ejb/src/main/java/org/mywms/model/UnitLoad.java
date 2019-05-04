/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The unit load is a transport help like a pallet, a box, a bin or just
 * a wrapping. A UnitLoad can be placed onto a StorageLocation.
 * UnitLoads and StorageLocations must equal in type. This means, that a
 * pallet (unit load) must be placed onto a tray (storage location)
 * which is capable of storing paletts.
 * 
 * @see StorageLocation
 * @author <a href="http://community.mywms.de/developer.jsp">Olaf Krause</a>
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name = "mywms_unitload")
public class UnitLoad
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    private String labelId = null;

    private UnitLoadType type = null;

    private int index = -1;

    /**
     * @return Returns the labelId.
     */
    @Column(nullable = false, unique=true)
    public String getLabelId() {
        return this.labelId;
    }

    /**
     * @param labelId The labelId to set.
     */
    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    /**
     * @return Returns the type.
     */
    @ManyToOne(optional = false)
    public UnitLoadType getType() {
        return this.type;
    }

    /**
     * @param type The type to set.
     */
    public void setType(UnitLoadType type) {
        this.type = type;
    }

    /**
     * The index is used by the StorageLocation to track the order of
     * UnitLoads stored in it. Setting a new index is allowed by the
     * StorageLocation only.
     * 
     * @return the index
     */
    @Column(name="location_index")
    public int getIndex() {
        return this.index;
    }

    /**
     * The index is used by the StorageLocation to track the order of
     * UnitLoads stored in it. A new index value is set by the
     * StorageLocation and only by the StorageLocation before it is
     * being persisted to the database.
     * 
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toUniqueString() {
        return getLabelId();
    }
    
    
}
