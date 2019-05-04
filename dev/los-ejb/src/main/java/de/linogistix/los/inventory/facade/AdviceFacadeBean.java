/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import de.linogistix.los.inventory.businessservice.LOSAdviceBusiness;
import de.linogistix.los.inventory.businessservice.LOSGoodsReceiptComponent;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.service.LOSGoodsReceiptService;
import de.linogistix.los.query.BODTO;

@Stateless
public class AdviceFacadeBean implements AdviceFacade {
	Logger log = Logger.getLogger(AdviceFacadeBean.class);

	@EJB
	private LOSAdviceBusiness adviceBusiness;
	
	@EJB
	private LOSGoodsReceiptComponent grComponent;
	
	@EJB
	private LOSGoodsReceiptService grService;
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;
	
	public void removeAdvise(BODTO<LOSAdvice> adv) throws InventoryException {
		log.debug("removeAdvise Start advId=" + adv.getId());
		LOSAdvice adv2 = manager.find(LOSAdvice.class, adv.getId());
		
		
		// Find GoodsReceipts with this advice
		List<LOSGoodsReceipt> grList = grService.getByAdvice(adv2);
		for( LOSGoodsReceipt gr : grList ) {
			grComponent.removeAssignedAdvice(adv2, gr);
		}
		adviceBusiness.removeAdvise(adv2.getClient(), adv2);
	}

	public void finishAdvise(BODTO<LOSAdvice> adv) throws InventoryException {
		log.debug("finishAdvise Start advId=" + adv.getId());
		LOSAdvice adv2 = manager.find(LOSAdvice.class, adv.getId());
		
		// Find GoodsReceipts with this advice
		List<LOSGoodsReceipt> grList = grService.getByAdvice(adv2);
		for( LOSGoodsReceipt gr : grList ) {
			if( gr.getReceiptState() != LOSGoodsReceiptState.CANCELED &&  gr.getReceiptState() != LOSGoodsReceiptState.FINISHED ) {
				throw new InventoryException(InventoryExceptionKey.GOODS_RECEIPT_NOT_FINISHED, gr.getGoodsReceiptNumber());
			}
		}
		adv2.setAdviceState(LOSAdviceState.FINISHED);
		adv2.setFinishDate(new Date());
	}
	
}
