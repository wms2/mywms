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
package de.wms2.mywms.delivery;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.Lot;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.PickingUnitLoad;
import de.wms2.mywms.picking.PickingUnitLoadEntityService;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.report.ReportBusiness;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Properties;
import net.sf.jasperreports.engine.JRParameter;

/**
 * @author krane
 *
 */
@Stateless
public class DeliverynoteGenerator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private ReportBusiness reportBusiness;
	@Inject
	private PickingUnitLoadEntityService pickingUnitLoadService;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private StockUnitEntityService stockUnitService;

	public byte[] generateReport(DeliveryOrder order) throws BusinessException {
		String logStr = "generateReport ";
		logger.log(Level.FINE, logStr + "order=" + order);

		Client client = null;
		List<DeliverynoteDto> reportItems = new ArrayList<DeliverynoteDto>();

		if (client == null) {
			client = order.getClient();
		}

		Map<ItemData, BigDecimal> orderAmountMap = new HashMap<>();
		Map<ItemData, BigDecimal> unitLoadAmountMap = new HashMap<>();
		Map<String, DeliverynoteDto> reportItemMap = new HashMap<>();

		for (DeliveryOrderLine orderLine : order.getLines()) {
			registerAmount(orderAmountMap, orderLine.getItemData(), orderLine.getPickedAmount());
		}

		List<PickingUnitLoad> pickingUnitLoads = pickingUnitLoadService.readByDeliveryOrder(order);
		for (PickingUnitLoad pickingUnitLoad : pickingUnitLoads) {
			UnitLoad unitLoad = pickingUnitLoad.getUnitLoad();
			List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
			for (StockUnit stock : stocksOnUnitLoad) {
				registerAmount(unitLoadAmountMap, stock.getItemData(), stock.getAmount());

				String key = "" + stock.getItemData().getId();
				registerItem(reportItemMap, key, "X", stock.getItemData(), null);
				registerItem(reportItemMap, key + "A", "A", stock.getItemData(),
						stock.getAmount());
				Lot lot = stock.getLot();
				if (lot != null) {
					String lotKey = key + "L" + lot.getName().hashCode();
					DeliverynoteDto reportItem = registerItem(reportItemMap, lotKey, "L",
							stock.getItemData(), stock.getAmount());
					reportItem.setLotNumber(lot.getName());
				}
				if (lot != null && lot.getBestBeforeEnd() != null) {
					String bestBeforeKey = key + "B" + lot.getBestBeforeEnd().hashCode();
					DeliverynoteDto reportItem = registerItem(reportItemMap, bestBeforeKey, "B",
							stock.getItemData(), stock.getAmount());
					reportItem.setBestBefore(lot.getBestBeforeEnd());
				}
				if (!StringUtils.isBlank(stock.getSerialNumber())) {
					String serialKey = key + "L" + stock.getSerialNumber().hashCode();
					DeliverynoteDto reportItem = registerItem(reportItemMap, serialKey, "S",
							stock.getItemData(), stock.getAmount());
					reportItem.setSerialNumber(stock.getSerialNumber());
				}
			}
		}

		// Not all picking carts can be resolved. Compare the sum of the amounts with
		// the sum of the picked amounts of the delivery order lines and add the
		// differences.
		for (ItemData itemData : orderAmountMap.keySet()) {
			BigDecimal orderAmount = orderAmountMap.get(itemData);
			if (orderAmount == null) {
				orderAmount = BigDecimal.ZERO;
			}
			BigDecimal unitLoadAmount = unitLoadAmountMap.get(itemData);
			if (unitLoadAmount == null) {
				unitLoadAmount = BigDecimal.ZERO;
			}

			if (orderAmount.compareTo(unitLoadAmount) > 0) {
				// More picked amount in order line than in cart
				// Add difference to sheet
				BigDecimal diffAmount = orderAmount.subtract(unitLoadAmount);

				String key = "" + itemData.getId();
				registerItem(reportItemMap, key, "X", itemData, null);
				registerItem(reportItemMap, key + "A", "A", itemData, diffAmount);

			} else if (orderAmount.compareTo(unitLoadAmount) < 0) {
				// more picked amount in cart than in order line
				// Not possible
				logger.log(Level.WARNING, logStr + "There is more amount in unit loads than order lines. itemData="
						+ itemData + ", unitLoadAmount=" + unitLoadAmount + ", orderLineAmount=" + orderAmount);
			}
		}

		reportItems.addAll(reportItemMap.values());

		Collections.sort(reportItems, new DeliverynoteDtoComparator());

		int lineNumber = 0;
		String itemDataNumber = null;
		for (DeliverynoteDto reportItem : reportItems) {
			if (itemDataNumber == null || !StringUtils.equals(itemDataNumber, reportItem.getProductNumber())) {
				itemDataNumber = reportItem.getProductNumber();
				lineNumber++;
			}
			reportItem.setLineNumber("" + lineNumber);
		}

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("order", order);

		String localeString = propertyBusiness.getString(Wms2Properties.KEY_REPORT_LOCALE, null);
		Locale locale = Translator.parseLocale(localeString);
		parameters.put(JRParameter.REPORT_LOCALE, locale);
		ResourceBundle bundle = Translator.getBundle(Wms2BundleResolver.class, null, locale);
		parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);

		byte[] data = reportBusiness.createPdfDocument(client, "Deliverynote", Wms2BundleResolver.class, reportItems,
				parameters);
		return data;
	}

	private void registerAmount(Map<ItemData, BigDecimal> registeredAmountMap, ItemData itemData, BigDecimal amount) {
		BigDecimal registeredAmount = registeredAmountMap.get(itemData);
		if (registeredAmount == null) {
			registeredAmountMap.put(itemData, amount);
		} else {
			registeredAmountMap.put(itemData, registeredAmount.add(amount));
		}
	}

	private DeliverynoteDto registerItem(Map<String, DeliverynoteDto> itemMap, String key, String type,
			ItemData itemData, BigDecimal amount) {
		DeliverynoteDto value = itemMap.get(key);
		if (value == null) {
			value = new DeliverynoteDto(type, itemData, amount);
			itemMap.put(key, value);
		} else {
			value.addAmount(amount);
		}
		return value;
	}

	private static class DeliverynoteDtoComparator implements Comparator<DeliverynoteDto>, Serializable {
		private static final long serialVersionUID = 1L;

		public int compare(DeliverynoteDto o1, DeliverynoteDto o2) {

			if (o1.getProductNumber() != null && o2.getProductNumber() != null) {
				int x = o1.getProductNumber().compareTo(o2.getProductNumber());
				if (x != 0) {
					return x;
				}
			}

			if (o1.getType() != null && o2.getType() != null) {
				int x = o1.getType().compareTo(o2.getType());
				if (x != 0) {
					return x;
				}
			}

			if (o1.getSerialNumber() != null && o2.getSerialNumber() == null) {
				return 1;
			}
			if (o1.getSerialNumber() == null && o2.getSerialNumber() != null) {
				return -1;
			}
			if (o1.getSerialNumber() != null && o2.getSerialNumber() != null) {
				int x = o1.getSerialNumber().compareTo(o2.getSerialNumber());
				if (x != 0) {
					return x;
				}
			}

			if (o1.getLotNumber() != null && o2.getLotNumber() == null) {
				return 1;
			}
			if (o1.getLotNumber() == null && o2.getLotNumber() != null) {
				return -1;
			}
			if (o1.getLotNumber() != null && o2.getLotNumber() != null) {
				int x = o1.getLotNumber().compareTo(o2.getLotNumber());
				if (x != 0) {
					return x;
				}
			}

			if (o1.getAmount() != null && o2.getAmount() != null) {
				return o1.getAmount().compareTo(o2.getAmount());
			}

			return 0;
		}
	}
}
