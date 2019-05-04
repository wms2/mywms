/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

/**
 *
 * @author Jordan
 */
@Entity
@Table(name="los_rack",
       uniqueConstraints={@UniqueConstraint(columnNames={"rname","client_id"})})
public class LOSRack extends BasicClientAssignedEntity{
	
	private static final long serialVersionUID = 1L;

    private String name;
    
    private int numberOfRows;
    
    private int numberOfColumns;
    
    private Integer labelOffset;
    
    private String aisle;
    
    @Column(name="rname", nullable=false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }


    /**
     * Every Locations within the rack is identified by a BarcodeLabel.
     * The label is attached to the rack. Most commonly all labels are atached
     * between two rows of StorageLocations (onto the metal traversee or onto a 
     * separate shield) at the same heigth.
     * Then one label identifies the storagelocation beneath and the other above, etc.
     * A labelOffset of 2 indicates that the labels are attached above the second
     * row. The label prints a down-arrow for row one and two and and up-arrow for 
     * columns 3 to the rest.
     * 
     * If null, offset information should be ignored.
     * 
     * @see de.linogistix.los.location.report.StorageLocationLabelService
     * @return
     */
    public Integer getLabelOffset() {
        return labelOffset;
    }

    public void setLabelOffset(Integer labelOffset) {
        this.labelOffset = labelOffset;
    }

    
	@Column(nullable=true)
	public String getAisle() {
		return aisle;
	}
	public void setAisle(String aisle) {
		this.aisle = aisle;
	}

	@PrePersist
	@PreUpdate
	public void checkValues() {
		if( aisle != null && aisle.trim().length()==0 ) {
			aisle = null;
		}
	}

	@Override
	public String toUniqueString() {
		return name;
	}
    
    
}
