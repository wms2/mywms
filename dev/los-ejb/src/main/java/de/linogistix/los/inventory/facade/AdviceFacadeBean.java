/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.query.BODTO;
import de.wms2.mywms.advice.AdviceBusiness;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.goodsreceipt.GoodsReceiptBusiness;
import de.wms2.mywms.goodsreceipt.GoodsReceiptEntityService;
import de.wms2.mywms.strategy.OrderState;

@Stateless
public class AdviceFacadeBean implements AdviceFacade {
	Logger log = Logger.getLogger(AdviceFacadeBean.class);

	@Inject
	private GoodsReceiptEntityService goodsReceiptService;
	@Inject
	private GoodsReceiptBusiness goodsReceiptBusiness;
	@Inject
	private AdviceBusiness adviceBusiness;

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;

	public void removeAdvise(BODTO<AdviceLine> adv) throws BusinessException {
		log.debug("removeAdvise Start advId=" + adv.getId());
		AdviceLine adv2 = manager.find(AdviceLine.class, adv.getId());

		List<GoodsReceipt> goodsReceipts = goodsReceiptService.readByAdviceLine(adv2);
		for (GoodsReceipt goodsReceipt : goodsReceipts) {
			goodsReceiptBusiness.removeAssignedAdviceLine(goodsReceipt, adv2);
		}
		adviceBusiness.removeOrder(adv2.getAdvice());
	}

	public void finishAdvise(BODTO<AdviceLine> adv) throws InventoryException, BusinessException {
		log.debug("finishAdvise Start advId=" + adv.getId());
		AdviceLine adv2 = manager.find(AdviceLine.class, adv.getId());

		List<GoodsReceipt> goodsReceipts = goodsReceiptService.readByAdviceLine(adv2);
		for (GoodsReceipt goodsReceipt : goodsReceipts) {
			if (goodsReceipt.getState() < OrderState.FINISHED) {
				throw new InventoryException(InventoryExceptionKey.GOODS_RECEIPT_NOT_FINISHED,
						goodsReceipt.getOrderNumber());
			}
		}
		adv2.setState(OrderState.FINISHED);
	}

}
