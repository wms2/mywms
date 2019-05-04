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
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;
@Entity
@Table(name="los_pickreceiptpos")
public class PickReceiptPosition extends BasicEntity {
	
	private static final long serialVersionUID = 1L;

	private String articleRef;
	
	private String articleDescr;
	
	private String lotRef;
	
	private BigDecimal amountordered;
	
	private BigDecimal amount;
	
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

	@Column(precision=17, scale=4)
	public BigDecimal getAmountordered() {
		return amountordered;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(precision=17, scale=4)
	public BigDecimal getAmount() {
		return amount;
	}

	public void setReceipt(PickReceipt receipt) {
		this.receipt = receipt;
	}

	@ManyToOne
	public PickReceipt getReceipt() {
		return receipt;
	}
	
	
}
