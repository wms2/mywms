/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.businessservice;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;
import org.mywms.model.ItemUnit;
import org.mywms.model.Role;
import org.mywms.service.ClientService;
import org.mywms.service.EntityNotFoundException;
import org.mywms.service.ItemUnitService;
import org.mywms.service.RoleService;

import de.linogistix.los.customization.EntityGenerator;
import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;

/**
 * @author krane
 *
 */
@Stateless
public class CommonBasicDataServiceBean implements CommonBasicDataService {

	private static final Logger log = Logger.getLogger(CommonBasicDataServiceBean.class);


	@EJB
	private ClientService clientService;
	@EJB
	private RoleService roleService;
	@EJB
	private EntityGenerator entityGenerator;
	@EJB
	private ItemUnitService unitService;
	@EJB
	private LOSSystemPropertyService propertyService;
	
	
	@PersistenceContext(unitName="myWMS")
	private EntityManager manager;
	
	
	public void createBasicData(Locale locale) throws FacadeException {

		log.info("Create Common Basic Data...");
		
		log.info("Create clients...");
		Client sys = clientService.getSystemClient();

		log.info("Create Roles...");
		createRole(org.mywms.globals.Role.ADMIN_STR);
		createRole(org.mywms.globals.Role.OPERATOR_STR);
		createRole(org.mywms.globals.Role.FOREMAN_STR);
		createRole(org.mywms.globals.Role.INVENTORY_STR);
	
		log.info("Create ItemUnits...");
		ItemUnit pce = null;
		try {
			pce = unitService.get(0);
		} catch (EntityNotFoundException e) {}
		if( pce == null || pce.getModified().compareTo(pce.getCreated())==0 ) {
			pce = unitService.getDefault();
			pce.setUnitName( resolve("BasicDataUnitNameDefault", locale) );
		}
		
		log.info("Create Properties...");
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_SHOW_DETAIL_PROPERTIES, "true", LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_SHOW_DETAIL_PROPERTIES", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_RESTORE_TABS, "false", LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_RESTORE_TABS", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_SELECTION_UNLIMITED, "false", LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_SELECTION_UNLIMITED", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_SELECTION_ON_START, "true", LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_SELECTION_ON_START", locale), false, false);
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_VERSION_MATCHER, ".*", LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_VERSION_MATCHER", locale), false, false);
				
		log.info("Create Common Basic Data. done.");
	}
	
	private Role createRole( String name ) {
		Role role = null;
		try {
			role = roleService.getByName(name);
		} catch (Exception e) {
			// ignore
		}
		if( role == null ) {
			role = entityGenerator.generateEntity( Role.class );
			role.setName(name);
			manager.persist(role);
		}
		return role;
	}
	
	private final String resolve( String key, Locale locale ) {
        if (key == null) {
            return "";
        }

        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle("de.linogistix.los.res.Bundle", locale, BundleResolver.class.getClassLoader());
            String s = bundle.getString(key);
            return s;
        }
        catch (MissingResourceException ex) {
        	log.error("Exception: "+ex.getMessage());
            return key;
        }
        catch (IllegalFormatException ife){
        	log.error("Exception: "+ife.getMessage());
        	return key;
        }
    }
}
