/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.mywms.service.BasicServiceBean;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class QueryUnitLoadAdviceServiceBean extends	BasicServiceBean<LOSUnitLoadAdvice> 
											implements QueryUnitLoadAdviceService, QueryUnitLoadAdviceServiceRemote 
{

	@EJB
	private ContextService ctxService;
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryUnitLoadAdviceService#getByLabelId(java.lang.String)
	 */
	public LOSUnitLoadAdvice getByLabelId(String labelId) throws UnAuthorizedException {
		return getByLabelId(labelId, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryUnitLoadAdviceServiceRemote#getByLabelId(java.lang.String, boolean)
	 */
	@SuppressWarnings("unused")
	public LOSUnitLoadAdvice getByLabelId(String labelId, boolean fetchEager) throws UnAuthorizedException {
		
		if( StringTools.isEmpty( ctxService.getCallerUserName() ) ){
    		throw new UnAuthorizedException();
    	}
		
		Query query = manager.createQuery("SELECT o FROM "
				+ LOSUnitLoadAdvice.class.getSimpleName() + " o "
				+ "WHERE o.labelId=:no");

		query.setParameter("no", labelId);

		try {
			LOSUnitLoadAdvice adv = (LOSUnitLoadAdvice) query.getSingleResult();
			
			if(!ctxService.getCallersClient().isSystemClient()
	        	&& !ctxService.getCallersClient().equals(adv.getClient()))
        	{
        		throw new UnAuthorizedException();
        	}
			
			if(fetchEager){
				
				for(LOSUnitLoadAdvicePosition pos:adv.getPositionList());
			}
			
			return adv;
			
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryUnitLoadAdviceService#getByAdviceNumber(java.lang.String)
	 */
	public LOSUnitLoadAdvice getByAdviceNumber(String adviceNumber) throws UnAuthorizedException {
		return getByAdviceNumber(adviceNumber, false);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.linogistix.los.inventory.service.QueryUnitLoadAdviceServiceRemote#getByAdviceNumber(java.lang.String, boolean)
	 */
	@SuppressWarnings("unused")
	public LOSUnitLoadAdvice getByAdviceNumber(String adviceNumber, boolean fetchEager) throws UnAuthorizedException {
		
		if( StringTools.isEmpty(ctxService.getCallerUserName()) ){
    		throw new UnAuthorizedException();
    	}
		
		Query query = manager.createQuery("SELECT o FROM "
				+ LOSUnitLoadAdvice.class.getSimpleName() + " o "
				+ "WHERE o.number=:no");

		query.setParameter("no", adviceNumber);

		try {
			LOSUnitLoadAdvice adv = (LOSUnitLoadAdvice) query.getSingleResult();
			
			if(!ctxService.getCallersClient().isSystemClient()
	        	&& !ctxService.getCallersClient().equals(adv.getClient()))
        	{
        		throw new UnAuthorizedException();
        	}
			
			if(fetchEager){
				
				for(LOSUnitLoadAdvicePosition pos:adv.getPositionList());
			}
			
			return adv;
			
		} catch (NoResultException ex) {
			return null;
		}
	}


}
