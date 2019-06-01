/* 
Copyright 2019 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.linogistix.inventory.browser.masternode;

import de.linogistix.common.bobrowser.bo.BOMasterNode;
import de.linogistix.common.bobrowser.bo.BO;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.PackagingUnitTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 * @author krane
 */
public class BOPackagingUnitMasterNode extends BOMasterNode {

    PackagingUnitTO to;

    /** Creates a new instance of BODeviceNode */
    public BOPackagingUnitMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (PackagingUnitTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> number = new BOMasterNodeProperty<String>("itemDataNumber", String.class, to.getItemDataNumber(), InventoryBundleResolver.class);
            sheet.put(number);
            BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, to.getItemDataName(), InventoryBundleResolver.class);
            sheet.put(itemDataName);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> number = new BOMasterNodeProperty<String>("itemDataNumber", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> itemDataName = new BOMasterNodeProperty<String>("itemDataName", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            number, itemDataName
        };

        return props;
    }
}
