package de.linogistix.mobileserver.processes.controller;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface ControllerFacade {

	public List<MobileFunction> getFunctions();
	
	public int getMenuPageSize();
	
}
