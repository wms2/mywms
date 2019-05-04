/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 *
 * @author Jordan
 */
@Entity
@Table(name="los_sequenceNumber")
public class LOSSequenceNumber implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String className;
    private long sequenceNumber;
    private int version;
    
    public void setClassName(String className) {
        this.className = className;
    }

    @Id
    public String getClassName() {
        return className;
    }

    @Column(nullable=false)
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }

    @Version
    public int getVersion() {
    	return version;
    }

    protected void setVersion(int version) {
    	this.version = version;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (className != null ? className.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LOSSequenceNumber)) {
            return false;
        }
        LOSSequenceNumber other = (LOSSequenceNumber) object;
        if ((this.className == null && other.className != null) || (this.className != null && !this.className.equals(other.className))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.linogistix.los.model.SequenceNumber[id=" + className + "]";
    }

}
