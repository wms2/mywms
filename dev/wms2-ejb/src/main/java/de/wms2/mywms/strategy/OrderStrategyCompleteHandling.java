package de.wms2.mywms.strategy;

public class OrderStrategyCompleteHandling {
	/**
	 * No exclusive complete handling
	 */
	public static final int NONE = 0;
	/**
	 * First exact matching amount, selected by FIFO
	 */
	public static final int AMOUNT_FIRST_MATCH = 1;
	/**
	 * First PLUS difference, selected by FIFO
	 */
	public static final int AMOUNT_FIRST_PLUS = 2;
	/**
	 * Exact matching amount with a combination of unit loads
	 */
	public static final int AMOUNT_MATCH = 3;
	/**
	 * Smallest PLUS or MINUS difference, selected by a combination of unit loads
	 */
	public static final int AMOUNT_SMALLEST_DIFF = 4;
	/**
	 * Smallest PLUS difference, selected by a combination of unit loads
	 */
	public static final int AMOUNT_SMALLEST_PLUS = 5;
}
