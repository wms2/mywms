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

package de.linogistix.los.stocktaking.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.stocktaking.model.LOSStocktakingOrder;
import de.linogistix.los.stocktaking.model.LOSStocktakingState;
import de.linogistix.los.stocktaking.query.dto.StockTakingOrderTO;

/**
 *
 * @author krane
 */
@Stateless
public class LOSStocktakingOrderQueryBean extends BusinessObjectQueryBean<LOSStocktakingOrder> implements LOSStocktakingOrderQueryRemote{
    
	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"id",
		"locationName",
		"unitLoadLabel",
		"operator",
		"state"
	};
	
	@Override
    public String getUniqueNameProp() {
        return "id";
    }
    
	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	public Class<StockTakingOrderTO> getBODTOClass() {
		return StockTakingOrderTO.class;
	}
	
	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
    	Long id;
		try{
			id = Long.parseLong(value);
		} catch (Throwable t){
			id = Long.valueOf(-1);
		}
		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken location = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "locationName",
				value);
		location.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken label = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "unitLoadLabel",
				value);
		label.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken user = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "operator",
				value);
		user.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		
		ret.add(name);
		ret.add(location);
		ret.add(label);
		ret.add(user);
		
		return ret;
	}

	
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.CREATED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("state1");
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.FREE);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("state2");
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.STARTED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("state3");
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.COUNTED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("state4");
			ret.add(token);
		}
		if( "DIFF".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", LOSStocktakingState.COUNTED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}
		
		return ret;
	}
}
