package de.wms2.mywms.product;

public class ItemDataState {

	/**
	 * Product is not ready to be handled. Used within creation processes.
	 */
	public static final int UNDEFINED = 0;

	/**
	 * Product can be used normally
	 */
	public static final int ACTIVE = 100;

	/**
	 * Product is marked as inactive. It can still be used and have stock
	 */
	public static final int INACTIVE = 800;

}
