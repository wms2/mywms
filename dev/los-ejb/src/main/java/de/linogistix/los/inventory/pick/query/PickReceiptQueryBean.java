/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.inventory.pick.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.pick.model.PickReceipt;
import de.linogistix.los.inventory.pick.query.dto.PickReceiptTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless

//public class LogItemQueryBean extends BusinessObjectQueryBean<Client> implements LogItemQueryRemote{
public class PickReceiptQueryBean extends BusinessObjectQueryBean<PickReceipt> implements PickReceiptQueryRemote {

	public static final String[] props = new String[]{
		"id", "version", "name",
		"orderNumber", "pickNumber", "labelID", "state", "date"
	};
	
	
    @Override
    public String getUniqueNameProp() {
    	return "labelID";
    }
    
    @Override
	public Class<PickReceiptTO> getBODTOClass() {
    	return PickReceiptTO.class;
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return props;
    }
    
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		

		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "name",
				value);
		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken orderNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "orderNumber",
				value);
		orderNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken labelID = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "labelID",
				value);
		labelID.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		
		TemplateQueryWhereToken pickNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "pickNumber",
				value);
		pickNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		
		ret.add(name);
		ret.add(orderNumber);
		ret.add(labelID);
		ret.add(pickNumber);
		
		return ret;
	}
    
}
