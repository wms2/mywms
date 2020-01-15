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

import de.linogistix.los.inventory.query.dto.LOSPickingUnitLoadTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.strategy.OrderState;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingUnitLoadQueryBean extends BusinessObjectQueryBean<Packet> implements LOSPickingUnitLoadQueryRemote{
	
	@Override
	public String getUniqueNameProp() {
		return "unitLoad.labelId";
	}
	
	@Override
	public Class<LOSPickingUnitLoadTO> getBODTOClass() {
		return LOSPickingUnitLoadTO.class;
	}
	
	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("state", false));
		propList.add(new BODTOConstructorProperty("unitLoad.labelId", false));
		propList.add(new BODTOConstructorProperty("unitLoad.storageLocation.name", false));
		propList.add(new BODTOConstructorProperty("pickingOrder.orderNumber", null, BODTOConstructorProperty.JoinType.LEFT, "pickingOrder"));
		propList.add(new BODTOConstructorProperty("deliveryOrder.orderNumber", null, BODTOConstructorProperty.JoinType.LEFT, "deliveryOrder"));
		
		return propList;
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		Integer iValue = null;
		try {
			iValue = Integer.valueOf(value);
		}
		catch( Throwable t) {}
		
		TemplateQueryWhereToken token;
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "pickingOrder.orderNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "deliveryOrder.orderNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "unitLoad.labelId", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "unitLoad.storageLocation.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		if( iValue != null ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", iValue);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}
		
		
		return ret;
	}
    
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", OrderState.SHIPPED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("finishedState");
			ret.add(token);
		}
		
		return ret;
	}
}
