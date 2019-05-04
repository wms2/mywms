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
import de.linogistix.los.query.BODTO;

import de.linogistix.los.location.query.dto.LOSWorkingAreaPositionTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 * @author krane
 */
public class BOLOSWorkingAreaPositionMasterNode extends BOMasterNode {

    LOSWorkingAreaPositionTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSWorkingAreaPositionMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSWorkingAreaPositionTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();

            BOMasterNodeProperty<String> workingAreaName = new BOMasterNodeProperty<String>("workingAreaName", String.class, to.getWorkingAreaName(), CommonBundleResolver.class);
            sheet.put(workingAreaName);
            BOMasterNodeProperty<String> clusterName = new BOMasterNodeProperty<String>("clusterName", String.class, to.getClusterName(), CommonBundleResolver.class);
            sheet.put(clusterName);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> workingAreaName = new BOMasterNodeProperty<String>("workingAreaName", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> clusterName = new BOMasterNodeProperty<String>("clusterName", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            workingAreaName, clusterName
        };

        return props;
    }
}
