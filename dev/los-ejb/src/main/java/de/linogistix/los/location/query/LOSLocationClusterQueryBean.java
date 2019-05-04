/*
 * LOSLocationCluster
 *
 * Created on 2009
 *
 * Copyright (c) 2009 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.location.query;

import javax.ejb.Stateless;

import de.linogistix.los.location.model.LOSLocationCluster;
import de.linogistix.los.query.BusinessObjectQueryBean;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSLocationClusterQueryBean extends BusinessObjectQueryBean<LOSLocationCluster> implements LOSLocationClusterQueryRemote {

    public String getUniqueNameProp() {
        return "name";
    }
}
