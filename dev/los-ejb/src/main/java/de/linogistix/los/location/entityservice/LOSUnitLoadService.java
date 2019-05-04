/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSUnitLoadService extends BasicService<LOSUnitLoad>{

    public LOSUnitLoad createLOSUnitLoad(Client client,
                                         String labelId,
                                         UnitLoadType type,
                                         LOSStorageLocation storageLocation) throws FacadeException;
    
    public List<LOSUnitLoad> getListByStorageLocation(LOSStorageLocation sl);
    
    public List<LOSUnitLoad> getListEmptyByStorageLocation(LOSStorageLocation sl);
    
    /**
	 * Searches for UnitLoads whose label starts with the given String 
	 * and which are belonging to the given Client. If the given Client is the 
	 * system client, the search is regardless of the client.
	 * 
	 * @param client the client the UnitLoads belong to or the system client.
	 * @param labelPart the characters the label should start with.
	 * @return true if there are one or more matching UnitLoads, false otherwise.
	 */
    public List<LOSUnitLoad> getListByLabelStartsWith(Client client, String labelPart);

    
    public LOSUnitLoad getByLabelId(Client client, String labelId) throws EntityNotFoundException;


    public LOSUnitLoad getNirwana();

    /**
     * Check whether the given UnitLoad is a carrier 
     * @param unitLoad
     * @return
     */
    public boolean hasChilds(LOSUnitLoad unitLoad);

    /**
     * Check whether the given UnitLoad is a carrier. Do not check the notOther unit load.
     * @param unitLoad
     * @param notOther
     * @return
     */
    public boolean hasOtherChilds(LOSUnitLoad unitLoad, LOSUnitLoad notOther);

    /**
     * Check whether the given unitLoad has parent as parent or grand-parent
     * This method works recursive and checks all levels of carriers and carrier-carriers  
     * @param unitLoad
     * @param parent
     * @return
     */
    public boolean hasParent(LOSUnitLoad unitLoad, LOSUnitLoad parent) throws FacadeException;
    
    /**
     * Count the number of child UnitLoads
     * @param unitLoad
     * @return
     */
    public Long getNumChilds(LOSUnitLoad unitLoad);
    public Long getNumChilds(Long unitLoadId);

    /**
     * Read the list of childs of a carrier
     * @param unitLoad
     * @return
     */
    public List<LOSUnitLoad> getChilds(LOSUnitLoad unitLoad);

    /**
     * Read the carrier of the given UnitLoad
     * @param unitLoad
     * @return
     */
    public LOSUnitLoad getParent(LOSUnitLoad unitLoad);
    
	/**
	 * Returns true, if a unit load is located on the location
	 * @param location
	 * @return
	 */
	public boolean existsByStorageLocation(LOSStorageLocation location);

}
