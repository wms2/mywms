/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.io.Serializable;

public class BODTOConstructorProperty implements Serializable{	
	
	public enum JoinType {
		JOIN,
		LEFT,
		INNER;
		
		public String statement(){
			switch(this){
			case JOIN: return "JOIN";
			case LEFT: return "LEFT JOIN";
			default:
				return "JOIN";
			}
		}
		
	}

	private static final long serialVersionUID = 1L;

	private String propertyName;
	
	private boolean entityReference;
	
	private boolean nullableReference;
	
	private String joinReference;

	private String selectToken;
	
	private JoinType jointType = null;
	
	public BODTOConstructorProperty(String propertyName, 
			boolean nullableReference ) {
		this.nullableReference = nullableReference;
		this.propertyName = propertyName;
		if (nullableReference){
			this.jointType = JoinType.LEFT;
		}
		
	}
	
	public BODTOConstructorProperty(String propertyName, String selectToken, JoinType type, String joinReference ) {
		this.propertyName = propertyName;
		this.joinReference = joinReference;
		this.selectToken = selectToken;
		this.jointType = type;
		if( type == JoinType.LEFT ) {
			nullableReference = true;
		}
	}

	public String getPropertyName() {
		return propertyName;
	}

	public boolean isEntityReference() {
		return entityReference;
	}

	public boolean isNullableReference() {
		return nullableReference;
	}
	
	public String getJoinReference(){
		if( this.joinReference!=null ) {
			return this.joinReference;
		}
		else {
			return this.getPropertyName();
		}
	}

	public void setJoinReference(String joinReference){
		this.joinReference = joinReference;
	}

	public String getSelectToken() {
		return this.selectToken;
	}
	
	public void setSelectToken(String selecToken){
		this.selectToken = selecToken;
	}

	public JoinType getJointType() {
		return jointType;
	}

	public void setJointType(JoinType jointType) {
		this.jointType = jointType;
	}
}
