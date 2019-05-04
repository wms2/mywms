/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;

import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.los.query.dto.LOSSystemPropertyTO;
import de.linogistix.los.query.BODTO;

import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSSystemPropertyMasterNode extends BOMasterNode {

    LOSSystemPropertyTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSSystemPropertyMasterNode(BODTO d) throws IntrospectionException {
        super(d);
        to = (LOSSystemPropertyTO) d;
    }

    /** Creates a new instance of BODeviceNode */
    public BOLOSSystemPropertyMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (LOSSystemPropertyTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {

            sheet = new Sheet.Set();

            BOMasterNodeProperty<String> value = new BOMasterNodeProperty<String>("value", String.class, to.getValue(), CommonBundleResolver.class);
            sheet.put(value);
            BOMasterNodeProperty<String> workstation = new BOMasterNodeProperty<String>("workstation", String.class, to.getWorkstation(), CommonBundleResolver.class);
            sheet.put(workstation);


        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> value = new BOMasterNodeProperty<String>("value", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> workstation = new BOMasterNodeProperty<String>("workstation", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            value, workstation
        };

        return props;
    }
}
