/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.query;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ListOrderByTokenAdapter extends XmlAdapter<List<OrderByToken>, String> {

	@Override
	public List<OrderByToken> marshal(String arg0) throws Exception {
		return new ArrayList<OrderByToken>();
	}
	
	@Override
	public String unmarshal(List<OrderByToken> arg0) throws Exception {
		return "";
	}
	
}
