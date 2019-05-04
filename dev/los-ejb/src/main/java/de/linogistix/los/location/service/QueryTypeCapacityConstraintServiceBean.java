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

import org.mywms.model.UnitLoadType;

import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;

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
	public LOSTypeCapacityConstraint getByTypes(LOSStorageLocationType slType, UnitLoadType ulType) {
		Query query = manager.createNamedQuery("LOSTypeCapacityConstraint.queryBySLTypeAndULType");
		query = query.setParameter("SLType", slType);
		query = query.setParameter("ULType", ulType);

		try{
			return (LOSTypeCapacityConstraint) query.getSingleResult();
			
		}catch(NoResultException nre){
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryTypeCapacityConstraintService#getListByLocationType(de.linogistix.los.location.model.LOSStorageLocationType)
	 */
	@SuppressWarnings("unchecked")
	public List<LOSTypeCapacityConstraint> getListByLocationType(LOSStorageLocationType slType) {
		
		StringBuffer sb = new StringBuffer("SELECT tcc FROM ");
		sb.append(LOSTypeCapacityConstraint.class.getSimpleName()+" tcc ");
		sb.append("WHERE tcc.storageLocationType=:slt");
		
		Query query = manager.createQuery(sb.toString());
		
		query.setParameter("slt", slType);
		
		return query.getResultList();
	}

}
