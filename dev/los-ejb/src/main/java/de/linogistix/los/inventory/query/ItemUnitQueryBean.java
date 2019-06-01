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

package de.linogistix.los.inventory.query;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import de.linogistix.los.inventory.service.ItemUnitService;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.wms2.mywms.product.ItemUnit;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class ItemUnitQueryBean extends BusinessObjectQueryBean<ItemUnit> implements ItemUnitQueryRemote{
    @EJB
    ItemUnitService iuService;
    
	@Override
    public String getUniqueNameProp() {
        return "name";
    }

	public ItemUnit getDefault() {
		return iuService.getDefault();
	}
    
}
