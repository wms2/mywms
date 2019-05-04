/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mywms.model.BasicClientAssignedEntity;
@Entity
@Table(name="los_orderreceiptpos")
public class OrderReceiptPosition extends BasicClientAssignedEntity {
	
	private static final long serialVersionUID = 1L;

	private String articleRef;
	
	private String articleDescr;
	
	private int articleScale;
	
	private String lotRef;
	
	private BigDecimal amountordered;
	
	private BigDecimal amount;
	
	private OrderReceipt receipt;

	private int pos;
	
	public OrderReceiptPosition(){
		
	}
	
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
		try {
			return amount.setScale(articleScale);
		}
		catch( Throwable e ) {}
		
		return amount;
	}

	public void setReceipt(OrderReceipt receipt) {
		this.receipt = receipt;
	}

	@ManyToOne
	public OrderReceipt getReceipt() {
		return receipt;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}

	public int getArticleScale() {
		return articleScale;
	}

	public void setArticleScale(int articleScale) {
		this.articleScale = articleScale;
	}
	
	
}
