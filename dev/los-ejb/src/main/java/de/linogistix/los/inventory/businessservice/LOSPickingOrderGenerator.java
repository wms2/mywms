/*
 * Copyright (c) 2009-2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.inventory.model.LOSPickingPosition;

/**
 * Strategy service to handle generation of picking orders.<br>
 * 
 * @author krane
 *
 */
@Local
public interface LOSPickingOrderGenerator {

	/**
	 * Generate picking orders for the given picking positions.<br>
	 * All strategies to generate multiple orders are considered. 
	 * 
	 * @param pickList
	 * @param sequenceName. Name of sequence or prefix. Can be null for default
	 * @return
	 * @throws FacadeException
	 */
	public List<LOSPickingOrder> createOrders( List<LOSPickingPosition> pickList, String sequenceName ) throws FacadeException;
	public List<LOSPickingOrder> createOrders( List<LOSPickingPosition> pickList ) throws FacadeException;

	/**
	 * One new picking order is created for the given picking positions.<br>
	 * This Method does not consider the strategies to generate multiple orders.<br> 
	 * The origin use is manual picking order creation from client.  
	 * 
	 * @param pickList
	 * @param sequenceName. Name of sequence or prefix. Can be null for default
	 * @return
	 * @throws FacadeException
	 */
	public LOSPickingOrder createSingleOrder( List<LOSPickingPosition> pickList, String sequenceName ) throws FacadeException;
	public LOSPickingOrder createSingleOrder( List<LOSPickingPosition> pickList ) throws FacadeException;
	
	/**
	 * Try to put the given picking positions to the given picking order.<br>
	 * 
	 * @param order
	 * @param pickList
	 * @throws FacadeException
	 */
	public  List<LOSPickingOrder> addToOrder( LOSPickingOrder order, List<LOSPickingPosition> pickList ) throws FacadeException;

}
