/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href"
 *
 */

package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.mywms.model.Client;

import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;

/**
 * 
 * @author <a href"
 */
@Stateless
public class LOSAdviceQueryBean extends BusinessObjectQueryBean<AdviceLine>
		implements LOSAdviceQueryRemote {
	
	@Override
	public String getUniqueNameProp() {
		return "lineNumber";
	}

	@Override
	public Class<LOSAdviceTO> getBODTOClass() {
		return LOSAdviceTO.class;
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
		propList.add(new BODTOConstructorProperty("lineNumber", false));
		propList.add(new BODTOConstructorProperty("state", false));
		propList.add(new BODTOConstructorProperty("amount", false));
		propList.add(new BODTOConstructorProperty("confirmedAmount", false));

		propList.add(new BODTOConstructorProperty("itemData.number", false));
		propList.add(new BODTOConstructorProperty("itemData.name", false));
		propList.add(new BODTOConstructorProperty("itemData.scale", false));
		propList.add(new BODTOConstructorProperty("lotNumber", false));
		propList.add(new BODTOConstructorProperty("advice.client.number", false));
		propList.add(new BODTOConstructorProperty("advice.deliveryDate", false));
		
		return propList;
	}

	public LOSResultList<BODTO<AdviceLine>> autoCompletionByClientLotItemdata(
                                                                String exp,
								BODTO<Client> client, 
								BODTO<ItemData> item, 
								BODTO<Lot> lot,
                                                                QueryDetail detail)
	{
		List<TemplateQueryWhereToken> tokenList = new ArrayList<TemplateQueryWhereToken>();
		
		Client cl = null;
		if(client != null){
			cl = manager.find(Client.class, client.getId());
		}
		
		ItemData itemData = null;
		if(item != null){
			itemData = manager.find(ItemData.class, item.getId());
			tokenList.add(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "itemData", itemData));
		}
		
		Lot l = null;
		if(lot != null){
			l = manager.find(Lot.class, lot.getId());
			tokenList.add(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lot", l));
		}
		
		return autoCompletion(exp, cl, tokenList.toArray(new TemplateQueryWhereToken[0]), detail);
	}
	

	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken item = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.number",
				value);
		item.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(item);

		TemplateQueryWhereToken itemName = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name",
				value);
		itemName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(itemName);
		
		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lotNumber",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(lot);
		
		TemplateQueryWhereToken reqid = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lineNumber",
				value);
		reqid.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(reqid);

		TemplateQueryWhereToken extNo = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "externalNumber",
				value);
		extNo.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(extNo);
		
		return ret;
	}

    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_SMALLER, "state", OrderState.FINISHED);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(token);
		}
		
		return ret;
	}

	public boolean hasSingleClient() {
		return clientService.isSingleClient();
	}
	
}
