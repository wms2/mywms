/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.service;

import javax.ejb.Stateless;

import org.mywms.service.BasicServiceBean;

import de.wms2.mywms.goodsreceipt.GoodsReceipt;

@Stateless
public class LOSGoodsReceiptServiceBean
        extends BasicServiceBean<GoodsReceipt>
        implements LOSGoodsReceiptService {
}
