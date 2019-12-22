/*
 * StorageLocationQueryBean.java
 *
 * Created on 14. September 2006, 06:53
 *
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.linogistix.los.inventory.query.dto.LotTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.LotEntityService;
import de.wms2.mywms.product.ItemData;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class LotQueryBean extends BusinessObjectQueryBean<Lot> implements
		LotQueryRemote {

	private static final Logger log = Logger.getLogger(LotQueryBean.class);

	private static final String[] dtoProps = new String[] { "id", "version",
			"name", "itemData.number", "itemData.name", "lock", "useNotBefore", "bestBeforeEnd" };

	@Inject
	private LotEntityService lotEntityService;

	
	/**
	 * In contrast to myWMS model we regard the name of a lot as unique.
	 * 
	 * @return
	 */
	@Override
	public String getUniqueNameProp() {
		return "name";
	}

	@Override
	public Class<LotTO> getBODTOClass() {
		return LotTO.class;
	}

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	public LOSResultList<BODTO<Lot>> autoCompletionByClientAndItemData(
			String lotExp, BODTO<ItemData> idat) {
		
		TemplateQuery q = new TemplateQuery();
		q.setBoClass(tClass);
		
		if(idat != null){
			TemplateQueryWhereToken it = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "itemData.id", idat.getId());
			q.addNewFilter().addWhereToken(it);
		}
		
		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "name", lotExp);
		q.addWhereToken(lot);
		
		QueryDetail det = new QueryDetail(0, Integer.MAX_VALUE,
				getUniqueNameProp(), true);
		
		LOSResultList<BODTO<Lot>> ret;
		try {
			ret = queryByTemplateHandles(det, q);
			return ret;
		} catch (BusinessObjectNotFoundException e) {
			log.error(e.getMessage(), e);
			return new LOSResultList<BODTO<Lot>>();
		} catch (BusinessObjectQueryException e) {
			log.error(e.getMessage(), e);
			return new LOSResultList<BODTO<Lot>>();
		}
	}

	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {

		List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

		TemplateQueryWhereToken item = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.number", value);
		item.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(item);

		TemplateQueryWhereToken iName = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name",
				value);
		iName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(iName);

		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "name", value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(lot);

		return ret;
	}
	
	public Lot queryByNameAndItemData(String lotName, ItemData itemData) {
		return lotEntityService.read(itemData, lotName);
	}

    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "AVAILABLE".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", 0);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		else if( "EXPIRED".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", 202);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		else if( "TOO_YOUNG".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", 203);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		else if( "PRODUCER_CALL".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", 204);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		else if( "QUALITY_FAULT".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", 205);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}

		
		return ret;
	}
}
