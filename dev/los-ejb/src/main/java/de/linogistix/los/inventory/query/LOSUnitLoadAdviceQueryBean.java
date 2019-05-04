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

import de.linogistix.los.inventory.model.LOSUnitLoadAdvice;
import de.linogistix.los.inventory.query.dto.LOSUnitLoadAdviceTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

@Stateless
public class LOSUnitLoadAdviceQueryBean extends BusinessObjectQueryBean<LOSUnitLoadAdvice>
		implements LOSUnitLoadAdviceQueryRemote {
	public final static String props[] = new String[]{
		"id", "version", "number", "labelId", "adviceType"
	};
	
    @Override
    public String getUniqueNameProp() {
        return "number";
    }
    
    @Override
    protected String[] getBODTOConstructorProps() {
    	return props;
    }
    
    @Override
	public Class<LOSUnitLoadAdviceTO> getBODTOClass() {
    	return LOSUnitLoadAdviceTO.class; 
    }

    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken labelId = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "labelId",
				value);
		labelId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		

		ret.add(number);
		ret.add(labelId);
		
		
		return ret;
	}

}
