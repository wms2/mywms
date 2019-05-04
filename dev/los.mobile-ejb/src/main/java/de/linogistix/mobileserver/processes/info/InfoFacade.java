/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobileserver.processes.info;

import java.util.List;

import javax.ejb.Remote;

import org.mywms.model.Client;

/**
 * @author krane
 *
 */
@Remote
public interface InfoFacade {
	
	public Client getDefaultClient();
	public InfoItemDataTO readItemData( String itemNumber );
	public InfoLocationTO readLocation( String locationName );
	public InfoUnitLoadTO readUnitLoad( String name );
	public List<InfoUnitLoadTO> readUnitLoadList( String locationName );
	public List<InfoStockUnitTO> readStockUnitList( String itemNumber );

}
