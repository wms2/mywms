/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemDataNumber;
import org.mywms.service.BasicService;

/**
 * @author krane
 *
 */
@Local
public interface ItemDataNumberService extends BasicService<ItemDataNumber> {
	
	public ItemDataNumber getByNumber( Client client, String number );
	public ItemDataNumber getByNumber( String number );

	public List<ItemDataNumber> getListByNumber( Client client, String number );

	public List<ItemDataNumber> getListByItemData( ItemData itemData );
	
	public ItemDataNumber create( ItemData itemData, String number ) throws FacadeException;

}
