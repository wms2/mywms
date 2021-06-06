/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.reference.facade;

import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.facade.FacadeException;

import de.linogistix.los.common.businessservice.CommonBasicDataService;
import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.businessservice.InventoryBasicDataService;
import de.linogistix.los.util.StringTools;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.module.ModuleSetup.SetupLevel;
import de.wms2.mywms.module.Wms2SetupService;
import de.wms2.mywms.project.module.ProjectSetupService;
import de.wms2.mywms.property.SystemProperty;

@Stateless
public class RefTopologyFacadeBean implements RefTopologyFacade {

	@EJB
	private ContextService contextService;
	@EJB
	private CommonBasicDataService commonBasicDataService;
	@EJB
	private InventoryBasicDataService inventoryBasicDataService;
	@EJB
	private LOSSystemPropertyService propertyService;
	@Inject
	private ProjectSetupService projectSetupService;
	@Inject
	private Wms2SetupService wmsSetupService;

	public boolean checkDemoData() throws FacadeException {
		String setupPropertyKey = "SETUP:de.linogistix.los.reference";
		SystemProperty setupProperty = propertyService.getByKey(setupPropertyKey);
		if (setupProperty != null
				&& StringUtils.equals(setupProperty.getPropertyValue(), SetupLevel.UNINITIALIZED.name())) {

			commonBasicDataService.createBasicData(getLocale());
			inventoryBasicDataService.createBasicData(getLocale());

			try {
				propertyService.setValue(setupPropertyKey, SetupLevel.INITIALIZED.name());
			} catch (UnAuthorizedException e) {
			}
			return false;
		}

		return true;
	}

	public void createDemoTopology() throws FacadeException {
		projectSetupService.setup(SetupLevel.DEMO_SMALL, getLocale());
		wmsSetupService.setup(SetupLevel.DEMO_SMALL, getLocale());
	}

	private Locale getLocale() {
		Locale locale = null;
		String localeName = contextService.getCallersUser().getLocale();
		if (!StringTools.isEmpty(localeName)) {
			locale = new Locale(localeName);
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return locale;
	}
}
