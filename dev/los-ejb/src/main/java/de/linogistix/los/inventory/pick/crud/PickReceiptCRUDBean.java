/*
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.pick.crud;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectDeleteException;
import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.pick.model.PickReceiptPosition;
import de.linogistix.los.inventory.pick.service.PickReceiptService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;



/**
 * @author trautm
 *
 */
@Stateless
public class PickReceiptCRUDBean extends BusinessObjectCRUDBean<PickReceipt> implements PickReceiptCRUDRemote{

	@EJB 
	private PickReceiptService service;
	
	@Override
	protected BasicService<PickReceipt> getBasicService() {	
		return service;
	}
	
	
	@Override
	public void delete(PickReceipt receipt) throws BusinessObjectNotFoundException,	BusinessObjectDeleteException, BusinessObjectSecurityException {
		receipt = manager.find(PickReceipt.class, receipt.getId());
		List<PickReceiptPosition> positionList = receipt.getPositions();
		for (PickReceiptPosition position : positionList){
			position = manager.find(PickReceiptPosition.class, position.getId());
			if (position != null){
				manager.remove(position);
			}
		}
		
		super.delete(receipt);
		
	}
}
