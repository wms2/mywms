/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.location.bobrowser.masternode;

import de.linogistix.common.bobrowser.bo.*;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import de.linogistix.los.query.BODTO;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.location.query.dto.StorageLocationTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOStorageLocationMasterNode extends BOMasterNode {

    StorageLocationTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOStorageLocationMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (StorageLocationTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.getLock() == BusinessObjectLockState.GOING_TO_DELETE.getLock()) {
            ret = "<font color=\"#C0C0C0\"><s>" + ret + "</s></font>";
        }
        
        return ret;
    }

//    @Override
//    public Image getIcon(int arg0) {
//        if (to.lock > 0){
//            ImageIcon img = new ImageIcon(super.getIcon(arg0));
//            LockedIcon decorated = new LockedIcon(img);
//            return decorated.getImage();
//        } else {
//            return super.getIcon(arg0);
//        }
//    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();

            BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, to.getClientNumber(), CommonBundleResolver.class);
            sheet.put(clientNumber);
            BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, to.getType(), CommonBundleResolver.class);
            sheet.put(type);
            BOMasterNodeProperty<String> area = new BOMasterNodeProperty<String>("area", String.class, to.getArea(), CommonBundleResolver.class);
            sheet.put(area);
            BOMasterNodeProperty<String> zone = new BOMasterNodeProperty<String>("zone", String.class, to.getZone(), CommonBundleResolver.class);
            sheet.put(zone);

            String strLock = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),CommonBundleResolver.class},"lock."+to.getLock(), new Object[0], false);
            if( strLock == null || strLock.length()==0 ) {
                strLock = String.valueOf(to.getLock());
            }
            BOMasterNodeProperty<String> lock = new BOMasterNodeProperty<String>("lock", String.class, strLock, CommonBundleResolver.class);
            sheet.put(lock);

        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> clientNumber = new BOMasterNodeProperty<String>("clientNumber", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> area = new BOMasterNodeProperty<String>("area", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> zone = new BOMasterNodeProperty<String>("zone", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> lock = new BOMasterNodeProperty<String>("lock", String.class, "", CommonBundleResolver.class);

        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            clientNumber, type, area, zone, lock
        };

        return props;
    }
}
