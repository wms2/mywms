/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.entityservice;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.User;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.res.BundleResolver;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.location.LocationType;
import de.wms2.mywms.strategy.TypeCapacityConstraint;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSStorageLocationTypeServiceBean 
        extends BasicServiceBean<LocationType>
        implements LOSStorageLocationTypeService, LOSStorageLocationTypeServiceRemote {
	private static final Logger log = Logger.getLogger(LOSStorageLocationTypeServiceBean.class);

	@EJB
	private ContextService ctxService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private QueryTypeCapacityConstraintService capaService;
	
    public LocationType create(String name)
            throws UniqueConstraintViolatedException 
    {
        if (name == null) {
            throw new NullPointerException(
                    "createStorageLocationType: name == null");
        }
        
        try{
            getByName(name);
            
            // TODO: erweiterung der ServiceExceptionKeys
            throw new UniqueConstraintViolatedException(
                    ServiceExceptionKey.STORAGELOCATION_NAME_NOT_UNIQUE);
            
        }catch(EntityNotFoundException enf){}
        
        LocationType type = entityGenerator.generateEntity(LocationType.class);
        type.setName(name);
        
        manager.persist(type);
        
        return type;
    }

    //----------------------------------------------------------------------
    public LocationType getByName(String name) 
            throws EntityNotFoundException 
    {
        
        Query query = manager.createQuery("SELECT slt FROM "
                        + LocationType.class.getSimpleName()
                        + " slt "
                        + "WHERE slt.name=:name ");

        query.setParameter("name", name);

        try {
            LocationType slt;
            slt = (LocationType) query.getSingleResult();
            return slt;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }
        
    }

    
    //----------------------------------------------------------------------
	@Override
	public void delete(LocationType slType)
			throws ConstraintViolatedException 
	{
		Collection<TypeCapacityConstraint> constraints;
		
		constraints = capaService.getListByLocationType(slType);
//		constraints = slType.getTypeCapacityConstraints();
		for(TypeCapacityConstraint c:constraints){
			manager.remove(c);
		}
		super.delete(slType);
	}

	//----------------------------------------------------------------------
	@Override
	public void deleteAll() {
		Query query = manager.createQuery(
                "DELETE FROM "+TypeCapacityConstraint.class.getSimpleName());
		query.executeUpdate();
		super.deleteAll();
	}

	//----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<TypeCapacityConstraint> getByLocationType(LocationType slType) {
		
		Query query = manager.createQuery(
						"SELECT tcc FROM "+TypeCapacityConstraint.class.getSimpleName()+" tcc "+
						"WHERE tcc.locationType=:slt");
		
		query.setParameter("slt", slType);
		
		return query.getResultList();
	}

	//----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<TypeCapacityConstraint> getByUnitLoadType(UnitLoadType ulType) {
		Query query = manager.createQuery(
				"SELECT tcc FROM "+TypeCapacityConstraint.class.getSimpleName()+" tcc "+
				"WHERE tcc.unitLoadType=:ult");

		query.setParameter("ult", ulType);

		return query.getResultList();
	}

	/**
	 * This implementations interprets a LOSStorageLocationType with id <code>0</code> as default.
	 */
	public LocationType getDefaultStorageLocationType() {
		
		Query query = manager.createQuery(
						"SELECT slt FROM "+LocationType.class.getSimpleName()+" slt "+
						"WHERE slt.id = 0 ");
		
		try {
			return (LocationType) query.getSingleResult();
		}
		catch( NoResultException ex ) {
			createSystemStorageLocationType(0, "SYSTEM_DATA_DEFAULT_LOCATION_TYPE", "SYSTEM_DATA_DEFAULT_LOCATION_TYPE_DESC");
			return (LocationType) query.getSingleResult();
		}
	}

	/**
	 * This implementations interprets a LOSStorageLocationType with id <code>1</code> as type without restrictions.
	 */
	public LocationType getNoRestrictionType() {
		Query query = manager.createQuery(
				"SELECT slt FROM "+LocationType.class.getSimpleName()+" slt "+
				"WHERE slt.id = 1 ");

		try {
			return (LocationType) query.getSingleResult();
		}
		catch( NoResultException ex ) {
			createSystemStorageLocationType(1, "SYSTEM_DATA_SYSTEM_LOCATION_TYPE", "SYSTEM_DATA_SYSTEM_LOCATION_TYPE_DESC");
			return (LocationType) query.getSingleResult();
		}
	}
    
	/**
	 * This implementations interprets a LOSStorageLocationType with id <code>2</code> as type with a fixed unit load attached to it.
	 */
	public LocationType getAttachedUnitLoadType() {
		Query query = manager.createQuery(
				"SELECT slt FROM "+LocationType.class.getSimpleName()+" slt "+
				"WHERE slt.id = 2 ");

		try {
			return (LocationType) query.getSingleResult();
		}
		catch( NoResultException ex ) {
			createSystemStorageLocationType(2, "SYSTEM_DATA_FIXED_LOCATION_TYPE", "SYSTEM_DATA_FIXED_LOCATION_TYPE_DESC");
			return (LocationType) query.getSingleResult();
		}
	}
    

	private void createSystemStorageLocationType(long id, String nameKey, String descriptionKey) {
		User user = ctxService.getCallersUser();
		Locale locale = null;
		if( user != null ) {
			try {
				locale = new Locale(user.getLocale());
			}
			catch( Throwable x ) {
				// egal
			}
		}
		if( locale == null ) {
			locale = Locale.getDefault();
		}
		String name = BundleHelper.resolve(BundleResolver.class, nameKey, locale);
		
		log.warn("Try to create system LocationType name="+nameKey+", desc="+descriptionKey+", lang="+locale);
		
		LocationType slType;
		try {
			slType = getByName(name);
		}
		catch( EntityNotFoundException ex ) {
			try {
				slType = create(name);
				String comment = BundleHelper.resolve(BundleResolver.class, descriptionKey, locale);
				comment = comment + "\n\n" + BundleHelper.resolve(BundleResolver.class, "SYSTEM_DATA_COMMENT", locale);
				slType.setAdditionalContent(comment);
			}
			catch( UniqueConstraintViolatedException ex2 ) {
				log.error("Cannot create system LocationType");
				return;
			}
		}
		manager.flush();
		
		String queryStr = "UPDATE " + LocationType.class.getSimpleName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", id);
		query.setParameter("idOld", slType.getId());
		query.executeUpdate();
		manager.flush();
		slType = null;
	}
}
