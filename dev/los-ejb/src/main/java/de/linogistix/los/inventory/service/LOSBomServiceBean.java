/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.facade.FacadeException;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ConstraintViolatedException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSBom;
import de.wms2.mywms.product.ItemData;

/**
 * @author krane
 *
 */
@Stateless
public class LOSBomServiceBean extends
		BasicServiceBean<LOSBom> implements LOSBomService {
	
	@EJB
	private EntityGenerator entityGenerator;
	
	public LOSBom create( ItemData parent, ItemData child, BigDecimal amount, boolean pickable ) throws FacadeException {
		if ( parent == null ) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "BOM parent must not be null");
		}
		if ( child == null ) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "BOM child must not be null");
		}
		if ( !parent.getClient().equals(child.getClient()) ) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, "BOM parent and child must have the same client");
		}
		if ( amount == null ) {
			amount = BigDecimal.ONE;
		}
		
		LOSBom bom = entityGenerator.generateEntity(LOSBom.class);

		bom.setParent(parent);
		bom.setChild(child);
		bom.setAmount(amount);
		bom.setPickable(pickable);

		manager.persist(bom);
		manager.flush();

		return bom;
	}

    @SuppressWarnings("unchecked")
	public List<LOSBom> getParentBomList( ItemData child ){
		

    	String queryStr = "SELECT bom FROM " + LOSBom.class.getSimpleName() + " bom WHERE bom.child = :child ";

        Query query = manager.createQuery( queryStr );

        query.setParameter("child", child);

        List<LOSBom> bomList = null;
        try {
        	bomList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        return bomList;
    }
    
    @SuppressWarnings("unchecked")
	public List<ItemData> getParentList( ItemData child ){
		
    	String queryStr = "SELECT bom.parent FROM " + LOSBom.class.getSimpleName() + " bom WHERE bom.child = :child ";

        Query query = manager.createQuery( queryStr );

        query.setParameter("child", child);

        List<ItemData> parentList = null;
        try {
        	parentList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        return parentList;
    }

    @SuppressWarnings("unchecked")
	public List<LOSBom> getChildBomList( ItemData parent ){

    	String queryStr = "SELECT bom FROM " + LOSBom.class.getSimpleName() + " bom WHERE bom.parent = :parent ";

        Query query = manager.createQuery( queryStr );

        query.setParameter("parent", parent);

        List<LOSBom> bomList = null;
        try {
        	bomList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        return bomList;
    }
    
    @SuppressWarnings("unchecked")
	public List<ItemData> getChildList( ItemData parent ){
		

    	String queryStr = "SELECT bom.child FROM " + LOSBom.class.getSimpleName() + " bom WHERE bom.parent = :parent ";

        Query query = manager.createQuery( queryStr );

        query.setParameter("parent", parent);

        List<ItemData> childList = null;
        try {
        	childList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        return childList;
    }
    
    


	@SuppressWarnings("unchecked")
	public LOSBom getBom( ItemData parent, ItemData child ) {
		

    	String queryStr = "SELECT bom FROM " + LOSBom.class.getSimpleName() + " bom WHERE bom.parent = :parent and bom.child = :child ";

        Query query = manager.createQuery( queryStr );

        query.setParameter("parent", parent);
        query.setParameter("child", child);

        List<LOSBom> posList = null;
        try {
        	posList = query.getResultList();
        }
        catch (NoResultException ex) {
        	// is handled below
        }
        
        return (posList != null && posList.size() > 0) ? posList.get(0) : null;
		
	}


	public void deleteAll(ItemData parent) throws FacadeException, ConstraintViolatedException {
    	List<LOSBom> bomList = getChildBomList(parent);
    	
    	for( LOSBom bom : bomList ) {
    		delete(bom);
    	}
	}



}
