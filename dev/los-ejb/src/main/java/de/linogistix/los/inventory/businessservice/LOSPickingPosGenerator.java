/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.model.LOSOrderStrategy;
import de.linogistix.los.inventory.model.LOSPickingPosition;

/**
 * Strategy service to handle generation of picking positions.<br>
 * 
 * @author krane
 *
 */
@Local
public interface LOSPickingPosGenerator {

	/**
	 * Generates picking positions for the customer order.<br>
	 * For every position and amount, not already in a picking position, a new picking position is created.<br>
	 * No picking order is created!<br>
	 * 
	 * @param order
	 * @param onlyComplete	If TRUE, the methods will throw an exception, if not everything can be done.
	 * @return
	 * @throws FacadeException
	 */
	public List<LOSPickingPosition> generatePicks( LOSCustomerOrder order, boolean completeOnly ) throws FacadeException;

	public List<LOSPickingPosition> generatePicks( LOSCustomerOrderPosition customerOrderPos, LOSOrderStrategy strategy, BigDecimal amount ) throws FacadeException;
	
	public LOSPickingPosition generatePick( BigDecimal amount, StockUnit pickFromStock, LOSOrderStrategy strat, LOSCustomerOrderPosition customerOrderPos ) throws FacadeException;

}
