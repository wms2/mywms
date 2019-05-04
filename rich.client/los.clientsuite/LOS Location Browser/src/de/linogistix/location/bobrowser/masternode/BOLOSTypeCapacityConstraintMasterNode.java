/*
 * Copyright (c) 2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.location.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.*;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.location.res.LocationBundleResolver;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.location.query.dto.LOSTypeCapacityConstraintTO;

import java.beans.IntrospectionException;
import java.math.BigDecimal;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 * @author krane
 */
public class BOLOSTypeCapacityConstraintMasterNode extends BOMasterNode {

    LOSTypeCapacityConstraintTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSTypeCapacityConstraintMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSTypeCapacityConstraintTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();

            BOMasterNodeProperty<String> locationTypeName = new BOMasterNodeProperty<String>("locationTypeName", String.class, to.getLocationTypeName(), LocationBundleResolver.class);
            sheet.put(locationTypeName);
            BOMasterNodeProperty<String> unitLoadTypeName = new BOMasterNodeProperty<String>("unitLoadTypeName", String.class, to.getUnitLoadTypeName(), LocationBundleResolver.class);
            sheet.put(unitLoadTypeName);
            BOMasterNodeProperty<BigDecimal> allocation = new BOMasterNodeProperty<BigDecimal>("allocation", BigDecimal.class, to.getAllocation(), CommonBundleResolver.class);
            sheet.put(allocation);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> locationTypeName = new BOMasterNodeProperty<String>("locationTypeName", String.class, "", LocationBundleResolver.class);
        BOMasterNodeProperty<String> unitLoadTypeName = new BOMasterNodeProperty<String>("unitLoadTypeName", String.class, "", LocationBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> allocation = new BOMasterNodeProperty<BigDecimal>("allocation", BigDecimal.class, BigDecimal.ZERO, CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            locationTypeName, unitLoadTypeName, allocation
        };

        return props;
    }
}
