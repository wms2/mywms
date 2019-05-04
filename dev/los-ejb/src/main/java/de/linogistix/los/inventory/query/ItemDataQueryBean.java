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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.ItemDataNumber;
import org.mywms.model.Lot;

import de.linogistix.los.inventory.query.dto.ItemDataTO;
import de.linogistix.los.inventory.service.ItemDataNumberService;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
@Stateless
public class ItemDataQueryBean extends BusinessObjectQueryBean<ItemData> implements ItemDataQueryRemote{

	private static final Logger log = Logger.getLogger(ItemDataQueryBean.class);
	
	@EJB
	ItemDataNumberService idnService;
    
	@Override
    public String getUniqueNameProp() {
        return "number";
    }
 
	private static final String[] dtoProps = new String[] { "id", "version", "number",  
		"name",
		"client.number",
		"handlingUnit.unitName"};

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	public Class<ItemDataTO> getBODTOClass() {
		return ItemDataTO.class;
	}

    @Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret =  new ArrayList<TemplateQueryWhereToken>();
		
		TemplateQueryWhereToken number = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "number",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(number);

		TemplateQueryWhereToken name = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "name",
				value);
		name.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(name);
		
		TemplateQueryWhereToken client = new TemplateQueryWhereToken(
				TemplateQueryWhereToken.OPERATOR_LIKE, "client.number",
				value);
		client.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(client);
		
		// I think, that it is not possible make this in one query with JPA and Hibernate 3.1
		List<ItemDataNumber> idnList = idnService.getListByNumber(null,value);
		if( idnList != null && idnList.size()>0 ) {
			int i=0;
			for( ItemDataNumber idn : idnList ) {
				TemplateQueryWhereToken token;
				token  = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_EQUAL, "id", idn.getItemData().getId());
				token .setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
				token .setParameterName("itemDataId"+i++);
				ret.add(token);
			}
		}
//		ItemDataNumber idn = idnService.getByNumber(value);
//		if( idn != null ) {
//			TemplateQueryWhereToken numbers = new TemplateQueryWhereToken(
//				TemplateQueryWhereToken.OPERATOR_EQUAL, "id", idn.getItemData().getId());
//			numbers.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
//			ret.add(numbers);
//		}
		
		return ret;
	}

    public LOSResultList<BODTO<ItemData>> autoCompletionClientAndLot(String exp, 
    																 BODTO<Client> client, 
    																 BODTO<Lot> lot,
    																 QueryDetail detail) {
        try {

            Lot l;
            LOSResultList<BODTO<ItemData>> ret;
            
            Client cl = null;
    		if(client != null){
    			cl = manager.find(Client.class, client.getId());
    		}

            if (lot != null) {
                l = manager.find(Lot.class, lot.getId());
                ret = new LOSResultList<BODTO<ItemData>>();
                ret.add(new BODTO<ItemData>(l.getItemData().getId(), l.getItemData().getVersion(), l.getItemData().getNumber()));
                ret.setResultSetSize(1L);
                ret.setStartResultIndex(0L);
                return ret;
            } else {
            	return this.autoCompletion(exp, null, cl, new TemplateQueryWhereToken[0], detail, false);
            }

        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            return new LOSResultList<BODTO<ItemData>>();
        }

    }


    
}
