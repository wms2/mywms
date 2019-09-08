/*
 * Copyright (c) 2010 - 2013 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOLOSStorageStrategyMasterNode;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.LOSStorageStrategyCRUDRemote;
import de.linogistix.los.inventory.query.LOSStorageStrategyQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.strategy.StorageStrategy;
import java.util.ArrayList;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;


/**
 *
 *  @author krane
 */
public class BOLOSStorageStrategy extends BO {
    
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR};
    }

  protected String initName() {
    return "StorageStrategy";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ItemData.png";
  }

  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(LOSStorageStrategyQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
      return null;
    }
    
    return ret;
  }
  
    
  protected BasicEntity initEntityTemplate() {
      StorageStrategy o;

    o = new StorageStrategy();
    o.setName("Template");

    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    BusinessObjectCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectCRUDRemote) loc.getStateless(LOSStorageStrategyCRUDRemote.class);
      
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
    protected Property[] initBoMasterNodeProperties() {
        return BOLOSStorageStrategyMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOLOSStorageStrategyMasterNode.class;
    }

    @Override
    public List<Object> getValueList(String fieldName) {
        if( fieldName.equals("clientMode")) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(0);
            entryList.add(1);
            entryList.add(2);
            return entryList;
        }
        if( fieldName.equals("orderByMode")) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(0);
            entryList.add(1);
            entryList.add(2);
            entryList.add(3);
            entryList.add(4);
            entryList.add(5);
            entryList.add(6);
            entryList.add(7);
            entryList.add(8);
            return entryList;
        }
        if( fieldName.equals("useStorage")) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(-1);
            entryList.add(0);
            entryList.add(1);
            return entryList;
        }
        if( fieldName.equals("usePicking")) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(-1);
            entryList.add(0);
            entryList.add(1);
            return entryList;
        }
        if( fieldName.equals("useReplenish")) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(-1);
            entryList.add(0);
            entryList.add(1);
            return entryList;
        }
        return super.getValueList(fieldName);
    }
}
