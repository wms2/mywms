/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TemplateQueryFilter implements Serializable {
	private static final long serialVersionUID = 1L;
	
    protected List<TemplateQueryWhereToken> whereTokens = new ArrayList<TemplateQueryWhereToken>();

	public List<TemplateQueryWhereToken> getWhereTokens() {
		return whereTokens;
	}

	public void setWhereTokens(List<TemplateQueryWhereToken> whereTokens) {
		this.whereTokens = whereTokens;
	}

    public void addWhereToken(TemplateQueryWhereToken token) {
        whereTokens.add(token);
    }

    public String getFilterStatement() {
    	StringBuffer where = new StringBuffer();
    	boolean isNew = true;
    	
        if (getWhereTokens() != null && getWhereTokens().size() > 0) {
        	where.append(" ( ");
            for (Iterator<TemplateQueryWhereToken> it = getWhereTokens().iterator(); it.hasNext();) {
                TemplateQueryWhereToken t = (TemplateQueryWhereToken) it.next();
                
                if( isNew ) {
                	isNew = false;
                }
                else {
	                where.append(t.getLogicalOperator());
	                where.append(" ");
                }
                where.append(t.getWhereStatement());
                where.append(" ");
            }
        	where.append(" ) ");
        }

    	return where.toString();
    }

}
