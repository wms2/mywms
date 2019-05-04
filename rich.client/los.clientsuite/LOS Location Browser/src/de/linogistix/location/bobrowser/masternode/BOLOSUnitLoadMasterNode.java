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
import de.linogistix.location.res.LocationBundleResolver;
import de.linogistix.los.query.BODTO;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.location.query.dto.UnitLoadTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOLOSUnitLoadMasterNode extends BOMasterNode {

    UnitLoadTO to;

    /** Creates a new instance of BODeviceNode */
    public BOLOSUnitLoadMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (UnitLoadTO) d;
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        
        if (to.getLock() == BusinessObjectLockState.GOING_TO_DELETE.getLock()) {
            ret = "<font color=\"#C0C0C0\"><s>" + ret + "</s></font>";
        }
        
        return ret;
    }

//     @Override
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
            BOMasterNodeProperty<String> typeName = new BOMasterNodeProperty<String>("typeName", String.class, to.getTypeName(), LocationBundleResolver.class);
            sheet.put(typeName);
            BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, to.getStorageLocation(), CommonBundleResolver.class);
            sheet.put(storageLocation);

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
        BOMasterNodeProperty<String> typeName = new BOMasterNodeProperty<String>("typeName", String.class, "", LocationBundleResolver.class);
        BOMasterNodeProperty<String> storageLocation = new BOMasterNodeProperty<String>("storageLocation", String.class, "", CommonBundleResolver.class);
        BOMasterNodeProperty<String> lock = new BOMasterNodeProperty<String>("lock", String.class, "", CommonBundleResolver.class);
        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            clientNumber, typeName, storageLocation, lock
        };

        return props;
    }
}
