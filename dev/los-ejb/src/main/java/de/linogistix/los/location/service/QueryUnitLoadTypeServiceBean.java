/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;

@Stateless
public class QueryUnitLoadTypeServiceBean implements QueryUnitLoadTypeService,
		QueryUnitLoadTypeServiceRemote {

	@EJB
	private ContextService ctxService;
	
	@EJB
	private UnitLoadTypeService ultService;

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

    /**
     * This implementation interprets UnitLoadType with id <code>1</code> as default unitload type. 
     */
	public UnitLoadType getPickLocationUnitLoadType() {
		return unitLoadTypeEntityService.getVirtual();
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadTypeService#getList()
	 */
	@SuppressWarnings("unchecked")
	public List<UnitLoadType> getList() {
			
		StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT ult FROM "
                	+ UnitLoadType.class.getSimpleName()+ " ult ");
		
		Query query = manager.createQuery(qstr.toString());
        
        return (List<UnitLoadType>) query.getResultList();
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

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadTypeService#getByName(java.lang.String)
	 */
	public UnitLoadType getByName(String name) {
		
		Query query = manager.createQuery("SELECT o FROM "
				+ UnitLoadType.class.getSimpleName() + " o "
				+ "WHERE o.name=:na");

		query.setParameter("na", name);

		try {
			UnitLoadType ult = (UnitLoadType) query.getSingleResult();
			return ult;
			
		} catch (NoResultException ex) {
			return null;
		}
	}
}
