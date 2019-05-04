/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.businessservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestPosition;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.location.model.LOSUnitLoad;

@Local
public interface LOSGoodsOutBusiness {

	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out) throws FacadeException;
	
	public LOSGoodsOutRequest finish(LOSGoodsOutRequest out, boolean force) throws FacadeException;
	
	/**
	 * Finish the order in the current state. Not confirmed positions will be canceled.
	 * @param out
	 * @return
	 * @throws FacadeException
	 */
	public LOSGoodsOutRequest finishOrder(LOSGoodsOutRequest out) throws FacadeException;

	public LOSGoodsOutRequestPosition finishPosition(LOSGoodsOutRequest out, LOSUnitLoad ul) throws FacadeException;
	
	public List<LOSGoodsOutRequestTO> getRaw();
	
	public LOSGoodsOutRequest accept(LOSGoodsOutRequest req) throws FacadeException;

	public LOSGoodsOutRequest cancel(LOSGoodsOutRequest req) throws FacadeException;
	
	public void remove(LOSGoodsOutRequest req) throws FacadeException;

}