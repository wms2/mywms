/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.mywms.model.Client;

import de.linogistix.los.inventory.query.dto.LOSPickingPositionTO;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.picking.PickingOrderLine;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingPositionQueryBean extends BusinessObjectQueryBean<PickingOrderLine> implements LOSPickingPositionQueryRemote{

	@EJB
	private ContextService ctxService;

	@Override
	public Class<LOSPickingPositionTO> getBODTOClass() {
		return LOSPickingPositionTO.class;
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
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("state", false));
		propList.add(new BODTOConstructorProperty("pickingType", false));
		propList.add(new BODTOConstructorProperty("amount", false));
		propList.add(new BODTOConstructorProperty("pickedAmount", false));
		propList.add(new BODTOConstructorProperty("pickFromUnitLoadLabel", false));
		propList.add(new BODTOConstructorProperty("pickFromLocationName", false));
		propList.add(new BODTOConstructorProperty("pickingOrder.orderNumber", null, BODTOConstructorProperty.JoinType.LEFT, "pickingOrder"));
		propList.add(new BODTOConstructorProperty("itemData", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		
		return propList;
	}



	@SuppressWarnings("unchecked")
	public List<PickingOrderLine> queryAll( Client client ) {
		
		if( !ctxService.getCallersClient().isSystemClient() ) {
			client = ctxService.getCallersClient();
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT pos FROM ");
		buffer.append(PickingOrderLine.class.getSimpleName());
		buffer.append(" pos ");
		if( client != null ) {
			buffer.append("WHERE client=:client");
		}
		Query q = manager.createQuery(new String(buffer));
		if( client != null ) {
			q = q.setParameter("client", client);
		}
		
		return q.getResultList();
	}

	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		Long iLong = null;
		Integer iInteger = null;
		try {
			iLong = Long.valueOf(value);
			iInteger = Integer.valueOf(value);
		}
		catch( Throwable t) {}
		
		TemplateQueryWhereToken token;
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "pickFromUnitLoadLabel", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "pickFromLocationName", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "pickingOrder.orderNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		if( iInteger != null ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", iInteger);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}
		if( iLong != null ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "id", iLong);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}
		
		
		return ret;
	}
    
    
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.PICKED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("finishedState");
			ret.add(token);
		}
		
		return ret;
	}
}
