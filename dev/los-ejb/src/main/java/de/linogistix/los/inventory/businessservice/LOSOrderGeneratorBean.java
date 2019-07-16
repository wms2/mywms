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

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.inventory.service.LOSCustomerOrderPositionService;
import de.linogistix.los.inventory.service.LOSCustomerOrderService;
import de.linogistix.los.model.State;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyEntityService;

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
	private OrderStrategyEntityService orderStratService;
	@EJB
	private ContextService contextService;
	@EJB
	private LOSCustomerOrderService orderService;
    @PersistenceContext(unitName = "myWMS")
    private  EntityManager manager;
    
	public DeliveryOrder createDeliveryOrder(Client client, OrderStrategy strat) throws FacadeException {
		String logStr = "createDeliveryOrder ";
		log.debug(logStr);
		
		if( client == null ) {
			client = contextService.getCallersClient();
		}
		if( strat == null ) {
			strat = orderStratService.getDefault(client);
		}
		// Check order number
		DeliveryOrder order = null;
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
		
		order = entityGenerator.generateEntity(DeliveryOrder.class);
		
		order.setOrderNumber(number);
		order.setClient(client);
		order.setOrderStrategy(strat);
		
		manager.persist(order);
		manager.flush();

		
		order.setLines(new ArrayList<DeliveryOrderLine>());
		
		log.debug(logStr+"New order created. number="+number);

		return order;
	}

	public DeliveryOrder addDeliveryOrderLine(DeliveryOrder order, ItemData item, Lot lot, String serialNumber, BigDecimal amount) throws FacadeException {
		String logStr = "addDeliveryOrderLine ";
		log.debug(logStr+" order="+order.getOrderNumber()+", item="+item.getNumber());
		
		String numberOrder = order.getOrderNumber();
		String number = null;
		int idx = order.getLines().size();
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

		DeliveryOrderLine pos = null;
		pos = entityGenerator.generateEntity(DeliveryOrderLine.class);
		pos.setItemData(item);
		pos.setLot(lot);
		pos.setSerialNumber(serialNumber);
		pos.setAmount(amount);
		pos.setClient(order.getClient());
		pos.setLineNumber(number);
		pos.setDeliveryOrder(order);
		pos.setState(State.RAW);
		
		manager.persist(pos);

		order.getLines().add(pos);

		return order;
	}


}
