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
package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.inventory.browser.masternode.BOStockUnitRecordMasterNode;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOInventoryJournalMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.query.InventoryJournalQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.inventory.InventoryJournal;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;



/**
 *
 * @author krane
 */
public class BOInventoryJournal extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {};
    }

  protected String initName() {
    return "InventoryJournals";
  }
  
  protected String initIconBaseWithExtension() {
    
    return "de/linogistix/bobrowser/res/icon/InventoryJournal.png";
  }
  
  
  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(InventoryJournalQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
    InventoryJournal o;
    
    o = new InventoryJournal();
    
    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    return null;
  }
  
  protected Class initBundleResolver() {
    return InventoryBundleResolver.class;
  }
  
  protected String[] initIdentifiableProperties() {
    return new String[]{"id"};
  }

    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOInventoryJournalMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOInventoryJournalMasterNode.class;
    }
}
