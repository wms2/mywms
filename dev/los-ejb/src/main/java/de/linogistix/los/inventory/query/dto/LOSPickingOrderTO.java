/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.picking.PickingOrder;

public class LOSPickingOrderTO extends BODTO<PickingOrder>  {

	private static final long serialVersionUID = 1L;
	
	private String clientNumber;
	private String customerOrderNumber;
	private int state;
	private int numPos;
	private int prio;
    private String userName;
    private String destinationName;
	
	public LOSPickingOrderTO(Long id, int version, String number, String clientNumber, String customerOrderNumber, int state, 
			int prio, String userName, String destinationName ) {
		this(id, version, number, clientNumber, customerOrderNumber, state, 
				-1, prio, userName, destinationName );
	}
	public LOSPickingOrderTO(Long id, int version, String number, String clientNumber, String customerOrderNumber, int state, 
			int numPos, int prio, String userName, String destinationName ) {
		super(id, version, number);
		this.clientNumber=clientNumber;
		this.customerOrderNumber = customerOrderNumber;
		this.state=state;
		this.numPos=numPos;
		this.prio = prio;
		this.userName = userName;
		this.destinationName = destinationName;
	}
	public LOSPickingOrderTO(Long id, int version, String number ) {
		super(id, version, number);
	}

	public String getClientNumber() {
		return clientNumber;
	}

	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCustomerOrderNumber() {
		return customerOrderNumber;
	}

	public void setCustomerOrderNumber(String customerOrderNumber) {
		this.customerOrderNumber = customerOrderNumber;
	}

	public int getNumPos() {
		return numPos;
	}

	public void setNumPos(int numPos) {
		this.numPos = numPos;
	}

	public int getPrio() {
		return prio;
	}

	public void setPrio(int prio) {
		this.prio = prio;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}



	
}
