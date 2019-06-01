/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;


import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.wms2.mywms.location.Area;


/**
 * @author krane
 *
 */
@Remote
public interface LOSAreaServiceRemote {
	
    public Area getByName(Client c, String name) throws EntityNotFoundException;

    public Area getDefault();

    public List<Area> getForGoodsIn();
    public List<Area> getForGoodsOut();
    public List<Area> getForStorage();
    public List<Area> getForPicking();
    public List<Area> getForTransfer();
    
}