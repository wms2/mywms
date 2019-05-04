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

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.model.LOSGoodsReceiptState;
import de.linogistix.los.inventory.query.dto.LOSGoodsReceiptTO;
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
public class LOSGoodsReceiptQueryBean extends BusinessObjectQueryBean<LOSGoodsReceipt> implements LOSGoodsReceiptQueryRemote{

	
    @Override
    public String getUniqueNameProp() {
        return "goodsReceiptNumber";
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
		propList.add(new BODTOConstructorProperty("goodsReceiptNumber", false));
		propList.add(new BODTOConstructorProperty("deliveryNoteNumber", false));
		propList.add(new BODTOConstructorProperty("receiptDate", false));
		propList.add(new BODTOConstructorProperty("receiptState", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("goodsInLocation", true));

		return propList;
	}
	
	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken goodsReceiptNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "goodsReceiptNumber",
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
		
		TemplateQueryWhereToken token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "goodsInLocation.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		
		
		return ret;
	}
    
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "receiptState", LOSGoodsReceiptState.RAW);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("rawState");
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "receiptState", LOSGoodsReceiptState.ACCEPTED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("acceptState");
			ret.add(token);
		}
		
		return ret;
	}

	@Override
	public LOSGoodsReceipt queryById(Long ID) throws BusinessObjectNotFoundException, BusinessObjectSecurityException {
		LOSGoodsReceipt goodsReceipt = super.queryById(ID);

		for (LOSGoodsReceiptPosition receiptLine : goodsReceipt.getPositionList()) {
			BusinessObjectHelper.eagerRead(receiptLine);
		}
		for (LOSAdvice advice : goodsReceipt.getAssignedAdvices()) {
			BusinessObjectHelper.eagerRead(advice);

			for (LOSGoodsReceiptPosition additionalReceiptLine : advice.getGrPositionList()) {
				BusinessObjectHelper.eagerRead(additionalReceiptLine);
			}

		}

		return goodsReceipt;
	}
}
