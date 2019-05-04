/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;


import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSCustomerOrder;
import de.linogistix.los.inventory.model.LOSCustomerOrderPosition;
import de.linogistix.los.inventory.query.dto.LOSCustomerOrderPositionTO;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQueryWhereToken;


/**
 * @author krane
 *
 */
@Stateless
public class LOSCustomerOrderPositionQueryBean extends BusinessObjectQueryBean<LOSCustomerOrderPosition> implements LOSCustomerOrderPositionQueryRemote{
	

	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}
	
	@Override
	public String getUniqueNameProp() {
		return "number";
	}
	
	@Override
	public String getOrderByProp() {
		return "order.number, o.index";
	}
	
	@Override
	public Class<LOSCustomerOrderPositionTO> getBODTOClass() {
		return LOSCustomerOrderPositionTO.class;
	}

	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("number", false));
		propList.add(new BODTOConstructorProperty("itemData.number", null, BODTOConstructorProperty.JoinType.JOIN, "itemData"));
//		propList.add(new BODTOConstructorProperty("itemData.number", false));
		propList.add(new BODTOConstructorProperty("itemData.name", false));
		propList.add(new BODTOConstructorProperty("itemData.scale", false));
		propList.add(new BODTOConstructorProperty("lot.name", null, BODTOConstructorProperty.JoinType.LEFT, "lot"));
		propList.add(new BODTOConstructorProperty("amount", false));
		propList.add(new BODTOConstructorProperty("amountPicked", false));
		propList.add(new BODTOConstructorProperty("state", false));
		propList.add(new BODTOConstructorProperty("order.number", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("externalId", false));
		
		return propList;
		
	}
	
	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(number);
		
		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lot.name",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(lot);
		
		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.number",
				value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(itemData);
		
		TemplateQueryWhereToken itemDataName = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name",
				value);
		itemDataName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(itemDataName);

		TemplateQueryWhereToken client = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(client);

		return ret;
	}

	
	public LOSResultList<BODTO<LOSCustomerOrderPosition>> autoCompletionByOrderRequest(
			String typed, BODTO<LOSCustomerOrder> orderTO, QueryDetail detail) {
		String typedToUpper = typed.toUpperCase();
		
		List<TemplateQueryWhereToken> tokenList = new ArrayList<TemplateQueryWhereToken>();
		
		Client client = null;
		
		if(orderTO != null){
			LOSCustomerOrder order = manager.find(LOSCustomerOrder.class, orderTO.getId());
			client = order.getClient();
			tokenList.add(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "order", order));
		}
		
		return autoCompletion(typedToUpper, client, tokenList.toArray(new TemplateQueryWhereToken[0]), detail);
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("finishedState");
			ret.add(token);
		}
		if( "AMOUNT_INCOMPLETE".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("finishedState");
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "amountPicked", null);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("o.amount");
			ret.add(token);
		}
		
		return ret;
	}

}
