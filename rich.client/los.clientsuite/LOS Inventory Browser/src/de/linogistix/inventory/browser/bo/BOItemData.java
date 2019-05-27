/*
 * BONodeUser.java
 *
 * Created on 1. Dezember 2006, 01:17
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.inventory.browser.bo;

import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.browser.masternode.BOItemDataMasterNode;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.ItemDataCRUDRemote;
import de.linogistix.los.inventory.query.ItemDataQueryRemote;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.inventory.query.ItemUnitQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.linogistix.los.query.QueryDetail;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemUnit;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.BasicEntity;

import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.util.Lookup;



/**
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOItemData extends BO {
    private ItemUnit itemUnit = null;
    
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.INVENTORY_STR};
    }

  protected String initName() {
    return "ItemDatas";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ItemData.png";
  }

  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(ItemDataQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
      return null;
    }
    
    readDefaultItemUnit();
    return ret;
  }
  
  
  private void readDefaultItemUnit() {
    ItemUnitQueryRemote itemUnitQuery = null;
    try{
        J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
        itemUnitQuery = (ItemUnitQueryRemote)loc.getStateless(ItemUnitQueryRemote.class);
      
        itemUnit = itemUnitQuery.getDefault();
        if( itemUnit == null ) {
            List<ItemUnit> list = itemUnitQuery.queryAll( new QueryDetail(0,1) );
            if( list.size()>0 ) {
                itemUnit=list.get(0);
            }
        }
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
  }
  
  
  protected BasicEntity initEntityTemplate() {
      ItemData o;

    o = new ItemData();
    o.setName("Template");
    o.setNumber("Template");
    o.setSerialNoRecordType(SerialNoRecordType.NO_RECORD);
    o.setItemUnit(itemUnit);

    LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);
    o.setClient( login.getUsersClient() );

    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    BusinessObjectCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectCRUDRemote) loc.getStateless(ItemDataCRUDRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
    }
    return ret;
  }
  
   protected String[] initIdentifiableProperties() {
    return new String[]{"number"};
  }


    @Override
    public Class getBundleResolver() {
        return InventoryBundleResolver.class;
    }
   
    @Override
    protected Property[] initBoMasterNodeProperties() {
        return BOItemDataMasterNode.boMasterNodeProperties();
    }

    @Override
    protected Class<? extends Node> initBoMasterNodeType() {
        return BOItemDataMasterNode.class;
    }

}
