/* 
Copyright 2019-2022 Matthias Krane
info@krane.engineer

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.product;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.delivery.DeliveryOrderLine;
import de.wms2.mywms.entity.EntityValidator;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.strategy.FixAssignment;
import de.wms2.mywms.transport.TransportOrder;
import de.wms2.mywms.util.Wms2BundleResolver;

/**
 * Validation service for ItemData
 * 
 * @author krane
 * 
 */
public class ItemDataValidator implements EntityValidator<ItemData> {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private GenericEntityService entitySerivce;
	@Inject
	private ItemDataEntityService productSerivce;

	@Override
	public void validateCreate(ItemData entity) throws BusinessException {
		validate(entity);
	}

	@Override
	public void validateUpdate(ItemData entityOld, ItemData entityNew) throws BusinessException {
		validate(entityNew);
	}

	public void validate(ItemData entity) throws BusinessException {
		String logStr = "validate ";

		if (entity.getClient() == null) {
			logger.log(Level.INFO, logStr + "missing client. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingClient");
		}

		if (StringUtils.isEmpty(entity.getNumber())) {
			logger.log(Level.INFO, logStr + "missing productNumber. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingNumber");
		}

		if (StringUtils.isEmpty(entity.getName())) {
			logger.log(Level.INFO, logStr + "missing name. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingName");
		}

		if (entity.getItemUnit() == null) {
			logger.log(Level.INFO, logStr + "missing productUnit. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.missingUnit");
		}

		if (productSerivce.existsByNumberIgnoreCase(entity.getNumber(), entity.getId())) {
			logger.log(Level.INFO, "Not unique. number=" + entity.getNumber());
			throw new BusinessException(Wms2BundleResolver.class, "Validator.notUniqueNumber");
		}
	}

	@Override
	public void validateDelete(ItemData entity) throws BusinessException {
		String logStr = "validateDelete ";

		if (entitySerivce.exists(AdviceLine.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to Advice. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByAdviceLine");
		}
		if (entitySerivce.exists(DeliveryOrderLine.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to DeliveryOrderLine. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByDeliveryOrderLine");
		}
		if (entitySerivce.exists(FixAssignment.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to FixAssignment. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByFixAssignment");
		}
		if (entitySerivce.exists(GoodsReceiptLine.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to GoodsReceiptLine. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByGoodsReceiptLine");
		}
		if (entitySerivce.exists(ItemDataNumber.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to ItemDataCode. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByItemDataNumber");
		}
		if (entitySerivce.exists(PickingOrderLine.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to PickingOrderLine. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByPickingOrderLine");
		}
		if (entitySerivce.exists(TransportOrder.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to ReplenishOrder. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByReplenishOrder");
		}
		if (entitySerivce.exists(StockUnit.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to StockUnit. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByStockUnit");
		}
		if (entitySerivce.exists(PackagingUnit.class, "itemData", entity)) {
			logger.log(Level.INFO, logStr + "Existing reference to PackagingUnit. entity=" + entity);
			throw new BusinessException(Wms2BundleResolver.class, "Validator.usedByPackingUnit");
		}
	}

}
