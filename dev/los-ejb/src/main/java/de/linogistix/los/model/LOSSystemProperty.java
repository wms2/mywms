/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

@Entity
@Table(name="los_sysprop", uniqueConstraints={
    @UniqueConstraint(columnNames={"client_id","syskey","workstation"})
})
@NamedQueries({
	@NamedQuery(name="LOSSystemProperty.queryValueByWorkstationKey", query="SELECT sp.value FROM LOSSystemProperty sp WHERE sp.key=:key and sp.workstation=:workstation"),
	@NamedQuery(name="LOSSystemProperty.queryValueByClientWorkstationKey", query="SELECT sp.value FROM LOSSystemProperty sp WHERE sp.key=:key and sp.workstation=:workstation and client=:client"),
	@NamedQuery(name="LOSSystemProperty.queryByClientWorkstationKey", query="FROM LOSSystemProperty sp WHERE sp.key=:key and sp.workstation=:workstation and client=:client")
})
public class LOSSystemProperty extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;
	
	public final static String WORKSTATION_DEFAULT = "DEFAULT";

	@Column(name="syskey", nullable=false)
	private String key;
	
	@Column(name="sysvalue")
	private String value;

	private String description;
	
	@Column(nullable=false)
	private String workstation = WORKSTATION_DEFAULT;
	
	private String groupName;
	
	private boolean hidden = false;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getWorkstation() {
		return workstation;
	}
	public void setWorkstation(String workstation) {
		this.workstation = workstation;
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	
	
}
