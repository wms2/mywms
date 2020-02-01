/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.inventory.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;
import org.mywms.globals.SerialNoRecordType;
import org.mywms.model.Client;
import org.mywms.model.User;

import de.linogistix.los.common.businessservice.LOSPrintService;
import de.linogistix.los.common.exception.UnAuthorizedException;
import de.linogistix.los.common.service.QueryClientService;
import de.linogistix.los.inventory.businessservice.LOSGoodsReceiptComponent;
import de.linogistix.los.inventory.crud.StockUnitCRUDRemote;
import de.linogistix.los.inventory.exception.InventoryException;
import de.linogistix.los.inventory.exception.InventoryExceptionKey;
import de.linogistix.los.inventory.model.LOSInventoryPropertyKey;
import de.linogistix.los.inventory.service.InventoryGeneratorService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.report.ReportException;
import de.linogistix.los.runtime.BusinessObjectSecurityException;
import de.linogistix.los.util.businessservice.ContextService;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.document.Document;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.goodsreceipt.GoodsReceiptBusiness;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.inventory.InventoryBusiness;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.StockUnitReportGenerator;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.inventory.UnitLoadEntityService;
import de.wms2.mywms.inventory.UnitLoadType;
import de.wms2.mywms.inventory.UnitLoadTypeEntityService;
import de.wms2.mywms.location.StorageLocation;
import de.wms2.mywms.location.StorageLocationEntityService;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.product.ItemDataEntityService;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.strategy.FixAssignmentEntityService;
import de.wms2.mywms.strategy.OrderState;

@Stateless
public class LOSGoodsReceiptFacadeBean implements LOSGoodsReceiptFacade {

    private final Logger logger = Logger.getLogger(LOSGoodsReceiptFacadeBean.class);
    
    @EJB
    private LOSGoodsReceiptComponent receiptComponent;
    @EJB
    private StockUnitCRUDRemote suCrud;
    @EJB
    private InventoryGeneratorService genService;
    @EJB
    private LOSSystemPropertyService propertyService;
	@EJB
	private ContextService contextService;
	@Inject
	private FixAssignmentEntityService fixService;
	@Inject
	private StockUnitReportGenerator suLabelReport;
	@EJB
	private QueryClientService queryClientService;
	@EJB
	private LOSPrintService printService;
	
	@Inject
	private UnitLoadEntityService unitLoadService;
	@Inject
	private StorageLocationEntityService locationService;
	@Inject
	private UnitLoadTypeEntityService unitLoadTypeService;
	@Inject
	private InventoryBusiness inventoryBusiness;
	@Inject
	private StockUnitEntityService stockUnitEntityService;
	@Inject
	private GoodsReceiptBusiness goodsReceiptBusiness;
	@Inject
	private ItemDataEntityService itemDataEntityService;
	@Inject
	private PersistenceManager manager;

    //-----------------------------------------------------------------------
    // Query data for Process input to choose from
    //----------------------------------------------------------------------

	@Override
    public List<BODTO<StorageLocation>> getGoodsReceiptLocations() throws LOSLocationException {
        List<StorageLocation> sls = locationService.getForGoodsIn(null);
        List<BODTO<StorageLocation>> ret = new ArrayList<BODTO<StorageLocation>>();
        for (StorageLocation sl : sls) {
            ret.add(new BODTO<StorageLocation>(sl.getId(), sl.getVersion(), sl.getName()));
        }

        return ret;
    }


    //------------------------------------------------------------------------
    // Receive Entity data from database
    //------------------------------------------------------------------------
	public GoodsReceipt getGoodsReceipt(GoodsReceipt lazy) {
		return manager.eagerRead(manager.reload(GoodsReceipt.class, lazy.getId()), false);
	}

    //------------------------------------------------------------------------
    // Process
    //-----------------------------------------------------------------------
    public GoodsReceipt createGoodsReceipt(BODTO<Client> clientDto, String forwarder, String senderName, String deliveryNoteNumber, Date receiptDate,
			BODTO<StorageLocation> goodsInLocation, String additionalContent, int orderType) {
        GoodsReceipt goodsReceipt;
        StorageLocation location = manager.find(StorageLocation.class, goodsInLocation.getId());
        Client client = manager.find(Client.class, clientDto.getId());

        goodsReceipt = goodsReceiptBusiness.createOrder(client, location);
        goodsReceipt.setAdditionalContent(additionalContent);
        goodsReceipt.setSenderName(senderName);
        goodsReceipt.setCarrierName(forwarder);
        goodsReceipt.setDeliveryNoteNumber(deliveryNoteNumber);
        goodsReceipt.setOrderType(orderType);
        goodsReceipt.setReceiptDate(receiptDate);

        return goodsReceipt;
    }

    public GoodsReceiptLine createGoodsReceiptLine(
            BODTO<Client> client,
            GoodsReceipt gr,
            String lotNumber, 
            Date bestBefore,
            BODTO<ItemData> item,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadType, 
            BigDecimal amount,
            BODTO<AdviceLine> advice,
            int lock,
            String lockCause) throws FacadeException, ReportException {
        return createGoodsReceiptLine(
                client, gr, lotNumber, bestBefore,item, unitLoadLabel, unitLoadType, amount, advice,
                lock, lockCause, null, null, null, null);
    }


    public GoodsReceiptLine createGoodsReceiptLine(
            BODTO<Client> clientDto,
            GoodsReceipt goodsReceiptDto,
            String lotNumber,
            Date bestBefore,
            BODTO<ItemData> itemDataDto,
            String unitLoadLabel,
            BODTO<UnitLoadType> unitLoadTypeDto, 
            BigDecimal amount,
            BODTO<AdviceLine> adviceDto,
            int lock,
            String lockCause, String serialNumber, String targetLocationName, String targetUnitLoadName, String printer) throws FacadeException, ReportException {

        GoodsReceipt goodsReceipt = manager.find(GoodsReceipt.class, goodsReceiptDto.getId());
        ItemData itemData = manager.find(ItemData.class, itemDataDto.getId());
		AdviceLine adviceLine = (adviceDto == null ? null : manager.find(AdviceLine.class, adviceDto.getId()));
		UnitLoadType unitLoadType = (adviceDto == null ? null : manager.find(UnitLoadType.class, unitLoadTypeDto.getId()));
       
		boolean printLabel = true;
		boolean isFixedLocation = false;
		StorageLocation targetLocation = null;
		UnitLoad targetUnitLoad = null;
		if (targetLocationName != null && targetLocationName.length() > 0) {
			try {
				targetLocation = locationService.readByName(targetLocationName);
			} catch (Exception e) {
			}
			if (targetLocation == null) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, targetLocationName);
			}

			// Check, whether the target location is a fixed assigned location.
			// On this locations a special handling is needed, which the posting methods do
			// not know??
			FixAssignment fix = fixService.readFirstByLocation(targetLocation);
			if (fix != null) {
				if (!fix.getItemData().equals(itemData)) {
					logger.error("Cannot store item data=" + itemData.getNumber() + " on fixed location for item data="
							+ fix.getItemData().getNumber());
					throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA,
							new Object[] { itemData.getNumber(), fix.getItemData().getNumber() });
				}
				printLabel = false;
				isFixedLocation = true;
			}
		}
		if (targetUnitLoadName != null && targetUnitLoadName.length() > 0) {
			try {
				targetUnitLoad = unitLoadService.readByLabel(targetUnitLoadName);
			} catch (Exception e) {
			}
			if (targetUnitLoad == null) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, targetUnitLoadName);
			}
		}

		GoodsReceiptLine goodsReceiptLine = goodsReceiptBusiness.receiveStock(goodsReceipt, adviceLine, unitLoadLabel,
				unitLoadType, itemData, null, amount, lotNumber, bestBefore, serialNumber, lock, lockCause, null, lockCause);

		UnitLoad unitLoad = unitLoadService.readByLabel(goodsReceiptLine.getUnitLoadLabel());
		if (unitLoad == null) {
			throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, targetUnitLoadName);
		}

		if (targetLocation != null) {
			try {
				if (isFixedLocation) {
					putOnFixedAssigned(contextService.getCallerUserName(), null, unitLoad, targetLocation);
				} else {
					inventoryBusiness.checkTransferUnitLoad(unitLoad, targetLocation, true);
					inventoryBusiness.transferUnitLoad(unitLoad, targetLocation, null, null, null);
				}
			} catch (FacadeException e) {
				logger.error("Error in Transfer to target=" + targetLocation.getName());
				throw e;
			}
		} else if (targetUnitLoad != null) {
			try {
				for (StockUnit stock : stockUnitEntityService.readByUnitLoad(unitLoad)) {
					inventoryBusiness.checkTransferStock(stock, targetUnitLoad, null);
					inventoryBusiness.transferStock(stock, targetUnitLoad, null, targetUnitLoad.getState(), null, null, null);
				}
			} catch (FacadeException e) {
				logger.error("Error in Transfer to target=" + targetUnitLoadName);
				throw e;
			}
		}

		if (printLabel && !StringUtils.isBlank(printer)) {
			Document label = suLabelReport.generateReport(unitLoad);
			printService.print(printer, label.getData(), label.getDocumentType());
		}

		return goodsReceiptLine;
    }

    public void acceptGoodsReceipt(GoodsReceipt goodsReceipt) throws InventoryException, BusinessException {
    	goodsReceipt = manager.find(GoodsReceipt.class, goodsReceipt.getId());

        if (goodsReceipt == null) {
            throw new InventoryException(InventoryExceptionKey.CREATE_GOODSRECEIPT, "");
        }

        goodsReceiptBusiness.releaseOperation(goodsReceipt, null, null, null);
    }

    public void finishGoodsReceipt(GoodsReceipt r) throws InventoryException, BusinessException {
    	GoodsReceipt foundGr = manager.find(GoodsReceipt.class, r.getId());

        if (foundGr == null) {
            throw new InventoryException(InventoryExceptionKey.CREATE_GOODSRECEIPT, r.getOrderNumber());
        }

        goodsReceiptBusiness.finishOrder(foundGr);
    }
    
    public void removeGoodsReceiptLine(GoodsReceiptLine goodsReceiptLine) throws InventoryException, FacadeException {
    	goodsReceiptLine = manager.find(GoodsReceiptLine.class, goodsReceiptLine.getId());
        goodsReceiptBusiness.removeGoodsReceiptLineWithStocks(goodsReceiptLine);
    }

	public void assignAdvice(BODTO<AdviceLine> adviceLineDto, GoodsReceipt goodsReceipt) throws BusinessException {
		AdviceLine adviceLine = manager.find(AdviceLine.class, adviceLineDto.getId());
		goodsReceipt = manager.find(GoodsReceipt.class, goodsReceipt.getId());

		goodsReceiptBusiness.assignAdviceLine(goodsReceipt, adviceLine);
		if (goodsReceipt.getState() < OrderState.RELEASED) {
			goodsReceiptBusiness.releaseOperation(goodsReceipt, null, null, null);
		}
	}

	public void removeAssigendAdvice(BODTO<AdviceLine> adviceLineDto, GoodsReceipt goodsReceipt)
			throws BusinessException {
		AdviceLine adviceLine = manager.find(AdviceLine.class, adviceLineDto.getId());
		goodsReceipt = manager.find(GoodsReceipt.class, goodsReceipt.getId());
		goodsReceiptBusiness.removeAssignedAdviceLine(goodsReceipt, adviceLine);
	}

	public void createStockUnitLabel(GoodsReceipt goodsReceipt, String printer) 	throws FacadeException {
		goodsReceipt = manager.find(GoodsReceipt.class, goodsReceipt.getId());
		for( GoodsReceiptLine line:goodsReceipt.getLines() ) {
			createStockUnitLabel(line, printer);
		}
	}
	
	private Document createStockUnitLabel(GoodsReceiptLine goodsReceiptLine, String printer) throws FacadeException {
		String logStr = "createStockUnitLabel ";

		StockUnit stock = null;
		if (goodsReceiptLine.getStockUnitId() != null) {
			stock = manager.find(StockUnit.class, goodsReceiptLine.getStockUnitId());
		}
		if (stock == null) {
			logger.info(logStr + "Position has no stock unit. => Cannot print label");
			throw new InventoryException(InventoryExceptionKey.GRPOS_HAS_NO_STOCK, "");
		}

		UnitLoad unitLoad = stock.getUnitLoad();

		Document label = suLabelReport.generateReport(unitLoad);
		printService.print(printer, label.getData(), label.getDocumentType());

		return label;

	}

	@Override
	public boolean checkSerialNumber(String serialNo, ItemData itemData) {
		if( itemData.getSerialNoRecordType() != SerialNoRecordType.ALWAYS_RECORD ) {
			return true;
		}
		if( serialNo == null || serialNo.length()==0 ) {
			return false;
		}
		
		List<StockUnit> list = stockUnitEntityService.readBySerialNumber(itemData, serialNo);
		for (StockUnit su : list) {
			if (BigDecimal.ZERO.compareTo(su.getAmount()) < 0) {
				return false;
			}
		}

		return true;
	}

	
	
	private void putOnFixedAssigned( String userName, String activityCode, UnitLoad unitload, StorageLocation targetLocation) throws InventoryException,
			FacadeException {
		
		// To picking? (implicitly if virtual ul type)
		UnitLoadType virtual = unitLoadTypeService.getVirtual();
		
		if (targetLocation.getUnitLoads() != null && targetLocation.getUnitLoads().size() > 0) {
			// There is aready a unit load on the destination. => Add stock
			
			UnitLoad onDestination = targetLocation.getUnitLoads().get(0);

    		for(StockUnit stock:stockUnitEntityService.readByUnitLoad(unitload)) {
        		inventoryBusiness.checkTransferStock(stock, onDestination, null);
        		inventoryBusiness.transferStock(stock, onDestination, null, stock.getState(), activityCode, null, null);
    		}

			logger.info("Transferred Stock to virtual UnitLoadType: "+unitload.toShortString());
		} else {
			unitload.setType(virtual);
			unitload.setLabelId(targetLocation.getName());
			inventoryBusiness.checkTransferUnitLoad(unitload, targetLocation, true);
			inventoryBusiness.transferUnitLoad(unitload, targetLocation, null, null, null);
		}
	}

	@Override
	public void createStock(
		    String clientNumber,
		    String lotName,
		    Date bestBefore,
		    String itemDataNumber,
		    String unitLoadLabel,
		    String unitLoadTypeName, 
		    BigDecimal amount,
		    int lock, String lockCause, 
		    String serialNumber, 
		    String targetLocationName, 
		    String targetUnitLoadName) throws FacadeException {
        UnitLoad ul;
        
        Client client = null;
		try {
			client = queryClientService.getByNumber(clientNumber);
		} catch (UnAuthorizedException e2) {
		}
        if (client == null){
        	throw new InventoryException(InventoryExceptionKey.NO_SUCH_CLIENT, clientNumber);
        }
        
        
        ItemData idat = itemDataEntityService.readByNumber(itemDataNumber);
        if (idat == null){
        	throw new InventoryException(InventoryExceptionKey.ITEMDATA_NOT_FOUND, itemDataNumber);
        }
        
        UnitLoadType ulType;
        
        if (unitLoadTypeName != null){
        	ulType = unitLoadTypeService.readByName(unitLoadTypeName);
        } else{
        	ulType = unitLoadTypeService.getDefault();
        }
                
        List<StorageLocation> sls = locationService.getForGoodsIn(null);
        StorageLocation sl = null;
        if( sls.size()<1 ) {
        	throw new InventoryException(InventoryExceptionKey.NO_GOODS_RECEIPT_LOCATION, "");
        }
    	sl = sls.get(0);
        
        
		if (StringUtils.isBlank(lotName) && idat.isLotMandatory()) {
			throw new InventoryException(InventoryExceptionKey.LOT_MANDATORY, idat.getNumber());
		}
		if (bestBefore==null && idat.isBestBeforeMandatory()) {
			throw new InventoryException(InventoryExceptionKey.LOT_MANDATORY, idat.getNumber());
		}
        boolean printLabel = true;
        boolean isFixedLocation = false;
        StorageLocation targetLocation = null;
        UnitLoad targetUnitLoad = null;
        if( targetLocationName != null && targetLocationName.length()>0 ) {
        	try {
				targetLocation = locationService.read(targetLocationName);
			} catch (Exception e) {
			}
			if( targetLocation == null ) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_STORAGELOCATION, targetLocationName);
			}
			
			// Check, whether the target location is a fixed assigned location.
			// On this locations a special handling is needed, which the posting methods do not know??
			FixAssignment fix = fixService.readFirstByLocation(targetLocation);
			if( fix != null ) {
				if( ! fix.getItemData().equals(idat) ) {
					logger.error("Cannot store item data="+ idat.getNumber()+" on fixed location for item data="+fix.getItemData().getNumber());
					throw new InventoryException(InventoryExceptionKey.WRONG_ITEMDATA, new Object[]{idat.getNumber(),fix.getItemData().getNumber()});
				}
				printLabel = false;
				isFixedLocation = true;
			}
        }
        if( targetUnitLoadName != null && targetUnitLoadName.length()>0 ) {
			targetUnitLoad = unitLoadService.readByLabel(targetUnitLoadName);
			if( targetUnitLoad == null ) {
				throw new InventoryException(InventoryExceptionKey.NO_SUCH_UNITLOAD, targetUnitLoadName);
			}
        }
        
        
        boolean printLabelAutomatically = propertyService.getBooleanDefault(LOSInventoryPropertyKey.PRINT_GOODS_RECEIPT_LABEL, false);
        if( ! printLabelAutomatically ) {
        	printLabel = false;
        }
        
        try {
	        if (unitLoadLabel != null && unitLoadLabel.length() > 0) {
	        	
	        	printLabel = false;
	        	
				ul = unitLoadService.readByLabel(unitLoadLabel);
				if (ul != null) {
	        		throw new InventoryException(InventoryExceptionKey.UNIT_LOAD_EXISTS, 
	        									 new Object[]{unitLoadLabel}); 
	        		
	        	} else { 
	                ul = receiptComponent.getOrCreateUnitLoad(client, sl, ulType, unitLoadLabel);
	        	}
	        } 
	        else {
		        if( targetUnitLoad != null ) {
		        	printLabel = false;
		        }
		        
	        	// do not use the label twice
	        	String label = null;
	        	int i = 0;
	        	while( i++ < 100 ) {
	        		label = genService.generateUnitLoadLabelId(client, ulType);
        			UnitLoad test = unitLoadService.readByLabel(label);
					if (test == null) {
						break;
					}
	        		logger.info("UnitLoadLabel " + label + " already exists. Try the next");
	        	}

                ul = receiptComponent.getOrCreateUnitLoad(client, sl, ulType, unitLoadLabel);
	        }     
	        
        } catch (InventoryException ex){
        	throw ex;
        } catch (Throwable ex) {
            logger.error(ex, ex);
            throw new InventoryException(InventoryExceptionKey.CREATE_UNITLOAD, new Object[0]);
        }
        
		ul.setState(StockState.ON_STOCK);
		User operator = contextService.getCallersUser();
		inventoryBusiness.checkCreateStockUnit(idat, amount, lotName, bestBefore, serialNumber);
		StockUnit su = inventoryBusiness.createStock(ul, idat, amount, lotName, bestBefore, serialNumber, null,
				ul.getState(), null, operator, null, true);
        if(lock > 0){
	        try {
				suCrud.lock(su, lock, lockCause);
			} catch (BusinessObjectSecurityException e1) {
				logger.error(e1.getMessage(), e1);
				throw new InventoryException(InventoryExceptionKey.STOCKUNIT_CONSTRAINT_VIOLATED, su.toUniqueString());
			}
        }
        
        manager.flush();
        
        if( targetLocation != null ) {
    		try {
	    		if( isFixedLocation ) {
	    			putOnFixedAssigned( contextService.getCallerUserName(), null, ul, targetLocation);
	    		}
	    		else {
					inventoryBusiness.checkTransferUnitLoad(ul, targetLocation, true);
					inventoryBusiness.transferUnitLoad(ul, targetLocation, null, null, null);
	    		}
			} catch (FacadeException e) {
				logger.error("Error in Transfer to target="+targetLocation.getName());
				throw e;
			}
        }
        else if( targetUnitLoad != null ) {
        	try {
        		for(StockUnit stock:stockUnitEntityService.readByUnitLoad(ul)) {
            		inventoryBusiness.checkTransferStock(stock, targetUnitLoad, null);
            		inventoryBusiness.transferStock(stock, targetUnitLoad, null, targetUnitLoad.getState(), null, null, null);
        		}
			} catch (FacadeException e) {
				logger.error("Error in Transfer to target="+targetUnitLoadName);
				throw e;
			}
        }
        
        if(printLabel){
        	Document label = suLabelReport.generateReport(ul);
        	printService.print(null, label.getData(), label.getDocumentType());
        }

	}

	@Override
	public String getOldestLocationToAdd( ItemData itemData, String lotNumber, Date bestBefore) throws FacadeException {
		Client callersClient = contextService.getCallersClient();

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT loc.name, su.id, su.amount, su.created ");
		sb.append("FROM ");
		sb.append(StockUnit.class.getSimpleName()+" su ");
		sb.append(" JOIN su.unitLoad ul  ");
		sb.append(" JOIN su.unitLoad.storageLocation loc  ");
		sb.append(" WHERE su.lock=0 ");
		sb.append(" and su.itemData=:itemData ");
		sb.append(" and su.amount>0 ");
        if (!callersClient.isSystemClient()) {
            sb.append("AND su.client = :cl ");
        }
		sb.append(" and loc.lock=0 ");
		sb.append(" and ul.lock=0 ");
		if (!StringUtils.isBlank(lotNumber)) {
			sb.append(" and su.lotNumber=:lotNumber ");
		}
		if (bestBefore != null) {
			sb.append(" and su.bestBefore=:bestBefore");
		}
		sb.append(" ORDER BY su.amount, su.created ");

		
		logger.debug("Search add location: "+sb.toString());
		
		Query query = manager.createQuery(sb.toString());

		query.setParameter("itemData", itemData);
		
        if (!callersClient.isSystemClient()) {
        	query.setParameter("cl", callersClient);
        }
		if (!StringUtils.isBlank(lotNumber)) {
			query.setParameter("lotNumber", lotNumber);
		}
		if (bestBefore != null) {
			query.setParameter("bestBefore", bestBefore);
		}

        query.setMaxResults(1);
        Object o = null;
        try {
        	o = query.getSingleResult();
        }
        catch(NoResultException e){}
        if( o == null ) {
        	return null;
        }
        Object[] oa = (Object[])o;
        
        return (String)oa[0];
	}
}
