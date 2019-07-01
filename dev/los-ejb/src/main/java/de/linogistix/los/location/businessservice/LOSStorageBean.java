/*
 * Copyright (c) 2006 - 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.location.businessservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.facade.FacadeException;

import de.linogistix.los.entityservice.BusinessObjectLockState;
import de.linogistix.los.location.constants.LOSStorageLocationLockState;
import de.linogistix.los.location.customization.CustomLocationService;
import de.linogistix.los.location.entityservice.LOSStorageLocationService;
import de.linogistix.los.location.entityservice.LOSUnitLoadService;
import de.linogistix.los.location.exception.LOSLocationException;
import de.linogistix.los.location.exception.LOSLocationExceptionKey;
import de.linogistix.los.location.model.LOSFixedLocationAssignment;
import de.linogistix.los.location.model.LOSUnitLoadRecord;
import de.linogistix.los.location.model.LOSUnitLoadRecordType;
import de.linogistix.los.location.service.QueryFixedAssignmentService;
import de.wms2.mywms.inventory.StockState;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.location.StorageLocation;


@Stateless
public class LOSStorageBean implements LOSStorage {

	private static final Logger log = Logger.getLogger(LOSStorageBean.class);
	private static int MAX_CARRIER_DEPTH = 10;
	

	@EJB
	private LOSStorageLocationService slService;

	@EJB
	private QueryFixedAssignmentService fixAssignmentService;
	
	@EJB
	private LOSUnitLoadService unitLoadService;
	@EJB
	private LocationReserver locationReserver;
	@EJB
	private CustomLocationService customLocationService;
		
	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	
	
	public void transferUnitLoad(String userName, StorageLocation dest, UnitLoad ul) 
		throws FacadeException 
	{
		transferUnitLoad(userName, dest, ul, -1, false, true, "", "");
	}


	public void transferUnitLoad(String userName, StorageLocation dest, UnitLoad unitLoad, int index, boolean ignoreLock, String info, String activityCode) throws FacadeException {
		transferUnitLoad(userName, dest, unitLoad, index, ignoreLock, true, info, activityCode);
		
	}

	public void transferUnitLoad(String userName, StorageLocation dest, UnitLoad unitLoad, int index, boolean ignoreLock, boolean reserve, String info, String activityCode) throws FacadeException { 
		String logStr = "transferUnitLoad ";
		
		// if we should check lock state before transfer
		if(!ignoreLock){
		
			// if lock state of destination is an other than 0 or 301
			if (dest.getLock() != LOSStorageLocationLockState.NOT_LOCKED.getLock() 
				&& dest.getLock() != LOSStorageLocationLockState.RETRIEVAL.getLock()) 
			{
				throw new LOSLocationException(
						LOSLocationExceptionKey.STORAGELOCATION_LOCKED,
						new Object[] { dest.getName(), dest.getLock() + "" });
			}
		}
		
		// if the destination is permanent assigned to a special item data
		// check if stocks on the unit load only contain that item
		LOSFixedLocationAssignment fixAss;

		if ((fixAss = fixAssignmentService.getByLocation(dest)) != null){

			boolean hasChilds = unitLoadService.hasChilds(unitLoad);
			
			if( hasChilds ) {
				log.error(logStr+"Carrier unit loads are not allowed on fixed locations");
				throw new LOSLocationException( LOSLocationExceptionKey.CARRIER_NOT_ON_FIXLOC, new String[]{});
			}
			for (StockUnit su : unitLoad.getStockUnitList()){
				if ( ! su.getItemData().equals(fixAss.getItemData())){
					throw new LOSLocationException(
							LOSLocationExceptionKey.WRONG_ITEMDATA_FIXASSIGNMENT, 
							new String[]{fixAss.getItemData().getNumber()});
				}
			}
		}

		if( unitLoad.getCarrierUnitLoadId() != null ) {
			// Maybe the carrier is no more carrier now
			UnitLoad carrier = unitLoadService.getParent(unitLoad);
			if( carrier != null ) {
				if( !unitLoadService.hasOtherChilds(carrier, unitLoad)) {
					carrier.setCarrier(false);
				}
			}
			unitLoad.setCarrierUnitLoadId(null);
			unitLoad.setCarrierUnitLoad(null);
		}

		// if all checks succeed
		StorageLocation source = unitLoad.getStorageLocation();
		locationReserver.deallocateLocation(source, unitLoad);
		if( reserve ) {
			locationReserver.allocateLocation(dest, unitLoad);
		}

		postTransfer(unitLoad, source, dest, index, userName, activityCode, info, 0);

		boolean exists = unitLoadService.existsByStorageLocation(source);
		if( !exists ) {
			locationReserver.deallocateLocationComplete(source);
		}

		if( !source.equals(dest) ) {
			customLocationService.onUnitLoadRemoved( source, unitLoad );
			customLocationService.onUnitLoadPlaced( dest, unitLoad );
		}

		log.info(logStr+"TRANSFERRED UnitLoad: " + unitLoad.getLabelId() + " *** to Location: " + dest.getName());

	}

	private void postTransfer( UnitLoad unitLoad, StorageLocation source, StorageLocation dest, int index, String userName, String activityCode, String comment, int depth ) throws LOSLocationException {
		String logStr = "postTransfer ";

		if( depth>MAX_CARRIER_DEPTH ) {
			log.error(logStr+"Cannot transfer unit load with more than "+MAX_CARRIER_DEPTH+" carriers");
			throw new LOSLocationException( LOSLocationExceptionKey.CARRIER_MAXDEPTH_EXCEEDED, new Object[] {MAX_CARRIER_DEPTH});
		}
		// Attention, version counter will be updated when adding the unit load to the unit-load-list of the storage location. 
		// => OptimisticLockException
//		source.getUnitLoads().remove(unitLoad);

		unitLoad.setStorageLocation(dest);
		if (index > -1) unitLoad.setIndex(index);
		// Attention, version counter will be updated when adding the unit load to the unit-load-list of the storage location. 
		// => OptimisticLockException
//		dest.getUnitLoads().add(unitLoad);

		LOSUnitLoadRecord rec = new LOSUnitLoadRecord();
		rec.setClient(unitLoad.getClient());
		rec.setActivityCode(activityCode);
		rec.setOperator(userName);
		rec.setRecordType(LOSUnitLoadRecordType.TRANSFERED);
		rec.setLabel(unitLoad.getLabelId());
		rec.setFromLocation(source.getName());
		rec.setToLocation(dest.getName());
		rec.setAdditionalContent(comment);
		rec.setUnitLoadType(unitLoad.getType().getName());
		
		manager.persist(rec);
		
		List<UnitLoad> childs = unitLoadService.getChilds(unitLoad);
		for( UnitLoad child : childs ) {
			if( child.equals(unitLoad) ) {
				log.error(logStr+"Selfreference detected! A unitLoad is its onw carrier. label="+unitLoad.getLabelId());
				continue;
			}
			postTransfer( child, source, dest, index, userName, activityCode, comment, depth+1 );
		}
	}

	public void transferToCarrier(String userName, UnitLoad source, UnitLoad destination, String info, String activityCode) throws FacadeException {
		String logStr = "transferToCarrier ";
		
		StorageLocation sourceLocation = source.getStorageLocation();
		
		if( source.equals(destination) ) {
			log.warn(logStr+"Source equals destination. Cannot place unitload on itself. label="+source.getLabelId());
			throw new LOSLocationException( LOSLocationExceptionKey.CARRIER_SELF_REFERENCE, new Object[] {source.getLabelId()});
		}

		if( unitLoadService.hasParent(destination, source) ) {
			log.warn(logStr+"Source has destination as child. Cannot place destination unitload on itself. source="+source.getLabelId()+", destination="+destination.getLabelId());
			throw new LOSLocationException( LOSLocationExceptionKey.CARRIER_SELF_REFERENCE, new Object[] {destination.getLabelId()});
		}

		if( source.getCarrierUnitLoadId() == null ) {
			locationReserver.deallocateLocation(source.getStorageLocation(), source);
		}
		else {
			// Maybe the carrier is no more carrier now
			UnitLoad carrier = unitLoadService.getParent(source);
			if( carrier != null ) {
				if( !unitLoadService.hasOtherChilds(carrier, source)) {
					carrier.setCarrier(false);
				}
			}
		}
		source.setCarrierUnitLoadId(destination.getId());
		source.setCarrierUnitLoad(destination);
		source.setStorageLocation(destination.getStorageLocation());
		destination.setCarrier(true);
		
		postTransferToUnitLoad( source, destination, userName, activityCode, info, 0 );
		
		if( !sourceLocation.equals(destination.getStorageLocation()) ) {
			customLocationService.onUnitLoadRemoved( sourceLocation, source );
		}
	}

	private void postTransferToUnitLoad( UnitLoad unitLoad, UnitLoad destination, String userName, String activityCode, String comment, int depth ) throws LOSLocationException {
		String logStr = "postTransfer ";

		if( depth>MAX_CARRIER_DEPTH ) {
			log.error(logStr+"Cannot transfer unit load with more than "+MAX_CARRIER_DEPTH+" carriers");
			throw new LOSLocationException( LOSLocationExceptionKey.CARRIER_MAXDEPTH_EXCEEDED, new Object[] {MAX_CARRIER_DEPTH});
		}

		unitLoad.setStorageLocation(destination.getStorageLocation());
		
		// Attention, version counter will be updated when adding the unit load to the unit-load-list of the storage location. 
		// => OptimisticLockException
//		destination.getStorageLocation().getUnitLoads().add(unitLoad);


		LOSUnitLoadRecord rec = new LOSUnitLoadRecord();
		rec.setClient(unitLoad.getClient());
		rec.setActivityCode(activityCode);
		rec.setOperator(userName);
		rec.setRecordType(LOSUnitLoadRecordType.TRANSFERED);
		rec.setLabel(unitLoad.getLabelId());
		rec.setFromLocation(unitLoad.getStorageLocation().getName());
		rec.setToLocation(destination.getLabelId());
		rec.setAdditionalContent(comment);
		rec.setUnitLoadType(unitLoad.getType().getName());

		manager.persist(rec);
		
		List<UnitLoad> childs = unitLoadService.getChilds(unitLoad);
		for( UnitLoad child : childs ) {
			if( child.equals(unitLoad) ) {
				log.error(logStr+"Selfreference detected! A unitLoad is its onw carrier. label="+unitLoad.getLabelId());
				continue;
			}
			postTransferToUnitLoad( child, destination, userName, activityCode, comment, depth+1 );
		}
	}

	
	public void sendToNirwana(String username, UnitLoad u)
			throws FacadeException {
		StorageLocation sl = slService.getNirwana();
		transferUnitLoad(username, sl, u, -1, false, true, "", "");
		u.setLock(BusinessObjectLockState.GOING_TO_DELETE.getLock());
		u.setState(StockState.DELETABLE);
		u.setLabelId(u.getLabelId() + "-X-" + u.getId());
		for(StockUnit stockUnit:u.getStockUnitList()) {
			int oldState = stockUnit.getState();
			stockUnit.setLock(BusinessObjectLockState.GOING_TO_DELETE.getLock());
			stockUnit.setState(StockState.DELETABLE);
		}
	}

	public void sendToClearing(String username, UnitLoad existing)
			throws FacadeException {
		StorageLocation sl = slService.getClearing();
		transferUnitLoad(username, sl, existing);
	}


}
