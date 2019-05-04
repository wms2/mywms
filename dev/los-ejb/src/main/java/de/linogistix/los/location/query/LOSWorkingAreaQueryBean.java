/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.query;

import javax.ejb.Stateless;

import de.linogistix.los.location.model.LOSWorkingArea;
import de.linogistix.los.query.BusinessObjectQueryBean;

/**
 * @author krane
 *
 */
@Stateless
public class LOSWorkingAreaQueryBean extends BusinessObjectQueryBean<LOSWorkingArea> implements LOSWorkingAreaQueryRemote {

    public String getUniqueNameProp() {
        return "name";
    }
    
}
