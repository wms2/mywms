/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.UnitLoadType;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.businessservice.LocationReserver;
import de.linogistix.los.location.customization.CustomLocationService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.exception.LOSLocationNotSuitableException;
import de.linogistix.los.location.exception.LOSLocationReservedException;
import de.linogistix.los.location.exception.LOSLocationWrongClientException;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.model.LOSUnitLoadPackageType;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
/**
 *
 * @author Jordan
 */
@Stateless
public class LOSUnitLoadServiceBean 
        extends BasicServiceBean<LOSUnitLoad>
        implements LOSUnitLoadService
{

	private static final Logger log = Logger.getLogger(LOSUnitLoadServiceBean.class);
	private static int MAX_CARRIER_DEPTH = 10;
	
	@EJB
	private ClientService clientService;

	@EJB
	private LOSStorageLocationService slService;
	
	@EJB
	private UnitLoadTypeQueryRemote uLoadTypeQueryRemote;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private LocationReserver locationReserver;
	@EJB
	private CustomLocationService customLocationService;
    public LOSUnitLoad createLOSUnitLoad(Client client, 
                                         String labelId, 
                                         UnitLoadType type,
                                         LOSStorageLocation storageLocation) throws FacadeException 
    {
        if (client == null 
            || labelId == null
            || type == null
            || storageLocation == null) 
        {
            throw new NullPointerException(
                    "createLOSUnitLoad: parameter == null");
        }
        
        LOSUnitLoad ul = entityGenerator.generateEntity( LOSUnitLoad.class );
        ul.setClient(client);
        ul.setLabelId(labelId);
        ul.setType(type);
        ul.setWeightCalculated(type.getWeight());
        
        try {
        	locationReserver.checkAllocateLocation(storageLocation, ul, false);
        } catch (LOSLocationReservedException ex){
        	throw ex.createRollbackException();
        } catch (LOSLocationWrongClientException ex){
        	throw ex.createRollbackException();
        } catch (LOSLocationNotSuitableException ex){
        	throw ex.createRollbackException();
        }
		
        ul.setStorageLocation(storageLocation);
        
        manager.persist(ul);
        
        customLocationService.onUnitLoadPlaced(storageLocation, ul);
        
        log.info("CREATED LOSUnitLoad: " + ul.toShortString());
        
        return ul;
    }

    //-----------------------------------------------------------------------
	public boolean existsByStorageLocation(LOSStorageLocation location) {
		Query query = manager.createNamedQuery("LOSUnitLoad.existsByLocation");
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

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	public List<LOSUnitLoad> getListByStorageLocation(LOSStorageLocation sl) {
		Query query = manager.createNamedQuery("LOSUnitLoad.queryByLocation");
		query.setParameter("location", sl);
                
        return (List<LOSUnitLoad>)query.getResultList();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	public List<LOSUnitLoad> getListEmptyByStorageLocation(LOSStorageLocation sl) {
        
        Query query = manager.createQuery(
                            "SELECT ul FROM "+LOSUnitLoad.class.getSimpleName()+" ul "
                            +"WHERE ul.storageLocation=:sl "
        					+" AND ul.stockUnitList IS EMPTY ");
        query.setParameter("sl", sl);
                
        return (List<LOSUnitLoad>)query.getResultList();
    }

    //-----------------------------------------------------------------------
    /**
     * @see LOSUnitLoadService.getListByLabelStartsWith(Client, String)
     */
	@SuppressWarnings("unchecked")
	public List<LOSUnitLoad> getListByLabelStartsWith(Client client, String labelPart) 
	{
		String lowerPart = labelPart.toLowerCase();
        int partLength = lowerPart.length();

        StringBuffer qstr = new StringBuffer();
        qstr.append("SELECT ul FROM " + LOSUnitLoad.class.getSimpleName() + " ul ")
            .append("WHERE SUBSTRING(ul.labelId, 1, :length) = :part ");

        if (!client.isSystemClient()) {
            qstr.append("AND ul.client = :client ");
        }

        qstr.append("ORDER BY ul.labelId ASC");

        Query query = manager.createQuery(qstr.toString());
        
        query.setParameter("length", partLength);
        query.setParameter("part", lowerPart);

        if (!client.isSystemClient()) {
            query.setParameter("client", client);
        }
        
        return (List<LOSUnitLoad>) query.getResultList();
	}

	public LOSUnitLoad getByLabelId(Client client, String labelId) throws EntityNotFoundException {
		Query query = manager.createNamedQuery("LOSUnitLoad.queryByLabel");
		query = query.setParameter("label", labelId);
        
		try {
			LOSUnitLoad ul = (LOSUnitLoad)query.getSingleResult();
			return ul;
		}
		catch (NoResultException ex) {
			throw new EntityNotFoundException(
					ServiceExceptionKey.NO_UNITLOAD_WITH_LABEL);
		}
	}


	public LOSUnitLoad getNirwana() {
		LOSUnitLoad ul;
		LOSStorageLocation nirwana;
		nirwana = slService.getNirwana();
		
		try {
			ul = (LOSUnitLoad) getByLabelId(clientService.getSystemClient(), nirwana.getName());
		} catch (EntityNotFoundException e) {
			
			UnitLoadType t;
			t = uLoadTypeQueryRemote.getDefaultUnitLoadType();
			if (t == null){
				throw new RuntimeException("Nirwana does not exists. Neither does Default UnitLoadType");
			}
			
			ul = entityGenerator.generateEntity( LOSUnitLoad.class );
			ul.setClient(clientService.getSystemClient());
			ul.setLabelId(nirwana.getName());
			ul.setPackageType(LOSUnitLoadPackageType.CONTAINER);
			ul.setStorageLocation(nirwana);
			ul.setType(t);
			manager.persist(ul);
			manager.flush();
		}
		
		return ul;
	}

    public boolean hasChilds(LOSUnitLoad unitLoad) {
    	if( unitLoad==null ) {
    		return false;
    	}
        String queryStr = "SELECT ul.id FROM " + LOSUnitLoad.class.getSimpleName() + " ul WHERE ul.carrierUnitLoadId = :id ";
        Query query = manager.createQuery(queryStr);
        query.setParameter("id", unitLoad.getId());

        try {
        	query.getSingleResult();
        }
	    catch(NoResultException nre){
	    	return false;
	    }
        catch(Throwable t) {
        }
        return true;
    }
    public boolean hasOtherChilds(LOSUnitLoad unitLoad, LOSUnitLoad notOther) {
    	if( unitLoad==null ) {
    		return false;
    	}
        String queryStr = "SELECT ul.id FROM " + LOSUnitLoad.class.getSimpleName() + " ul WHERE ul.carrierUnitLoadId = :id AND ul!=:notOther";
        Query query = manager.createQuery(queryStr);
        query.setParameter("id", unitLoad.getId());
        query.setParameter("notOther", notOther);

        try {
        	query.getSingleResult();
        }
	    catch(NoResultException nre){
	    	return false;
	    }
        catch(Throwable t) {
        }
        return true;
    }

    @SuppressWarnings("unchecked")
	public List<LOSUnitLoad> getChilds(LOSUnitLoad unitLoad) {
		Query query = manager.createNamedQuery("LOSUnitLoad.queryByCarrierId");
		query.setParameter("carrierId", unitLoad.getId());
		
        return query.getResultList();
    }

    public Long getNumChilds(LOSUnitLoad unitLoad) {
		Query query = manager.createNamedQuery("LOSUnitLoad.countByCarrierId");
		query.setParameter("carrierId", unitLoad.getId());

        return (Long)query.getSingleResult();
    }
    public Long getNumChilds(Long unitLoadId) {
		Query query = manager.createNamedQuery("LOSUnitLoad.countByCarrierId");
		query.setParameter("carrierId", unitLoadId);

        return (Long)query.getSingleResult();
    }

    public LOSUnitLoad getParent(LOSUnitLoad unitLoad) {
    	if( unitLoad.getCarrierUnitLoadId() == null ) {
    		return null;
    	}
    	
		try {
			return get(unitLoad.getCarrierUnitLoadId());
		} catch (EntityNotFoundException e) {
		}
    		
		return null;
    }
    
    public boolean hasParent(LOSUnitLoad unitLoad, LOSUnitLoad parentToCheck) throws FacadeException {
    	return hasParent(unitLoad, parentToCheck, 0);
    }
    public boolean hasParent(LOSUnitLoad unitLoad, LOSUnitLoad parentToCheck, int depth) throws FacadeException {
    	String logStr="hasParent ";
    	
		if( unitLoad.getCarrierUnitLoadId() == null ) {
			return false;
		}

		if( depth>MAX_CARRIER_DEPTH ) {
			log.error(logStr+"Cannot transfer unit load with more than "+MAX_CARRIER_DEPTH+" carriers");
			throw new LOSLocationException( LOSLocationExceptionKey.CARRIER_MAXDEPTH_EXCEEDED, new Object[] {MAX_CARRIER_DEPTH});
		}

		if( unitLoad.equals(parentToCheck) ) {
			log.debug(logStr+"unitLoad is equal. label="+unitLoad.getLabelId());
			return true;
		}
		if( unitLoad.getCarrierUnitLoadId().equals(parentToCheck.getId()) ) {
			log.debug(logStr+"carrier match of unit load. label="+unitLoad.getLabelId()+", carrierId="+unitLoad.getCarrierUnitLoadId());
			return true;
		}
		
		LOSUnitLoad parentOfUnitLoad = getParent(unitLoad);
		if( parentOfUnitLoad == null ) {
			return false;
		}
		
		return hasParent(parentOfUnitLoad, parentToCheck, depth+1);
    }
}
