/*
/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.query.dto.LOSSystemPropertyTO;
import de.wms2.mywms.property.SystemProperty;

/**
 * @author krane
 *
 */
@Stateless
public class LOSSystemPropertyQueryBean extends BusinessObjectQueryBean<SystemProperty> implements LOSSystemPropertyQueryRemote {

	
	static List<BODTOConstructorProperty> BODTOConstructorProperties = new ArrayList<BODTOConstructorProperty>();

	static{
		BODTOConstructorProperties.add(new BODTOConstructorProperty("id", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("version", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("propertyGroup", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("propertyKey", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("client.number", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("propertyContext", false));
		BODTOConstructorProperties.add(new BODTOConstructorProperty("propertyValue", false));
	}
	
	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		return BODTOConstructorProperties;
	}

	@Override
	public Class<LOSSystemPropertyTO> getBODTOClass() {
		return LOSSystemPropertyTO.class;
	}

	public String getUniqueNameProp() {
	    return "propertyKey";
	}

	
    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
    	Long id = null;
		try{
			id = Long.parseLong(value);
		} catch (Throwable t){
		}
		if( id != null ) {
			TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
			idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(idt);
		}
		
		TemplateQueryWhereToken client= new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(client);

		TemplateQueryWhereToken key = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "propertyKey",
				value);
		key.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(key);
		
		TemplateQueryWhereToken workstation = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "propertyContext",
				value);
		workstation.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(workstation);

		TemplateQueryWhereToken valuet = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "propertyValue",
				value);
		valuet.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(valuet);
		
		TemplateQueryWhereToken group = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "propertyGroup",
				value);
		group.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(group);

		return ret;
	}
}
