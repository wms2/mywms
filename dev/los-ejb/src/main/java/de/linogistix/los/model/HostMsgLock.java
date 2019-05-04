/*
 * Copyright (c) 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.model;

import org.mywms.model.BasicEntity;


/**
 * @author krane
 *
 */
public class HostMsgLock extends HostMsg{

	private BasicEntity entity;
	private int lockOld;
	
	public HostMsgLock(BasicEntity entity, int lockOld) {
		this.entity=entity;
		this.lockOld=lockOld;
	}
	
	public BasicEntity getEntity() {
		return entity;
	}
	public void setEntity(BasicEntity entity) {
		this.entity = entity;
	}
	public int getLockOld() {
		return lockOld;
	}
	public void setLockOld(int lockOld) {
		this.lockOld = lockOld;
	}

}