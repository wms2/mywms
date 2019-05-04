/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.model.OrderReceipt;
import de.linogistix.los.inventory.query.dto.OrderReceiptTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class OrderReceiptQueryBean extends
		BusinessObjectQueryBean<OrderReceipt> implements
		OrderReceiptQueryRemote {

	private static final String[] props = new String[]{
		"id", "version","orderNumber",
		"orderReference", "date", "state"
	};
	
	
	@Override
	public String getUniqueNameProp() {
		return "orderNumber";
	}

	@Override
	public Class<OrderReceiptTO> getBODTOClass() {
		return OrderReceiptTO.class;
	}
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return props;
	}
	
	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "name",
				value);
		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken orderRef = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderReference",
				value);
		orderRef.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken orderNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderNumber",
				value);
		orderNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		
		ret.add(name);
		ret.add(orderNumber);
		ret.add(orderRef);
		
		return ret;
	}

}
