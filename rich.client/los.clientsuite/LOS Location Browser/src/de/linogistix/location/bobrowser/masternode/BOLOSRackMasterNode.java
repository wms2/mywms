/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.location.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.location.res.LocationBundleResolver;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.query.dto.LOSRackTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Sheet;

/**
 *
 * @author Jordan
 */
public class BOLOSRackMasterNode extends BOMasterNode{

    private LOSRackTO to;
    
    public BOLOSRackMasterNode(BODTO d, BO bo)throws IntrospectionException
    {
        super(d, bo);
        to = (LOSRackTO) d;
    }
    
    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.lock != LOSStorageLocationLockState.NOT_LOCKED) {
            ret = "<font color=\"#CC0000\">" + ret + "</font>";
        }
        
        return ret;
    }
    
    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> aisle = new BOMasterNodeProperty<String>("aisle", String.class, to.getAisle(), CommonBundleResolver.class);
            sheet.put(aisle);
            BOMasterNodeProperty<Integer> numLocation = new BOMasterNodeProperty<Integer>("numLocation", Integer.class, to.getNumLocation(), LocationBundleResolver.class);
            sheet.put(numLocation);
            BOMasterNodeProperty<Integer> locationIndexMin = new BOMasterNodeProperty<Integer>("locationIndexMin", Integer.class, to.getLocationIndexMin(), LocationBundleResolver.class);
            sheet.put(locationIndexMin);
            BOMasterNodeProperty<Integer> locationIndexMax = new BOMasterNodeProperty<Integer>("locationIndexMax", Integer.class, to.getLocationIndexMax(), LocationBundleResolver.class);
            sheet.put(locationIndexMax);
//            BOMasterNodeProperty<String> lock = new BOMasterNodeProperty<String>("lock", String.class, to.lock.getMessage(), CommonBundleResolver.class);
//            sheet.put(lock);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> aisle = new BOMasterNodeProperty<String>("aisle", String.class, null, CommonBundleResolver.class);
        BOMasterNodeProperty<Integer> numLocation = new BOMasterNodeProperty<Integer>("numLocation", Integer.class, null, LocationBundleResolver.class);
        BOMasterNodeProperty<Integer> locationIndexMin = new BOMasterNodeProperty<Integer>("locationIndexMin", Integer.class, null, LocationBundleResolver.class);
        BOMasterNodeProperty<Integer> locationIndexMax = new BOMasterNodeProperty<Integer>("locationIndexMax", Integer.class, null, LocationBundleResolver.class);
//        BOMasterNodeProperty<String> lock = new BOMasterNodeProperty<String>("lock", String.class, " ", CommonBundleResolver.class);
        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            aisle, numLocation, locationIndexMin, locationIndexMax
        };

        return props;
    }
}
