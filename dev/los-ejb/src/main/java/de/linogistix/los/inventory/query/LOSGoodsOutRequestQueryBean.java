/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.model.LOSGoodsOutRequest;
import de.linogistix.los.inventory.model.LOSGoodsOutRequestState;
import de.linogistix.los.inventory.query.dto.LOSGoodsOutRequestTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

@Stateless
public class LOSGoodsOutRequestQueryBean extends BusinessObjectQueryBean<LOSGoodsOutRequest> implements
		LOSGoodsOutRequestQueryRemote {

	
	@Override
	public String getUniqueNameProp() {
		return "number";
	}
	
	@Override
	public Class<LOSGoodsOutRequestTO> getBODTOClass() {
		return LOSGoodsOutRequestTO.class;
	}
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}

	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("number", false));
		propList.add(new BODTOConstructorProperty("number", false));
		propList.add(new BODTOConstructorProperty("outState", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("customerOrder.orderNumber", "co.orderNumber", BODTOConstructorProperty.JoinType.LEFT, "customerOrder as co"));
		propList.add(new BODTOConstructorProperty("customerOrder.externalNumber", "co.externalNumber", BODTOConstructorProperty.JoinType.JOIN, "co"));
		propList.add(new BODTOConstructorProperty("customerOrder.externalId", "co.externalId", BODTOConstructorProperty.JoinType.JOIN, "co"));
		propList.add(new BODTOConstructorProperty("customerOrder.customerNumber", "co.customerNumber", BODTOConstructorProperty.JoinType.JOIN, "co"));
		propList.add(new BODTOConstructorProperty("customerOrder.customerName", "co.customerName", BODTOConstructorProperty.JoinType.JOIN, "co"));
		propList.add(new BODTOConstructorProperty("shippingDate", false));
//		propList.add(new BODTOConstructorProperty("positions.size", false));
		
		return propList;
		
	}
	
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		
		TemplateQueryWhereToken token;
		token = new TemplateQueryWhereToken( TemplateQueryWhereToken.OPERATOR_LIKE, "number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken( TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken( TemplateQueryWhereToken.OPERATOR_LIKE, "customerOrder.orderNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		
		return ret;
	}

    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "outState", LOSGoodsOutRequestState.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("finishedState");
			ret.add(token);
		}
		
		return ret;
	}
}
