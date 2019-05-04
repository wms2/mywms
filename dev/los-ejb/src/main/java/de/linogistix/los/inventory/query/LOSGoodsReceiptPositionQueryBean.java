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

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptPositionTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSGoodsReceiptPositionQueryBean extends BusinessObjectQueryBean<LOSGoodsReceiptPosition> implements LOSGoodsReceiptPositionQueryRemote{

	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}
	
	@Override
	public String getUniqueNameProp() {
        return "positionNumber";
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
		propList.add(new BODTOConstructorProperty("positionNumber", false));
		propList.add(new BODTOConstructorProperty("orderReference", false));
		propList.add(new BODTOConstructorProperty("amount", false));
		propList.add(new BODTOConstructorProperty("itemData", false));
		propList.add(new BODTOConstructorProperty("lot", false));
		propList.add(new BODTOConstructorProperty("scale", false));
		propList.add(new BODTOConstructorProperty("operator.name", null, BODTOConstructorProperty.JoinType.LEFT, "operator"));
		propList.add(new BODTOConstructorProperty("qaLock", false));
		propList.add(new BODTOConstructorProperty("unitLoad", false));
		propList.add(new BODTOConstructorProperty("created", false));
		
		return propList;
		
	}
    
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken positionNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "positionNumber",
				value);
		positionNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken orderReference = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderReference",
				value);
		orderReference.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData",
				value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lot",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		
		ret.add(positionNumber);
		ret.add(orderReference);
		ret.add(itemData);
		ret.add(lot);
		
		
		return ret;
	}
    
    
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "goodsReceipt.receiptState", LOSGoodsReceiptState.RAW);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("rawState");
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "goodsReceipt.receiptState", LOSGoodsReceiptState.ACCEPTED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("acceptState");
			ret.add(token);
		}
		
		return ret;
	}
    
	@Override
	public LOSGoodsReceiptPosition queryById(Long ID) throws BusinessObjectNotFoundException, BusinessObjectSecurityException {
		LOSGoodsReceiptPosition position = super.queryById(ID);

		LOSAdvice advice = position.getRelatedAdvice();
		if (advice != null) {
			BusinessObjectHelper.eagerRead(advice);
			
			for(LOSGoodsReceiptPosition receiptLine : advice.getGrPositionList()) {
				BusinessObjectHelper.eagerRead(receiptLine);
			}
		}

		return position;
	}
}
