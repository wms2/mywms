/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;
import org.mywms.model.ItemData;
import org.mywms.service.BasicService;
import org.mywms.service.ConstraintViolatedException;

import de.linogistix.los.inventory.model.LOSBom;

/**
 * @author krane
 *
 */
@Local
public interface LOSBomService extends BasicService<LOSBom> {
	
	public LOSBom create( ItemData parent, ItemData child, BigDecimal amount, boolean pickable ) throws FacadeException;
	
	/**
	 * deletes all Boms with the given parent
	 * @param parent
	 * @throws FacadeException
	 * @throws ConstraintViolatedException
	 */
	public void deleteAll( ItemData parent ) throws FacadeException, ConstraintViolatedException;

	/**
	 * Selects a list of all boms, where the given item data is a child item data
	 * @param parent
	 * @return
	 */
	public List<LOSBom> getParentBomList( ItemData child );
	
	/**
	 * Selects a list of all boms, where the given item data is the parent item data
	 * @param parent
	 * @return
	 */
	public List<LOSBom> getChildBomList( ItemData parent );
	
	/**
	 * Selects a list of all parent item data, where the given ItemData is a child item data
	 * @param child
	 * @return
	 */
	public List<ItemData> getParentList( ItemData child );
	
	/**
	 * Selects a list of all child item data, where the given ItemData is the parent item data
	 * @param child
	 * @return
	 */
	public List<ItemData> getChildList( ItemData parent );

	public LOSBom getBom( ItemData parent, ItemData child );

}
