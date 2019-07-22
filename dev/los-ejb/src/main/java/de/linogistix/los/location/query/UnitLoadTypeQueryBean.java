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


import javax.ejb.Stateless;
import javax.inject.Inject;

import de.linogistix.los.query.BusinessObjectQueryBean;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class UnitLoadTypeQueryBean extends BusinessObjectQueryBean<UnitLoadType> implements UnitLoadTypeQueryRemote{
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	
    @Override
    public String getUniqueNameProp() {
        return "name";
    }

	public UnitLoadType getDefaultUnitLoadType() {
		return unitLoadTypeService.getDefault();
	}

	public UnitLoadType getPickLocationUnitLoadType() {
		return unitLoadTypeService.getVirtual();
	}
	
}
