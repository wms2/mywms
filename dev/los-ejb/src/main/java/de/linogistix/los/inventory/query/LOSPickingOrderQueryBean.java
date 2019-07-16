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

import de.linogistix.los.inventory.query.dto.LOSPickingOrderTO;
import de.linogistix.los.model.State;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.util.businessservice.ContextService;
import de.wms2.mywms.picking.PickingOrder;

/**
 * @author krane
 *
 */
@Stateless
public class LOSPickingOrderQueryBean extends BusinessObjectQueryBean<PickingOrder> implements LOSPickingOrderQueryRemote {

	@EJB
	private ContextService ctxService;

	@Override
	public String getUniqueNameProp() {
		return "orderNumber";
	}
	
	@Override
	public Class<LOSPickingOrderTO> getBODTOClass() {
		return LOSPickingOrderTO.class;
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
		propList.add(new BODTOConstructorProperty("orderNumber", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("deliveryOrder.orderNumber", null, BODTOConstructorProperty.JoinType.LEFT, "deliveryOrder"));
		propList.add(new BODTOConstructorProperty("state", false));
//		propList.add(new BODTOConstructorProperty("positions.size", false));
		propList.add(new BODTOConstructorProperty("prio", false));
		propList.add(new BODTOConstructorProperty("operator.name", null, BODTOConstructorProperty.JoinType.LEFT, "operator"));
		propList.add(new BODTOConstructorProperty("destination.name", null, BODTOConstructorProperty.JoinType.LEFT, "destination"));
		
		return propList;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<PickingOrder> queryAll( Client client ) {
		
		if( !ctxService.getCallersClient().isSystemClient() ) {
			client = ctxService.getCallersClient();
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT order FROM ");
		buffer.append(PickingOrder.class.getSimpleName());
		buffer.append(" order ");
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
		

		Integer iValue = null;
		try {
			iValue = Integer.valueOf(value);
		}
		catch( Throwable t) {}

		
		TemplateQueryWhereToken token;
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "orderNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "deliveryOrder.orderNumber", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		if( iValue != null ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "state", iValue);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
	
		}
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "operator.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "destination.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		return ret;
	}
    
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", State.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			token.setParameterName("finishedState");
			ret.add(token);
		}
		
		return ret;
	}

}
