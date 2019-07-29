/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.pick.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import org.mywms.model.BasicEntity;
public class PickReceiptPosition extends BasicEntity {
	
	private static final long serialVersionUID = 1L;

	private String articleRef;
	
	private String articleDescr;
	
	private String lotRef;
	
	@Column(precision=17, scale=4)
	private BigDecimal amountordered;
	
	@Column(precision=17, scale=4)
	private BigDecimal amount;
	
	@ManyToOne
	private PickReceipt receipt;

	public void setArticleRef(String articleRef) {
		this.articleRef = articleRef;
	}

	public String getArticleRef() {
		return articleRef;
	}

	public void setArticleDescr(String articleDescr) {
		this.articleDescr = articleDescr;
	}

	public String getArticleDescr() {
		return articleDescr;
	}

	public void setLotRef(String lotRef) {
		this.lotRef = lotRef;
	}

	public String getLotRef() {
		return lotRef;
	}


	public void setAmountordered(BigDecimal amountordered) {
		this.amountordered = amountordered;
	}

	public BigDecimal getAmountordered() {
		return amountordered;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setReceipt(PickReceipt receipt) {
		this.receipt = receipt;
	}

	public PickReceipt getReceipt() {
		return receipt;
	}
	
	
}
