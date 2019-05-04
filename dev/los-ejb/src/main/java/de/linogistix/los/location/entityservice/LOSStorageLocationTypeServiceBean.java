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
import org.mywms.model.UnitLoadType;
import org.mywms.model.User;
import org.mywms.service.BasicServiceBean;
import org.mywms.service.ConstraintViolatedException;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.UniqueConstraintViolatedException;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.location.model.LOSStorageLocationType;
import de.linogistix.los.location.model.LOSTypeCapacityConstraint;
import de.linogistix.los.location.res.BundleResolver;
import de.linogistix.los.location.service.QueryTypeCapacityConstraintService;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.los.util.businessservice.ContextService;

/**
 *
 * @author Jordan
 */
@Stateless
public class LOSStorageLocationTypeServiceBean 
        extends BasicServiceBean<LOSStorageLocationType>
        implements LOSStorageLocationTypeService, LOSStorageLocationTypeServiceRemote {
	private static final Logger log = Logger.getLogger(LOSStorageLocationTypeServiceBean.class);

	@EJB
	private ContextService ctxService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private QueryTypeCapacityConstraintService capaService;
	
    public LOSStorageLocationType create(String name)
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
        
        LOSStorageLocationType type = entityGenerator.generateEntity(LOSStorageLocationType.class);
        type.setName(name);
        
        manager.persist(type);
        
        return type;
    }

    //----------------------------------------------------------------------
    public LOSStorageLocationType getByName(String name) 
            throws EntityNotFoundException 
    {
        
        Query query = manager.createQuery("SELECT slt FROM "
                        + LOSStorageLocationType.class.getSimpleName()
                        + " slt "
                        + "WHERE slt.name=:name ");

        query.setParameter("name", name);

        try {
            LOSStorageLocationType slt;
            slt = (LOSStorageLocationType) query.getSingleResult();
            return slt;
        }
        catch (NoResultException ex) {
            throw new EntityNotFoundException(
                    ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }
        
    }

    
    //----------------------------------------------------------------------
	@Override
	public void delete(LOSStorageLocationType slType)
			throws ConstraintViolatedException 
	{
		Collection<LOSTypeCapacityConstraint> constraints;
		
		constraints = capaService.getListByLocationType(slType);
//		constraints = slType.getTypeCapacityConstraints();
		for(LOSTypeCapacityConstraint c:constraints){
			manager.remove(c);
		}
		super.delete(slType);
	}

	//----------------------------------------------------------------------
	@Override
	public void deleteAll() {
		Query query = manager.createQuery(
                "DELETE FROM "+LOSTypeCapacityConstraint.class.getSimpleName());
		query.executeUpdate();
		super.deleteAll();
	}

	//----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<LOSTypeCapacityConstraint> getByLocationType(LOSStorageLocationType slType) {
		
		Query query = manager.createQuery(
						"SELECT tcc FROM "+LOSTypeCapacityConstraint.class.getSimpleName()+" tcc "+
						"WHERE tcc.storageLocationType=:slt");
		
		query.setParameter("slt", slType);
		
		return query.getResultList();
	}

	//----------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public List<LOSTypeCapacityConstraint> getByUnitLoadType(UnitLoadType ulType) {
		Query query = manager.createQuery(
				"SELECT tcc FROM "+LOSTypeCapacityConstraint.class.getSimpleName()+" tcc "+
				"WHERE tcc.unitLoadType=:ult");

		query.setParameter("ult", ulType);

		return query.getResultList();
	}

	/**
	 * This implementations interprets a LOSStorageLocationType with id <code>0</code> as default.
	 */
	public LOSStorageLocationType getDefaultStorageLocationType() {
		
		Query query = manager.createQuery(
						"SELECT slt FROM "+LOSStorageLocationType.class.getSimpleName()+" slt "+
						"WHERE slt.id = 0 ");
		
		try {
			return (LOSStorageLocationType) query.getSingleResult();
		}
		catch( NoResultException ex ) {
			createSystemStorageLocationType(0, "SYSTEM_DATA_DEFAULT_LOCATION_TYPE", "SYSTEM_DATA_DEFAULT_LOCATION_TYPE_DESC");
			return (LOSStorageLocationType) query.getSingleResult();
		}
	}

	/**
	 * This implementations interprets a LOSStorageLocationType with id <code>1</code> as type without restrictions.
	 */
	public LOSStorageLocationType getNoRestrictionType() {
		Query query = manager.createQuery(
				"SELECT slt FROM "+LOSStorageLocationType.class.getSimpleName()+" slt "+
				"WHERE slt.id = 1 ");

		try {
			return (LOSStorageLocationType) query.getSingleResult();
		}
		catch( NoResultException ex ) {
			createSystemStorageLocationType(1, "SYSTEM_DATA_SYSTEM_LOCATION_TYPE", "SYSTEM_DATA_SYSTEM_LOCATION_TYPE_DESC");
			return (LOSStorageLocationType) query.getSingleResult();
		}
	}
    
	/**
	 * This implementations interprets a LOSStorageLocationType with id <code>2</code> as type with a fixed unit load attached to it.
	 */
	public LOSStorageLocationType getAttachedUnitLoadType() {
		Query query = manager.createQuery(
				"SELECT slt FROM "+LOSStorageLocationType.class.getSimpleName()+" slt "+
				"WHERE slt.id = 2 ");

		try {
			return (LOSStorageLocationType) query.getSingleResult();
		}
		catch( NoResultException ex ) {
			createSystemStorageLocationType(2, "SYSTEM_DATA_FIXED_LOCATION_TYPE", "SYSTEM_DATA_FIXED_LOCATION_TYPE_DESC");
			return (LOSStorageLocationType) query.getSingleResult();
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
		
		LOSStorageLocationType slType;
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
		
		String queryStr = "UPDATE " + LOSStorageLocationType.class.getSimpleName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", id);
		query.setParameter("idOld", slType.getId());
		query.executeUpdate();
		manager.flush();
		slType = null;
	}
}
