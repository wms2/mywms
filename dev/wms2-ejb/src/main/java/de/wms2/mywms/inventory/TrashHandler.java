/* 
Copyright 2019 Matthias Krane
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
package de.wms2.mywms.inventory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.FlushModeType;

import de.wms2.mywms.advice.Advice;
import de.wms2.mywms.advice.AdviceLine;
import de.wms2.mywms.entity.GenericEntityService;
import de.wms2.mywms.entity.PersistenceManager;
import de.wms2.mywms.goodsreceipt.GoodsReceipt;
import de.wms2.mywms.goodsreceipt.GoodsReceiptLine;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.replenish.ReplenishOrder;
import de.wms2.mywms.strategy.LocationReservationEntityService;
import de.wms2.mywms.transport.TransportOrder;

/**
 * @author krane
 *
 */
@Stateless
public class TrashHandler {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;
	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private PacketEntityService packetService;
	@Inject
	private LocationReservationEntityService reservationService;
	@Inject
	private GenericEntityService genericService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean removeStockUnit(StockUnit entity) {
		String logStr = "removeStockUnit ";
		logger.log(Level.INFO, logStr + "StockUnit=" + entity);

		try {
			manager.setFlushMode(FlushModeType.COMMIT);
			entity = manager.find(StockUnit.class, entity.getId());
			if (entity == null) {
				logger.log(Level.WARNING, logStr + "Entity is already deleted. Cannot remove.");
				return false;
			}
			manager.remove(entity);
			manager.flush();

			return true;
		} catch (Throwable t) {
			logger.log(Level.SEVERE, logStr + "Cannot remove item. " + t.getClass().getName() + ", " + t.getMessage());
		}
		return false;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean removeUnitLoad(UnitLoad unitLoad) {
		String logStr = "removeUnitLoad ";
		logger.log(Level.INFO, logStr + "unitLoad=" + unitLoad);
		try {
			manager.setFlushMode(FlushModeType.COMMIT);
			try {
				unitLoad = manager.find(UnitLoad.class, unitLoad.getId());
			} catch (Throwable t) {
				logger.log(Level.WARNING,
						logStr + "Cannot read UnitLoad. Exception=" + t.getClass().getName() + ", " + t.getMessage());
			}
			if (unitLoad == null) {
				logger.log(Level.WARNING, logStr + "UnitLoad is already deleted. Cannot remove.");
				return false;
			}

			List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
			for (StockUnit stock : stocksOnUnitLoad) {
				manager.remove(stock);
			}

			List<Packet> packets = packetService.readList(unitLoad, null, null, null);
			for (Packet packet : packets) {
				manager.remove(packet);
			}

			reservationService.remove(null, unitLoad);

			manager.remove(unitLoad);
			manager.flush();

			return true;
		} catch (Throwable t) {
			logger.log(Level.SEVERE, logStr + "Cannot remove item. " + t.getClass().getName() + ", " + t.getMessage());
		}

		return false;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean removeGoodsReceipt(GoodsReceipt goodsReceipt) {
		String logStr = "removeGoodsReceipt ";
		logger.log(Level.INFO, logStr + "goodsReceipt=" + goodsReceipt);
		try {
			manager.setFlushMode(FlushModeType.COMMIT);
			goodsReceipt = manager.reload(goodsReceipt, false);
			if (goodsReceipt == null) {
				logger.log(Level.WARNING, logStr + "GoodsReceipt is already deleted. Cannot remove.");
				return false;
			}

			List<GoodsReceiptLine> lines = goodsReceipt.getLines();
			for (GoodsReceiptLine line : lines) {
				manager.remove(line);
			}
			manager.remove(goodsReceipt);
			manager.flush();

			return true;
		} catch (Throwable t) {
			logger.log(Level.SEVERE, logStr + "Cannot remove item. " + t.getClass().getName() + ", " + t.getMessage());
		}

		return false;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean removeAdvice(Advice advice) {
		String logStr = "removeAdvice ";
		logger.log(Level.INFO, logStr + "advice=" + advice);
		try {
			advice = manager.reload(advice, false);
			if (advice == null) {
				logger.log(Level.WARNING, logStr + "Advice is already deleted. Cannot remove.");
				return false;
			}

			List<AdviceLine> lines = advice.getLines();
			for (AdviceLine line : lines) {
				if (genericService.existsReference(GoodsReceipt.class, "adviceLines", line)) {
					logger.log(Level.FINE, logStr
							+ "Advice is referenced by GoodsReceipt.assignedAdvices. Do not remove. advice=" + advice);
					return false;
				}

				if (genericService.exists(GoodsReceiptLine.class, "adviceLine", line)) {
					logger.log(Level.FINE, logStr
							+ "Advice is referenced by GoodsReceipt.assignedAdvices. Do not remove. advice=" + advice);
					return false;
				}
				manager.remove(line);
			}

			manager.remove(advice);
			manager.flush();

			return true;
		} catch (Throwable t) {
			logger.log(Level.SEVERE, logStr + "Cannot remove item. " + t.getClass().getName() + ", " + t.getMessage());
		}

		return false;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean removeReplenishOrder(ReplenishOrder replenishOrder) {
		String logStr = "removeReplenishOrder ";
		logger.log(Level.INFO, logStr + "replenishOrder=" + replenishOrder);
		try {
			manager.setFlushMode(FlushModeType.COMMIT);
			replenishOrder = manager.reload(replenishOrder, false);
			if (replenishOrder == null) {
				logger.log(Level.WARNING, logStr + "ReplenishOrder is already deleted. Cannot remove.");
				return false;
			}

			manager.remove(replenishOrder);
			manager.flush();

			return true;
		} catch (Throwable t) {
			logger.log(Level.SEVERE, logStr + "Cannot remove item. " + t.getClass().getName() + ", " + t.getMessage());
		}

		return false;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean removeTransportOrder(TransportOrder transportOrder) {
		String logStr = "removeTransportOrder ";
		logger.log(Level.INFO, logStr + "transportOrder=" + transportOrder);
		try {
			manager.setFlushMode(FlushModeType.COMMIT);
			transportOrder = manager.reload(transportOrder, false);
			if (transportOrder == null) {
				logger.log(Level.WARNING, logStr + "RelocateOrder is already deleted. Cannot remove.");
				return false;
			}

			manager.remove(transportOrder);
			manager.flush();

			return true;
		} catch (Throwable t) {
			logger.log(Level.SEVERE,
					logStr + "Cannot remove item. " + t.getClass().getName() + ", " + t.getMessage());
		}

		return false;
	}

}
