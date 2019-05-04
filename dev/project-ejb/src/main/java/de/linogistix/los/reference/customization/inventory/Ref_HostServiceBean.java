package de.linogistix.los.reference.customization.inventory;

import javax.ejb.Stateless;

import org.mywms.facade.FacadeException;

import de.linogistix.los.common.businessservice.HostMsgService;
import de.linogistix.los.common.service.HostMsgAdapterLog;
import de.linogistix.los.model.HostMsg;

@Stateless
public class Ref_HostServiceBean implements HostMsgService{


	public void sendMsg(HostMsg msg) throws FacadeException {
		 new HostMsgAdapterLog().sendMsg(msg);
	}

	
}
