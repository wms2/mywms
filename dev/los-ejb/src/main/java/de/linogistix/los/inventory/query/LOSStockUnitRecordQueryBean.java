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

import de.linogistix.los.inventory.model.LOSStockUnitRecord;
import de.linogistix.los.inventory.query.dto.LOSStockUnitRecordTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSStockUnitRecordQueryBean extends
		BusinessObjectQueryBean<LOSStockUnitRecord> implements
		LOSStockUnitRecordQueryRemote {

	private static final String[] dtoProps = new String[] { "id", "version",
			"id", "itemData", "lot", "amount", "amountStock",
			"fromStockUnitIdentity", "fromStorageLocation", "fromUnitLoad",
			"toStockUnitIdentity", "toStorageLocation", "toUnitLoad",
			"activityCode", "type", "created", "unitLoadType" };

	@Override
	public String getUniqueNameProp() {
		return "id";
	}

	@Override
	public Class<LOSStockUnitRecordTO> getBODTOClass() {
		return LOSStockUnitRecordTO.class;
	}

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		// Please have in mind: This table is big. 
		
		
//    	Long id;
//		try{
//			id = Long.parseLong(value);
//		} catch (Throwable t){
//			id = new Long(-1);
//		}
//		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
//		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		ret.add(name);
//		
//		TemplateQueryWhereToken client = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
//		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		ret.add(client);

		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData",
				value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(itemData);
		
//		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_LIKE, "lot",
//				value);
//		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		ret.add(lot);
//		
//		TemplateQueryWhereToken fromStockUnitIdentity = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_LIKE, "fromStockUnitIdentity",
//				value);
//		fromStockUnitIdentity.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		ret.add(fromStockUnitIdentity);
//		
//		TemplateQueryWhereToken fromSl = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_LIKE, "fromStorageLocation", value);
//		fromSl.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		ret.add(fromSl);
//		
//		TemplateQueryWhereToken fromUl = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_LIKE, "fromUnitLoad", value);
//		fromUl.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		ret.add(fromUl);
//		
//		TemplateQueryWhereToken toStockUnitIdentity = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_LIKE, "toStockUnitIdentity",
//				value);
//		toStockUnitIdentity.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		ret.add(toStockUnitIdentity);

		TemplateQueryWhereToken toSl = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "toStorageLocation", value);
		toSl.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(toSl);
		
		TemplateQueryWhereToken toUl = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "toUnitLoad", value);
		toUl.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(toUl);
		
		TemplateQueryWhereToken activityCode= new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "activityCode",
				value);
		activityCode.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(activityCode);
		
		
		return ret;	
	}

}
