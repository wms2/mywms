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

import de.linogistix.los.location.model.LOSArea;


/**
 * @author krane
 *
 */
@Remote
public interface LOSAreaServiceRemote {
	
    public LOSArea getByName(Client c, String name) throws EntityNotFoundException;

    public LOSArea getDefault();

    public List<LOSArea> getForGoodsIn();
    public List<LOSArea> getForGoodsOut();
    public List<LOSArea> getForStorage();
    public List<LOSArea> getForPicking();
    public List<LOSArea> getForTransfer();
    
}