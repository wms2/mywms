package de.linogistix.mobile.processes.picking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.model.Client;

import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSPickingOrder;
import de.linogistix.los.model.State;
import de.linogistix.los.util.StringTools;
import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobileserver.processes.picking.PickingMobileFacade;
import de.linogistix.mobileserver.processes.picking.PickingMobileOrder;
import de.linogistix.mobileserver.processes.picking.PickingMobilePos;
import de.linogistix.mobileserver.processes.picking.PickingMobileUnitLoad;



/**
 * <h1>Data structure for picking dialog</h1>
 * With this class all accesses to the backend are done.
 * <p>
 * HowTo handle:
 * <li><b>Initialization:</b> reset(). Remove all stored information. No callback to the backend is done. 
 * Only internal structures are reseted.
 * </li>
 * <li><b>Add picking orders:</b> loadOrder(). When adding a picking order, the necessary information is
 * loaded from the backend. 
 * </li>
 * <li><b>Start pick handling loop:</b> startPicking().
 * Initializes structures to loop over all open picks. To navigate within the picks use setFirstPick(), setNextPick(), setPrevPick().<br>
 * The unit load navigation will return null values.<br>
 * The unit load information methods (getPickToLabel()...) will give information about the pick-to unit load of the current pick.
 * </li>
 * <li><b>Start unitLoad loop:</b> startPutAway(). </li>
 * Initializes structures to loop over all open picks. To navigate within the picks use setFirstPickTo(), setNextPickTo(), setPrevPickTo(), setPickTo(index).<br>
 * The pick navigation will return null values. 
 * The unit load information methods (getPickToLabel()...) will give information about the current pick-to unit load. Not of a unit load regarding to a pick. 
 * </p> 
 * 
 * @author krane
 */
public class PickingMobileData extends BasicBackingBean implements Serializable {
	Logger log = Logger.getLogger(PickingMobileData.class);
	private static final long serialVersionUID = 1L;
	
	protected PickingMobileOrder currentOrder = null;
	protected List<PickingMobilePos> pickList = new ArrayList<PickingMobilePos>();
	protected PickingMobileUnitLoad currentPickTo = null;
	protected int currentPickIdx = -1;
	protected PickingMobileFacade facade;
	
	public PickingMobileData() {
		facade = getStateless(PickingMobileFacade.class);
	}

	
	
	// ************************************************************************
	// Operation PICKUP
	// These methods are only defined in this operation mode
	// ************************************************************************
	
	
	
	
	public void loadOrder( long orderId ) throws FacadeException {
		String logStr = "loadOrder ";
		log.debug(logStr+"id="+orderId);
		
		currentOrder = null;
		pickList = new ArrayList<PickingMobilePos>();
		currentPickTo = null;

		currentOrder = facade.readOrder(orderId);
		
		if( currentOrder == null ) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataNoOrder"));
		}
		
		if( currentOrder.state < State.RESERVED ) {
			facade.reserveOrder(orderId);
		}
		
		pickList = facade.readPickList(orderId);
		
		currentPickTo = facade.readUnitLoad(orderId);
	}
	
	public void reloadOrder( long orderId ) throws FacadeException {
		String logStr = "reloadOrder ";
		PickingMobileOrder order = facade.readOrder(orderId);
		if( order == null ) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataNoOrder"));
		}
		
		currentOrder = order;
		
		List<PickingMobilePos> pickListNew = facade.readPickList(orderId);

		// Add new picks to the structure
		for( PickingMobilePos pickNew : pickListNew ) {
			boolean found = false;
			for( PickingMobilePos pickOld : pickList ) {
				if( pickOld.id == pickNew.id ) {
					found=true;
					break;
				}
			}
			if( !found ) {
				log.info(logStr+"Found new pick: "+pickNew.toString());
				pickList.add(pickNew);
			}
		}
	}

	/**
	 * Removes all orders from the data structures.<br>
	 * The reservation of the orders is released.
	 * Unit loads are removed. Unit loads must not be used!
	 */
	public void removeAllOrders() throws FacadeException {

		if( currentOrder != null ) {
			facade.releaseOrder( currentOrder.id );
		}
		if( currentPickTo != null ) {
			facade.removeUnitLoadIfEmpty( currentPickTo.label );
		}
		
		reset();
	}
	
	/**
	 * Registers a new label for the pick-to unit-load of the current order
	 * Valid in Mode.PICK and Mode.PICKUP
	 */
	public void registerPickToLabel( String label ) throws FacadeException {
		String logStr = "registerPickToLabel ";

		if( StringTools.isEmpty(label) ) {
			return;
		}
		if( currentOrder == null ) {
			log.error(logStr+"Cannot register PickTo without order");
			return;
		}
		
		if( currentPickTo != null && currentPickTo.label != null ) {
			facade.changeUnitLoadLabel(currentPickTo.label, label, currentOrder.id);
			currentPickTo.label = label;
		}
		else {
			currentPickTo = facade.createUnitLoad( label, currentOrder.id, -1 );
		}
		
	}
	
	// ************************************************************************
	// Operation PICK
	// These methods are only defined in this operation mode
	// ************************************************************************
	/**
	 * Start operation mode. Used for initializations.
	 */
	public void startPicking() throws FacadeException {
		currentPickIdx = -1;
		
		if( currentOrder != null && currentOrder.state < State.STARTED ) {
			facade.startOrder(currentOrder.id);
		}
		
		sortPicks();
	}
	
	/**
	 * Navigate to the first pick in the list.<br>
	 * The picks will be are sorted.
	 */
	public boolean setFirstPick() throws FacadeException {
		currentPickIdx=-1;
		return setNextPick();
	}
	
	/**
	 * Sets the internal structures to the current pick.<br>
	 * If the current pick is not valid, the next available pick will be activated.<br>
	 * 
	 * @return true, if a current pick is set.
	 */
	public boolean setCurrentPick() throws FacadeException {
		PickingMobilePos pick = null;
		pick = getCurrentPick();
		if( pick == null || pick.state >= State.PICKED || pick.state < State.ASSIGNED ) {
			return setNextPick();
		}
		return true;
	}

	/**
	 * Sets the internal structures to the next available pick in the list.<br>
	 * The pick is reloaded from the backend to check its current state.<br>
	 * If no more pick are available, the complete structures are reloaded from the backend.
	 * This is done to check for possible external changes.
	 * 
	 * @return true, if a new current pick is set.
	 */
	public boolean setNextPick() throws FacadeException {
		String logStr = "setNextPick ";
		
		currentPickIdx++;
		if( currentPickIdx < 0 ) {
			currentPickIdx = 0;
		}
		PickingMobilePos pick = null;

		while( currentPickIdx < pickList.size() ) {
			pick = getCurrentPick();
			if( pick == null ) {
				break;
			}
			if( pick.state >= State.PICKED || pick.state < State.ASSIGNED ) {
				currentPickIdx++;
				continue;
			}
			pick = facade.reloadPick(pick);
			if( pick.state >= State.PICKED || pick.state < State.ASSIGNED ) {
				currentPickIdx++;
				continue;
			}
			log.debug(logStr+"new pick. idx="+currentPickIdx+", id="+pick.id);
			return true;
		}
		
		reload();
		
		currentPickIdx = 0;
		while( currentPickIdx < pickList.size() ) {
			pick = getCurrentPick();
			if( pick == null ) {
				break;
			}
			if( pick.state >= State.PICKED || pick.state < State.ASSIGNED ) {
				currentPickIdx++;
				continue;
			}
			log.debug(logStr+"new pick. idx="+currentPickIdx+", id="+pick.id);
			return true;
		}
		
		log.debug(logStr+"no new pick available");
		return false;
	}
	
	/**
	 * Sets the internal structures to the previous available pick in the list.<br>
	 * The pick is reloaded from the backend to check its current state.<br>
	 * If no more pick s available, the first pick is selected.
	 * 
	 * @return true, if a new current pick is set.
	 */
	public boolean setPrevPick() throws FacadeException {
		String logStr = "setPrevPick ";
		
		currentPickIdx--;
		if( currentPickIdx >= pickList.size() ) {
			currentPickIdx = pickList.size()-1;
		}
		PickingMobilePos pick = null;

		while( currentPickIdx >= 0 ) {
			pick = getCurrentPick();
			if( pick == null ) {
				break;
			}
			if( pick.state >= State.PICKED || pick.state < State.ASSIGNED ) {
				currentPickIdx--;
				continue;
			}
			pick = facade.reloadPick(pick);
			if( pick.state >= State.PICKED || pick.state < State.ASSIGNED ) {
				currentPickIdx--;
				continue;
			}

			log.debug(logStr+"new pick. idx="+currentPickIdx+", id="+pick.id);
			return true;
		}
		
		currentPickIdx = Integer.MIN_VALUE;
		return setNextPick();
	}


	/**
	 * Check, whether an input is correct to identify the source of the current pick.<br>
	 * If the source does not match, it is tried to change to the given source.
	 */
	public void checkPickSource( String code ) throws FacadeException {
		String logStr = "checkPickSource ";
		log.debug(logStr+"code="+code);
		
		PickingMobilePos pick = getCurrentPick();
		if( code.equals(pick.locationName) ) {
			return;
		}
		if( !StringTools.isEmpty(pick.locationCode) ) {
			if( code.equals(pick.locationCode) ) {
				return;
			}
		}
		if( code.equals(pick.unitLoadLabel) ) {
			return;
		}
		
		facade.changePickFromStockUnit(pick, code);
	}
	
	/**
	 * Confirmation of the current pick.
	 */
	public void confirmPick( BigDecimal amount, BigDecimal amountRemain, List<String> serialNoList, boolean counted ) throws FacadeException {
		String logStr = "confirmPick ";
		log.debug(logStr+"amount="+amount+", remain="+amountRemain);

		PickingMobilePos pick = getCurrentPick();
		if( pick == null ) {
			log.warn(logStr+"No current pick");
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataNoCurrentPick") );
		}
		
		if( currentPickTo == null ) {
			// create new pick-to unit load
			String label = facade.generatePickToLabel();
			if( label == null ) {
				log.warn(logStr+"Cannot generate label");
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataCannotGenerateLabel"));
			}
			PickingMobileOrder mOrder = currentOrder;
			if( mOrder == null ) {
				log.warn(logStr+"No order found for pick");
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataNoOrderForPick"));
			}
			
			currentPickTo = new PickingMobileUnitLoad( mOrder, label );
		}
		else if ( currentPickTo.label == null ) {
			String label = facade.generatePickToLabel();
			if( label == null ) {
				log.warn(logStr+"Cannot generate label");
				throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataCannotGenerateLabel"));
			}
			currentPickTo.label = label;
		}
		
		facade.confirmPick( pick, currentPickTo, amount, amountRemain, serialNoList, counted );
		pick.state = State.PICKED;
		currentPickTo.state = State.STARTED;
	}
	
	public boolean isLocationEmpty() {
		PickingMobilePos pick = getCurrentPick();
		if( pick.checkLocationEmpty && pick.amount.compareTo(pick.amountStock)>=0 ) {
			return true;
		}
		return false;
	}
	
	protected void sortPicks() {
		if( pickList != null ) {
			Collections.sort(pickList, facade.getPickingComparator());
		}
	}

	/**
	 * Get the current pick.
	 */
	public PickingMobilePos getCurrentPick() {
		if( pickList == null || pickList.size() == 0 ) {
			return null;
		}
		if( currentPickIdx < 0 ) {
			return null;
		}
		if( currentPickIdx >= pickList.size() ) {
			return null;
		}
		
		PickingMobilePos pick = pickList.get(currentPickIdx);
		return pick;
	}
	
	
	// ************************************************************************
	// Operation PUTAWAY
	// ************************************************************************
	/**
	 * Start operation mode. Used for initializations.
	 */
	public void startPutAway() throws FacadeException {
		if( currentPickTo != null ) {
			if( facade.removeUnitLoadIfEmpty( currentPickTo.label ) ) {
				currentPickTo = null;
			}
		}
	}

	/**
	 * The current pick-to unit load will be transfered to the given location.<br>
	 */
	public void transferPickTo(String target, int state) throws FacadeException {
		if( currentPickTo == null ) {
			log.error("Cannot find current pick-to");
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataNoPickTo") );
		}
		facade.transferUnitLoad(currentPickTo.label, target, state);
	}
	
	/**
	 * The current pick-to unit load will get a new label
	 * @param labelNew If null, a new label will be generated automatically (from sequence)
	 */
	public void changePickToLabel( String labelNew ) throws FacadeException {
		if( currentPickTo == null ) {
			log.info("Cannot read current unit load");
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataNoPickTo"));
		}
		if( labelNew == null ) {
			labelNew = facade.generatePickToLabel();
		}

		if( labelNew != null && labelNew.equals(currentPickTo.label) ) {
			// entered existing label
			return;
		}
	
		facade.changeUnitLoadLabel( currentPickTo.label, labelNew, currentOrder == null ? -1 : currentOrder.id );
		currentPickTo.label = labelNew;
		return;
	}
	
	/**
	 * Get the current pick-to unit load. Valid in Mode.PUTAWAY
	 */
	public PickingMobileUnitLoad getCurrentPickTo() {
		return currentPickTo;
	}
	
	public void reload() throws FacadeException {
		String logStr = "reload ";
		log.debug(logStr);

		if( currentOrder != null ) {
			reloadOrder(currentOrder.id);
		}
		
		currentPickIdx = -1;
		
		sortPicks();
	}
	
	
	
	// ************************************************************************
	// General operation methods
	// ************************************************************************
	/**
	 * Is called, when picking dialog has finished a picking process.<br>
	 * Finish orders, cleanup. Reset operation mode. 
	 */
	public void finishProcess() throws FacadeException {
		String logStr = "finishProcess ";
		log.debug(logStr);
		
		if( currentOrder != null ) {
			log.debug(logStr+"finish order. id="+currentOrder.id+", number="+currentOrder.customerOrderNumber);
			facade.finishOrder(currentOrder.id);
			currentOrder.state=State.FINISHED;
		}
	}
	
	/**
	 * Initialize the internal structures
	 */
	public void reset() {
		String logStr = "reset ";
		log.debug(logStr);

		currentOrder = null;
		pickList = new ArrayList<PickingMobilePos>();
		currentPickTo = null;
	}
	
	/**
	 * Reads the current information from database.
	 */
	public void resume(boolean useTransport) throws FacadeException {
		String logStr = "resume ";
		log.debug(logStr);
		reset();

		List<LOSPickingOrder> orders = facade.getStartedOrders(useTransport);
		if( orders == null || orders.size()==0 ) {
			return;
		}

		log.warn(logStr+"Found orders to resume");
		
		for( LOSPickingOrder order : orders ) {
			loadOrder(order.getId());
			break;
		}
	}

	public void checkSerial(List<String> serialList, String code) throws FacadeException {
		if( StringTools.isEmpty(code) ) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgEnterSerial") );
		}
		if( serialList != null && serialList.size()>0 ) {
			for( String s : serialList ) {
				if( code.equals(s) ) {
					throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgSerialUnique") );
				}
			}
		}
		
		if( getCurrentPick() == null ) {
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataNoCurrentPick") );
		}
		
		facade.checkSerial( getCurrentPick().clientNumber, getCurrentPick().itemNo, code );
	}

	public void printLabel( String printer ) throws FacadeException {
		if( currentPickTo == null ) {
			log.error("Cannot find current pick-to");
			throw new InventoryException(InventoryExceptionKey.CUSTOM_TEXT, resolve("MsgDataNoPickTo") );
		}
		facade.printLabel(currentPickTo.label, printer);
	}

	
	// ************************************************************************
	// Access Attributes
	// ************************************************************************
	public Client getDefaultClient() {
		return facade.getDefaultClient();
	}
	
	public List<SelectItem> getCalculatedPickingOrders(String code, boolean usePick, boolean useTransport) {
		return facade.getCalculatedPickingOrders(code, null, null, usePick, useTransport, -1);
	}
	
	public PickingMobileOrder readOrder(long id) {
		return facade.readOrder(id);
	}
	
	public int getNumOrders() {
		return currentOrder == null ? 0 : 1;
	}
	
	public BigDecimal getAmount() {
		PickingMobilePos pick = getCurrentPick();
		return pick == null ? BigDecimal.ZERO : pick.amount;
	}
	
	public PickingMobileOrder getCurrentOrder() {
		if( currentOrder == null ) {
			return new PickingMobileOrder();
		}
		return currentOrder;
	}
	
	@Override
	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.picking.PickingMobileBundle", loc);
		return bundle;
	}

}
