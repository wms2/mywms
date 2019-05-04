/*
 * Copyright (c) 2006 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: myWMS
 */
package org.mywms.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.mywms.globals.ServiceExceptionKey;
import org.mywms.model.Client;
import org.mywms.model.ItemData;
import org.mywms.model.StockUnit;
import org.mywms.model.UnitLoad;

/**
 * @see org.mywms.service.StockUnitService
 * @author Taieb El Fakiri
 * @version $Revision$ provided by $Author$
 */
@Stateless
public class StockUnitServiceBean
    extends BasicServiceBean<StockUnit>
    implements StockUnitService
{

    @SuppressWarnings("unused")
    private static final Logger log =
        Logger.getLogger(StockUnitServiceBean.class.getName());

    /**
     * @see org.mywms.service.StockUnitService#create(Client, UnitLoad,
     *      ItemData)
     */
    public StockUnit create(Client client, UnitLoad unitLoad, ItemData itemData, BigDecimal amount)
    {
        if (client == null || (unitLoad == null || itemData == null)) {
            throw new NullPointerException("createStockUnit: parameter == null");
        }

        client = manager.merge(client);
        unitLoad = manager.merge(unitLoad);
        itemData = manager.merge(itemData);

        StockUnit stockUnit = new StockUnit();
        stockUnit.setClient(client);
        stockUnit.setUnitLoad(unitLoad);
        stockUnit.setItemData(itemData);
        stockUnit.setAmount(amount);

        manager.persist(stockUnit);
        manager.flush();
        
        return stockUnit;
    }

    /**
     * @see org.mywms.service.StockUnitService#getListByUnitLoad(UnitLoad)
     */
    @SuppressWarnings("unchecked")
    public List<StockUnit> getListByUnitLoad(UnitLoad unitLoad) {
        Query query =
            manager.createQuery("SELECT su FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.unitLoad = :ul");
        query.setParameter("ul", unitLoad);
        return (List<StockUnit>) query.getResultList();
    }

    /**
     * @see org.mywms.service.StockUnitService#getListByItemData(org.mywms.model.ItemData)
     */
    @SuppressWarnings("unchecked")
    public List<StockUnit> getListByItemData(ItemData itemData) {
        Query query =
            manager.createQuery("SELECT su FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.itemData = :id"
                + " ORDER BY su.created ASC");

        query.setParameter("id", itemData);

        return (List<StockUnit>) query.getResultList();
    }

    /**
     * Searches for StockUnits of the specified item data.
     * 
     * @param itemData the item data of the stock unit
     * @return a list of stock units of the specified item data
     */
    @SuppressWarnings("unchecked")
    public List<StockUnit> getListByItemDataOrderByDate(ItemData itemData) {
        Query query =
            manager.createQuery("SELECT su FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.itemData = :id "
                + " ORDER BY su.created ");

        query.setParameter("id", itemData);

        return (List<StockUnit>) query.getResultList();
    }

    /**
     * Searches for StockUnits of the specified item data.
     * 
     * @param itemData the item data of the stock unit
     * @return a list of stock units of the specified item data
     */
    @SuppressWarnings("unchecked")
    public List<StockUnit> getListByItemDataOrderByAvailableAmount(
        ItemData itemData)
    {
        Query query =
            manager.createQuery("SELECT su FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.itemData = :id "
                + " ORDER BY (su.amount - su.reservedAmount) ASC, su.created ASC");

        query.setParameter("id", itemData);

        return (List<StockUnit>) query.getResultList();
    }

    /**
     * @see org.mywms.service.StockUnitService#getAvailableStock(org.mywms.model.ItemData)
     */
    public int getAvailableStock(ItemData itemData) {
        Query query =
            manager.createQuery("SELECT sum(su.amount - su.reservedAmount) FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.itemData= :id");
        query.setParameter("id", itemData);

        Long result = (Long) query.getSingleResult();

        if (result != null) {
            return result.intValue();
        }
        else {
            return 0;
        }
    }

    /**
     * @see org.mywms.service.StockUnitService#getCount(org.mywms.model.ItemData)
     */
    public int getCount(ItemData itemData) {
        Query query =
            manager.createQuery("SELECT count(su) FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.itemData= :id");
        query.setParameter("id", itemData);

        Long result = (Long) query.getSingleResult();

        if (result != null) {
            return result.intValue();
        }
        else {
            return 0;
        }
    }

    /**
     * @see org.mywms.service.StockUnitService#getReservedStock(org.mywms.model.ItemData)
     */
    public int getReservedStock(ItemData itemData) {
        Query query =
            manager.createQuery("SELECT sum(su.reservedAmount) FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.itemData= :id");
        query.setParameter("id", itemData);

        Long result = (Long) query.getSingleResult();

        if (result != null) {
            return result.intValue();
        }
        else {
            return 0;
        }
    }

    /**
     * @see org.mywms.service.StockUnitService#getStock(org.mywms.model.ItemData)
     */
    public int getStock(ItemData itemData) {
        Query query =
            manager.createQuery("SELECT sum(su.amount) FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.itemData= :itemData");
        query.setParameter("itemData", itemData);

        Long result = (Long) query.getSingleResult();

        if (result != null) {
            return result.intValue();
        }
        else {
            return 0;
        }
    }

    /**
     * @see org.mywms.service.StockUnitService#getInfo(org.mywms.model.ItemData)
     */
    public StockUnitInfoTO getInfo(ItemData itemData) {
        Query query =
            manager.createQuery("SELECT "
                + "sum(su.amount), "
                + "sum(su.reservedAmount), "
                + "count(su) "
                + "FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.itemData=:id");
        query.setParameter("id", itemData);

        Object[] result = (Object[]) query.getSingleResult();

        StockUnitInfoTO infoTO = new StockUnitInfoTO();

        if (result == null || result[0] == null) {
            return infoTO;
        }

        infoTO.stock = ((Long) result[0]).intValue();
        infoTO.reservedStock = ((Long) result[1]).intValue();
        infoTO.availableStock = infoTO.stock - infoTO.reservedStock;
        infoTO.count = ((Long) result[2]).intValue();

        return infoTO;
    }
    
    /**
     * @throws EntityNotFoundException 
     * @see org.mywms.service.StockUnitService#getStock(org.mywms.model.ItemData)
     */
	public StockUnit getByLabelId(String labelId) throws EntityNotFoundException{
        Query query =
            manager.createQuery("SELECT su FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.labelId= :adId");
        query = query.setParameter("adId", labelId);

        try{
        	StockUnit ret = (StockUnit) query.getSingleResult();
        	return ret;
        } catch (NoResultException ex){
        	throw new EntityNotFoundException(ServiceExceptionKey.NO_ENTITY_WITH_NAME);
        }

        
    }

    /**
     * @throws EntityNotFoundException 
     * @see org.mywms.service.StockUnitService#getStock(org.mywms.model.ItemData)
     */
	@SuppressWarnings("unchecked")
	public List<StockUnit> getBySerialNumber(ItemData idat, String serialNumber) {
        Query query =
            manager.createQuery("SELECT su FROM "
                + StockUnit.class.getSimpleName()
                + " su "
                + " WHERE su.serialNumber = :serialNumber");
        query = query.setParameter("serialNumber", serialNumber);

        try{
        	List<StockUnit> ret = query.getResultList();
        	return ret;
        } catch (NoResultException ex){
        	return null;
        }
        
    }

}
