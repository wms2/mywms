package de.linogistix.los.common.businessservice;

import javax.ejb.Local;

import org.mywms.facade.FacadeException;

import de.linogistix.los.model.HostMsg;

@Local
public interface HostMsgService {
	
	public void sendMsg( HostMsg msg ) throws FacadeException;
	
}
