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
package de.linogistix.los.location.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.location.query.dto.LOSFixedLocationAssignmentTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.strategy.FixAssignment;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSFixedLocationAssignmentQueryBean extends BusinessObjectQueryBean<FixAssignment> implements LOSFixedLocationAssignmentQueryRemote {

	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"id",
		"storageLocation.name",
		"itemData.number",
		"itemData.name",
		"itemData.scale",
		"maxAmount"
	};

	@Override
	public String getOrderByProp() {
		return "itemData.number, o.storageLocation.name";
	}
	
    @Override
	public Class<LOSFixedLocationAssignmentTO> getBODTOClass() {
    	return LOSFixedLocationAssignmentTO.class;
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return dtoProps;
    }
    
    @Override
    protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
    	List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken item = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.number", value);
		item.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemName = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name", value);
		itemName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		TemplateQueryWhereToken loc = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "storageLocation.name", value);
		loc.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(item);
		ret.add(itemName);
		ret.add(loc);
		
		return ret;
    }
    
}
