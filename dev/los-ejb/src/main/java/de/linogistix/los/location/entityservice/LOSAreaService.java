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

import de.wms2.mywms.location.Area;


/**
 * 
 * @author Markus Jordan
 * @version $Revision: 339 $ provided by $Author: trautmann $
 */
@Local
public interface LOSAreaService
	extends BasicService<Area>
{
	
    Area createLOSArea(Client c, String name);
    
    public Area getByName(Client c, String name) throws EntityNotFoundException;

    public Area getDefault();

    public List<Area> getForGoodsIn();
    public List<Area> getForGoodsOut();
    public List<Area> getForStorage();
    public List<Area> getForPicking();
    public List<Area> getForTransfer();
    
}