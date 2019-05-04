/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.entityservice;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.User;

import de.linogistix.los.common.businessservice.HostMsgService;
import de.linogistix.los.model.HostMsgLock;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.businessservice.ContextService;

@Stateless
public class BusinessObjectLockBean implements BusinessObjectLockService {

	private static final Logger log = Logger.getLogger(BusinessObjectLockBean.class);

	@PersistenceContext(unitName = "myWMS")
	protected EntityManager manager;
	
	@EJB
	private ContextService context;
	
	@EJB
	private HostMsgService msgService;
	
	public void lock(BasicEntity entity, int lock, String lockCause) throws BusinessObjectSecurityException {
		String logStr="lock ";
		User user = context.getCallersUser();
		
		
		if (!context.checkClient(entity)) {
			throw new BusinessObjectSecurityException(user);
		}
		entity = manager.find(entity.getClass(), entity.getId());
		int lockOld = entity.getLock();
		entity.setLock(lock);
		
		try {
			msgService.sendMsg( new HostMsgLock(entity, lockOld) );
		} catch (FacadeException e) {
			// fucking special exceptions. The real cause will be hidden
			log.error(logStr+"EXCEPTION="+e.getClass().getSimpleName()+", "+e.getMessage());
			throw new BusinessObjectSecurityException(null);
		}
		
		log.info(logStr+"class="+entity.getClass().getSimpleName()+", entity="+entity.toShortString()+", lockOld="+lockOld+", lockNew="+lock+", user="+context.getCallerUserName() );
		
	}


}
