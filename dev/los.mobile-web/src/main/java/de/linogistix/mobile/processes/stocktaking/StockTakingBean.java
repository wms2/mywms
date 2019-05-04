/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.stocktaking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.service.QueryItemDataServiceRemote;
import de.linogistix.los.inventory.service.QueryStockServiceRemote;
import de.linogistix.los.location.model.LOSStorageLocation;
import de.linogistix.los.location.model.LOSUnitLoad;
import de.linogistix.los.location.service.QueryStorageLocationServiceRemote;
import de.linogistix.los.location.service.QueryUnitLoadServiceRemote;
import de.linogistix.los.query.ClientQueryRemote;
import de.linogistix.los.query.LOSResultList;
import de.linogistix.los.query.QueryDetail;
import de.linogistix.los.query.exception.BusinessObjectQueryException;
import de.linogistix.los.stocktaking.exception.LOSStockTakingException;
import de.linogistix.los.stocktaking.facade.LOSStocktakingFacade;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;

public class StockTakingBean extends BasicDialogBean {
	private static final Logger log = Logger.getLogger(StockTakingBean.class);

	protected LOSStorageLocation stockTakingLocation;
	
	protected String stockTakingLocationName;
	
	protected String lastLocationName;
	
	protected String unitLoadLabel;
	
	protected LOSUnitLoad unitLoadToCount;
	
	protected List<LOSUnitLoad> expectedUnitLoadList;
	
	protected HashSet<String> countedUnitLoadList;
	
	protected String countedAmount;
	
	protected QueryUnitLoadServiceRemote queryUnitLoadService;
	
	protected QueryStockServiceRemote queryStockService;
	
	protected QueryStorageLocationServiceRemote queryLocationService;
	
	protected QueryItemDataServiceRemote queryItemData;
	
	protected LOSStocktakingFacade stFacade;
	
	protected ClientQueryRemote queryClient;
	
	protected StockUnit expectedStock;
	
	protected List<StockUnit> expectedStockList;
	
	protected String currentItem = "";
	
	protected String currentUnit = "";
	
	protected String currentLot = "";

	protected String currentStock = "";
	
	protected String currentSerial = "";
	
	protected int countedStocks = 0;
	
	protected List<String> ulTypeList = null;

	protected String currentULType = "";
	
	protected Client currentClient = null;
	
	protected String inputClientNumber = "";
	
	public StockTakingBean() {
		super();
		
		queryUnitLoadService = super.getStateless(QueryUnitLoadServiceRemote.class);
		
		queryItemData = super.getStateless(QueryItemDataServiceRemote.class);
		
		queryStockService = super.getStateless(QueryStockServiceRemote.class);
		
		queryLocationService = super.getStateless(QueryStorageLocationServiceRemote.class);
		
		queryClient = super.getStateless(ClientQueryRemote.class);
		
		stFacade = super.getStateless(LOSStocktakingFacade.class);
	}
	
	public String getNavigationKey() {
		return processStart();
	}
	
	public String getTitle() {
		return resolve("Stocktaking");
	}

	//---------------------------------------------------------------------
	public String countLocation(){
		if( stockTakingLocationName != null ) {
			stockTakingLocationName = stockTakingLocationName.trim();
		}
		
		stockTakingLocation = null;
		try {
			stockTakingLocation = queryLocationService.getByName(stockTakingLocationName);
			if( stockTakingLocation !=null ) {
				stockTakingLocationName = stockTakingLocation.getName();
			}
		} catch (UnAuthorizedException e) {
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			stockTakingLocationName = "";
			return StockTakingNavigation.SCAN_LOCATION.name();
		}

		
		if(stockTakingLocation == null){
			JSFHelper.getInstance().message(resolve("MsgLocationUnknown", new Object[]{stockTakingLocationName}));
			stockTakingLocationName = "";
			return StockTakingNavigation.SCAN_LOCATION.name();
		}
		if( currentClient == null ) {
			currentClient = stFacade.getDefaultClient();
			if(currentClient == null){
				log.error("currentClient not set! Start again");
				return processStart();
			}
		}

		Client systemClient = queryClient.getSystemClient();
		Client locationClient = stockTakingLocation.getClient();
		if( !locationClient.equals(systemClient) &&  !locationClient.equals(currentClient) ) {
			JSFHelper.getInstance().message(resolve("MsgLocationDiffClient"));
			return StockTakingNavigation.SCAN_LOCATION.name();
		}

		
		countedUnitLoadList = new HashSet<String>();
		expectedUnitLoadList = queryUnitLoadService.getListByLocation(stockTakingLocation);
		
		try {
			stFacade.processLocationStart(stockTakingLocation);
			
		} catch (LOSStockTakingException e) {
			log.error("LOSStockTakingException: " + e.getMessage());
			stockTakingLocationName = "";
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return StockTakingNavigation.SCAN_LOCATION.name();
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			stockTakingLocationName = "";
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			return StockTakingNavigation.SCAN_LOCATION.name();
		}
		
		return StockTakingNavigation.SCAN_UNITLOAD.name();
	}
	
	public String processStart() {
		currentClient = stFacade.getDefaultClient();

		if( currentClient == null ) {
			return StockTakingNavigation.STOCKTAKING_ENTER_CLIENT.name();
		}
		else {
			return StockTakingNavigation.SCAN_LOCATION.name();
		}
	}
	
	public String processEnterClient() {
		if( inputClientNumber == null ) {
			JSFHelper.getInstance().message(resolve("MsgEnterClient"));
			inputClientNumber = "";
			return StockTakingNavigation.STOCKTAKING_ENTER_CLIENT.name();
		}
		inputClientNumber = inputClientNumber.trim();
		if( inputClientNumber.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterClient"));
			inputClientNumber = "";
			return StockTakingNavigation.STOCKTAKING_ENTER_CLIENT.name();
		}

		try {
			currentClient = queryClient.getByNumberIgnoreCase(inputClientNumber);
		} catch (UnAuthorizedException e) {
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			inputClientNumber = "";
			return StockTakingNavigation.STOCKTAKING_ENTER_CLIENT.name();
		}
		if( currentClient == null ) {
			JSFHelper.getInstance().message(resolve("MsgEnterValidClient"));
			return StockTakingNavigation.STOCKTAKING_ENTER_CLIENT.name();
		}
		
		inputClientNumber = "";
		return StockTakingNavigation.SCAN_LOCATION.name();

	}
	//---------------------------------------------------------------------
	public String processLocationEmpty(){
		
		stockTakingLocation = getStorageLocation();
		if(stockTakingLocation == null){
			JSFHelper.getInstance().message(resolve("MsgLocationUnknown", new Object[]{stockTakingLocationName}));
			stockTakingLocationName = "";
			return StockTakingNavigation.SCAN_LOCATION.name();
		}
		
		expectedUnitLoadList = queryUnitLoadService.getListByLocation(stockTakingLocation);
		
		if(expectedUnitLoadList.size()>0){
			
			return StockTakingNavigation.CONFIRM_LOCATION_EMPTY.name();
		}
		
		try {
			stFacade.processLocationEmpty(stockTakingLocation);
			
		} catch (LOSStockTakingException e) {
			log.error("LOSStockTakingException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
		}
		
		lastLocationName = stockTakingLocationName;
		stockTakingLocationName = "";
		expectedStockList = null;
		countedStocks = 0;
		unitLoadLabel = "";
		currentULType = "";
		currentItem = "";
		currentUnit = "";
		currentLot = "";
		currentStock = "";
		countedAmount = "";
		currentSerial = "";

		return StockTakingNavigation.SCAN_LOCATION.name();
	}
		
	//---------------------------------------------------------------------
	public String processLocationFinished(){
		
		// if there are still planned unit loads
		if(expectedUnitLoadList.size() > 0){
			return StockTakingNavigation.CONFIRM_UNITLOADS_MISSING.name();
		}
		
		// if we expect nothing and user counted nothing process location empty
		if(countedUnitLoadList.size() == 0 && expectedUnitLoadList.size() == 0){
			return processLocationEmpty();
		}
		
		try {
			stFacade.processLocationFinish(stockTakingLocation);
		} catch (LOSStockTakingException e) {
			log.error("LOSStockTakingException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return StockTakingNavigation.SCAN_UNITLOAD.name();
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			return StockTakingNavigation.SCAN_UNITLOAD.name();
		}
		
		lastLocationName = stockTakingLocationName;
		stockTakingLocationName = "";
		expectedStockList = null;
		countedStocks = 0;
		unitLoadLabel = "";
		currentULType = "";
		currentItem = "";
		currentUnit = "";
		currentLot = "";
		currentStock = "";
		countedAmount = "";
		currentSerial = "";
		
		return StockTakingNavigation.SCAN_LOCATION.name();
	}
	
	//---------------------------------------------------------------------
	public String confirmMissingUnitLoads(){
		
		try{
			
			for(LOSUnitLoad ul:expectedUnitLoadList){
				stFacade.processUnitloadMissing(ul);
			}
		
			stFacade.processLocationFinish(stockTakingLocation);
			
		} catch (LOSStockTakingException e) {
			log.error("LOSStockTakingException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return StockTakingNavigation.SCAN_UNITLOAD.name();
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			return StockTakingNavigation.SCAN_UNITLOAD.name();
		}
		
		lastLocationName = stockTakingLocationName;
		stockTakingLocationName = "";
		
		return StockTakingNavigation.SCAN_LOCATION.name();
	}
	
	//---------------------------------------------------------------------
	public String cancelMissingUnitLoads(){
		
		return StockTakingNavigation.SCAN_UNITLOAD.name();
	}
	
	//---------------------------------------------------------------------
	public String countStocks(){
		
		unitLoadLabel = unitLoadLabel == null ? "" : unitLoadLabel.trim();
		if( unitLoadLabel.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterUnitLoad"));
			return StockTakingNavigation.SCAN_UNITLOAD.name();
		}
		try {
			unitLoadToCount = queryUnitLoadService.getByLabelId(unitLoadLabel);


			if( currentClient == null ) {
				currentClient = stFacade.getDefaultClient();
				if(currentClient == null){
					log.error("currentClient not set! Start again");
					return processStart();
				}
			}
			if(unitLoadToCount == null || !unitLoadToCount.getStorageLocation().equals(stockTakingLocation)) {
				return StockTakingNavigation.CONFIRM_UNEXPECTED_UNITLOAD.name();
			}
			if( !unitLoadToCount.getClient().isSystemClient() && !currentClient.equals(unitLoadToCount.getClient()) ) {
				JSFHelper.getInstance().message(resolve("MsgUnitLoadDiffClient"));
				return StockTakingNavigation.SCAN_UNITLOAD.name();
			}
			
			
			try {
				stFacade.processUnitloadStart(stockTakingLocationName, unitLoadLabel);
			} catch (LOSStockTakingException e) {
				log.error("LOSStockTakingException: " + e.getMessage(), e);
				JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
				return StockTakingNavigation.SCAN_UNITLOAD.name();
			}
			
			expectedStockList = queryStockService.getListByUnitLoad(unitLoadToCount);
			countedStocks = 0;

			if(expectedStockList.size()>0){
				expectedStock = expectedStockList.get(0);
				currentStock = expectedStock.getId().toString();
				currentItem = expectedStock.getItemData().getNumber();
				currentUnit = expectedStock.getItemUnit().getUnitName();
				currentLot = expectedStock.getLot() == null ? "" : expectedStock.getLot().getName();
				currentSerial = expectedStock.getSerialNumber() == null ? "" : expectedStock.getSerialNumber();
				
				return StockTakingNavigation.COUNT_STOCKS.name();

			}
			else {
				
				return StockTakingNavigation.NEW_ITEMDATA.name();
				
			}
			
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(resolve("MsgUnitLoadDiffClient"));
			return StockTakingNavigation.SCAN_UNITLOAD.name();
		}
		
	}
	
	//---------------------------------------------------------------------
	public String confirmUnexpectedUnitLoad(){
		
		ulTypeList = null;
		
		try {
			stFacade.processUnitloadStart(stockTakingLocationName, unitLoadLabel);
			
			ulTypeList = stFacade.getUnitloadTypes(null, stockTakingLocationName);
			
		} catch (LOSStockTakingException e) {
			log.error("LOSStockTakingException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			return "";
		}

		countedStocks = 0;
		
		expectedStockList = new ArrayList<StockUnit>();		
		
		if( ulTypeList == null || ulTypeList.size()==0) {
			log.error("No suitable unit load type for location name <"+stockTakingLocationName+"> found");
			currentULType = "";
		}
		else if( ulTypeList.size() == 1 ) {
			currentULType = ulTypeList.get(0);
			log.info("Only one unit load type <"+currentULType+"> for location name <"+stockTakingLocationName+"> found");
		}
		else {
			return StockTakingNavigation.NEW_ULTYPE.name();
		}
			
		
		return StockTakingNavigation.NEW_ITEMDATA.name();
	}
	
	//---------------------------------------------------------------------
	public String cancelUnexpectedUnitLoad(){
		
		unitLoadLabel = "";
		currentULType = "";
		return StockTakingNavigation.SCAN_UNITLOAD.name();
	}

	//---------------------------------------------------------------------
	public String cancelCounting(){
		String methodName = "cancelCounting ";
		log.info(methodName+"Start cancel stocktaking of location <" + stockTakingLocationName + ">");
		
		LOSStorageLocation loc = getStorageLocation();
		if( loc == null ) {
			// no location currently started
			JSFHelper.getInstance().message(resolve("MsgNoCurrentOrder"));
			return StockTakingNavigation.SCAN_UNITLOAD.name();
		}
		try {
			stFacade.processLocationCancel(loc);
		} catch (LOSStockTakingException e) {
			log.error("LOSStockTakingException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
			return "";
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			return "";
		}

		lastLocationName = stockTakingLocationName;
		stockTakingLocationName = "";
		unitLoadLabel = "";
		currentULType = "";
		expectedStockList = null;
		countedStocks = 0;
		currentItem = "";
		currentUnit = "";
		currentLot = "";
		currentStock = "";
		countedAmount = "";
		currentSerial = "";
		
		return StockTakingNavigation.SCAN_LOCATION.name();
	}

	//---------------------------------------------------------------------
	public String processStockCounted_Next(){
		String methodName = "processStockCounted_Next ";
		log.debug(methodName+"Start");

		if(currentItem.length() == 0){
			JSFHelper.getInstance().message(resolve("MsgEnterItemData"));
			return StockTakingNavigation.NEW_ITEMDATA.name();
		}
		
		if(countedAmount.length()==0){
			JSFHelper.getInstance().message(resolve("MsgEnterAmount"));
			return StockTakingNavigation.COUNT_STOCKS.name();
		}
		
		BigDecimal amount = null;
		try {
			amount = new BigDecimal(countedAmount);
		}
		catch( NumberFormatException e ) {
			log.error(methodName+"NumberFormatException: " + countedAmount);
			JSFHelper.getInstance().message(resolve("MsgEnterValidAmount"));
			return StockTakingNavigation.COUNT_STOCKS.name();
		}
		
		
		// if we expect counting
		if(expectedStock != null){
			try {
				stFacade.processStockCount(expectedStock, amount);
				
				// clear counted stock data
				currentItem = "";
				currentUnit = "";
				currentLot = "";
				currentStock = "";
				countedAmount = "";
				currentSerial = "";
				expectedStock = null;
				expectedStockList.remove(0);
				countedStocks++;
				
				// if we have another stock to count
				if(expectedStockList.size()>0){
					
					expectedStock = expectedStockList.get(0);
					currentStock = expectedStock.getId().toString();
					currentItem = expectedStock.getItemData().getNumber();
					currentUnit = expectedStock.getItemUnit().getUnitName();
					currentLot = expectedStock.getLot() == null ? "" : expectedStock.getLot().getName();
					currentSerial = expectedStock.getSerialNumber() == null ? "" : expectedStock.getSerialNumber();
				}
				
			} catch (LOSStockTakingException e) {
				log.error("LOSStockTakingException: " + e.getMessage(), e);
				JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
				return StockTakingNavigation.COUNT_STOCKS.name();
			} catch (UnAuthorizedException e) {
				log.error("UnAuthorizedException: " + e.getMessage(), e);
				JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
				return "";
			}
		}
		// if there is an unexpected stock counted
		else{
			if( currentClient == null ) {
				currentClient = stFacade.getDefaultClient();
				if(currentClient == null){
					log.error("currentClient not set! Start again");
					return processStart();
				}
			}

			try {
				stFacade.processStockCount(currentClient == null ? null : currentClient.getNumber(), 
												   stockTakingLocationName, 
												   unitLoadLabel,
												   currentItem, 
												   currentLot, 
												   currentSerial, 
												   amount,
												   currentULType);
				
				// clear counted stock data
				countedStocks++;
				currentItem = "";
				currentUnit = "";
				currentLot = "";
				currentStock = "";
				countedAmount = "";
				currentSerial = "";

			
			} catch (LOSStockTakingException e) {
				log.error("LOSStockTakingException: " + e.getMessage(), e);
				JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
				return StockTakingNavigation.COUNT_STOCKS.name();
			} catch (UnAuthorizedException e) {
				log.error("UnAuthorizedException: " + e.getMessage(), e);
				JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
				return StockTakingNavigation.COUNT_STOCKS.name();
			} catch (Exception e) {
				log.error("Exception: " + e.getMessage(), e);
				JSFHelper.getInstance().message(e.getMessage());
				return StockTakingNavigation.COUNT_STOCKS.name();
			}
			
		}
		
		countedUnitLoadList.add(unitLoadLabel);

		// Remove the current UnitLoad from the list of expected UnitLoads, if complete counted
		// In further masks, the cancellation may bring undefined results
		if(unitLoadToCount != null){
			if(expectedUnitLoadList.contains(unitLoadToCount)){
				expectedUnitLoadList.remove(unitLoadToCount);
			}
		}

		if( expectedStockList == null || expectedStockList.size() == 0 ) {
			return StockTakingNavigation.NEW_ITEMDATA.name();
		}
		else {
			return StockTakingNavigation.COUNT_STOCKS.name();
		}
	}
	
	//---------------------------------------------------------------------
	public String processStockCounted_FinishUnitLoad(){
		String methodName = "processStockCounted_FinishUnitLoad ";
		log.debug(methodName+"Start");
		
		if(currentItem.length()>0){
			
			if(countedAmount.length()==0){
				JSFHelper.getInstance().message(resolve("MsgEnterAmount"));
				return StockTakingNavigation.COUNT_STOCKS.name();
			}
			
			processStockCounted_Next();

			
		}
		
		// if there are still planned stocks
		if(expectedStockList!=null && expectedStockList.size()>0){
			return StockTakingNavigation.CONFIRM_STOCKS_MISSING.name();
		}
		
		
		if(unitLoadToCount != null){
			
			if(expectedUnitLoadList.contains(unitLoadToCount)){
				expectedUnitLoadList.remove(unitLoadToCount);
			}
		}
		
		unitLoadLabel = "";
		currentULType = "";
		expectedStockList = null;
		countedStocks = 0;
		currentItem = "";
		currentUnit = "";
		currentLot = "";
		currentStock = "";
		countedAmount = "";
		currentSerial = "";

		return StockTakingNavigation.SCAN_UNITLOAD.name();
	}
	
	//---------------------------------------------------------------------
	public String confirmLocationEmpty(){
						
		try {
			stFacade.processLocationEmpty(stockTakingLocation);
			lastLocationName = stockTakingLocationName;
			stockTakingLocationName = "";
		} catch (LOSStockTakingException e) {
			log.error("LOSStockTakingException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			return "";
		}
		
		return StockTakingNavigation.SCAN_LOCATION.name();
	}
	
	//---------------------------------------------------------------------
	public String cancelLocationEmpty(){
		
		return StockTakingNavigation.SCAN_LOCATION.name();
		
	}
	
	//---------------------------------------------------------------------
	public String backToMenu(){
	
		lastLocationName = "";
		stockTakingLocationName = "";
		
		return StockTakingNavigation.BACK_TO_MENU.name();
	}
	
	//---------------------------------------------------------------------
	public String getPlannedUnitLoads(){
		
		StringBuffer sb = new StringBuffer();
		
		int i = 0;
		for(LOSUnitLoad ul:expectedUnitLoadList){
			if( i++ > 0 ) {
				sb.append(", ");
			}
			
			sb.append(ul.getLabelId());
		}
		
		return sb.toString();
	}
	
	//---------------------------------------------------------------------
	public String getCountedUnitLoads(){
		
		StringBuffer sb = new StringBuffer();
		
		int i = 0;
		for(String ul:countedUnitLoadList){
			if( i++ > 0 ) {
				sb.append(", ");
			}
			sb.append(ul);
		}
		
		return sb.toString();
		
	}
	
	//---------------------------------------------------------------------
	public String getUnitLoadCount(){
		String ret = "-";
		if( countedUnitLoadList != null && countedUnitLoadList.size() > 0 ) {
			ret = String.valueOf(countedUnitLoadList.size());
		}
		return ret;
	}

	//---------------------------------------------------------------------
	public String getStockUnitCount(){
		String ret = "";
		int expected = 0;
		if( expectedStockList != null && expectedStockList.size() > 0 ) {
			expected = expectedStockList.size();
			expected += countedStocks;
			
			ret = "" + (countedStocks+1) + " / " + expected;
		}
		else {
			ret = "" + (countedStocks+1);
		}
		
		return ret;
	}

	//---------------------------------------------------------------------
	public boolean isEnableFinishUl(){
		if( expectedStockList == null ) 
			return true;
		if( expectedStockList.size() <= 1 ) 
			return true;
		
		return false;
	}

	//---------------------------------------------------------------------
	public String getMissingStocks(){
		
		StringBuffer sb = new StringBuffer();
		
		int i = 0;
		for(StockUnit su:expectedStockList){
			if( i++ > 0 ) {
				sb.append(", ");
			}
			
			sb.append("Artikel="+su.getItemData().getNumber());
		}
		
		return sb.toString();
	}
	
	//---------------------------------------------------------------------
	public String confirmStocksMissing(){
						
		try {
			for(StockUnit su:expectedStockList){
				
				stFacade.processStockCount(su, BigDecimal.ZERO);
			}
			
			countedUnitLoadList.add(unitLoadLabel);
			
			if(unitLoadToCount != null){
				
				if(expectedUnitLoadList.contains(unitLoadToCount)){
					expectedUnitLoadList.remove(unitLoadToCount);
				}
			}
			
			// clear counted stock data
			expectedStockList = null;
			countedStocks = 0;
			currentItem = "";
			currentUnit = "";
			currentLot = "";
			currentStock = "";
			countedAmount = "";
			expectedStock = null;
			unitLoadLabel = "";
			currentSerial = "";
			
		} catch (LOSStockTakingException e) {
			log.error("LOSStockTakingException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(e.getLocalizedMessage(getLocale()));
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			JSFHelper.getInstance().message(resolve("MsgNotAuthozized"));
			return "";
		}
		
		return StockTakingNavigation.SCAN_UNITLOAD.name();
	}
	
	//---------------------------------------------------------------------
	public String cancelStocksMissing(){
		
		return StockTakingNavigation.COUNT_STOCKS.name();
		
	}
	
	//---------------------------------------------------------------------
	protected LOSStorageLocation getStorageLocation(){
		
		try {
			LOSStorageLocation sl = queryLocationService.getByName(stockTakingLocationName);

			return sl;
			
		} catch (UnAuthorizedException e) {
			log.error("UnAuthorizedException: " + e.getMessage(), e);
			return null;
		}
		
	}
	
	//---------------------------------------------------------------------
	
	public boolean isExpectedStock(){
		
		if(expectedStock != null){
			return true;
		}
		else{
			return false;
		}
	}

	public String cancelNew() {
		String methodName = "cancelNew ";
		log.info(methodName+"Start cancel stocktaking of location <" + stockTakingLocationName + "> unitLoadLabel <" + unitLoadLabel + ">");
		
		currentItem = "";
		currentUnit = "";
		currentLot = "";
		currentStock = "";
		currentULType = "";
		countedAmount = "";
		expectedStock = null;
		unitLoadLabel = "";
		currentSerial = "";


		return StockTakingNavigation.SCAN_UNITLOAD.name();
	}

	public String newItemData() {
		currentItem = currentItem == null ? "" : currentItem.trim();
		if( currentItem.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterItemData"));
			return StockTakingNavigation.NEW_ITEMDATA.name();
		}
		
		ItemData itemData = queryItemData.getByItemNumber(currentItem);
		if( itemData == null ) {
			JSFHelper.getInstance().message(resolve("MsgItemDataUndefined"));
			return StockTakingNavigation.NEW_ITEMDATA.name();
		}
		currentItem = itemData.getNumber();
		
		if( currentClient == null ) {
			currentClient = stFacade.getDefaultClient();
			if(currentClient == null){
				log.error("currentClient not set! Start again");
				return processStart();
			}
		}

		if( !currentClient.equals(itemData.getClient()) ) {
			JSFHelper.getInstance().message(resolve("MsgItemDataDiffClient"));
			return StockTakingNavigation.NEW_ITEMDATA.name();
		}
		
		currentUnit = itemData.getHandlingUnit().getUnitName();

		if( itemData.isLotMandatory() ) { 
			return StockTakingNavigation.NEW_LOT.name();
		}
		
		if( itemData.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) { 
			return StockTakingNavigation.NEW_SERIAL.name();
		}
		
		return StockTakingNavigation.COUNT_STOCKS.name();
	}
	
	public String newUnitloadType() {
		currentULType = currentULType == null ? "" : currentULType.trim();
		if( currentULType.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterUnitLoadType"));
			return StockTakingNavigation.NEW_ULTYPE.name();
		}
		
		return StockTakingNavigation.NEW_ITEMDATA.name();
	}
	
	public String newLot() {
		currentLot = currentLot == null ? "" : currentLot.trim();
		if( currentLot.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterLot"));
			return StockTakingNavigation.NEW_LOT.name();
		}

		ItemData itemData = queryItemData.getByItemNumber(currentItem);
		if( itemData == null ) {
			JSFHelper.getInstance().message(resolve("MsgItemDataUndefined"));
			return StockTakingNavigation.NEW_ITEMDATA.name();
		}
		
		if( itemData.getSerialNoRecordType() == SerialNoRecordType.ALWAYS_RECORD ) { 
			return StockTakingNavigation.NEW_SERIAL.name();
		}

		return StockTakingNavigation.COUNT_STOCKS.name();
	}
	
	public String newSerial() {
		currentSerial = currentSerial == null ? "" : currentSerial.trim();
		if( currentSerial.length() == 0 ) {
			JSFHelper.getInstance().message(resolve("MsgEnterSerialNo"));
			return StockTakingNavigation.NEW_SERIAL.name();
		}
		
		return StockTakingNavigation.COUNT_STOCKS.name();
	}
	

	public void ulTypeChanged(ValueChangeEvent vce) {
        currentULType = (String)vce.getNewValue();
		log.info("Setting ULType <"+currentULType+">");
    }

	public String getLocationProposal() {
		log.info("Search next location...");
		return stFacade.getNextLocation( currentClient, lastLocationName );

	}

	public String getStockTakingLocationName() {
		return stockTakingLocationName;
	}

	public void setStockTakingLocationName(String stockTakingLocation) {
		this.stockTakingLocationName = stockTakingLocation;
	}

	public String getUnitLoadLabel() {
		return unitLoadLabel;
	}

	public void setUnitLoadLabel(String unitLoadLabel) {
		this.unitLoadLabel = unitLoadLabel;
	}
	
	public String getCurrentStock(){
		
		return currentStock;
	}
	
	public void setCurrentStock(String stock){
		currentStock = stock;
	}
	
	public String getCurrentItem(){
		return currentItem;
	}
	
	public void setCurrentItem(String item){
		currentItem = item;
	}
	
	public String getCurrentUnit() {
		return currentUnit;
	}

	public String getCurrentLot(){
		return currentLot;
	}
	
	public void setCurrentLot(String lot){
		currentLot = lot;
	}

	public String getCountedAmount() {
		return countedAmount;
	}

	public void setCountedAmount(String countedAmount) {
		this.countedAmount = countedAmount;
	}

	public String getCurrentSerial() {
		return currentSerial;
	}

	public void setCurrentSerial(String serialNo) {
		this.currentSerial = serialNo;
	}

	public List<SelectItem> getUlTypeList() {
		List<SelectItem> sel = new ArrayList<SelectItem>();
		for( String ult : ulTypeList ) {
			sel.add( new SelectItem(ult) );
		}
		return sel;
	}

	public String getCurrentULType() {
		return currentULType;
	}

	public void setCurrentULType(String currentULType) {
		this.currentULType = currentULType;
	}

	public String getInputClientNumber() {
		
		try {
			LOSResultList<Client> resList = queryClient.queryAll(new QueryDetail(0, Integer.MAX_VALUE));
			if(resList.size() == 1){
				inputClientNumber = resList.get(0).getNumber();
			}
			else{
				inputClientNumber = "";
			}
		} catch (BusinessObjectQueryException e) {
			// if query fails, do not initialize client input field
			// not critical
		}
		
		return inputClientNumber;
	}

	public void setInputClientNumber(String inputClientNumber) {
		this.inputClientNumber = inputClientNumber;
	}

	protected ResourceBundle getResourceBundle() {
		ResourceBundle bundle;
		Locale loc;
		loc = getUIViewRoot().getLocale();
		bundle = ResourceBundle.getBundle("de.linogistix.mobile.processes.stocktaking.StockTakingBundle", loc);
		return bundle;
	}
	
}
