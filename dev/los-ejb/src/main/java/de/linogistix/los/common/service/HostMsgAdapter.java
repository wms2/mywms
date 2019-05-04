package de.linogistix.los.common.service;

import org.mywms.facade.FacadeException;

import de.linogistix.los.model.HostMsg;

public interface HostMsgAdapter {
	
	public void sendMsg( HostMsg msg ) throws FacadeException;
	
}
