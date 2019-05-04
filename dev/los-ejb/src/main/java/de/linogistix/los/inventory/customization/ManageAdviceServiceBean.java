/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

import de.linogistix.los.common.exception.OutOfRangeException;
import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.exception.StockExistException;
import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSGoodsReceiptService;

public class ManageAdviceServiceBean implements ManageAdviceService {

	@EJB
	private InventoryGeneratorService genService;
	
	@EJB
	private LOSGoodsReceiptService grService;
	@EJB
	private EntityGenerator entityGenerator;

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	private static final Logger log = Logger.getLogger(ManageAdviceServiceBean.class);
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#createAdvice(org.mywms.model.Client, java.lang.String, org.mywms.model.ItemData, java.math.BigDecimal)
	 */
	public LOSAdvice createAdvice(Client cl, String adviceNumber, ItemData item, BigDecimal notifiedAmount) 
		throws OutOfRangeException
	{
		if(cl == null){
			log.error(" --- CLIENT NULL ---");
			throw new NullPointerException(" --- CLIENT NULL ---");
		}
		
		if(adviceNumber == null){
			log.error(" --- ADVICE NUMBER NULL ---");
			throw new NullPointerException(" --- ADVICE NUMBER NULL ---");
		}
		
		if(item == null){
			log.error(" --- ITEM DATA NULL ---");
			throw new NullPointerException(" --- ITEM DATA NULL ---");
		}
		
		if(notifiedAmount == null){
			log.error(" --- NOTIFIED AMOUNT NULL ---");
			throw new NullPointerException(" --- NOTIFIED AMOUNT NULL ---");
		}
		
		if(notifiedAmount.compareTo(BigDecimal.ZERO) < 0){
			log.error(" --- !!! NOTIFIED AMOUNT NEGATIVE "+notifiedAmount+" !!! ---");
			throw new OutOfRangeException();
		}
		
		log.info("[CREATED]-[ADVICE]-Cl "+cl.getNumber()+"-No "+adviceNumber+"-IT "+item.getNumber()+"-AMOUNT "+notifiedAmount);
		
		return getManagedInstance(cl, adviceNumber, item, notifiedAmount);
	}

	/*
	 *  -----------------------------------------------------------------------------------
	 */
	private LOSAdvice getManagedInstance(Client cl, String adviceNumber, ItemData item, BigDecimal notifiedAmount){
		
		notifiedAmount = notifiedAmount.setScale(item.getScale(), RoundingMode.HALF_UP);
		
		LOSAdvice adv = entityGenerator.generateEntity( LOSAdvice.class );
		adv.setClient(cl);
		adv.setAdviceNumber(adviceNumber);
		adv.setItemData(item);
		adv.setNotifiedAmount(notifiedAmount);
		adv.setAdviceState(LOSAdviceState.RAW);
		
		manager.persist(adv);
		
		return adv;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#getNewAdviceNumber()
	 */
	public String getNewAdviceNumber() {
		return genService.generateAdviceNumber(null);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#updateExpectedDelivery(de.linogistix.los.inventory.model.LOSAdvice, java.util.Date)
	 */
	public LOSAdvice updateExpectedDelivery(LOSAdvice adv, Date newValue) {
		
		if((adv.getExpectedDelivery() == null && newValue != null)
			|| !adv.getExpectedDelivery().equals(newValue)){
			
			adv.setExpectedDelivery(newValue);
			
			log.info("[UPDATED]-[ADVICE]-NO "+adv.getAdviceNumber()+"-[NEW] Expected Delivery "+newValue);
		}
		
		return manager.merge(adv);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#updateAdditionalContent(de.linogistix.los.inventory.model.LOSAdvice, java.lang.String)
	 */
	public LOSAdvice updateAdditionalContent(LOSAdvice adv, String newValue) {
		
		if((adv.getAdditionalContent() == null && newValue != null)
			|| !adv.getAdditionalContent().equals(newValue)){
			
			adv.setAdditionalContent(newValue);
			
			log.info("[UPDATED]-[ADVICE]-NO "+adv.getAdviceNumber()+"-[NEW] AdditionalContent "+newValue);
		}
		
		return manager.merge(adv);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#updateExternalAdviceNumber(de.linogistix.los.inventory.model.LOSAdvice, java.lang.String)
	 */
	public LOSAdvice updateExternalAdviceNumber(LOSAdvice adv, String newValue) {
		
		if((adv.getExternalAdviceNumber() == null && newValue != null)
			|| !adv.getExternalAdviceNumber().equals(newValue)){
			
			adv.setExternalAdviceNumber(newValue);
			
			log.info("[UPDATED]-[ADVICE]-NO "+adv.getAdviceNumber()+"-[NEW] External number "+newValue);
		}
		
		return manager.merge(adv);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#updateItemData(de.linogistix.los.inventory.model.LOSAdvice, org.mywms.model.ItemData)
	 */
	public LOSAdvice updateItemData(LOSAdvice adv, ItemData newValue) throws StockExistException {
		
		if(!adv.getItemData().equals(newValue)){
			
			if(adv.getGrPositionList().size()>0){
				throw new StockExistException();
			}
			
			adv.setItemData(newValue);
			
			log.info("[UPDATED]-[ADVICE]-NO "+adv.getAdviceNumber()+"-[NEW] Item data "+newValue.getNumber());
		}
		
		return manager.merge(adv); 
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#updateLot(de.linogistix.los.inventory.model.LOSAdvice, org.mywms.model.Lot)
	 */
	public LOSAdvice updateLot(LOSAdvice adv, Lot newValue) throws StockExistException {
		
		if((adv.getLot() == null && newValue != null)
			|| !adv.getLot().equals(newValue)){
			
			if(adv.getGrPositionList().size()>0){
				throw new StockExistException();
			}
			
			adv.setLot(newValue);
			
			log.info("[UPDATED]-[ADVICE]-NO "+adv.getAdviceNumber()
					+"-[NEW] Lot ("+newValue.getName()+","+newValue.getItemData().getNumber()+")");
		}
		
		return manager.merge(adv);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#updateNotifiedAmount(de.linogistix.los.inventory.model.LOSAdvice, java.math.BigDecimal)
	 */
	public LOSAdvice updateNotifiedAmount(LOSAdvice adv, BigDecimal newValue) throws StockExistException, OutOfRangeException {
		
		if(newValue.compareTo(BigDecimal.ZERO) < 0){
			throw new OutOfRangeException();
		}
		
		newValue = newValue.setScale(adv.getItemData().getScale(), RoundingMode.HALF_UP);
		
		// if something changed
		if(adv.getNotifiedAmount().compareTo(newValue) != 0){
			
			// check how many stocks we already received
			if(adv.getReceiptAmount().compareTo(newValue) > 0)
			{
				throw new StockExistException();
			}
			
			adv.setNotifiedAmount(newValue);
		
			log.info("[UPDATED]-[ADVICE]-NO "+adv.getAdviceNumber()+"-[NEW] Notified amount "+newValue);
		}
		
		return manager.merge(adv);
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageAdviceService#deleteAdvice(de.linogistix.los.inventory.model.LOSAdvice)
	 */
	public void deleteAdvice(LOSAdvice adv) throws InventoryException {
		
		if(adv.getAdviceState().equals(LOSAdviceState.GOODS_TO_COME)){
			throw new InventoryException(InventoryExceptionKey.WRONG_STATE, new Object[]{adv.getAdviceNumber(), adv.getAdviceState()});
		}
		
		manager.remove(adv);
		
		
	}
	
	public void updateFromHost(LOSAdvice adv) throws Exception {
	}

    public boolean isAdviceChangeable( LOSAdvice adv ) {
    	if( adv == null ) {
			log.info("The advice is not changeable. Advice=NULL. Abort");
			return false;
    	}
    	
		if(adv.getGrPositionList().size()>0){
			log.info("There are assigned GR-Positions. Advice="+adv.getAdviceNumber());
			return false;
		}
		
		List<LOSGoodsReceipt> grList = grService.getByAdvice(adv);
		if( grList != null && grList.size()>0 ) {
			log.info("There exists "+grList.size()+" assigned GRs. GR="+grList.get(0).getGoodsReceiptNumber()+", Advice="+adv.getAdviceNumber());
			return false;
		}
	
		return true;
    }
    
    
}
