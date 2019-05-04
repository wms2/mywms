/*
 * Copyright (c) 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.model.BasicClientAssignedEntity;
import org.mywms.model.Client;
import org.mywms.model.ItemDataNumber;
import org.mywms.model.User;

import de.linogistix.los.inventory.model.LOSBom;
import de.linogistix.los.inventory.query.dto.LOSBomTO;
import de.linogistix.los.inventory.service.ItemDataNumberService;
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
 * @author krane
 *
 */
@Stateless
public class LOSBomQueryBean extends BusinessObjectQueryBean<LOSBom> implements LOSBomQueryRemote{
	Logger log = Logger.getLogger(LOSBomQueryBean.class);

	@EJB
	ItemDataNumberService idnService;

	private static final String[] dtoProps = new String[] { 
		"id", "version", "id",  
		"parent.client.number",
		"parent.number",
		"parent.name",
		"child.number",
		"child.name",
		"amount",
		"index",
		"pickable",
		"child.scale"
		};

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	@Override
	public Class<LOSBomTO> getBODTOClass() {
		return LOSBomTO.class;
	}

	public LOSResultList<BODTO<LOSBom>> queryByDefault( String master, String child, QueryDetail detail ) throws BusinessObjectNotFoundException, BusinessObjectQueryException{

		TemplateQuery q = new TemplateQuery();
		q.setBoClass(LOSBom.class);


		if( master != null && master.length() > 0 ) {
			TemplateQueryFilter filter = q.addNewFilter();
			
			TemplateQueryWhereToken t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "parent.number", master);
			t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			filter.addWhereToken(t);
			
			t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "parent.name", master);
			t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			filter.addWhereToken(t);
			
			t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "parent.client.number", master);
			t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			filter.addWhereToken(t);
			
			ItemDataNumber idn = idnService.getByNumber(master);
			if( idn != null ) {
				t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "parent.id", idn.getItemData().getId());
				t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
				filter.addWhereToken(t);
			}
		}
		if( child != null && child.length() > 0 ) {
			TemplateQueryFilter filter = q.addNewFilter();

			TemplateQueryWhereToken t= new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "child.number", child);
			t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			filter.addWhereToken(t);
			
			t= new TemplateQueryWhereToken(	TemplateQueryWhereToken.OPERATOR_LIKE, "child.name", child);
			t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			filter.addWhereToken(t);
			
			t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "child.client.number", child);
			t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			filter.addWhereToken(t);
			
			ItemDataNumber idn = idnService.getByNumber(child);
			if( idn != null ) {
				t = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "child.id", idn.getItemData().getId());
				t.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
				filter.addWhereToken(t);
			}

		}
		
		try {
			return queryByTemplateHandles(detail, q);
		} catch (BusinessObjectNotFoundException bex) {
			return new LOSResultList<BODTO<LOSBom>>();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		}

	}

    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
    	Long id;
		try{
			id = Long.parseLong(value);
		} catch (Throwable t){
			id = new Long(-1);
		}
		TemplateQueryWhereToken idt = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", id);
		idt.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(idt);

		TemplateQueryWhereToken parent= new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "parent.number",
				value);
		parent.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(parent);
		parent= new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "parent.name",
				value);
		parent.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(parent);

		TemplateQueryWhereToken child = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "child.number",
				value);
		child.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(child);
		child = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "child.name",
				value);
		child.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(child);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "parent.client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(client);

		// I think, that it is not possible make this in one query with JPA and Hibernate 3.1
		ItemDataNumber idn = idnService.getByNumber(value);
		if( idn != null ) {
			TemplateQueryWhereToken numbers = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_EQUAL, "child.id", idn.getItemData().getId());
			numbers.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(numbers);
			numbers = new TemplateQueryWhereToken(
					TemplateQueryWhereToken.OPERATOR_EQUAL, "parent.id", idn.getItemData().getId());
			numbers.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
			ret.add(numbers);
		}

		return ret;
	}

    
	// ------------------------------------------------------------------------------
    // In this copy of the BusinessObjectQueryBean method, only the ORDERBY section has been changed
    // If sometime, the order is configurable, it can be ported back.
	@SuppressWarnings("unchecked")
	public LOSResultList<LOSBom> queryByTemplate(QueryDetail detail,
			TemplateQuery query) throws BusinessObjectNotFoundException,
			BusinessObjectQueryException {
		List<LOSBom> ret;
		Query q;
		Client callersClient;
		User user;

		long start = System.currentTimeMillis();

		if (detail == null) {
			log.error("detail must not be null");
			throw new NullPointerException();
		} else if (query == null) {
			log.error("query must not be null");
			throw new NullPointerException();
		}

		try {

			user = getCallersUser();

			if (user != null && (!user.getClient().isSystemClient())) {
				// restrict to result set belonging to this client
				callersClient = user.getClient();
				if (callersClient != null) {
					if (BasicClientAssignedEntity.class
							.isAssignableFrom(tClass)) {
						query.addNewFilter().addWhereToken(new TemplateQueryWhereToken(
								TemplateQueryWhereToken.OPERATOR_EQUAL,
								"client.id", callersClient.getId()));
					} else if (Client.class.isAssignableFrom(tClass)){
						query.addNewFilter().addWhereToken(new TemplateQueryWhereToken(
								TemplateQueryWhereToken.OPERATOR_EQUAL,
								"id", callersClient.getId()));
					}
				} else {
					throw new NullPointerException(
							"user without client is not allwoed");
				}
			}

			StringBuffer s = new StringBuffer(query.getStatement());

			if (detail.getOrderBy() != null && detail.getOrderBy().size() != 0) {
				s.append(" ORDER BY ");
				for (Iterator<?> it = detail.getOrderBy().iterator(); it.hasNext();) {
					OrderByToken t = (OrderByToken) it.next();
					s.append("o.");
					s.append(t.getAttribute());
					s.append(" ");
					if (!t.isAscending()) {
						s.append("DESC ");
					}
					s.append(",");
				}
				s.append(" o.parent.number, o.index, o.id ");
			} else {
				s.append(" ORDER BY o.parent.number, o.index, o.id ");
			}

			String qs = new String(s);
			log.debug("Query String: " + qs);

			q = manager.createQuery(qs);
			Query countQuery = manager.createQuery(query.getCountStatement());

			if (query.getWhereTokens() != null) {
				for (TemplateQueryWhereToken wt : query.getWhereTokens()) {
					if (wt != null) {
						if (wt.getOperator().equals(
								TemplateQueryWhereToken.OPERATOR_LIKE)) {
							wt.transformLikeParameter();
						}
						if (wt.getOperator().equals(
								TemplateQueryWhereToken.OPERATOR_FALSE)
								|| wt.getOperator().equals(
										TemplateQueryWhereToken.OPERATOR_TRUE)) {
							continue;
						}
						if(wt.getOperator().equals(TemplateQueryWhereToken.OPERATOR_ISEMPTY)
							|| wt.getOperator().equals(TemplateQueryWhereToken.OPERATOR_ISNOTEMPTY)){
							continue;
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
						else{
							q.setParameter(wt.getParameterName(), wt.getValue());
							countQuery.setParameter(wt.getParameterName(), wt.getValue());
						}
					}
				}
			}

			if (detail != null) {
				q = q.setFirstResult(detail.getStartResultIndex());
				q = q.setMaxResults(detail.getMaxResults());
				// q = q.setFirstResult(detail.getStartIndex());
			} else {
				log.error("--- !!!     No QueryDetail     !!! ---");
			}

			ret = q.getResultList();

			if (ret == null) {
				throw new BusinessObjectNotFoundException();
			}

			int resultSize = ((Long) countQuery.getSingleResult()).intValue();

			log.debug("--- Result size : " + resultSize);

			LOSResultList<LOSBom> resultList = new LOSResultList<LOSBom>(ret);
			resultList.setResultSetSize(resultSize);
			resultList.setStartResultIndex(detail.getStartResultIndex());
			enrichResultSet(resultList);
			return resultList;

		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new BusinessObjectQueryException();
		} finally {
			long stop = System.currentTimeMillis();
			log.debug("query took " + (stop - start));
		}
	}

}
