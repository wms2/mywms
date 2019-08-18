/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.query.dto.LOSGoodsOutPositionTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.shipping.ShippingOrderLine;
import de.wms2.mywms.strategy.OrderState;


@Stateless
public class LOSGoodsOutRequestPositionQueryBean extends BusinessObjectQueryBean<ShippingOrderLine> implements
		LOSGoodsOutRequestPositionQueryRemote {
	
	@Override
	public String getUniqueNameProp() {
		return "packet.unitLoad.labelId";
	}

	private static final String[] dtoProps = new String[] { "id", "version",   
		"state",
		"packet.unitLoad.labelId",
		"packet.unitLoad.storageLocation.name",
		"shippingOrder.orderNumber"};

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	public Class<LOSGoodsOutPositionTO> getBODTOClass() {
		return LOSGoodsOutPositionTO.class;
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
    	Long id;
		try{
			id = Long.parseLong(value);
		} catch (Throwable t){
			id = new Long(-1);
		}
		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken unitLoad = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "packet.unitLoad.labelId",
				value);
		unitLoad.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "shippingOrder.orderNumber",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(name);
		ret.add(unitLoad);
		ret.add(number);
		
		return ret;
	}
    
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", OrderState.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("finishedState");
			ret.add(token);
		}
		
		return ret;
	}
    
}
