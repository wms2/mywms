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

package de.linogistix.los.location.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.location.model.LOSUnitLoadRecord;
import de.linogistix.los.location.query.dto.LOSUnitLoadRecordTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSUnitLoadRecordQueryBean 
        extends BusinessObjectQueryBean<LOSUnitLoadRecord> 
        implements LOSUnitLoadRecordQueryRemote
{
	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"label",
		"fromLocation",
		"toLocation",
		"created",
		"activityCode", "recordType", "unitLoadType"
	};
	
	@Override
	public String getOrderByProp() {
		return "id";
	}
	
	@Override
	public String getUniqueNameProp() {
		
		return "activityCode";
	}
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	public Class<LOSUnitLoadRecordTO> getBODTOClass() {
		return LOSUnitLoadRecordTO.class;
	}

	@Override
	public LOSResultList<BODTO<LOSUnitLoadRecord>> queryByTemplateHandles(QueryDetail detail, 
																	TemplateQuery query)
		throws BusinessObjectNotFoundException, BusinessObjectQueryException 
	{
		detail.addOrderByToken("created", false);
		detail.addOrderByToken("label", true);
		
		return super.queryByTemplateHandles(detail, query);
	}

	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		
		List<TemplateQueryWhereToken> wtList = new ArrayList<TemplateQueryWhereToken>();
		
		// Please have in mind: This table is big.

//		TemplateQueryWhereToken client = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
//		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//		wtList.add(client);

		TemplateQueryWhereToken wt = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "label", value);
		wt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		wtList.add(wt);

		TemplateQueryWhereToken fromLocation = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "fromLocation", value);
		fromLocation.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		wtList.add(fromLocation);

		TemplateQueryWhereToken toLocation = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "toLocation", value);
		toLocation.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		wtList.add(toLocation);

		return wtList;
	}
}
