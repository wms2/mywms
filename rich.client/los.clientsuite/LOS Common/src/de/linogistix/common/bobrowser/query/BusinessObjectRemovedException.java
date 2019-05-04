/*
 * BusinessObjectRemovedException.java
 *
 * Created on 20. Januar 2007, 23:11
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import org.mywms.facade.FacadeException;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BusinessObjectRemovedException extends FacadeException {

    /** Creates a new instance of BusinessObjectRemovedException */
    public BusinessObjectRemovedException(String id) {
        super("This BusinessObject has been removed", "ERROR.BusinessObjectRemovedException", new Object[]{id});
        super.setBundleResolver(de.linogistix.common.res.CommonBundleResolver.class);
    }
}
