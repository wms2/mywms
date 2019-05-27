/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.location.query;

import de.linogistix.los.query.BusinessObjectQueryBean;
import de.wms2.mywms.location.Area;

import javax.ejb.Stateless;



/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSAreaQueryBean extends BusinessObjectQueryBean<Area> implements LOSAreaQueryRemote {

    public String getUniqueNameProp() {
        return "name";
    }
}
