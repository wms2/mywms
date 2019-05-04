/*
 * TopologyBean.java
 *
 * Created on 12. September 2006, 09:57
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.example;

import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;
import org.mywms.model.Client;
import org.mywms.service.ClientService;
import org.mywms.service.RoleService;
import org.mywms.service.UserService;

import de.linogistix.los.crud.BusinessObjectCreationException;
import de.linogistix.los.crud.BusinessObjectExistsException;
import de.linogistix.los.crud.ClientCRUDRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;

/**
 * Creates an example topology
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless()
public class CommonTestTopologyBean implements CommonTestTopologyRemote {

	private static final Logger log = Logger.getLogger(CommonTestTopologyBean.class);
	// --------------------------------------------------------------------------
	protected Client SYSTEMCLIENT;
	protected Client TESTCLIENT;
	protected Client TESTMANDANT;
		
	
	@EJB
    RoleService roleService;
    @EJB
    UserService userService;
    @EJB
    ClientService clientService;
	@EJB
	ClientQueryRemote clientQuery;
	@EJB
	ClientCRUDRemote clientCrud;
	
	
	@PersistenceContext(unitName = "myWMS")
	protected EntityManager em;

	/** Creates a new instance of TopologyBean */
	public CommonTestTopologyBean() {
	}
	
	//---------------------------------------------------
	
	//-----------------------------------------------------------------

	public void create() throws CommonTopologyException {
		try {
			createClients();
			em.flush();
		} catch (FacadeException ex) {
			log.error(ex, ex);
			throw new CommonTopologyException();
		}

	}

	public void createClients() throws CommonTopologyException,
			BusinessObjectExistsException, BusinessObjectCreationException,
			BusinessObjectSecurityException {

		SYSTEMCLIENT = clientQuery.getSystemClient();

		if (SYSTEMCLIENT == null) {
			log.error("No System CLient found");
			throw new CommonTopologyException();
		}

		try {
			TESTCLIENT = clientQuery.queryByIdentity(TESTCLIENT_NUMBER);
		} catch (BusinessObjectNotFoundException ex) {
			TESTCLIENT = new Client();
			TESTCLIENT.setName(TESTCLIENT_NUMBER);
			TESTCLIENT.setNumber(TESTCLIENT_NUMBER);
			TESTCLIENT.setCode(TESTCLIENT_NUMBER);
			em.persist(TESTCLIENT);
		}
		try {
			TESTMANDANT = clientQuery.queryByIdentity(TESTMANDANT_NUMBER);
		} catch (BusinessObjectNotFoundException ex) {
			TESTMANDANT = new Client();
			TESTMANDANT.setName(TESTMANDANT_NUMBER);
			TESTMANDANT.setNumber(TESTMANDANT_NUMBER);
			TESTMANDANT.setCode(TESTMANDANT_NUMBER);
			em.persist(TESTMANDANT);
		}
		
	}

	// ------------------------------------------------------------------------
	@SuppressWarnings("unchecked")
	public void remove(Class<BasicEntity> clazz) throws CommonTopologyException {

		try {
			List<BasicEntity> l;
			l = em.createQuery("SELECT o FROM " + clazz.getName() + " o")
					.getResultList();
			for (Iterator<BasicEntity> iter = l.iterator(); iter.hasNext();) {
				BasicEntity element = iter.next();
				element = (BasicEntity) em.find(clazz, element.getId());
				em.remove(element);
			}
			em.flush();

		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new CommonTopologyException();
		}
	}

	private void initClient() throws CommonTopologyException{
		try {
			TESTCLIENT = clientQuery.queryByIdentity(TESTCLIENT_NUMBER);
			TESTCLIENT = em.find(Client.class, TESTCLIENT.getId());
			
			TESTMANDANT = clientQuery.queryByIdentity(TESTMANDANT_NUMBER);
			TESTMANDANT = em.find(Client.class, TESTMANDANT.getId());
			
		}catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new CommonTopologyException();
		}		
	}

	public void clear() throws CommonTopologyException {
		try {
			initClient();			
			clearClient();
			
		} catch (CommonTopologyException ex) {
			throw ex;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new CommonTopologyException();
		}
	}

	public void clearClient() throws CommonTopologyException {

		// Delete Client
		try {
			if (TESTCLIENT != null)
				em.remove(TESTCLIENT);
			em.flush();
			
			if (TESTMANDANT != null)
				em.remove(TESTMANDANT);
			em.flush();
		} catch (Throwable t) {
			log.error("could not delete Testclient:" + t.getMessage(), t);
			throw new CommonTopologyException();
		}
	}

}
