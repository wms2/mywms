package de.wms2.mywms.inventory;

/**
 * This event fired when a stock unit changes its state.
 * 
 * @author krane
 *
 */
public class StockUnitStateChangeEvent {
	private StockUnit stock;
	private int oldState;

	public StockUnitStateChangeEvent(StockUnit stock, int oldState) {
		this.stock = stock;
		this.oldState = oldState;
	}

	public StockUnit getStock() {
		return stock;
	}

	public int getOldState() {
		return oldState;
	}

}
