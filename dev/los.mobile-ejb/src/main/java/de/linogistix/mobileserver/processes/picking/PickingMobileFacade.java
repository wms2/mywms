/*
 * Copyright (c) 2012-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.picking;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Remote;
import javax.faces.model.SelectItem;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSPickingOrder;

/**
 * @author krane
 *
 */
@Remote
public interface PickingMobileFacade {
	
	public Client getDefaultClient();
	
	List<SelectItem> getCalculatedPickingOrders(String code, Long clientId, Long workingAreaId, boolean usePick, boolean useTransport, int limit);
	public List<SelectItem> getWorkingAreaList(String code);

	public List<LOSPickingOrder> getStartedOrders(boolean useTransport);
	
	public void printLabel( String label, String printer ) throws FacadeException;

	
	public PickingMobileOrder readOrder(long id);
	public List<PickingMobilePos> readPickList( long orderId );
	public PickingMobileUnitLoad readUnitLoad( long orderId );
	public List<PickingMobileUnitLoad> readUnitLoadList( long orderId );
	public PickingMobilePos reloadPick(PickingMobilePos pick);
	
	
	public void reserveOrder(long id) throws FacadeException ;
	public void startOrder( long id ) throws FacadeException;
	public void releaseOrder( long id ) throws FacadeException;
	public void finishOrder(long orderId) throws FacadeException ;
	
	
	public void confirmPick(PickingMobilePos pickTO, PickingMobileUnitLoad pickTo, BigDecimal amountPicked, BigDecimal amountRemain, List<String> serialNoList, boolean counted ) throws FacadeException;
	
	public void transferUnitLoad(String label, String target, int state) throws FacadeException;
	
	
	public String generatePickToLabel() throws FacadeException;
	
	public void changeUnitLoadLabel( String labelOld, String labelNew, long pickingOrderId ) throws FacadeException;
	
	public PickingMobileUnitLoad createUnitLoad( String label, long pickingOrderId, int index ) throws FacadeException;
	
	public boolean removeUnitLoadIfEmpty( String label ) throws FacadeException;

	public void checkSerial( String clientNumber, String itemNumber, String code ) throws FacadeException ;
	public PickingMobilePos changePickFromStockUnit( PickingMobilePos to, String label ) throws FacadeException;
	public Comparator<PickingMobilePos> getPickingComparator();

	void checkLocationScan( String code ) throws FacadeException;
}
