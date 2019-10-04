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
import de.linogistix.los.inventory.query.dto.AddressTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 * A {@link BOMasterDevice} for BasicEnity {@link Device}.
 *
 * @author trautm
 */
public class BOAddressMasterNode extends BOMasterNode {

    AddressTO to;

    /** Creates a new instance of BODeviceNode */
    public BOAddressMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        to = (AddressTO) d;
    }


    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            BOMasterNodeProperty<String> name = new BOMasterNodeProperty<String>("name", String.class, to.getName(), InventoryBundleResolver.class);
            sheet.put(name);
        }
        return new PropertySet[]{};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {
        BOMasterNodeProperty<String> name = new BOMasterNodeProperty<String>("name", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            
        };

        return props;
    }
}
