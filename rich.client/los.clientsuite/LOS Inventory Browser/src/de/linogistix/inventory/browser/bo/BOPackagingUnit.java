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
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOPackagingUnitMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.PackagingUnitCRUDRemote;
import de.linogistix.los.inventory.query.PackagingUnitQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.product.PackagingUnit;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author krane
 */
public class BOPackagingUnit extends BO {
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }

  protected String initName() {
    return "PackagingUnits";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ItemUnit.png";
  }

  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(PackagingUnitQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
  
  protected BasicEntity initEntityTemplate() {
      PackagingUnit o;
    
    o = new PackagingUnit();
    o.setName("Template");
    
    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    BusinessObjectCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectCRUDRemote) loc.getStateless(PackagingUnitCRUDRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
   protected String[] initIdentifiableProperties() {
    return new String[]{"name"};
  }

    @Override
    public Class getBundleResolver() {
        return InventoryBundleResolver.class;
    }
 
    @Override
    protected Node.Property[] initBoMasterNodeProperties() {
        return BOPackagingUnitMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOPackagingUnitMasterNode.class;
    }
}
