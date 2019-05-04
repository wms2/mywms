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

package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import de.linogistix.los.inventory.model.StockUnitLabel;
import de.linogistix.los.inventory.query.dto.StockUnitLabelTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

import javax.ejb.Stateless;




/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class StockUnitLabelQueryBean extends BusinessObjectQueryBean<StockUnitLabel> implements StockUnitLabelQueryRemote{

	private static final String[] props = new String[]{
		"id", "version","labelID", "itemdataRef", "scale","itemUnit","lotRef", "amount", "dateRef"
	};
	
    @Override
    public String getUniqueNameProp() {
        return "name";
    }
    
    @Override
	public Class<StockUnitLabelTO> getBODTOClass() {
    	return StockUnitLabelTO.class;
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return props;
    }
    
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		

		TemplateQueryWhereToken labelID = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "labelID",
				value);
		labelID.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemdataRef = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemdataRef",
				value);
		itemdataRef.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken lotRef = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lotRef",
				value);
		
		lotRef.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		ret.add(labelID);
		ret.add(itemdataRef);
		ret.add(lotRef);
		
		return ret;
	}


    
}
