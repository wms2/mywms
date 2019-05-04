/*
 * Created on 20.02.2007, 18:37:29
 *
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.crud;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.mywms.service.BasicService;

import de.linogistix.los.crud.BusinessObjectCRUDBean;
import de.linogistix.los.crud.BusinessObjectDeleteException;
import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.model.OrderReceiptPosition;
import de.linogistix.los.inventory.service.OrderReceiptService;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;



/**
 * @author trautm
 *
 */
@Stateless
public class OrderReceiptCRUDBean extends BusinessObjectCRUDBean<OrderReceipt> implements OrderReceiptCRUDRemote {

	@EJB 
	OrderReceiptService service;

    @Override
    protected BasicService<OrderReceipt> getBasicService() {
        return service;
    }
	
	@Override
	public void delete(OrderReceipt receipt) throws BusinessObjectNotFoundException,	BusinessObjectDeleteException, BusinessObjectSecurityException {
		receipt = manager.find(OrderReceipt.class, receipt.getId());
		List<OrderReceiptPosition> positionList = receipt.getPositions();
		for (OrderReceiptPosition position : positionList){
			position = manager.find(OrderReceiptPosition.class, position.getId());
			if (position != null){
				manager.remove(position);
			}
		}
		
		super.delete(receipt);
		
	}
}
