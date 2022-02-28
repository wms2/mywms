/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.query.dto.LOSStorageRequestTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.transport.TransportOrder;
import de.wms2.mywms.transport.TransportOrderType;

/**
 *
 * @author krane
 */
@Stateless
public class LOSStorageRequestQueryBean extends BusinessObjectQueryBean<TransportOrder> implements LOSStorageRequestQueryRemote{

    @Override
    public String getUniqueNameProp() {
        return "orderNumber";
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
		propList.add(new BODTOConstructorProperty("orderNumber", false));
		propList.add(new BODTOConstructorProperty("state", false));
		propList.add(new BODTOConstructorProperty("orderType", false));
		propList.add(new BODTOConstructorProperty("unitLoad.labelId", null, BODTOConstructorProperty.JoinType.LEFT, "unitLoad"));
		propList.add(new BODTOConstructorProperty("itemData", null, BODTOConstructorProperty.JoinType.LEFT, "itemData"));
		propList.add(new BODTOConstructorProperty("sourceLocation.name", null, BODTOConstructorProperty.JoinType.LEFT, "sourceLocation"));
		propList.add(new BODTOConstructorProperty("destinationLocation.name", null, BODTOConstructorProperty.JoinType.LEFT, "destinationLocation"));
		propList.add(new BODTOConstructorProperty("amount", false));
		propList.add(new BODTOConstructorProperty("client.number", false));

		return propList;
	}

	@Override
	public Class<LOSStorageRequestTO> getBODTOClass() {
		return LOSStorageRequestTO.class;
	}

	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderNumber",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken label = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "unitLoad.labelId",
				value);
		label.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken destination = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "destinationLocation.name",
				value);
		destination.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		
		ret.add(number);
		ret.add(label);
		ret.add(destination);
		
		
		return ret;
	}

    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", OrderState.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		if( "TYPE_INBOUND".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderType", TransportOrderType.INBOUND);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		if( "TYPE_TRANSFER".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderType", TransportOrderType.UNDEFINED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderType", TransportOrderType.TRANSFER);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderType", TransportOrderType.OUTBOUND);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		if( "TYPE_REPLENISH".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderType", TransportOrderType.REPLENISH);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderType", TransportOrderType.REPLENISH_AREA);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "orderType", TransportOrderType.REPLENISH_FIX);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}

		return ret;
	}

}
