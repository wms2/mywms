/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.strategy.OrderState;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSGoodsReceiptQueryBean extends BusinessObjectQueryBean<GoodsReceipt> implements LOSGoodsReceiptQueryRemote{

	
    @Override
    public String getUniqueNameProp() {
        return "orderNumber";
    }
    
    @Override
	public Class<LOSGoodsReceiptTO> getBODTOClass() {
    	return LOSGoodsReceiptTO.class; 
    }
    
	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("orderNumber", false));
		propList.add(new BODTOConstructorProperty("deliveryNoteNumber", false));
		propList.add(new BODTOConstructorProperty("receiptDate", false));
		propList.add(new BODTOConstructorProperty("state", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("storageLocation", true));

		return propList;
	}
	
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken goodsReceiptNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderNumber",
				value);
		goodsReceiptNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken deliveryNoteNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "deliveryNoteNumber",
				value);
		deliveryNoteNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		ret.add(goodsReceiptNumber);
		ret.add(deliveryNoteNumber);
		ret.add(client);
		
		TemplateQueryWhereToken token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "storageLocation.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		
		
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

	@Override
	public GoodsReceipt queryById(Long ID) throws BusinessObjectNotFoundException, BusinessObjectSecurityException {
		GoodsReceipt goodsReceipt = super.queryById(ID);

		for (GoodsReceiptLine receiptLine : goodsReceipt.getLines()) {
			BusinessObjectHelper.eagerRead(receiptLine);
		}
		for (AdviceLine advice : goodsReceipt.getAdviceLines()) {
			BusinessObjectHelper.eagerRead(advice);
		}

		return goodsReceipt;
	}
}
