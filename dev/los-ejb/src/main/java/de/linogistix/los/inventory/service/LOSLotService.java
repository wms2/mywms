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

import org.mywms.service.BasicService;

import de.wms2.mywms.inventory.Lot;

/**
 *
 * @author trautm
 */
@Local
public interface LOSLotService extends BasicService<Lot>{

    /**
     * Returns List of {@link Lot} which are too old. i.e. their bestBeforeEnd date has expired.
     * @param c only lots of this client
     * @return
     */
    public List<Lot> getTooOld();
    
    /**
     * Returns List of {@link Lot} which are too young. i.e. their useNotBefore date has not been reached
     * @param c
     * @return
     */
    public List<Lot>  getNotToUse();
    
    /**
     * Returns List of {@link Lot} which can be used from today, i.e. their useNotBefore date has been reached
     * @param c
     * @return
     */
    public List<Lot> getToUseFromNow();

	/**
	 * Sets dates as indicated and locks the Lot if necessary.
	 * 
	 * @param lot
	 * @param bestBeforeEnd
	 * @param useNotBefore
	 */
	void processLotDates(Lot lot, Date bestBeforeEnd, Date useNotBefore);

}
