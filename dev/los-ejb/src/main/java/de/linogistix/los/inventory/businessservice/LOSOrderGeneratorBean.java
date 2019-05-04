/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSCustomerOrderPositionService;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.inventory.service.LOSOrderStrategyService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.businessservice.ContextService;

/**
 *
 * @author krane
 */
@Stateless
public class LOSOrderGeneratorBean implements LOSOrderGenerator {
	Logger log = Logger.getLogger(LOSOrderGeneratorBean.class);
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private InventoryGeneratorService genService;
	@EJB
	private LOSCustomerOrderPositionService positionService;
	@EJB
	private LOSOrderStrategyService orderStratService;
	@EJB
	private ContextService contextService;
	@EJB
	private LOSCustomerOrderService orderService;
    @PersistenceContext(unitName = "myWMS")
    private  EntityManager manager;
    
	public LOSCustomerOrder createCustomerOrder(Client client, LOSOrderStrategy strat) throws FacadeException {
		String logStr = "createCustomerOrder ";
		log.debug(logStr);
		
		if( client == null ) {
			client = contextService.getCallersClient();
		}
		if( strat == null ) {
			strat = orderStratService.getDefault(client);
		}
		// Check order number
		LOSCustomerOrder order = null;
		String number = null; 
		int i = 0;
		while( i++ < 10000 ) {
			number = genService.generateOrderNumber(client);
			if( orderService.existsByNumber(number) ) {
				log.warn(logStr+"order already exists!!! number=" + number+". Try next...");
				continue;
			}
			break;
		}		
		
		order = entityGenerator.generateEntity(LOSCustomerOrder.class);
		
		order.setNumber(number);
		order.setClient(client);
		order.setStrategy(strat);
		
		manager.persist(order);
		manager.flush();

		
		order.setPositions(new ArrayList<LOSCustomerOrderPosition>());
		
		log.debug(logStr+"New order created. number="+number);

		return order;
	}

	public LOSCustomerOrder addCustomerOrderPos(LOSCustomerOrder order, ItemData item, Lot lot, String serialNumber, BigDecimal amount) throws FacadeException {
		String logStr = "addCustomerOrderPos ";
		log.debug(logStr+" order="+order.getNumber()+", item="+item.getNumber());
		
		String numberOrder = order.getNumber();
		String number = null;
		int idx = order.getPositions().size();
		int i = 0;
		while( true ) {
			i++;
			idx++;
//			number = numberOrder+"-"+String.format("%1$03d", idx);
			number = numberOrder+"-"+idx;
			if( !positionService.existsByNumber(number) ) {
				break;
			}
			if( i>1000 ) {
				log.error(logStr+"Cannot get unique position number");
				throw new InventoryException(InventoryExceptionKey.ORDER_NO_UNIQUE_NUMBER, "");
			}
			log.warn(logStr+"Position already exists. try next");
		}

		LOSCustomerOrderPosition pos = null;
		pos = entityGenerator.generateEntity(LOSCustomerOrderPosition.class);
		pos.setIndex(idx);
		pos.setItemData(item);
		pos.setLot(lot);
		pos.setSerialNumber(serialNumber);
		pos.setAmount(amount);
		pos.setClient(order.getClient());
		pos.setNumber(number);
		pos.setOrder(order);
		pos.setState(State.RAW);
		
		manager.persist(pos);

		order.getPositions().add(pos);

		return order;
	}


}
