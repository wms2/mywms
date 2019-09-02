/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 * www.linogistix.com
 * 
 * Project: myWMS-LOS
*/
package de.linogistix.mobile.processes.goodsreceipt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.service.EntityNotFoundException;

import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.inventory.facade.LOSGoodsReceiptFacade;
import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.query.LOSAdviceQueryRemote;
import de.linogistix.los.inventory.service.QueryGoodsReceiptServiceRemote;
import de.linogistix.los.inventory.service.QueryLotServiceRemote;
import de.linogistix.los.inventory.service.dto.GoodsReceiptTO;
import de.linogistix.los.location.query.UnitLoadTypeQueryRemote;
import de.linogistix.los.location.service.QueryUnitLoadTypeServiceRemote;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.exception.BusinessObjectNotFoundException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.DateHelper;
import de.linogistix.los.util.entityservice.LOSSystemPropertyServiceRemote;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.strategy.OrderState;

public class GoodsReceiptBean extends BasicDialogBean {
	protected Logger log = Logger.getLogger(GoodsReceiptBean.class);

	private UnitLoadTypeQueryRemote unitLoadTypeQuery;

	protected QueryLotServiceRemote queryLotService;
	
	protected QueryUnitLoadTypeServiceRemote queryUltService;
	
	protected LOSAdviceQueryRemote queryAdviceService;
	
	protected QueryGoodsReceiptServiceRemote queryGoodsReceiptService;
	
	protected LOSSystemPropertyServiceRemote propQuery;
	
	protected LOSGoodsReceiptFacade goodsReceiptFacade;
	
	protected GoodsReceipt currentGoodsReceipt;
	
	protected GoodsReceiptTO selectedGoodsReceiptTO;
	
	protected HashMap<String, GoodsReceiptTO> goodsReceiptMap;
	
	protected AdviceLine currentAdvice;
	
	protected HashMap<String, AdviceLine> adviceMap;
	
	protected Lot currentLot;
	
	protected Date currentValidToDate;
	
	protected UnitLoadType currentUnitLoadType;
	
	protected BigDecimal currentAmountPerUnitLoad;
	
	protected BigDecimal currentPostedAmount = BigDecimal.ZERO;
	
	protected int currentAmountOfProcessedUnitLoads = 0;
	
	protected HashMap<String, UnitLoadType> unitLoadTypeMap;
	
	protected String lotName_Input = "";
	
	protected String validTo_Input = "";
	
	protected String serialNo_Input = "";
	
	protected String amountPerUnitLoad_Input = "";
	
	protected String unitLoadLabel_Input = "";
	
	protected String stockLabel_Input = "";
	
	protected String amountPerStock_Input = "";
	
	protected boolean processingSingleStock = false;
	
	protected boolean adviceComboBoxChanged = false;
		
	public GoodsReceiptBean(){
		super();
		
		propQuery = super.getStateless(LOSSystemPropertyServiceRemote.class);
		queryLotService = super.getStateless(QueryLotServiceRemote.class);
		queryAdviceService = super.getStateless(LOSAdviceQueryRemote.class);
		queryUltService = super.getStateless(QueryUnitLoadTypeServiceRemote.class);
		queryGoodsReceiptService = super.getStateless(QueryGoodsReceiptServiceRemote.class);
		goodsReceiptFacade = super.getStateless(LOSGoodsReceiptFacade.class);
		unitLoadTypeQuery = super.getStateless(UnitLoadTypeQueryRemote.class);

	}
	public String getNavigationKey() {
		return GoodsReceiptNavigationEnum.GOODS_RECEIPT.name();
	}
	
	public String getTitle() {
		return resolve("GOODS_IN");
	}

	//---------------------------------------------------------------------
	public List<SelectItem> getGoodsReceiptList(){
		log.info("getGoodsReceiptList");

		if( goodsReceiptMap == null ) {
			log.info("Read new Map");
			goodsReceiptMap = new HashMap<String, GoodsReceiptTO>();
		
			List<GoodsReceiptTO> grList;
			grList = queryGoodsReceiptService.getDtoListByStates(OrderState.CREATED, OrderState.STARTED);
			
			for(GoodsReceiptTO gr:grList){
				StringBuffer label = new StringBuffer(gr.getGoodsReceiptNo()+"/");
				label.append(gr.getSuplier()+"/");
				label.append(gr.getDeliveryNoteNo());
//				log.info("Add: " + gr.getGoodsReceiptNo());
	
				goodsReceiptMap.put(gr.getGoodsReceiptNo(), gr);
			}
		}
		
		List<SelectItem> selList = new ArrayList<SelectItem>();
		for( Entry<String,GoodsReceiptTO> entry : goodsReceiptMap.entrySet()) {
			GoodsReceiptTO gr = entry.getValue();
			StringBuffer label = new StringBuffer(gr.getGoodsReceiptNo()+"/");
			label.append(gr.getSuplier()+"/");
			label.append(gr.getDeliveryNoteNo());
			selList.add(new SelectItem(entry.getKey(), label.toString()));
		}
		
		return selList;
	}
	
	//---------------------------------------------------------------------
	public void setSelectedGoodsReceipt(String sel){
		selectedGoodsReceiptTO = (goodsReceiptMap == null) ? null : goodsReceiptMap.get(sel);
	}
	
	//---------------------------------------------------------------------
	public String getSelectedGoodsReceipt(){
		return (selectedGoodsReceiptTO == null) ? null : selectedGoodsReceiptTO.getGoodsReceiptNo();
	}
	
	//---------------------------------------------------------------------
	public String processGoodsReceiptChoosen(){
		goodsReceiptMap = null;

		if( selectedGoodsReceiptTO == null ) {
			JSFHelper.getInstance().message(resolve("javax.faces.component.UIInput.REQUIRED"));
			return "";
		}
		try {
			currentGoodsReceipt = queryGoodsReceiptService.fetchEager(selectedGoodsReceiptTO.getId());
			
			if(currentGoodsReceipt.getAdviceLines().size() == 0){
				JSFHelper.getInstance().message(super.resolve("NO_ADVICES_ASSIGNED", new Object[]{}));
				return "";
			}
			
		} catch (EntityNotFoundException e) {
			JSFHelper.getInstance().message("Gewaehlter WE wurde geloescht!");
			return "";
		} catch (UnAuthorizedException e) {
			JSFHelper.getInstance().message("NOT AUTHORIZED");
			return "";
		}
		
		return GoodsReceiptNavigationEnum.CHOOSE_ADVICE.name();
	}
	
	//---------------------------------------------------------------------
	public List<SelectItem> getAssignedAdviceList(){
		
		adviceMap = new HashMap<String, AdviceLine>();
		
		List<AdviceLine> adList = currentGoodsReceipt.getAdviceLines();
		
		List<SelectItem> selList = new ArrayList<SelectItem>(adList.size());
		
		for(AdviceLine ad:adList){
			
			StringBuffer label = new StringBuffer(ad.getItemData().getNumber()+"/");
			if(ad.getLotNumber() != null){
				label.append(ad.getLotNumber()+"/");
			}else{
				label.append("---/");
			}
			label.append(ad.getAmount());
			
			adviceMap.put(ad.getLineNumber(), ad);
			selList.add(new SelectItem(ad.getLineNumber(), label.toString()));
		}
		
		if(currentAdvice == null && adList.size() > 0){
			currentAdvice = adList.get(0);
		}
		
		return selList;
	}
	
	//---------------------------------------------------------------------
	public void setSelectedAdvice(String sel){
		
		currentAdvice = adviceMap.get(sel);
	}
	
	//---------------------------------------------------------------------
	public String getSelectedAdvice(){
		
		if(currentAdvice != null){
			return currentAdvice.getLineNumber();
		}
		else{
			return null;
		}
	}
	
	//---------------------------------------------------------------------
	public void adviceComboBoxValueChanged(ValueChangeEvent vce){
		
		adviceComboBoxChanged = true;
				
		currentAdvice = adviceMap.get(vce.getNewValue());
		
	}
	
	//---------------------------------------------------------------------
	public String getChooseAdviceSubmited(){
		
		// onSubmit clear input fields
		
		if(adviceComboBoxChanged){
			lotName_Input = "";
			validTo_Input = "";
			adviceComboBoxChanged = false;
		}
		
		return "";
	}
	
	//---------------------------------------------------------------------
	public List<SelectItem> getUnitLoadTypeList(){
		
		unitLoadTypeMap = new HashMap<String, UnitLoadType>();
		
		List<UnitLoadType> ultList = queryUltService.getSortedList(true, false, false, false, false);
		
		List<SelectItem> selList = new ArrayList<SelectItem>(ultList.size());
		
		for(UnitLoadType ult:ultList){
			
			unitLoadTypeMap.put(ult.getName(), ult);
			selList.add(new SelectItem(ult.getName()));
		}
		
		return selList;
	}
	
	//---------------------------------------------------------------------
	public void setSelectedUnitLoadType(String sel){
		
		currentUnitLoadType = unitLoadTypeMap.get(sel);
	}
	
	//---------------------------------------------------------------------
	public String getSelectedUnitLoadType(){
		
		if(currentUnitLoadType != null){
			return currentUnitLoadType.getName();
		}
		else{
			return null;
		}
	}
	
	//---------------------------------------------------------------------
	public String processAdviceChoosen(){
		lotName_Input = lotName_Input == null ? "" : lotName_Input.trim();
		
		ItemData item =currentAdvice.getItemData();
		currentUnitLoadType = item.getDefaultUnitLoadType();
		currentUnitLoadType = reloadUnitLoadType(currentUnitLoadType);

		if( lotName_Input.length() == 0 ){
			if( item.isLotMandatory() ){
				JSFHelper.getInstance().message(super.resolve("LOT_INPUT_REQUIRED", new Object[]{}));
				return GoodsReceiptNavigationEnum.CHOOSE_ADVICE.name();
			}
			else {
				currentLot = null;
				return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();
			}
		}
		if(validTo_Input == ""){
			if(item.getShelflife() > 0){
				JSFHelper.getInstance().message(super.resolve("VALIDTO_INPUT_REQUIRED", new Object[]{}));
				return GoodsReceiptNavigationEnum.CHOOSE_ADVICE.name();
			}
		}
		else{
			currentValidToDate = DateHelper.parseInputString(validTo_Input);
			if( currentValidToDate == null ) {
				JSFHelper.getInstance().message(super.resolve("INVALID_DATE_INPUT", new Object[]{}));
				return GoodsReceiptNavigationEnum.CHOOSE_ADVICE.name();
			}
		}
		
		// Check if the valid period of received goods is long enough
		if(item.getShelflife() > 0){
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, item.getShelflife());
			Date minimumValidTo = cal.getTime();
			
			if(minimumValidTo.after(currentValidToDate)){
				JSFHelper.getInstance().message(
						super.resolve("VALID_PERIOD_TO_SHORT", 
						new Object[]{""+item.getShelflife()})
				);
				return GoodsReceiptNavigationEnum.CHOOSE_ADVICE.name();
			}
			
		}
		
		currentUnitLoadType = item.getDefaultUnitLoadType();
		currentUnitLoadType = reloadUnitLoadType(currentUnitLoadType);
		
		currentLot = queryLotService.getByNameAndItemData(lotName_Input, item);
		if(currentLot != null){
			return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();
		}
		else{
			return GoodsReceiptNavigationEnum.CONFIRM_NEW_LOT.name();
		}
	}
	
	//---------------------------------------------------------------------
	public String confirmNewLot(){
		
		currentLot = goodsReceiptFacade.createLot(
							lotName_Input, 
							currentAdvice.getItemData(), 
							currentValidToDate);
		
		return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();
	}
	
	//---------------------------------------------------------------------
	public String cancelNewLot(){
		return GoodsReceiptNavigationEnum.CHOOSE_ADVICE.name();
	}
	
	//---------------------------------------------------------------------
	public String postUnitLoad(){
		
		ItemData item = currentAdvice.getItemData();
		
		
		try{
			currentAmountPerUnitLoad = new BigDecimal(amountPerUnitLoad_Input);
		}catch(NumberFormatException nfe){
			
			JSFHelper.getInstance().message(super.resolve("INVALID_AMOUNT_INPUT", new Object[]{}));
			return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();
		}
		
		if(item.getSerialNoRecordType().equals(SerialNoRecordType.ALWAYS_RECORD) && !processingSingleStock && !BigDecimal.ONE.equals(currentAmountPerUnitLoad)) {
			JSFHelper.getInstance().message(super.resolve("PROCESSING_SINGLESTOCK_REQUIRED", new Object[]{}));
			return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();
		}
		
		Client cl = currentGoodsReceipt.getClient();

		int lock = (int)propQuery.getLongDefault(cl, getWorkstationName(), LOSInventoryPropertyKey.GOODS_IN_DEFAULT_LOCK, 0);

		try {
			BODTO<Lot> lotTo = null;
			if( currentLot != null ) {
				lotTo = new BODTO<Lot>(currentLot.getId(), currentLot.getVersion(), currentLot.getName());
			}

			goodsReceiptFacade.createGoodsReceiptLine(
										new BODTO<Client>(cl.getId(), cl.getVersion(), cl.getNumber()), 
										currentGoodsReceipt, 
										lotTo, 
										new BODTO<ItemData>(item.getId(), item.getVersion(), item.getNumber()),
										unitLoadLabel_Input,
										new BODTO<UnitLoadType>(currentUnitLoadType.getId(), currentUnitLoadType.getVersion(), currentUnitLoadType.getName()),
										currentAmountPerUnitLoad,
										new BODTO<AdviceLine>(currentAdvice.getId(), currentAdvice.getVersion(), currentAdvice.getLineNumber()),
										lock, "");

			currentAmountOfProcessedUnitLoads++;
			currentPostedAmount = currentPostedAmount.add(currentAmountPerUnitLoad);
			
			// update advice
			currentAdvice = queryAdviceService.queryById(currentAdvice.getId());
			adviceMap.put(currentAdvice.getLineNumber(), currentAdvice);
			
		} catch (FacadeException e) {
//			e.printStackTrace();
			JSFHelper.getInstance().message(e.getLocalizedMessage(super.getLocale()));
			return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return GoodsReceiptNavigationEnum.GOODSRECEIPT_SUMMARY.name();
	}
	
	//---------------------------------------------------------------------
	public String processNextUnitLoad(){
		
		unitLoadLabel_Input = "";
		processingSingleStock = false;
		
		return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();	
		
	}
	
	//---------------------------------------------------------------------
	public String processSingleStockChoosen(){
		
		ItemData item = currentAdvice.getItemData();
		
		if(item.getSerialNoRecordType().equals(SerialNoRecordType.ALWAYS_RECORD)){
			amountPerStock_Input = "1";
		}
		
		processingSingleStock = true;
		
		return GoodsReceiptNavigationEnum.SINGLE_STOCK.name();
	}
	
	//---------------------------------------------------------------------
	public String nextStock(){
		
		postStock();
		
		amountPerStock_Input = "";
		serialNo_Input = "";
		
		return GoodsReceiptNavigationEnum.SINGLE_STOCK.name();
	}
	
	//---------------------------------------------------------------------
	public String finishStockProcessing(){
		
		postStock();
		
		amountPerStock_Input = "";
		serialNo_Input = "";
		processingSingleStock = false;
		
		return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();
	}
	
	//---------------------------------------------------------------------
	private String postStock(){
		
		return "TODO";
		
	}
	
	//---------------------------------------------------------------------
	public String cancelProcessingSingleStock(){
		
		amountPerStock_Input = "";
		processingSingleStock = false;
		
		return GoodsReceiptNavigationEnum.PROCESS_UNITLOAD.name();
	}
	
	//---------------------------------------------------------------------
	public String processChangeStockData(){

		lotName_Input = "";
		validTo_Input = "";
		serialNo_Input = "";
		amountPerStock_Input = "";
		amountPerUnitLoad_Input = "";
		unitLoadLabel_Input = "";
		processingSingleStock = false;
		
		currentAdvice = null;
		currentAmountPerUnitLoad = null;
		currentLot = null;
		currentUnitLoadType = null;
		currentValidToDate = null;
		currentPostedAmount = BigDecimal.ZERO;
		currentAmountOfProcessedUnitLoads = 0;
		
		return GoodsReceiptNavigationEnum.CHOOSE_ADVICE.name();
	}
	
	//---------------------------------------------------------------------
	public String finishGoodsReceiptProcess(){
		resetData();
		
		return GoodsReceiptNavigationEnum.BACK_TO_MENU.name();
	}
	
	//---------------------------------------------------------------------
	public String getLotName_Input(){
		if(currentAdvice.getLotNumber() != null
			&& lotName_Input.equals(""))
		{
			lotName_Input = currentAdvice.getLotNumber();
		}
		
		return lotName_Input;
		
	}
	
	//---------------------------------------------------------------------
	public void setLotName_Input(String lotName){
		
		lotName_Input = lotName;
	}
	
	//---------------------------------------------------------------------
	public String getCurrentItemNumber(){
		return currentAdvice.getItemData().getNumber();
	}
	
	//---------------------------------------------------------------------
	public String getCurrentNotifiedAmount(){
		return currentAdvice.getAmount().toString();
	}
	
	//---------------------------------------------------------------------
	public String getCurrentAmountToCome(){
		BigDecimal atc = currentAdvice.getAmount().subtract(currentAdvice.getConfirmedAmount());
		return atc.toString();
	}
	
	//---------------------------------------------------------------------
	public String getValidTo_Input() {
		return validTo_Input;
	}

	//---------------------------------------------------------------------
	public void setValidTo_Input(String validToInput) {
		validTo_Input = validToInput;
	}

	//---------------------------------------------------------------------
	public boolean isSerialRequired(){
		
		if(currentAdvice.getItemData().getSerialNoRecordType().equals(SerialNoRecordType.ALWAYS_RECORD)){
			return true;
		}
		else{
			return true;
		}
	}
	
	//---------------------------------------------------------------------
	public String getSerialNo_Input() {
		return serialNo_Input;
	}

	//---------------------------------------------------------------------
	public void setSerialNo_Input(String serialNoInput) {
		serialNo_Input = serialNoInput;
	}

	//---------------------------------------------------------------------
	public String getStockLabel_Input() {
		return stockLabel_Input;
	}

	//---------------------------------------------------------------------
	public void setStockLabel_Input(String stockLabelInput) {
		stockLabel_Input = stockLabelInput;
	}

	//---------------------------------------------------------------------
	public String getAmountPerStock_Input() {
		return amountPerStock_Input;
	}

	//---------------------------------------------------------------------
	public void setAmountPerStock_Input(String amountPerStockInput) {
		amountPerStock_Input = amountPerStockInput;
	}

	//---------------------------------------------------------------------
	public String getAmountPerUnitLoad_Input() {
		return amountPerUnitLoad_Input;
	}

	//---------------------------------------------------------------------
	public void setAmountPerUnitLoad_Input(String amountInput) {
		amountPerUnitLoad_Input = amountInput;
	}

	//---------------------------------------------------------------------
	public String getUnitLoadLabel_Input() {
		return unitLoadLabel_Input;
	}

	//---------------------------------------------------------------------
	public void setUnitLoadLabel_Input(String unitLoadLabelInput) {
		unitLoadLabel_Input = unitLoadLabelInput.trim();
	}

	//---------------------------------------------------------------------
	public int getCurrentAmountOfProcessedUnitLoads() {
		return currentAmountOfProcessedUnitLoads;
	}

	//---------------------------------------------------------------------
	public void setCurrentAmountOfProcessedUnitLoads(int amountOfProcessedUnitLoads) {
		this.currentAmountOfProcessedUnitLoads = amountOfProcessedUnitLoads;
	}

	public String getPostedAmount(){
		return currentPostedAmount.toString();
	}
	
	public String getCurrentUnit() {
		return currentAdvice.getItemData().getItemUnit().getName();
	}

	//---------------------------------------------------------------------
	public String backToMenu(){
		resetData();
		
		return GoodsReceiptNavigationEnum.BACK_TO_MENU.name();
	}
	
	//---------------------------------------------------------------------
	public String backToSelection(){
		resetData();
		
		return GoodsReceiptNavigationEnum.GOODS_RECEIPT.toString();
	}
	
	
	

	private void resetData() {
		selectedGoodsReceiptTO = null;
		goodsReceiptMap = null;
		processingSingleStock = false;
		
		currentGoodsReceipt = null;
		currentAdvice = null;
		currentLot = null;
		currentValidToDate = null;
		currentUnitLoadType = null;
		currentAmountOfProcessedUnitLoads = 0;
		currentAmountPerUnitLoad = null;
		currentPostedAmount = BigDecimal.ZERO;
		
		lotName_Input = "";
		validTo_Input = "";
		serialNo_Input = "";
		amountPerStock_Input = "";
		amountPerUnitLoad_Input = "";
		unitLoadLabel_Input = "";
		stockLabel_Input = "";
		
	}

	private UnitLoadType reloadUnitLoadType(UnitLoadType type) {
		if (type != null) {
			try {
				// Just to load lazy loaded entity
				type = unitLoadTypeQuery.queryById(type.getId());
			} catch (BusinessObjectNotFoundException | BusinessObjectSecurityException e) {
				// ignore
			}
		}
		return type;
	}
}
