/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.location.model.LOSWorkingAreaPosition;
import de.linogistix.los.location.query.dto.LOSWorkingAreaPositionTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 * @author krane
 *
 */
@Stateless
public class LOSWorkingAreaPositionQueryBean extends BusinessObjectQueryBean<LOSWorkingAreaPosition> implements LOSWorkingAreaPositionQueryRemote {

	@Override
	public String getOrderByProp() {
		return "workingArea.name, o.cluster.name";
	}
	
	private static final String[] dtoProps = new String[] { "id", "version", 
		"workingArea.name", 
		"cluster.name"};

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	public Class<LOSWorkingAreaPositionTO> getBODTOClass() {
		return LOSWorkingAreaPositionTO.class;
	}

    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();

		TemplateQueryWhereToken token;

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "workingArea.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "cluster.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		return ret;
	}
}
