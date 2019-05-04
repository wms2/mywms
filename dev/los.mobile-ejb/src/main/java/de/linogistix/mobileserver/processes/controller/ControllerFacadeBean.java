/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.mobileserver.processes.controller;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class ControllerFacadeBean implements ControllerFacade {

	@EJB
	private ManageMobile mobileService;
	
	
	public List<MobileFunction> getFunctions() {
		return mobileService.getFunctions();
	}
	
	
	public int getMenuPageSize() {
		return mobileService.getMenuPageSize();
	}
	
}
