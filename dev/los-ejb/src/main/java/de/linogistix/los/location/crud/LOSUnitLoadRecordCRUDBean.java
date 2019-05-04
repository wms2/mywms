/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.crud;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.location.entityservice.LOSUnitLoadRecordService;
import de.linogistix.los.location.model.LOSUnitLoadRecord;

@Stateless
public class LOSUnitLoadRecordCRUDBean 
			extends BusinessObjectCRUDBean<LOSUnitLoadRecord>
			implements LOSUnitLoadRecordCRUDRemote 
{

	@EJB
	private LOSUnitLoadRecordService ulRecService;
	
	@Override
	protected BasicService<LOSUnitLoadRecord> getBasicService() {
		return ulRecService; 
	}

	
}
