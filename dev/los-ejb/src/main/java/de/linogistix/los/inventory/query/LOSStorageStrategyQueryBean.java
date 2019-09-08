/*
 * Copyright (c) 2010 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-3PL
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.query.dto.LOSStorageStrategyTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.wms2.mywms.strategy.StorageStrategy;
import de.wms2.mywms.strategy.StorageStrategyRichClientConverter;


/**
 * @author krane
 *
 */
@Stateless
public class LOSStorageStrategyQueryBean extends BusinessObjectQueryBean<StorageStrategy> implements LOSStorageStrategyQueryRemote{
	

	@Override
	public Class<LOSStorageStrategyTO> getBODTOClass() {
		return LOSStorageStrategyTO.class;
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
		propList.add(new BODTOConstructorProperty("name", false));

		return propList;
	}
	@Override
    public String getUniqueNameProp() {
        return "name";
    }



    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();

		TemplateQueryWhereToken token;

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "name", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		return ret;
	}

	@Override
	public StorageStrategy queryById(Long ID) throws BusinessObjectNotFoundException, BusinessObjectSecurityException {
		StorageStrategy strategy = super.queryById(ID);
		strategy.setOrderByMode(StorageStrategyRichClientConverter.convertSortsToOrderByMode(strategy.getSorts()));
		return strategy;
	}

}
