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

package de.linogistix.los.inventory.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.inventory.query.dto.StockUnitTO;
import de.linogistix.los.inventory.service.StockUnitLockState;
import de.linogistix.los.location.constants.LOSUnitLoadLockState;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.OrderByToken;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryFilter;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.query.exception.BusinessObjectQueryException;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class StockUnitQueryBean extends BusinessObjectQueryBean<StockUnit>
		implements StockUnitQueryRemote {

	private static final Logger logger = Logger.getLogger(StockUnitQueryBean.class);

//	public String getUniqueNameProp() {
//		return "labelId";
//	}

	@Override
	public Class<StockUnitTO> getBODTOClass() {
		return StockUnitTO.class;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public LOSResultList<BODTO<StockUnit>> queryByTemplateHandles(QueryDetail detail, TemplateQuery query, boolean count)
			throws BusinessObjectNotFoundException,	BusinessObjectQueryException 
	{
//		String conditionStr = "WHERE ";
		StringBuffer sbCount = new StringBuffer(" SELECT COUNT(su) FROM ");
		sbCount.append(StockUnit.class.getName()+" su  ");
		sbCount.append(" LEFT OUTER JOIN su.lot AS l ");
		sbCount.append(" JOIN su.unitLoad ul ");
//		sbCount.append(LOSUnitLoad.class.getSimpleName()+" ul ");
		sbCount.append("WHERE su.unitLoad = ul ");
		
		StringBuffer sbQuery = new StringBuffer(" SELECT NEW ");
		sbQuery.append(StockUnitTO.class.getName());
		sbQuery.append("(");
		sbQuery.append("su.id,");
		sbQuery.append("su.version,");
		//dgrys portierung wildfly 8.2
		//sbQuery.append("su.labelId,");
		sbQuery.append("su.lock,");
		sbQuery.append("su.lot,");
		sbQuery.append("su.itemData.number,");
		sbQuery.append("su.itemData.name,");
		sbQuery.append("ul.version,");
		sbQuery.append("ul.labelId,");
		sbQuery.append("ul.storageLocation.name,");
		sbQuery.append("su.amount,");
		sbQuery.append("su.reservedAmount,");
		sbQuery.append("su.itemData.scale");		
		sbQuery.append(") FROM ");			
		sbQuery.append(StockUnit.class.getSimpleName()+" su ");
		sbQuery.append(" LEFT OUTER JOIN su.lot AS l ");
		sbQuery.append(" JOIN su.unitLoad ul ");
//		sbQuery.append(LOSUnitLoad.class.getSimpleName()+" ul ");
		sbQuery.append("WHERE su.unitLoad = ul ");
		
		if(!getCallersUser().getClient().isSystemClient()){
			sbQuery.append("AND su.client =:cl ");
			sbCount.append(" AND su.client =:cl ");
//			conditionStr = "AND ";
		}
		
		if( !count ) {
			// special handling for autocompletion
			sbQuery.append(" AND su.lock!="+BusinessObjectLockState.GOING_TO_DELETE.getLock()+" AND ul.lock!="+LOSUnitLoadLockState.GOING_TO_DELETE.getLock()+" AND ul.lock!="+LOSUnitLoadLockState.SHIPPED.getLock());
		}
		
		
		List<TemplateQueryFilter> wfList = query.getWhereFilter();
		int iFilter = 0;
		for( TemplateQueryFilter filter : wfList ) {
			List<TemplateQueryWhereToken> wtList = query.getWhereTokens(filter);

//			iFilter++;
//			if( iFilter>1 ) {
//				sbCount.append(" AND ");
//				sbQuery.append(" AND ");
//			}
//			sbCount.append(" ( ");
//			sbQuery.append(" ( ");
			
			if (wtList != null && wtList.size() > 0) {
	
				sbCount.append(" AND ( ");
				sbQuery.append("AND ( ");
				
				int i=1;
				for (TemplateQueryWhereToken wt : wtList) {
					
					if(i > 1){
						sbCount.append(" ");
						sbCount.append(wt.getLogicalOperator());
					}
					sbCount.append(" ");
					sbCount.append(getWhereStatement(wt));
					sbCount.append(" ");
					
					if(i > 1){
						sbQuery.append(" ");
						sbQuery.append(wt.getLogicalOperator());
					}
					sbQuery.append(" ");
					sbQuery.append(getWhereStatement(wt));
					sbQuery.append(" ");
					
					i++;
				}
				sbQuery.append(" ) ");
				sbCount.append(" ) ");
			}
//			sbCount.append(" ) ");
//			sbQuery.append(" ) ");

		}
		
		if (detail.getOrderBy() != null && detail.getOrderBy().size() != 0) {
			sbQuery.append(" ORDER BY ");
			for (Iterator it = detail.getOrderBy().iterator(); it.hasNext();) {
				OrderByToken t = (OrderByToken) it.next();
				sbQuery.append("su.");
				sbQuery.append(t.getAttribute());
				sbQuery.append(" ");
				if (!t.isAscending()) {
					sbQuery.append("DESC ");
				}
				if (it.hasNext()) {
					sbQuery.append(",");
				}
			}
		} else {
//			sbQuery.append(" ORDER BY ");
//			sbQuery.append("su.");
//			sbQuery.append(getUniqueNameProp());
//			sbQuery.append(" ");
			sbQuery.append(" ORDER BY su.itemData.number, ul.storageLocation.name, ul.labelId, su.id ");
		}
		
		logger.info("--- Query by Template : "+sbQuery.toString());
		
		Query q = manager.createQuery(sbQuery.toString());
		Query countQuery = manager.createQuery(sbCount.toString());
		
		q.setFirstResult(detail.getStartResultIndex());
		q.setMaxResults(detail.getMaxResults());

		if(!getCallersUser().getClient().isSystemClient()){
			q.setParameter("cl", getCallersUser().getClient());
			countQuery.setParameter("cl", getCallersUser().getClient());
		}
		
		if (query.getWhereTokens() != null) {
			for (TemplateQueryWhereToken wt : query.getWhereTokens()) {
				if (wt != null) {
					if (wt.getOperator().equals(TemplateQueryWhereToken.OPERATOR_LIKE)) {
						transformLikeParameter(wt);
					}
					if(wt.getOperator().equals(TemplateQueryWhereToken.OPERATOR_AT)
							   && wt.getValue() instanceof Date)
					{
						Date param = (Date) wt.getValue();
						
						Calendar cal00 = Calendar.getInstance();
			        	cal00.setTime(param);
			        	cal00.set(Calendar.HOUR_OF_DAY, 0);
			        	cal00.set(Calendar.MINUTE, 0);
			        	
			        	Calendar cal24 = Calendar.getInstance();
			        	cal24.setTime(param);
			        	cal24.set(Calendar.HOUR_OF_DAY, 23);
			        	cal24.set(Calendar.MINUTE, 59);
						
						q.setParameter(wt.getParameterName()+"1", cal00.getTime());
						q.setParameter(wt.getParameterName()+"2", cal24.getTime());
						
						countQuery.setParameter(wt.getParameterName()+"1", cal00.getTime());
						countQuery.setParameter(wt.getParameterName()+"2", cal24.getTime());
					}
					else {
						countQuery.setParameter(wt.getParameterName(), wt.getValue());
						q.setParameter(wt.getParameterName(), wt.getValue());
					}
				}
			}
		}

		LOSResultList<BODTO<StockUnit>> resultList;
		resultList = new LOSResultList<BODTO<StockUnit>>(q.getResultList());

		if( count ) {
			resultList.setResultSetSize((Long) countQuery.getSingleResult());
		}
		return resultList;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LOSResultList<StockUnitTO> queryByItemData(
			BODTO<ItemData> idat,
			QueryDetail detail) throws BusinessObjectQueryException{
		
		if (idat == null){
			throw new NullPointerException("ItemData must not be null");
		}
		
		TemplateQuery q = new TemplateQuery();
		q.setBoClass(tClass);
		TemplateQueryWhereToken l = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "itemData.id", idat.getId());
		q.addWhereToken(l);
		LOSResultList ret;
		try {
			ret = queryByTemplateHandles(detail, q);
			return ret;
		} catch (BusinessObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			return new LOSResultList<StockUnitTO>();
		} catch (BusinessObjectQueryException e) {
			logger.error(e.getMessage(), e);
			return new LOSResultList<StockUnitTO>();
		}
		
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LOSResultList<StockUnitTO> queryByLot(
			BODTO<Lot> lot,
			QueryDetail detail) throws BusinessObjectQueryException{
		
		TemplateQuery q = new TemplateQuery();
		q.setBoClass(tClass);
		TemplateQueryWhereToken l = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "lot.id", lot.getId());
		q.addWhereToken(l);
		LOSResultList ret;
		try {
			ret = queryByTemplateHandles(detail, q);
			return ret;
		} catch (BusinessObjectNotFoundException e) {
			logger.error(e.getMessage(), e);
			return new LOSResultList<StockUnitTO>();
		} catch (BusinessObjectQueryException e) {
			return new LOSResultList<StockUnitTO>();
		}
		
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public LOSResultList<StockUnitTO> queryByStorageLocation(
			BODTO<LOSStorageLocation> sl,
			QueryDetail detail) throws BusinessObjectQueryException{
		try{
			if (sl == null){
				throw new NullPointerException("StorageLocation must not be null");
			}
			
			LOSStorageLocation storloc = manager.find(LOSStorageLocation.class, sl.getId());
			
			List<StockUnitTO> ret = new ArrayList<StockUnitTO>();
			StringBuffer sb ;
			sb = new StringBuffer(" SELECT COUNT(su) FROM ");
			
			sb.append(StockUnit.class.getName()+" su  ");
			sb.append(" JOIN su.unitLoad ul  ");
			sb.append(" WHERE ul.storageLocation = :sloc ");
			
			if(!getCallersUser().getClient().isSystemClient()){
				sb.append("AND su.client =:cl ");
			}
			
			int i=0;
			if (detail.getOrderBy() != null){
				for (OrderByToken tok : detail.getOrderBy()){
					if (i==0) {
						sb.append(" ORDER BY ");
						sb.append(tok.getAttribute() + " " + tok.isAscending());
					} else{
						sb.append(" AND " + tok.getAttribute() + " " + (tok.isAscending()?"ASC":"DESC"));
					}
					i++;
				}
			}
			
			Query countQ = manager.createQuery(sb.toString());
			countQ.setParameter("sloc", storloc);
			
			if(!getCallersUser().getClient().isSystemClient()){
				countQ.setParameter("cl", getCallersUser().getClient());
			}
			
			int resultSize = ((Long) countQ.getSingleResult()).intValue();
	
			sb = new StringBuffer();
			sb.append(" SELECT NEW ");
			sb.append(StockUnitTO.class.getName());
			sb.append("(");
			sb.append("su.id,");
			sb.append("su.version,");
			sb.append("su.labelId,");
			sb.append("su.lock,");
			sb.append("su.lot,");
			sb.append("it.number,");
			sb.append("it.name,");
			sb.append("ul.version,");
			sb.append("ul.labelId,");
			sb.append("ul.storageLocation.name,");
			sb.append("su.amount,");
			sb.append("su.reservedAmount,");
			sb.append("it.scale");						
			sb.append(") FROM ");			
			sb.append(StockUnit.class.getName()+" su ");
			sb.append(" LEFT OUTER JOIN su.lot AS l, ");
			sb.append(UnitLoad.class.getSimpleName()+" ul, ");
			sb.append(ItemData.class.getSimpleName()+" it ");
			sb.append("WHERE su.unitLoad = ul AND ");
			sb.append("su.itemData = it ");			
			sb.append(" AND ul.storageLocation = :sloc ");
			
			if(!getCallersUser().getClient().isSystemClient()){
				sb.append("AND su.client =:cl ");
			}
			
			i=0;
			if (detail.getOrderBy() != null){
				for (OrderByToken tok : detail.getOrderBy()){
					if (i==0) {
						sb.append(" ORDER BY ");
						sb.append(tok.getAttribute() + " " + (tok.isAscending()?"ASC":"DESC"));
					} else{
						sb.append(" AND " + tok.getAttribute() + " " + (tok.isAscending()?"ASC":"DESC"));
					}
					i++;
				}
			}
			
			logger.info("--- Querying stocks by storagelocation : "+sb.toString());
			Query query = manager.createQuery(sb.toString());
			
			query.setParameter("sloc", storloc);
			
			if(!getCallersUser().getClient().isSystemClient()){
				query.setParameter("cl", getCallersUser().getClient());
			}
			
			query.setMaxResults(detail.getMaxResults());
			query.setFirstResult(detail.getStartResultIndex());
			
			ret = query.getResultList();
			LOSResultList<StockUnitTO> lsoResultList = new LOSResultList<StockUnitTO>(ret);
			lsoResultList.setResultSetSize(resultSize);
			lsoResultList.setStartResultIndex(detail.getStartResultIndex());
			
			return lsoResultList;
		} catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		}
		
	}
	
	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "client.number", value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(client);

//dgrys portierung wildfly 8.2
//		TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_LIKE, getUniqueNameProp(),
//				value);
//		idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken item = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.number",
				value);
		item.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken itemName = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "itemData.name",
				value);
		itemName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		TemplateQueryWhereToken lot = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "lot.name",
				value);
		lot.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken ul = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "unitLoad.labelId",
				value);
		ul.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		
		TemplateQueryWhereToken sl1 = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "unitLoad.storageLocation.name",
				value);
		sl1.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);

		//dgrys portierung wildfly 8.2
		//ret.add(idt);
		ret.add(item);
		ret.add(itemName);
		ret.add(lot);
		ret.add(ul);
		ret.add(sl1);
		
		return ret;
	}
	
    @Override
	protected List<TemplateQueryWhereToken> getFilterTokens(String filterString) {

		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		TemplateQueryWhereToken token;

		if( "AVAILABLE".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", 0);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "unitLoad.lock", 0);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_GREATER, "amount", BigDecimal.ZERO);
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		if( "OUT".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", StockUnitLockState.PICKED_FOR_GOODSOUT.getLock());
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		if( "QS".equals(filterString) ) {
			token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "lock", StockUnitLockState.QUALITY_FAULT.getLock());
			token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_AND);
			ret.add(token);
		}
		
		return ret;
	}
    
	@Override
	public LOSResultList<StockUnitTO> queryByDefault(
			BODTO<Client> client, 
			BODTO<Lot> lot,
			BODTO<ItemData> itemData,
			BODTO<LOSStorageLocation> storageLocation,
			QueryDetail detail) throws BusinessObjectNotFoundException, BusinessObjectQueryException{
		return queryByParameter(client, lot, itemData, storageLocation, detail);
		
	}
	//dgrys portierung wildfly 8.2
//	@Override
//	public LOSResultList<StockUnit> queryByLabelId( QueryDetail detail, String suId)
//			throws BusinessObjectNotFoundException, BusinessObjectQueryException {
//		TemplateQuery q = new TemplateQuery();
//		q.setBoClass(tClass);
//		TemplateQueryWhereToken l = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_EQUAL, "labelId", suId);
//		q.addWhereToken(l);
//		
//		return queryByTemplate(detail, q);
//	}


	@SuppressWarnings("unchecked")
	public LOSResultList<StockUnitTO> queryByParameter(
			BODTO<Client> client, 
			BODTO<Lot> lot,
			BODTO<ItemData> itemData,
			BODTO<LOSStorageLocation> storageLocation,
			QueryDetail detail) throws BusinessObjectQueryException{
		try{
			
			StringBuffer sb = new StringBuffer();
			sb.append(" FROM " + StockUnit.class.getName()+" su  ");
			sb.append(" LEFT OUTER JOIN su.lot AS l, ");
			sb.append(UnitLoad.class.getSimpleName()+" ul, ");
			sb.append(ItemData.class.getSimpleName()+" it ");
			sb.append("WHERE su.unitLoad = ul AND ");
			sb.append("su.itemData = it ");
			
			if(!getCallersUser().getClient().isSystemClient()){
				sb.append("AND su.client =:cl ");
			}
			
			sb.append(" AND su.amount>0 ");
			sb.append("   AND su.lock!=" + BusinessObjectLockState.GOING_TO_DELETE.getLock());
		
			if( storageLocation != null ) {
				sb.append(" AND ul.storageLocation.id="+storageLocation.getId());
			}
			if( itemData != null ) {
				sb.append(" AND it.id="+itemData.getId());
			}
			if( lot != null ) {
				sb.append(" AND su.lot.id="+lot.getId());
			}
			if( client != null ) {
				sb.append(" AND su.client.id="+client.getId());
			}
			String queryStr1 = sb.toString();
			
			
			List<StockUnitTO> ret = new ArrayList<StockUnitTO>();
			sb = new StringBuffer(" SELECT COUNT(su) ");
			sb.append(queryStr1);

			Query countQ = manager.createQuery(sb.toString());
			if(!getCallersUser().getClient().isSystemClient()){
				countQ.setParameter("cl", getCallersUser().getClient());
			}
			
			int resultSize = ((Long) countQ.getSingleResult()).intValue();
	
			sb = new StringBuffer();
			sb.append(" SELECT NEW ");
			sb.append(StockUnitTO.class.getName());
			sb.append("(");			
			sb.append("su.id,");
			sb.append("su.version,");
			sb.append("su.labelId,");
			sb.append("su.lock,");
			sb.append("su.lot,");
			sb.append("it.number,");
			sb.append("it.name,");
			sb.append("ul.version,");
			sb.append("ul.labelId,");
			sb.append("ul.storageLocation.name,");
			sb.append("su.amount,");
			sb.append("su.reservedAmount,");
			sb.append("it.scale )");
						
			sb.append(queryStr1);
			
			int i=0;
			if (detail.getOrderBy() != null && detail.getOrderBy().size()>0){
				for (OrderByToken tok : detail.getOrderBy()){
					if (i==0) {
						sb.append(" ORDER BY ");
						sb.append("su."+tok.getAttribute() + " " + (tok.isAscending()?"ASC":"DESC"));
					} else{
						sb.append(" AND " + tok.getAttribute() + " " + (tok.isAscending()?"ASC":"DESC"));
					}
					i++;
				}
			}
			else {
				sb.append(" ORDER BY it.number, ul.storageLocation.name, ul.labelId, su.id ");
			}
			logger.info("--- Querying stocks by parameter : "+sb.toString());
			Query query = manager.createQuery(sb.toString());
			query.setMaxResults(detail.getMaxResults());
			query.setFirstResult(detail.getStartResultIndex());
			
			if(!getCallersUser().getClient().isSystemClient()){
				query.setParameter("cl", getCallersUser().getClient());
			}
			
			ret = query.getResultList();
			LOSResultList<StockUnitTO> lsoResultList = new LOSResultList<StockUnitTO>(ret);
			lsoResultList.setResultSetSize(resultSize);
			lsoResultList.setStartResultIndex(detail.getStartResultIndex());
			
			return lsoResultList;
		} catch (Throwable t){
			logger.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		}
		
	}

	private String getWhereStatement(TemplateQueryWhereToken t){
    	StringBuffer s = new StringBuffer();
    	if (t.getOperator().equals(TemplateQueryWhereToken.OPERATOR_CONTAINS) || t.getOperator().equals(TemplateQueryWhereToken.OPERATOR_CONTAINS_NOT)) {
            s.append(":");
            s.append((t.getParameterName()));
            s.append(" ");
            s.append(t.getOperator());
            s.append(" ");
            s.append(" su.");
            s.append(t.getParameter());
            s.append(" ");
        } else if (t.getOperator().equals(TemplateQueryWhereToken.OPERATOR_LIKE)) {
            s.append(" LOWER (su.");
            s.append(t.getParameter());
            s.append(") ");
            s.append(t.getOperator());
            s.append(" :");
            s.append((t.getParameterName()));
            s.append(" ");
        } 
        else if(t.getOperator().equals(TemplateQueryWhereToken.OPERATOR_AT)
        		&& t.getValue() instanceof Date)
        {
        	s.append(" su.");
        	s.append(t.getParameter());
        	s.append(" BETWEEN :").append(t.getParameterName()+"1 ");
        	s.append("AND :").append(t.getParameterName()+"2 ");
        }
        else {
            s.append(" su.");
            s.append(t.getParameter());
            s.append(" ");
            s.append(t.getOperator());
            s.append(" ");
            if (!t.isUnaryOperator()) {
                s.append(":");
                s.append((t.getParameterName()));
                s.append(" ");
            }
        }
    	return new String(s);
    }
	
	void transformLikeParameter(TemplateQueryWhereToken tok) {

		String val;
		if (!tok.getOperator().equals(TemplateQueryWhereToken.OPERATOR_LIKE)) {
			throw new IllegalArgumentException("Only Like operators allowed");
		}
		val = (String) tok.getValue();
		tok.setValue(TemplateQueryWhereToken.transformLikeParam(val));
	}
 
}
