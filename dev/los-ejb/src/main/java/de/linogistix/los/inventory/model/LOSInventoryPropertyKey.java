/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.model;

public class LOSInventoryPropertyKey {

	/**
	 * The default location for goods receiving
	 */
	public static final String DEFAULT_GOODS_RECEIPT_LOCATION_NAME = "GOODS_RECEIPT_LOCATION_DEFAULT";

//	/**
//	 * The default location for goods out
//	 */
//	public static final String DEFAULT_GOODS_OUT_LOCATION_NAME = "GOODS_OUT_LOCATION_DEFAULT";
	
	/**
	 * Print unit load labels automatically when creating a goods receipt position
	 */
	public static final String PRINT_GOODS_RECEIPT_LABEL = "GOODS_RECEIPT_PRINT_LABEL";

	/**
	 * Store printed label
	 */
	public static final String STORE_GOODS_RECEIPT_LABEL = "GOODS_RECEIPT_STORE_LABEL";

	/**
	 * A default value for the lock of the receipt stock
	 */
	public static final String GOODS_IN_DEFAULT_LOCK = "GOODS_IN_DEFAULT_LOCK";

	public static final String GOODS_RECEIPT_PRINTER = "GOODS_RECEIPT_PRINTER_NAME";

	public static final String SHIPPING_RENAME_UNITLOAD = "SHIPPING_RENAME_UNITLOAD";
	public static final String SHIPPING_LOCATION = "SHIPPING_LOCATION";
}
