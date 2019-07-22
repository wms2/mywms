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
import javax.inject.Inject;

import org.mywms.model.Client;

import de.linogistix.los.location.query.dto.StorageLocationTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LOSStorageLocationQueryBean 
        extends BusinessObjectQueryBean<StorageLocation> 
        implements LOSStorageLocationQueryRemote
{
	
	@Inject
	private StorageLocationEntityService locationService;

	
	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}

	@Override
	public String getUniqueNameProp() {
		return "name";
	}

	@Override
	public Class<StorageLocationTO> getBODTOClass() {
		return StorageLocationTO.class;
	}

	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("name", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("locationType.name", false));
		propList.add(new BODTOConstructorProperty("area.name", null, BODTOConstructorProperty.JoinType.LEFT, "area"));
		propList.add(new BODTOConstructorProperty("zone.name", null, BODTOConstructorProperty.JoinType.LEFT, "zone"));
		propList.add(new BODTOConstructorProperty("lock", false));
		
		return propList;
	}

	
    

	public LOSResultList<BODTO<StorageLocation>> autoCompletionClientAndAreaType(String searchString, 
											BODTO<Client> clientTO, 
											QueryDetail detail) 
	{
		Client client;
		
		List<TemplateQueryWhereToken> tokenList = new ArrayList<TemplateQueryWhereToken>();
		
		if(clientTO != null){
			client = manager.find(Client.class, clientTO.getId());
		}
		else{
			client = clientService.getSystemClient();
		}
		
		return autoCompletion(searchString, client, tokenList.toArray(new TemplateQueryWhereToken[0]), detail);
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
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "scanCode", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "plcCode", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "locationType.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "area.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "zone.name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "field", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		if( iValue != null ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", iValue);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}

		return ret;
	}
    
	public StorageLocation getClearing() {
		return locationService.getClearing();
	}

	public StorageLocation getNirwana() {
		return locationService.getTrash();
	}
	
	public String getNirwanaName(){
		return getNirwana().getName();
	}
	
}
