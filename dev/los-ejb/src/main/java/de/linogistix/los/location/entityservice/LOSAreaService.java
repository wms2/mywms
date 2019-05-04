/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package de.linogistix.los.location.entityservice;


import java.util.List;

import javax.ejb.Local;

import org.mywms.model.Client;
import org.mywms.service.BasicService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.location.model.LOSArea;


/**
 * This interface declares the service for the entity LOSRack and LOSRackLocation.
 * 
 * @author Markus Jordan
 * @version $Revision: 339 $ provided by $Author: trautmann $
 */
@Local
public interface LOSAreaService
	extends BasicService<LOSArea>
{
	
    LOSArea createLOSArea(Client c, String name);
    
    public LOSArea getByName(Client c, String name) throws EntityNotFoundException;

    public LOSArea getDefault();

    public List<LOSArea> getForGoodsIn();
    public List<LOSArea> getForGoodsOut();
    public List<LOSArea> getForStorage();
    public List<LOSArea> getForPicking();
    public List<LOSArea> getForTransfer();
    
}