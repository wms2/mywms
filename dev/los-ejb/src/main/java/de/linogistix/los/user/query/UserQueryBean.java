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

package de.linogistix.los.user.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import org.mywms.model.User;

import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQuery;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class UserQueryBean extends BusinessObjectQueryBean<User> implements
		UserQueryRemote {

	/**
	 * @param userName may contain * wildcards
	 * 
	 */
	public List<User> queryByName(String userName, QueryDetail detail) {
		TemplateQuery q;
		TemplateQueryWhereToken t;

		try {

			if (userName == null) throw new NullPointerException();
			

//			if (detail == null){
//				detail = new QueryDetail();
//				detail.setMaxResults(50);
//				List<OrderByToken> ol = new ArrayList<OrderByToken>();
//				OrderByToken ot = new OrderByToken("name", true);
//				ol.add(ot);
//				detail.setOrderBy(ol);
//			}
			
			q = new TemplateQuery();
			q.setBoClass(User.class);
			
			t = new TemplateQueryWhereToken();
			t.setOperator(TemplateQueryWhereToken.OPERATOR_LIKE);
			t.setParameter("name");
			t.setValue(userName);
			List<TemplateQueryWhereToken> l = new ArrayList<TemplateQueryWhereToken>();
			l.add(t);

			q.setWhereTokens(l);

			return queryByTemplate(detail, q);
			
		} catch (Throwable e) {
			return new ArrayList<User>();
		}

	}

    @Override
    public String getUniqueNameProp() {
        return "name";
    }
    
   

}
