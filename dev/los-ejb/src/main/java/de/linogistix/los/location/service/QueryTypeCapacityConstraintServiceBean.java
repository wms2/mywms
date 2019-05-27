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
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

@Stateless
public class QueryTypeCapacityConstraintServiceBean 
		implements QueryTypeCapacityConstraintService, QueryTypeCapacityConstraintServiceRemote
{

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryTypeCapacityConstraintService#getByTypes(de.linogistix.los.location.model.LOSStorageLocationType, org.mywms.model.UnitLoadType)
	 */
	public TypeCapacityConstraint getByTypes(LocationType slType, UnitLoadType ulType) {
		Query query = manager.createNamedQuery("LOSTypeCapacityConstraint.queryBySLTypeAndULType");
		query = query.setParameter("SLType", slType);
		query = query.setParameter("ULType", ulType);

		try{
			return (TypeCapacityConstraint) query.getSingleResult();
			
		}catch(NoResultException nre){
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryTypeCapacityConstraintService#getListByLocationType(de.linogistix.los.location.model.LOSStorageLocationType)
	 */
	@SuppressWarnings("unchecked")
	public List<TypeCapacityConstraint> getListByLocationType(LocationType slType) {
		
		StringBuffer sb = new StringBuffer("SELECT tcc FROM ");
		sb.append(TypeCapacityConstraint.class.getSimpleName()+" tcc ");
		sb.append("WHERE tcc.locationType=:slt");
		
		Query query = manager.createQuery(sb.toString());
		
		query.setParameter("slt", slType);
		
		return query.getResultList();
	}

}
