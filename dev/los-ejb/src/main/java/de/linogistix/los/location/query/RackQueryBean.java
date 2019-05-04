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

package de.linogistix.los.location.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import de.linogistix.los.location.model.LOSRack;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.query.dto.LOSRackTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class RackQueryBean extends BusinessObjectQueryBean<LOSRack> implements RackQueryRemote {
	private Logger log = Logger.getLogger(RackQueryBean.class);

	private static final String[] dtoProps = new String[]{
		"id",
		"version",
		"name",
		"lock",
		"aisle"
	};
	
	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}
	
	@Override
	public Class<LOSRackTO> getBODTOClass() {
		return LOSRackTO.class;
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

		token = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "aisle", value);
		token.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(token);
		
		return ret;
	}
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    protected void enrichResultSet(LOSResultList results) {
    	super.enrichResultSet(results);
    	
		try {
			for( Object x : results ) {
				
				LOSRackTO to = (LOSRackTO)x;

				String queryStr = "SELECT min(orderIndex), max(orderIndex), count(*) FROM "+LOSStorageLocation.class.getSimpleName()+" loc WHERE loc.rack.id=:rackid";
				Query query = manager.createQuery(queryStr);
				query.setParameter("rackid", to.getId());
				Object res = query.getSingleResult();
				if( res != null ) {
					Object[] resa = (Object[])res;
					to.setLocationIndexMin( (Integer)resa[0] );
					to.setLocationIndexMax( (Integer)resa[1] );
					Long numLoc = (Long)resa[2];
					to.setNumLocation( numLoc.intValue() );
				}
			}
		}
		catch( Throwable t ) {
			log.warn("Cannot read index information: "+t.getClass().getSimpleName()+", "+t.getMessage());
		}
	}
}
