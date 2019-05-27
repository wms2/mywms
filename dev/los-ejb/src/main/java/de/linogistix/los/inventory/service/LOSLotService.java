/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

/**
 *
 * @author trautm
 */
@Local
public interface LOSLotService extends BasicService<Lot>{

	/**
	 *  Creates a new Lot
	 *  
	 * @param c the client this lot belongs to
	 * @param idat the lot's item data
	 * @param lotRef the name of the lot
	 * @param production the production date of the lot
	 * @param useNotBefore before this date the lot is not valid 
	 * @param bestBeforeEnd after this date the lot i snot valid
	 * @return the created lot
	 */
    public Lot create(Client c, ItemData idat, String lotRef, Date production, Date useNotBefore, Date bestBeforeEnd);
    
    /**
     * Returns List od {@link Lot} by name.
     * @param c the client of the lot
     * @param lotName the name of the lot
     * @return list of mathcing lots
     * 
     * @throws EntityNotFoundException
     */
    public List<Lot> getListByName(Client c, String lotName, String itemDataNumber);
    
    /**
     * Returns List of {@link Lot} by {@link Client} which are too old. i.e. their bestBeforeEnd date has expired.
     * @param c only lots of this client
     * @return
     */
    public List<Lot> getTooOld(Client c);
    
    /**
     * Returns List of {@link Lot} by {@link Client} which are too young. i.e. their useNotBefore date has not been reached
     * @param c
     * @return
     */
    public List<Lot>  getNotToUse(Client c);
    
    /**
     * Returns List of {@link Lot} which can be used from today, i.e. their useNotBefore date has been reached
     * @param c
     * @return
     */
    public List<Lot> getToUseFromNow(Client c);
    
    /**
     * Retuns {@link Lot} of matching {@link Client} by name of the lot and associated {@link ItemData}.
     * @param c the CLient of the Lot
     * @param lotName the name of the lot
     * @param idat the item data of the lot
     * @return matching lot
     * @throws EntityNotFoundException
     */
	public Lot getByNameAndItemData(Client c, String lotName, String idat) throws EntityNotFoundException;

}
