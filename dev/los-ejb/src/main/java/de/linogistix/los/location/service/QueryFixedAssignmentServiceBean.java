package de.linogistix.los.location.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mywms.model.ItemData;

import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSStorageLocation;

@Stateless
public class QueryFixedAssignmentServiceBean implements QueryFixedAssignmentService, QueryFixedAssignmentServiceRemote {
	
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	public LOSFixedLocationAssignment getByLocation(LOSStorageLocation sl) {
		Query query = manager.createNamedQuery("LOSFixedLocationAssignment.queryByLocation");
		query.setParameter("location", sl);
		query.setMaxResults(1);
		try{
			return (LOSFixedLocationAssignment) query.getSingleResult();
		}catch(NoResultException nre){
			return null;
		}
	}

	public boolean existsByItemData(ItemData item) {
		Query query = manager.createNamedQuery("LOSFixedLocationAssignment.existsByItem");
		query.setParameter("item", item);
        query.setMaxResults(1);
        
        try {
        	query.getSingleResult();
        }
	    catch(NoResultException nre){
	    	return false;
	    }
        return true;
	}

	public boolean existsByLocation(LOSStorageLocation location) {
		Query query = manager.createNamedQuery("LOSFixedLocationAssignment.existsByLocation");
		query.setParameter("location", location);
        query.setMaxResults(1);
        
        try {
        	query.getSingleResult();
        }
	    catch(NoResultException nre){
	    	return false;
	    }
        return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<LOSFixedLocationAssignment> getByItemData(ItemData item) {
		Query query = manager.createNamedQuery("LOSFixedLocationAssignment.queryByItem");
		query.setParameter("item", item);
		return query.getResultList();
	}

	
}
