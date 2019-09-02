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

import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptPositionTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.strategy.OrderState;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSGoodsReceiptPositionQueryBean extends BusinessObjectQueryBean<GoodsReceiptLine> implements LOSGoodsReceiptPositionQueryRemote{

	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}
	
	@Override
	public String getUniqueNameProp() {
        return "lineNumber";
	}
	
	@Override
	public Class<LOSGoodsReceiptPositionTO> getBODTOClass() {
    	return LOSGoodsReceiptPositionTO.class; 
	}

	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("lineNumber", false));
		propList.add(new BODTOConstructorProperty("amount", false));
		propList.add(new BODTOConstructorProperty("itemData.name", "itemData.name", BODTOConstructorProperty.JoinType.LEFT, "itemData as itemData"));
		propList.add(new BODTOConstructorProperty("lotNumber", false));
		propList.add(new BODTOConstructorProperty("operator.name", "operator.name", BODTOConstructorProperty.JoinType.LEFT, "operator as operator"));
		propList.add(new BODTOConstructorProperty("lockType", false));
		propList.add(new BODTOConstructorProperty("unitLoadLabel", false));
		propList.add(new BODTOConstructorProperty("created", false));

		return propList;
		
	}
    
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken positionNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lineNumber",
				value);
		positionNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(positionNumber);

		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name",
				value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(itemData);

		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lotNumber",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(lot);
		
		
		return ret;
	}
    
    
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", OrderState.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}
		
		return ret;
	}
    
}
