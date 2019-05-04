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
 * The warehouse zone defines areas of the warehouse. A standard zoning
 * is A-B-C-zoning, where the A zone is reserved for high throughput
 * goods and the C zone is used to store rarely handled goods.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Olaf Krause</a>
 * @version $Revision$ provided by $Author$
 */
@Entity
@Table(name = "mywms_zone", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name", "client_id"
    })
})
public class Zone
    extends BasicClientAssignedEntity
{
    private static final long serialVersionUID = 1L;

    private String name = "";

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
