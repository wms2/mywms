/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.model.LOSUnitLoadAdvicePosition;
import de.linogistix.los.inventory.query.dto.LOSUnitLoadAdvicePositionTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

@Stateless
public class LOSUnitLoadAdvicePositionQueryBean extends
		BusinessObjectQueryBean<LOSUnitLoadAdvicePosition> implements
		LOSUnitLoadAdvicePositionQueryRemote {
	
	public final static String props[] = new String[]{
		"id", "version", "positionNumber", "itemData.name", "notifiedAmount"
	};
	
    @Override
    public String getUniqueNameProp() {
        return "positionNumber";
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return props;
    }
    
    @Override
	public Class<LOSUnitLoadAdvicePositionTO> getBODTOClass() {
    	return LOSUnitLoadAdvicePositionTO.class; 
    }

    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken positionNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "positionNumber",
				value);
		positionNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name",
				value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		
		ret.add(positionNumber);
		ret.add(itemData);
		
		
		return ret;
	}

}
