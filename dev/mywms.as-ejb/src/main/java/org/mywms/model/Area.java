/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * The area is collection of logical places in a warehouse joining
 * locations of similar operations together. For example, standard areas
 * could be are:
 * <ul>
 * <li> goods in
 * <li> goods out
 * <li> bonded warehouse
 * <li> quality assurance/lock
 * <li> supply
 * <li> picking
 * </ul>
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name = "mywms_area", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name", "client_id"
    })
})
public class Area
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    private String name = null;

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

    @Override
    public String toUniqueString() {
        return getName();
    }
    
    
}
