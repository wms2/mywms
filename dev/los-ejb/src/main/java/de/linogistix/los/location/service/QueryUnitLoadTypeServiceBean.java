/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;

@Stateless
public class QueryUnitLoadTypeServiceBean implements QueryUnitLoadTypeServiceRemote {

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;

	@Inject
	private UnitLoadTypeEntityService unitLoadTypeEntityService;

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadTypeService#getDefaultUnitLoadType()
	 */
	public UnitLoadType getDefaultUnitLoadType(){
		return unitLoadTypeEntityService.getDefault();
    }


    /*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadTypeService#getSortedList(boolean, boolean, boolean, boolean, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<UnitLoadType> getSortedList(boolean orderByName,
											boolean orderByHeight,
											boolean orderByWidth,
											boolean orderByDepth,
											boolean orderByWeight)
	{
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT ult FROM "
                	+ UnitLoadType.class.getSimpleName()+ " ult ");
		
        String orderClause = "ORDER BY ";
        if(orderByName){
        	qstr.append(orderClause+"ult.name");
        	orderClause = " , ";
        }
        if(orderByHeight){
        	qstr.append(orderClause+"ult.height");
        	orderClause = " , ";
        }
        if(orderByWidth){
        	qstr.append(orderClause+"ult.width");
        	orderClause = " , ";
        }
        if(orderByDepth){
        	qstr.append(orderClause+"ult.depth");
        	orderClause = " , ";
        }
        if(orderByWeight){
        	qstr.append(orderClause+"ult.weight");
        	orderClause = " , ";
        }
        
        Query query = manager.createQuery(qstr.toString());

        return (List<UnitLoadType>) query.getResultList();
	}
}
