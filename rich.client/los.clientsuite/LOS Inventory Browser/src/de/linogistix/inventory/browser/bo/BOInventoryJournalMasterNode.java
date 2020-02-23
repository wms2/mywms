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
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;

import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.dto.InventoryJournalTO;
import de.linogistix.los.query.BODTO;
import java.beans.IntrospectionException;
import java.math.BigDecimal;
import java.util.Date;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.Sheet;

/**
 *
 * @author krane
 */
public class BOInventoryJournalMasterNode extends BOMasterNode {

    InventoryJournalTO recordTo;

    /** Creates a new instance of BODeviceNode */
    public BOInventoryJournalMasterNode(BODTO d, BO bo) throws IntrospectionException {
        super(d, bo);
        recordTo = (InventoryJournalTO) d;
  
    }

    @Override
    public String getHtmlDisplayName() {
        String ret = getDisplayName();
        return ret;
    }

    @Override
    public String getDisplayName() {
        return recordTo.getName();
    }

    @Override
    public PropertySet[] getPropertySets() {

        if (sheet == null) {
            sheet = new Sheet.Set();
            String typeStr = BundleResolve.resolve(new Class[]{bo.getBundleResolver(),InventoryBundleResolver.class},"recordType."+recordTo.getType(), new Object[0], false);
            if( typeStr == null || typeStr.length()==0 ) {
                typeStr = String.valueOf(recordTo.getType());
            }
            BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, typeStr, InventoryBundleResolver.class);
            sheet.put(type);
            BOMasterNodeProperty<String> activityCode = new BOMasterNodeProperty<String>("activityCode", String.class, recordTo.getActivityCode(), InventoryBundleResolver.class);
            sheet.put(activityCode);
            BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, recordTo.getAmount(), InventoryBundleResolver.class);
            sheet.put(amount);
            BOMasterNodeProperty<BigDecimal> amountStock = new BOMasterNodeProperty<BigDecimal>("amountStock", BigDecimal.class, recordTo.getAmountStock(), InventoryBundleResolver.class);
            sheet.put(amountStock);
            BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, recordTo.getItemData(), InventoryBundleResolver.class);
            sheet.put(itemData);
            BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, recordTo.getLot(), InventoryBundleResolver.class);
            sheet.put(lot);
            BOMasterNodeProperty<String> toSl = new BOMasterNodeProperty<String>("toSl", String.class, recordTo.getToSl(), InventoryBundleResolver.class);
            sheet.put(toSl);
            BOMasterNodeProperty<String> toUl = new BOMasterNodeProperty<String>("toUl", String.class, recordTo.getToUl(), InventoryBundleResolver.class);
            sheet.put(toUl);
            BOMasterNodeProperty<Date> recordDate = new BOMasterNodeProperty<Date>("recordDate", Date.class, recordTo.getRecordDate(), CommonBundleResolver.class);
            sheet.put(recordDate);
        }
        return new PropertySet[]{sheet};
    }

    //-------------------------------------------------------------------------
    public static Property[] boMasterNodeProperties() {

        BOMasterNodeProperty<String> type = new BOMasterNodeProperty<String>("type", String.class, "", InventoryBundleResolver.class, true);
        BOMasterNodeProperty<String> activityCode = new BOMasterNodeProperty<String>("activityCode", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> itemData = new BOMasterNodeProperty<String>("itemData", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> lot = new BOMasterNodeProperty<String>("lot", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> amount = new BOMasterNodeProperty<BigDecimal>("amount", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);
        BOMasterNodeProperty<BigDecimal> amountStock = new BOMasterNodeProperty<BigDecimal>("amountStock", BigDecimal.class, new BigDecimal(0), InventoryBundleResolver.class);
         
        BOMasterNodeProperty<String> toSl = new BOMasterNodeProperty<String>("toSl", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<String> toUl = new BOMasterNodeProperty<String>("toUl", String.class, "", InventoryBundleResolver.class);
        BOMasterNodeProperty<Date> recordDate = new BOMasterNodeProperty<Date>("recordDate", Date.class, new Date(), CommonBundleResolver.class);

        
        BOMasterNodeProperty[] props = new BOMasterNodeProperty[]{
            type,
            activityCode, 
            itemData,
            lot, 
            amount, amountStock,
            toSl, toUl, recordDate
        };

        return props;
    }
}
