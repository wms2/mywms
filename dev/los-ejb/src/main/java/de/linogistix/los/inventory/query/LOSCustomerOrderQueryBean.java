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

import de.linogistix.los.inventory.query.dto.LOSCustomerOrderTO;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.strategy.OrderState;

/** 
*
* @author krane
*/
@Stateless
public class LOSCustomerOrderQueryBean extends BusinessObjectQueryBean<DeliveryOrder> implements LOSCustomerOrderQueryRemote {
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}

	@Override
	public String getUniqueNameProp() {
		return "orderNumber";
	}

	@Override
	public Class<LOSCustomerOrderTO> getBODTOClass() {
		return LOSCustomerOrderTO.class;
	}

	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("orderNumber", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("externalNumber", false));
		propList.add(new BODTOConstructorProperty("deliveryDate", false));
		propList.add(new BODTOConstructorProperty("state", false));
		propList.add(new BODTOConstructorProperty("destination.name", null, BODTOConstructorProperty.JoinType.LEFT, "destination"));
		propList.add(new BODTOConstructorProperty("customerName", false));
		propList.add(new BODTOConstructorProperty("prio", false));
		propList.add(new BODTOConstructorProperty("orderStrategy.name", false));
//		propList.add(new BODTOConstructorProperty("positions.size", false));
		
		return propList;
		
	}


    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
    	
		Integer iValue = null;
		try {
			iValue = Integer.valueOf(value);
		}
		catch( Throwable t) {}

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken token;
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "orderNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "externalNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "destination.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "orderStrategy.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		if( iValue != null ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", iValue);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}

		return ret;
	}

    
  	public LOSResultList<BODTO<DeliveryOrder>> autoCompletionOpenOrders(String typed, BODTO<Client> clientTO, QueryDetail detail) {	
		
		Client client = null;
		
		if(clientTO != null){
			client = manager.find(Client.class, clientTO.getId());
		}
		
	
		List<TemplateQueryWhereToken> tokenList = new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token = null;
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", State.RAW);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		token.setParameterName("state1");
		tokenList.add(token);
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", State.PENDING);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		token.setParameterName("state2");
		tokenList.add(token);
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", OrderState.CREATED);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		token.setParameterName("state3");
		tokenList.add(token);
		// 05.07.2013, krane. 
		// If state = started there may be a valid picking order. In this case it is not good
		// to use the order in the treat-order-dialog.
		// This method is only used by this dialog 
//		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", State.STARTED);
//		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		token.setParameterName("state3");
//		tokenList.add(token);
		
		
		return autoCompletion(typed, client, tokenList.toArray(new TemplateQueryWhereToken[0]), detail);
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
		
		return ret;
	}

}
