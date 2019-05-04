/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import de.linogistix.los.model.LOSSequenceNumber;


@Stateless
public class LOSSequenceTransactionServiceBean implements LOSSequenceTransactionService {

	@PersistenceContext(unitName = "myWMS")
	private EntityManager entityManager;
    

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public long getNextNoNewTransaction(String sequenceName) {
    	LOSSequenceNumber sn = null;
    	
    	sn = entityManager.find(LOSSequenceNumber.class, sequenceName);
        if( sn == null ) {
            sn = new LOSSequenceNumber();
            sn.setClassName(sequenceName);
            sn.setSequenceNumber(0);
            entityManager.persist(sn);
        }
        else {
        	entityManager.lock(sn, LockModeType.WRITE);
        }

        sn.setSequenceNumber(sn.getSequenceNumber() + 1);
        return sn.getSequenceNumber();
    }
    
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void resetSequenceInNewTransaction(String sequenceName)  {
    	LOSSequenceNumber sn = null;
		
        try {
        	sn = entityManager.find(LOSSequenceNumber.class, sequenceName);
            entityManager.lock(sn, LockModeType.WRITE);
        } catch (NoResultException e) {}
        if( sn == null ) {
            sn = new LOSSequenceNumber();
            sn.setClassName(sequenceName);
            sn.setSequenceNumber(0);
            entityManager.persist(sn);
        }
        
        sn.setSequenceNumber(0);
        
	}

}
