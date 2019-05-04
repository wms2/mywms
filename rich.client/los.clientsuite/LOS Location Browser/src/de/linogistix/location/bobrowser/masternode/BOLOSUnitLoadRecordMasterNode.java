/*
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.location.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.*;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.BODTO;

import de.linogistix.los.location.query.dto.LOSUnitLoadRecordTO;
import java.beans.IntrospectionException;
import java.util.Date;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSUnitLoadRecordMasterNode extends BOMasterNode {

    LOSUnitLoadRecordTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSUnitLoadRecordMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSUnitLoadRecordTO) d;
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, "LOSUnitLoadRecordType."+to.getType(), CommonBundleResolver.class, true);
            sheet.put(type);
            BOMasterNodeProperty<String> fromLocation = new BOMasterNodeProperty<String>("fromLocation", String.class, to.getFromLocation(), CommonBundleResolver.class);
            sheet.put(fromLocation);
            BOMasterNodeProperty<String> toLocation = new BOMasterNodeProperty<String>("toLocation", String.class, to.getToLocation(), CommonBundleResolver.class);
            sheet.put(toLocation);
            BOMasterNodeProperty<Date> recordDate = new BOMasterNodeProperty<Date>("recordDate", Date.class, to.getRecordDate(), CommonBundleResolver.class);
            sheet.put(recordDate);
            
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> fromLocation = new BOMasterNodeProperty<String>("fromLocation", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> toLocation = new BOMasterNodeProperty<String>("toLocation", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<Date> recordDate = new BOMasterNodeProperty<Date>("recordDate", Date.class, new Date(), CommonBundleResolver.class);
        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            type, fromLocation, toLocation, recordDate
        };

        return props;
    }
}
