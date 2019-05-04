package de.linogistix.los.inventory.model;

import java.math.BigDecimal;

import org.mywms.model.ItemData;
import org.mywms.model.Lot;
import org.mywms.model.StockUnit;

import de.linogistix.los.model.HostMsg;

public class HostMsgStock extends HostMsg{

	private ItemData itemData;
	private BigDecimal amount;
	private Lot lot;
	private String serialNumber;
	private int lock;
	private String unitLoadLabel;
	private String operator;
	private String activityCode;
	private LOSStockUnitRecordType recordType;

	public HostMsgStock( StockUnit su, BigDecimal amount, String operator, LOSStockUnitRecordType recordType, String activityCode ) {
		this.itemData = su.getItemData();
		this.amount = amount;
		this.lot = su.getLot();
		this.serialNumber = su.getSerialNumber();
		this.lock = su.getLock();
		this.unitLoadLabel = su.getUnitLoad().getLabelId();
		this.operator = operator;
		this.recordType = recordType;
		this.activityCode = activityCode;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Lot getLot() {
		return lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getLock() {
		return lock;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public LOSStockUnitRecordType getRecordType() {
		return recordType;
	}

	public void setRecordType(LOSStockUnitRecordType recordType) {
		this.recordType = recordType;
	}

}
