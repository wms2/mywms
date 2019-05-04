/*
 * Copyright (c) 2010-2013 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.mobileserver.processes.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.mobileserver.processes.picking.PickingMobileComparator;
import de.linogistix.mobileserver.processes.picking.PickingMobilePos;


public class ManageMobileBean implements ManageMobile {
	
	
	public List<MobileFunction> getFunctions() {
		List<MobileFunction> functionList = new ArrayList<MobileFunction>();
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.info.InfoBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.goodsreceipt.GoodsReceiptBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.storage.StorageBackingBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.gr_direct.GRDirectBean", "MODE_GOODS_RECEIPT") );
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.gr_direct.GRDirectBean", "MODE_MATERIAL") );
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.picking.PickingMobileBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.shipping.ShippingBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.replenish.ReplenishMobileBean"));
		functionList.add(new MobileFunction("de.linogistix.mobile.processes.stocktaking.StockTakingBean"));
		
		return functionList;
	}
	

	public int getMenuPageSize() {
		return 3;
	}
	
	public Comparator<PickingMobilePos> getPickingComparator() {
		return new PickingMobileComparator(false);
	}
	
	public String getPickingSelectionText(LOSPickingOrder pickingOrder) {
		return pickingOrder.getNumber();
	}
}
