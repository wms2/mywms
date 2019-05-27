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
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;

import de.linogistix.los.inventory.model.LOSAdvice;
import de.linogistix.los.inventory.model.LOSAdviceState;
import de.linogistix.los.inventory.model.LOSGoodsReceipt;
import de.linogistix.los.inventory.model.LOSGoodsReceiptPosition;
import de.linogistix.los.inventory.query.dto.LOSAdviceTO;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BODTOConstructorProperty;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.OrderByToken;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.BusinessObjectHelper;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.product.ItemData;

/**
 * 
 * @author <a href"
 */
@Stateless
public class LOSAdviceQueryBean extends BusinessObjectQueryBean<LOSAdvice>
		implements LOSAdviceQueryRemote {
	
	private static final Logger log = Logger.getLogger(LOSAdviceQueryBean.class);
	
	@Override
	public String getUniqueNameProp() {
		return "adviceNumber";
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
		propList.add(new BODTOConstructorProperty("adviceNumber", false));
		propList.add(new BODTOConstructorProperty("client.number", false));
		propList.add(new BODTOConstructorProperty("adviceState", false));
		propList.add(new BODTOConstructorProperty("receiptAmount", false));
		propList.add(new BODTOConstructorProperty("notifiedAmount", false));

		propList.add(new BODTOConstructorProperty("itemData.number", false));
		propList.add(new BODTOConstructorProperty("itemData.name", false));
		propList.add(new BODTOConstructorProperty("itemData.scale", false));
		propList.add(new BODTOConstructorProperty("lot.name", null, BODTOConstructorProperty.JoinType.LEFT, "lot"));
		propList.add(new BODTOConstructorProperty("expectedDelivery", false));
		
		return propList;
	}

	public List<BODTO<LOSAdvice>>  queryGoodsToCome(QueryDetail qd) throws BusinessObjectNotFoundException, BusinessObjectQueryException{
		TemplateQuery q;

		q = new TemplateQuery();
		q.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "", LOSAdviceState.FINISHED));
		q.addWhereToken(new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL, "", LOSAdviceState.OVERLOAD));
		
		return queryByTemplateHandles(qd, q);

		
		
	}

	public LOSResultList<BODTO<LOSAdvice>> autoCompletionByClientLotItemdata(
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
		
		TemplateQueryWhereToken clientNumber = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		clientNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(clientNumber);
		
		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lot.name",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(lot);
		
		TemplateQueryWhereToken reqid = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "adviceNumber",
				value);
		reqid.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(reqid);

		TemplateQueryWhereToken extNo = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "externalAdviceNumber",
				value);
		extNo.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(extNo);
		
		TemplateQueryWhereToken extId = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "externalId",
				value);
		extId.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(extId);
		
		return ret;
	}

	@SuppressWarnings("unchecked")
	public LOSResultList<BODTO<LOSAdvice>> queryByAssingigGoodsReceipt(BODTO<LOSGoodsReceipt> gr, QueryDetail detail) {
		LOSGoodsReceipt goodsReceipt = manager.find(LOSGoodsReceipt.class, gr.getId());
		
		StringBuffer s= new StringBuffer();
		s.append("SELECT new ");
		s.append(LOSAdviceTO.class.getName());
		s.append("(");
		s.append("adv");
		s.append(")");
		s.append(" FROM ");
		s.append(LOSAdvice.class.getSimpleName());
		s.append(" adv, ");
		s.append(LOSGoodsReceipt.class.getSimpleName());
		s.append(" gr ");
		s.append(" WHERE ");
		s.append(" adv in elements(gr.assignedAdvices) ");
		s.append(" AND gr = :gr ");
		int i=0;
		if (detail.getOrderBy() != null){
			for (OrderByToken tok : detail.getOrderBy()){
				if (i==0) {
					s.append(" ORDER BY ");
					s.append(tok.getAttribute() + " " + (tok.isAscending()?"ASC":"DESC"));
				} else{
					s.append(" AND " + tok.getAttribute() + " " + (tok.isAscending()?"ASC":"DESC"));
				}
				i++;
			}
		}
		String st = s.toString();
		
		log.info(st);
		
		Query query = manager.createQuery(st);
		query.setParameter("gr", goodsReceipt);
		query.setMaxResults(detail.getMaxResults());
		query.setFirstResult(detail.getStartResultIndex());
		
		List<BODTO<LOSAdvice>> ret = query.getResultList();

		LOSResultList<BODTO<LOSAdvice>> lsoResultList = new LOSResultList<BODTO<LOSAdvice>>(ret);
		lsoResultList.setResultSetSize(detail.getMaxResults());
		lsoResultList.setStartResultIndex(detail.getStartResultIndex());
		
		return lsoResultList;
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "OPEN".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "adviceState", LOSAdviceState.RAW);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("state1");
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "adviceState", LOSAdviceState.GOODS_TO_COME);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("state2");
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "adviceState", LOSAdviceState.PROCESSING);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			token.setParameterName("state3");
			ret.add(token);
		}
		
		return ret;
	}

	public boolean hasSingleClient() {
		Client systemClient = clientService.getSystemClient();
		List<Client> clients = clientService.getList(systemClient);
		return clients.size()<2;
	}
	
	
	@Override
	public LOSAdvice queryById(Long ID) throws BusinessObjectNotFoundException, BusinessObjectSecurityException {
		LOSAdvice advice= super.queryById(ID);

		for(LOSGoodsReceiptPosition receiptLine : advice.getGrPositionList()) {
			BusinessObjectHelper.eagerRead(receiptLine);
		}

		List<LOSGoodsReceiptPosition> grPositionList = new ArrayList<>(advice.getGrPositionList().size());
		grPositionList.addAll(advice.getGrPositionList());
		advice.setGrPositionList(grPositionList);
		
		return advice;
	}
}
