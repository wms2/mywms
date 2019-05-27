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

/**
 * The role enables user to operate specific processes. Standard roles
 * are for example:
 * <ul>
 * <li> goods in
 * <li> goods out
 * <li> transport
 * <li> picking
 * <li> maintenance
 * <li> topology manager
 * <li> request manager
 * <li> administrator
 * </ul>
 * 
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name = "mywms_role")
public class Role
    extends BasicEntity
{
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    private String name = null;

    @Column(nullable = false)
    private String description = "";

    /**
     * The system unique name of the role
     * 
     * @return Returns the name.
     */
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
     * @return Returns the description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toUniqueString() {
        return getName();
    }
 
}
