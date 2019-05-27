/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.service;

import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.User;
import org.mywms.service.UniqueConstraintViolatedException;

import de.linogistix.los.location.res.BundleResolver;
import de.linogistix.los.util.BundleHelper;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.inventory.UnitLoadType;

@Stateless
public class QueryUnitLoadTypeServiceBean implements QueryUnitLoadTypeService,
		QueryUnitLoadTypeServiceRemote {

	private static final Logger log = Logger.getLogger(QueryUnitLoadTypeServiceBean.class);

	@EJB
	private ContextService ctxService;
	
	@EJB
	private UnitLoadTypeService ultService;

	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.location.service.QueryUnitLoadTypeService#getDefaultUnitLoadType()
	 */
	public UnitLoadType getDefaultUnitLoadType(){
    	
    	StringBuffer sb = new StringBuffer("SELECT ult FROM ");
    	sb.append(UnitLoadType.class.getSimpleName()+ " ult ");
    	sb.append("WHERE ult.id=0");
    	
    	Query query = manager.createQuery(sb.toString());
    	
    	try {
            return (UnitLoadType) query.getSingleResult();
        }
        catch (NoResultException ex) {
        	createSystemUnitLoadType(0, "SYSTEM_DATA_DEFAULT_UNITLOAD_TYPE", "SYSTEM_DATA_DEFAULT_UNITLOAD_TYPE_DESC");
        	try {
        		return (UnitLoadType) query.getSingleResult();
        	}
        	catch (NoResultException ex2) {
        		return null;
        	}
        }
    }

    /**
     * This implementation interprets UnitLoadType with id <code>1</code> as default unitload type. 
     */
	public UnitLoadType getPickLocationUnitLoadType() {
		StringBuffer sb = new StringBuffer("SELECT ult FROM ");
    	sb.append(UnitLoadType.class.getSimpleName()+ " ult ");
    	sb.append("WHERE ult.id=1");
    	
    	Query query = manager.createQuery(sb.toString());
    	
    	try {
            return (UnitLoadType) query.getSingleResult();
        }
        catch (NoResultException ex) {
        	createSystemUnitLoadType(1, "SYSTEM_DATA_PICKLOCATION_UNITLOAD_TYPE", "SYSTEM_DATA_PICKLOCATION_UNITLOAD_TYPE_DESC");
        	try {
        		return (UnitLoadType) query.getSingleResult();
        	}
        	catch (NoResultException ex2) {
        		return null;
        	}
        }
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

	
	private void createSystemUnitLoadType(long id, String nameKey, String descriptionKey) {
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
		
		UnitLoadType ulType = null;
		ulType = getByName(name);
		if( ulType == null ) {
			try{
				ulType = ultService.create(name);
				String comment = BundleHelper.resolve(BundleResolver.class, descriptionKey, locale);
				comment = comment + "\n\n" + BundleHelper.resolve(BundleResolver.class, "SYSTEM_DATA_COMMENT", locale);
				ulType.setAdditionalContent(comment);
			}
			catch( UniqueConstraintViolatedException ex2 ) {
				log.error("Cannot create system UnitLoadType");
				return;
			}
		}
		manager.flush();
		
		String queryStr = "UPDATE " + UnitLoadType.class.getSimpleName() + " SET id=:idNew WHERE id=:idOld";
		Query query = manager.createQuery(queryStr);
		query.setParameter("idNew", id);
		query.setParameter("idOld", ulType.getId());
		query.executeUpdate();
		manager.flush();
		ulType = null;
	}
}
