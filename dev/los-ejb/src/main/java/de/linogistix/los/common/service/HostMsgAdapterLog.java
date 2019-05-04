package de.linogistix.los.common.service;

import org.apache.log4j.Logger;

import de.linogistix.los.model.HostMsg;

public class HostMsgAdapterLog implements HostMsgAdapter {
    private static Logger log = Logger.getLogger(HostMsgAdapterLog.class);


	public void sendMsg( HostMsg msg ) {
		log.info(msg.toString());
	}

}
