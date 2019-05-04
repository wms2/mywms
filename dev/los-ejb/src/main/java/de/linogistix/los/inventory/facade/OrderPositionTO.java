/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * A TO for Orders send from ERP to WMS.
 * 
 * @author trautm
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)             
@XmlType(
		name = "OrderPositionTO",
		namespace="http://com.linogistix/connector/wms/inventory" 
)
public class OrderPositionTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String clientRef;
    
    public String batchRef;
    
    public String articleRef;
    
    public BigDecimal amount;
	
	public OrderPositionTO(){
		this("", "", "", new BigDecimal(0));
	}
	
	public OrderPositionTO(String clientRef, String batchRef, String articleRef, BigDecimal amount){
		this.clientRef = clientRef;
		this.batchRef = batchRef;
        this.articleRef = articleRef;
        this.amount = amount;
	}
	
	@Override
	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("ItemDataTO: ");
		
		ret.append("[clientRef=");
		ret.append(clientRef);
		ret.append("] ");
		ret.append("[batchRef=");
		ret.append(batchRef);
		ret.append("] ");
		ret.append("[articleRef=");
		ret.append(articleRef);
		ret.append("] ");
        ret.append("[amount=");
		ret.append(amount);
		ret.append("] ");
		
		return new String(ret);
	}

	public String getClientRef() {
		return clientRef;
	}

	public void setClientRef(String clientRef) {
		this.clientRef = clientRef;
	}

	public String getBatchRef() {
		return batchRef;
	}

	public void setBatchRef(String batchRef) {
		this.batchRef = batchRef;
	}

	public String getArticleRef() {
		return articleRef;
	}

	public void setArticleRef(String articleRef) {
		this.articleRef = articleRef;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
}
