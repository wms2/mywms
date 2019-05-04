/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.customization;

import java.math.BigDecimal;
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
import de.linogistix.los.inventory.model.LOSAdviceType;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.inventory.model.LOSUnitLoadAdviceState;
import de.linogistix.los.inventory.service.InventoryGeneratorService;

public class ManageUnitLoadAdviceServiceBean implements ManageUnitLoadAdviceService 
{

	
	@EJB
	private InventoryGeneratorService seqService;
	
	@EJB
	private EntityGenerator entityGenerator;
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	private static final Logger log = Logger.getLogger(ManageUnitLoadAdviceServiceBean.class);
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageUnitLoadAdviceService#getNewAdviceNumber()
	 */
	public String getNewAdviceNumber() {
		return seqService.generateUnitLoadAdviceNumber(null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageUnitLoadAdviceService#createAdvice(org.mywms.model.Client, java.lang.String, de.linogistix.los.inventory.model.LOSAdviceType, java.lang.String)
	 */
	public LOSUnitLoadAdvice createAdvice(Client cl, String adviceNumber, LOSAdviceType type, String labelId) 
	{
		if(cl == null){
			log.error(" --- CLIENT NULL ---");
			throw new NullPointerException(" --- CLIENT NULL ---");
		}
		
		if(adviceNumber == null){
			log.error(" --- ADVICE NUMBER NULL ---");
			throw new NullPointerException(" --- ADVICE NUMBER NULL ---");
		}
		
		if(type == null){
			log.error(" --- ADVICE TYPE NULL ---");
			throw new NullPointerException(" --- ADVICE TYPE NULL ---");
		}
		
		if(labelId == null){
			log.error(" --- LABEL ID NULL ---");
			throw new NullPointerException(" --- LABEL ID NULL ---");
		}
		
		LOSUnitLoadAdvice uadv = entityGenerator.generateEntity( LOSUnitLoadAdvice.class );
		uadv.setClient(cl);
		uadv.setAdviceType(type);
		uadv.setNumber(adviceNumber);
		uadv.setLabelId(labelId);
		uadv.setAdviceState(LOSUnitLoadAdviceState.CREATED);
		
		manager.persist(uadv);
		
		log.info("[CREATED]-[UNITLOAD ADVICE]-Cl "+cl.getNumber()+"-No "+adviceNumber
				+"-AVT "+type+"-LABEL "+labelId);
		
		return uadv;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageUnitLoadAdviceService#addPosition(de.linogistix.los.inventory.model.LOSUnitLoadAdvice, org.mywms.model.Lot, org.mywms.model.ItemData, java.math.BigDecimal)
	 */
	public LOSUnitLoadAdvice addPosition(LOSUnitLoadAdvice unitLoadAdvice, Lot lot, 
										 ItemData item, BigDecimal notifiedAmount)
			throws OutOfRangeException 
	{
		if(unitLoadAdvice == null){
			log.error(" --- UNITLOAD ADVICE NULL ---");
			throw new NullPointerException(" --- UNITLOAD ADVICE NULL ---");
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
		
		LOSUnitLoadAdvicePosition pos = entityGenerator.generateEntity( LOSUnitLoadAdvicePosition.class );
		pos.setClient(unitLoadAdvice.getClient());
		pos.setUnitLoadAdvice(unitLoadAdvice);
		
		List<LOSUnitLoadAdvicePosition> posList = unitLoadAdvice.getPositionList();
		int posNo = posList.size()+1;
		
		pos.setPositionNumber(unitLoadAdvice.getNumber()+"-"+posNo);
		pos.setItemData(item);
		pos.setNotifiedAmount(notifiedAmount);
		pos.setLot(lot);
		
		manager.persist(pos);
		
		unitLoadAdvice.getPositionList().add(pos);
		
		log.info("[ADDED]-[ULADVICE POS]-ADV "+unitLoadAdvice.getNumber()+"-IT "+item.getNumber()
				+"-LOT "+lot+"-AMOUNT "+notifiedAmount);
		
		return unitLoadAdvice;
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageUnitLoadAdviceService#updateAdviceType(de.linogistix.los.inventory.model.LOSUnitLoadAdvice, de.linogistix.los.inventory.model.LOSAdviceType)
	 */
	public LOSUnitLoadAdvice updateAdviceType(LOSUnitLoadAdvice ulAdvice, LOSAdviceType newType) 
	{
		if(newType == null || ulAdvice.getAdviceType().equals(newType)){
			return ulAdvice;
		}
			
		log.info("[UPDATE]-[ULADVICE]-ADV "+ulAdvice.getNumber()
				+"-[OLD] Type "+ulAdvice.getAdviceType().name()+"-[NEW] Type "+newType.name());
		
		ulAdvice.setAdviceType(newType);
		
		return manager.merge(ulAdvice);
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.customization.ManageUnitLoadAdviceService#deleteUnitLoadAdvice(de.linogistix.los.inventory.model.LOSUnitLoadAdvice)
	 */
	public void deleteUnitLoadAdvice(LOSUnitLoadAdvice ulAdvice) {
		
		if(ulAdvice == null){
			log.error(" --- UNITLOAD ADVICE NULL ---");
			throw new NullPointerException(" --- UNITLOAD ADVICE NULL ---");
		}

		String advNo = ulAdvice.getNumber();
		String labelId = ulAdvice.getLabelId();
		
		for(LOSUnitLoadAdvicePosition pos:ulAdvice.getPositionList()){
			manager.remove(pos);
		}
		
		manager.remove(ulAdvice);
		
		log.info("[DELETE][ULADVICE]-ADV "+advNo+"-[UL] "+labelId);
		
	}

	public LOSUnitLoadAdvice switchState(LOSUnitLoadAdvice ulAdvice, LOSUnitLoadAdviceState newState, String switchStateInfo) 
			throws InventoryException {
		return null;
	}

}
