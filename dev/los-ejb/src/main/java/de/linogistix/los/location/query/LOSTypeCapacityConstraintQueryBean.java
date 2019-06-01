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

import de.linogistix.los.location.query.dto.LOSTypeCapacityConstraintTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.strategy.TypeCapacityConstraint;


/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSTypeCapacityConstraintQueryBean extends BusinessObjectQueryBean<TypeCapacityConstraint> implements LOSTypeCapacityConstraintQueryRemote{	
  
	@Override
	public String getOrderByProp() {
		return "locationType.name, o.unitLoadType.name";
	}
	
	private static final String[] dtoProps = new String[] { "id", "version", 
		"locationType.name", 
		"unitLoadType.name",
		"allocation"};

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	public Class<LOSTypeCapacityConstraintTO> getBODTOClass() {
		return LOSTypeCapacityConstraintTO.class;
	}

    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();

		TemplateQueryWhereToken token;

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "locationType.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "unitLoadType.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		return ret;
	}

}
