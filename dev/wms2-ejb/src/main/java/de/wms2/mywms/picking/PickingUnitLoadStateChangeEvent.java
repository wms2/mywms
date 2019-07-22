package de.wms2.mywms.picking;

/**
 * This event fired when a picking unit load changes its state.
 * 
 * @author krane
 *
 */
public class PickingUnitLoadStateChangeEvent {
	private PickingUnitLoad pickingUnitLoad;
	private int oldState;

	public PickingUnitLoadStateChangeEvent(PickingUnitLoad pickingUnitLoad, int oldState) {
		this.pickingUnitLoad = pickingUnitLoad;
		this.oldState = oldState;
	}

	public PickingUnitLoad getPickingUnitLoad() {
		return pickingUnitLoad;
	}

	public int getOldState() {
		return oldState;
	}
}
