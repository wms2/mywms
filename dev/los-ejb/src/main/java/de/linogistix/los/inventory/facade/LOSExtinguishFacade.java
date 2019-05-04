/*
 * Copyright (c) 2006 - 2011 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.StockUnit;

/**
 * @author krane
 *
 */
@Remote
public interface LOSExtinguishFacade {
	
	public void generateOrder( String clientNumber, String lotName, String itemDataNumber ) throws FacadeException;
	
	public void generateOrder( List<Long> stockIds ) throws FacadeException;

	public void calculateLotLocks() throws FacadeException;
	
	public void generateOrder( Client client, List<StockUnit> stockList ) throws FacadeException;

}
