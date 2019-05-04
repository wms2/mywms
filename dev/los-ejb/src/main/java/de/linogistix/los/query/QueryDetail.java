/*
 * QueryDetail.java
 *
 * Created on 7. Oktober 2006, 23:52
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used in BusinessObject queries. Defines maximum number of results and a list of 
 * tokens which are used to order the result set.
 *  
 * @see TemplateQuery
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class QueryDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<OrderByToken> orderBy = new ArrayList<OrderByToken>();
    private int maxResults = 50;
    
    private int startResultIndex = 0;

    /**
     * 
     * @param maxResults max. size of result set in {@link BusinessObjectQueryRemote} 
     */
    public QueryDetail(int startIndex, int maxResults) {
        this.maxResults = maxResults;
        this.startResultIndex = startIndex;
        this.orderBy = new ArrayList<OrderByToken>();
    }

    /**
     * 
     * @param maxResults max. size of result set in {@link BusinessObjectQueryRemote} 
     * @param orderByParam name of a property that is used for ordering
     * @param ascending if true order result set ascending, otherwise descending
     */
    public QueryDetail(int startIndex, int maxResults, String orderByParam, boolean ascending) {
        List<OrderByToken> l = new ArrayList<OrderByToken>();
        this.maxResults = maxResults;
        this.startResultIndex = startIndex;
        OrderByToken tok = new OrderByToken(orderByParam, ascending);
        l.add(tok);
        this.orderBy = l;
    }
    
    public void addOrderByToken(String orderByParam, boolean ascending){

    	
        if (this.orderBy == null){
            this.orderBy = new ArrayList<OrderByToken>();
        }
        
        OrderByToken tok = new OrderByToken(orderByParam, ascending);

        orderBy.add(tok);
    }
    
    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getStartResultIndex() {
		return startResultIndex;
	}

	public void setStartResultIndex(int startResultIndex) {
            this.startResultIndex = startResultIndex;
	}


   public List<OrderByToken> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<OrderByToken> orderBy) {
        this.orderBy = orderBy;
    }


}
