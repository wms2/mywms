package de.linogistix.mobileserver.processes.replenish;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;

import de.linogistix.los.inventory.model.LOSReplenishOrder;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;

public class ReplenishMobileOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id = -1L;
	private String clientNumber = "";
	private String itemNumber = "";
	private String itemName = "";
	private String itemUnit = "";
	private int itemScale = 4;
	private String sourceLocationName = "";
	private String sourceLocationCode = "";
	private String destinationLocationName = "";
	private String destinationLocationCode = "";
	private String sourceUnitLoadLabel = "";
	private BigDecimal amountRequested = null;
	private BigDecimal amountSource = null;
	private BigDecimal amountPicked = null;
	private BigDecimal amountDestination = null;
	private BigDecimal amountDestinationMax = null;
	private String created = "";
	private int state = State.RAW;
	private long sourceStockId = -1;
	
	public void setItem( ItemData item ) {
		if( item != null ) {
			itemNumber = item.getNumber();
			itemName = item.getName();
			itemUnit = item.getHandlingUnit().getUnitName();
			itemScale = item.getScale();
			if( StringTools.isEmpty(clientNumber) ) {
				clientNumber = item.getClient().getNumber();
			}
		}
	}
	
	public void setDestination( LOSStorageLocation destination ) {
		if( destination != null ) {
			destinationLocationName = destination.getName();
			destinationLocationCode = destination.getScanCode();
		}
	}

	public void setStock( StockUnit stock ) {
		if( stock != null ) {
			LOSUnitLoad unitLoad = (LOSUnitLoad)stock.getUnitLoad();
			if( state<State.PICKED ) {
				sourceLocationName = unitLoad.getStorageLocation().getName();
				sourceLocationCode = unitLoad.getStorageLocation().getScanCode();
				sourceUnitLoadLabel = unitLoad.getLabelId();
				amountSource = stock.getAmount();
			}
	        sourceStockId = stock.getId();
		}
	}
	
	public void setOrder( LOSReplenishOrder order ) {
		id = order.getId();
		
		clientNumber = order.getClient().getNumber();
		amountRequested = order.getRequestedAmount();

		setItem(order.getItemData());
		setDestination(order.getDestination());
		setStock(order.getStockUnit());
		
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        created = formatter.format(order.getCreated());
        state = order.getState();
        sourceStockId = order.getStockUnit().getId();
	}
	@Override
	public boolean equals(Object obj) {
		if( obj == null ) {
			return false;
		}
		if( !(obj instanceof ReplenishMobileOrder) ) {
			return false;
		}
		if( id != (((ReplenishMobileOrder)obj).id) ) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getClientNumber() {
		return clientNumber;
	}
	public void setClientNumber(String clientNumber) {
		this.clientNumber = clientNumber;
	}
	public String getItemNumber() {
		return itemNumber;
	}
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemUnit() {
		return itemUnit;
	}
	public void setItemUnit(String itemUnit) {
		this.itemUnit = itemUnit;
	}
	public BigDecimal getAmountRequested() {
		return amountRequested;
	}
	public void setAmountRequested(BigDecimal amountRequested) {
		this.amountRequested = amountRequested;
	}
	public BigDecimal getAmountSource() {
		return amountSource;
	}
	public void setAmountSource(BigDecimal amountSource) {
		this.amountSource = amountSource;
	}
	public BigDecimal getAmountPicked() {
		return amountPicked;
	}
	public void setAmountPicked(BigDecimal amountPicked) {
		this.amountPicked = amountPicked;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getSourceUnitLoadLabel() {
		return sourceUnitLoadLabel;
	}
	public void setSourceUnitLoadLabel(String sourceUnitLoadLabel) {
		this.sourceUnitLoadLabel = sourceUnitLoadLabel;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getSourceLocationName() {
		return sourceLocationName;
	}
	public void setSourceLocationName(String sourceLocationName) {
		this.sourceLocationName = sourceLocationName;
	}
	public String getSourceLocationCode() {
		return sourceLocationCode;
	}
	public void setSourceLocationCode(String sourceLocationCode) {
		this.sourceLocationCode = sourceLocationCode;
	}
	public String getDestinationLocationName() {
		return destinationLocationName;
	}
	public void setDestinationLocationName(String destinationLocationName) {
		this.destinationLocationName = destinationLocationName;
	}
	public String getDestinationLocationCode() {
		return destinationLocationCode;
	}
	public void setDestinationLocationCode(String destinationLocationCode) {
		this.destinationLocationCode = destinationLocationCode;
	}
	public long getSourceStockId() {
		return sourceStockId;
	}
	public void setSourceStockId(long sourceStockId) {
		this.sourceStockId = sourceStockId;
	}
	public BigDecimal getAmountDestination() {
		return amountDestination;
	}
	public void setAmountDestination(BigDecimal amountDestination) {
		this.amountDestination = amountDestination;
	}
	public BigDecimal getAmountDestinationMax() {
		return amountDestinationMax;
	}
	public void setAmountDestinationMax(BigDecimal amountDestinationMax) {
		this.amountDestinationMax = amountDestinationMax;
	}
	public int getItemScale() {
		return itemScale;
	}
	public void setItemScale(int itemScale) {
		this.itemScale = itemScale;
	}

	
}
