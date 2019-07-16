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

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import de.linogistix.los.inventory.query.dto.LOSOrderStrategyTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.strategy.OrderStrategy;

/** 
*
* @author krane
*/
@Stateless
public class LOSOrderStrategyQueryBean extends BusinessObjectQueryBean<OrderStrategy> implements LOSOrderStrategyQueryRemote {
	private Logger log = Logger.getLogger(LOSOrderStrategyQueryBean.class);

	@Override
	protected String[] getBODTOConstructorProps() {
		return new String[]{};
	}

	@Override
	public String getUniqueNameProp() {
		return "name";
	}

	@Override
	public Class<LOSOrderStrategyTO> getBODTOClass() {
		return LOSOrderStrategyTO.class;
	}

	@Override
	protected List<BODTOConstructorProperty> getBODTOConstructorProperties() {
		List<BODTOConstructorProperty> propList = super.getBODTOConstructorProperties();
		
		propList.add(new BODTOConstructorProperty("id", false));
		propList.add(new BODTOConstructorProperty("version", false));
		propList.add(new BODTOConstructorProperty("name", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		
		return propList;
	}


    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {

    	List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken token;
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);

		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<String> getNametList() {
		String queryStr = 
				"SELECT o FROM " + OrderStrategy.class.getSimpleName() + " o " +
				"WHERE manualCreationIndex>=0 " +
				"ORDER BY manualCreationIndex ";

		List<String> ret = new ArrayList<String>();
		
		try {
			Query query = manager.createQuery(queryStr);
		
			List<OrderStrategy> stratList = query.getResultList();
			
			for( OrderStrategy strat : stratList ) {
				ret.add(strat.getName());
			}
		}
		catch(Exception e) {
			log.error("Error in query: "+e.getMessage(), e);
		}
		
		return ret;
	}

}
