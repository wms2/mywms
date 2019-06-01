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
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.StorageLocation;

/**
 *
 * @author Jordan
 */
@Local
public interface LOSUnitLoadService extends BasicService<UnitLoad>{

    public UnitLoad createLOSUnitLoad(Client client,
                                         String labelId,
                                         UnitLoadType type,
                                         StorageLocation storageLocation) throws FacadeException;
    
    public List<UnitLoad> getListByStorageLocation(StorageLocation sl);
    
    public List<UnitLoad> getListEmptyByStorageLocation(StorageLocation sl);
    
    /**
	 * Searches for UnitLoads whose label starts with the given String 
	 * and which are belonging to the given Client. If the given Client is the 
	 * system client, the search is regardless of the client.
	 * 
	 * @param client the client the UnitLoads belong to or the system client.
	 * @param labelPart the characters the label should start with.
	 * @return true if there are one or more matching UnitLoads, false otherwise.
	 */
    public List<UnitLoad> getListByLabelStartsWith(Client client, String labelPart);

    
    public UnitLoad getByLabelId(Client client, String labelId) throws EntityNotFoundException;


    public UnitLoad getNirwana();

    /**
     * Check whether the given UnitLoad is a carrier 
     * @param unitLoad
     * @return
     */
    public boolean hasChilds(UnitLoad unitLoad);

    /**
     * Check whether the given UnitLoad is a carrier. Do not check the notOther unit load.
     * @param unitLoad
     * @param notOther
     * @return
     */
    public boolean hasOtherChilds(UnitLoad unitLoad, UnitLoad notOther);

    /**
     * Check whether the given unitLoad has parent as parent or grand-parent
     * This method works recursive and checks all levels of carriers and carrier-carriers  
     * @param unitLoad
     * @param parent
     * @return
     */
    public boolean hasParent(UnitLoad unitLoad, UnitLoad parent) throws FacadeException;
    
    /**
     * Count the number of child UnitLoads
     * @param unitLoad
     * @return
     */
    public Long getNumChilds(UnitLoad unitLoad);
    public Long getNumChilds(Long unitLoadId);

    /**
     * Read the list of childs of a carrier
     * @param unitLoad
     * @return
     */
    public List<UnitLoad> getChilds(UnitLoad unitLoad);

    /**
     * Read the carrier of the given UnitLoad
     * @param unitLoad
     * @return
     */
    public UnitLoad getParent(UnitLoad unitLoad);
    
	/**
	 * Returns true, if a unit load is located on the location
	 * @param location
	 * @return
	 */
	public boolean existsByStorageLocation(StorageLocation location);

}
