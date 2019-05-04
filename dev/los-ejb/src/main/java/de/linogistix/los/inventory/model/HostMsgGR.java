package de.linogistix.los.inventory.model;

import de.linogistix.los.model.HostMsg;

public class HostMsgGR extends HostMsg{

	private LOSGoodsReceipt gr;
	private String grNumber;
	
	public HostMsgGR( LOSGoodsReceipt gr ) {
		this.gr = gr;
		this.grNumber = (gr == null ? "?" : gr.getGoodsReceiptNumber());
	}

	public LOSGoodsReceipt getGr() {
		return gr;
	}

	public void setGr(LOSGoodsReceipt gr) {
		this.gr = gr;
	}

	public String getGrNumber() {
		return grNumber;
	}

	public void setGrNumber(String grNumber) {
		this.grNumber = grNumber;
	}

	
}
