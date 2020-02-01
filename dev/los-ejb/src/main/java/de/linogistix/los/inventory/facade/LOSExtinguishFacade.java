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

/**
 * @author krane
 *
 */
@Remote
public interface LOSExtinguishFacade {
	
	public void generateStockUnitOrder( List<Long> stockIds ) throws FacadeException;
	public void generateUnitLoadOrder( List<Long> unitLoadIds ) throws FacadeException;

}
