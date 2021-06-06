/*
 * Copyright (c) 2012-2013 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */
package de.linogistix.inventory.browser.bo;


import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.userlogin.LoginService;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.inventory.res.InventoryBundleResolver;
import de.linogistix.los.crud.BusinessObjectCRUDRemote;
import de.linogistix.los.inventory.crud.LOSOrderStrategyCRUDRemote;
import de.linogistix.los.inventory.query.LOSOrderStrategyQueryRemote;
import de.linogistix.los.query.BusinessObjectQueryRemote;
import de.wms2.mywms.strategy.OrderStrategy;
import de.wms2.mywms.strategy.OrderStrategyCompleteHandling;
import java.util.ArrayList;
import java.util.List;
import org.mywms.globals.Role;
import org.mywms.model.BasicEntity;
import org.openide.util.Lookup;



/**
 *
 *  @author krane
 */
public class BOOrderStrategy extends BO {
    
    @Override
    public String[] getAllowedRoles() {
        return new String[] {Role.ADMIN_STR,Role.OPERATOR_STR,Role.FOREMAN_STR,Role.INVENTORY_STR,Role.CLEARING_STR};
    }
    
    @Override
    public String[] getAllowedRolesCRUD() {
        return new String[] {Role.ADMIN_STR,Role.FOREMAN_STR};
    }

  protected String initName() {
    return "OrderStrategy";
  }
  
  protected String initIconBaseWithExtension() {
    return "de/linogistix/bobrowser/res/icon/ItemData.png";
  }

  protected BusinessObjectQueryRemote initQueryService() {
    
    BusinessObjectQueryRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectQueryRemote)loc.getStateless(LOSOrderStrategyQueryRemote.class);
      
    } catch (Throwable t){
      ExceptionAnnotator.annotate(t);
      return null;
    }
    
    return ret;
  }
  
    
  protected BasicEntity initEntityTemplate() {
      OrderStrategy o;

    o = new OrderStrategy();
    LoginService login = (LoginService) Lookup.getDefault().lookup(LoginService.class);

    o.setName("Template");

    return o;
    
  }
  
  protected BusinessObjectCRUDRemote initCRUDService(){
    BusinessObjectCRUDRemote ret = null;
    
    try{
      J2EEServiceLocator loc = (J2EEServiceLocator)Lookup.getDefault().lookup(J2EEServiceLocator.class);
      ret = (BusinessObjectCRUDRemote) loc.getStateless(LOSOrderStrategyCRUDRemote.class);
      
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
    public List<Object> getValueList(String fieldName) {
        if( "completeHandling".equals(fieldName) ) {
            List<Object> entryList = new ArrayList<Object>();
            entryList.add(OrderStrategyCompleteHandling.NONE);
            entryList.add(OrderStrategyCompleteHandling.AMOUNT_FIRST_MATCH);
            entryList.add(OrderStrategyCompleteHandling.AMOUNT_FIRST_PLUS);
            entryList.add(OrderStrategyCompleteHandling.AMOUNT_MATCH);
            entryList.add(OrderStrategyCompleteHandling.AMOUNT_SMALLEST_DIFF);
            entryList.add(OrderStrategyCompleteHandling.AMOUNT_SMALLEST_PLUS);
            return entryList;
        }
        return super.getValueList(fieldName);
    }
}
