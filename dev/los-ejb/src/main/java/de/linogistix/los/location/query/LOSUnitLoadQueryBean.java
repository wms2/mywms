/*
 * LOSUnitLoadQueryBean.java
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

import de.linogistix.los.location.constants.LOSUnitLoadLockState;
import de.linogistix.los.location.query.dto.UnitLoadTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.UnitLoad;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSUnitLoadQueryBean 
        extends BusinessObjectQueryBean<UnitLoad> 
        implements LOSUnitLoadQueryRemote
{
	
	@Override
	public String getUniqueNameProp() {
		return "labelId";
	}

	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}

	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("labelId", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("lock", false));
		propList.add(new BODTOConstructorProperty("storageLocation.name", false));
		propList.add(new BODTOConstructorProperty("isCarrier", false));
		propList.add(new BODTOConstructorProperty("unitLoadType.name", false));
		
		return propList;
	}

	@Override
	public Class<UnitLoadTO> getBODTOClass() {
		return UnitLoadTO.class;
	}

	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
    	
		Integer iValue = null;
		try {
			iValue = Integer.valueOf(value);
		}
		catch( Throwable t) {}
    	
    	
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();

		TemplateQueryWhereToken token;

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "storageLocation.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "labelId", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		if( iValue != null ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", iValue);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}
		
		return ret;
	}
    
    @Override
  	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

  		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
  		TemplateQueryWhereToken token;

  		if( "AVAILABLE".equals(filterString) ) {
  			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", 0);
  			token.setParameterName("availableLock");
  			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
  			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", StockState.ON_STOCK);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
  		}
  		if( "CARRIER".equals(filterString) ) {
  			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_TRUE, "isCarrier", 0);
  			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
  			ret.add(token);
  			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "lock", 9);
  			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
  			ret.add(token);
  		}
  		if( "EMPTY".equals(filterString) ) {
  			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_ISEMPTY, "stockUnitList", "");
  			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
  			ret.add(token);
  			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_FALSE, "isCarrier", 0);
  			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
  			ret.add(token);
  			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "lock", 9);
  			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
  			ret.add(token);
  		}
		if( "OUT".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_GREATER, "state", StockState.PICKED-1);
  			token.setParameterName("state1");
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", StockState.SHIPPED+1);
  			token.setParameterName("state2");
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}


  		return ret;
  	}
    
}
