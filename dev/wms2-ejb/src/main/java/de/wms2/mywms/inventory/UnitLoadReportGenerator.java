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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.address.Address;
import de.wms2.mywms.delivery.DeliveryOrder;
import de.wms2.mywms.document.Document;
import de.wms2.mywms.document.DocumentType;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.report.ReportBusiness;
import de.wms2.mywms.shipping.ShippingOrder;
import de.wms2.mywms.shipping.ShippingOrderLine;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Properties;
import net.sf.jasperreports.engine.JRParameter;

public class UnitLoadReportGenerator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PacketEntityService pickingUnitLoadService;
	@Inject
	private ReportBusiness reportBusiness;
	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private SystemPropertyBusiness propertyBusiness;

	public Document generateReport(UnitLoad unitLoad) throws BusinessException {
		String logStr = "generateReport ";
		logger.log(Level.INFO, logStr + "unitLoad=" + unitLoad);

		Client client = unitLoad.getClient();

		List<StockUnitReportDto> reportItems = calculateReportItems(unitLoad);

		DeliveryOrder deliveryOrder = null;
		PickingOrder pickingOrder = null;
		Address address = null;

		Packet packet = pickingUnitLoadService.readFirstByUnitLoad(unitLoad);
		if (packet != null) {
			deliveryOrder = packet.getDeliveryOrder();
			pickingOrder = packet.getPickingOrder();
		}
		if (deliveryOrder != null) {
			address = deliveryOrder.getAddress();
		}
		if ((address == null || address.isEmpty()) && pickingOrder != null) {
			address = pickingOrder.getAddress();
		}
		if (address == null) {
			address = new Address();
		}

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("unitLoad", unitLoad);
		parameters.put("pickingOrder", pickingOrder);
		parameters.put("deliveryOrder", deliveryOrder);
		parameters.put("address", address);

		String localeString = propertyBusiness.getString(Wms2Properties.KEY_REPORT_LOCALE, null);
		Locale locale = Translator.parseLocale(localeString);
		parameters.put(JRParameter.REPORT_LOCALE, locale);
		ResourceBundle bundle = Translator.getBundle(Wms2BundleResolver.class, null, locale);
		parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);

		byte[] data = reportBusiness.createPdfDocument(client, "Packlist", Wms2BundleResolver.class, reportItems,
				parameters);
		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("Packlist");

		return doc;
	}

	public Document generateReport(PickingOrder pickingOrder) throws BusinessException {
		String logStr = "generateReport ";
		logger.log(Level.INFO, logStr + "pickingOrder=" + pickingOrder);

		Client client = pickingOrder.getClient();

		List<StockUnitReportDto> reportItems = calculateReportItems(pickingOrder.getPackets());

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("order", pickingOrder);
		Address address = pickingOrder.getAddress();
		if (address == null || address.isEmpty()) {
			DeliveryOrder deliveryOrder = pickingOrder.getDeliveryOrder();
			if (deliveryOrder != null) {
				address = deliveryOrder.getAddress();
			}
		}
		if (address == null) {
			address = new Address();
		}
		parameters.put("address", address);

		String localeString = propertyBusiness.getString(Wms2Properties.KEY_REPORT_LOCALE, null);
		Locale locale = Translator.parseLocale(localeString);
		parameters.put(JRParameter.REPORT_LOCALE, locale);
		ResourceBundle bundle = Translator.getBundle(Wms2BundleResolver.class, null, locale);
		parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);

		byte[] data = reportBusiness.createPdfDocument(client, "PickingPacketlist", Wms2BundleResolver.class,
				reportItems, parameters);
		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("Packetlist");

		return doc;
	}

	public Document generateReport(ShippingOrder shippingOrder) throws BusinessException {
		String logStr = "generateReport ";
		logger.log(Level.INFO, logStr + "shippingOrder=" + shippingOrder);

		Client client = shippingOrder.getClient();

		Collection<Packet> packets = new ArrayList<Packet>(shippingOrder.getLines().size());
		for (ShippingOrderLine shippingOrderLine : shippingOrder.getLines()) {
			packets.add(shippingOrderLine.getPacket());
		}
		List<StockUnitReportDto> reportItems = calculateReportItems(packets);

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("order", shippingOrder);
		Address address = shippingOrder.getAddress();
		if (address == null || address.isEmpty()) {
			DeliveryOrder deliveryOrder = shippingOrder.getDeliveryOrder();
			if (deliveryOrder != null) {
				address = deliveryOrder.getAddress();
			}
		}
		if (address == null) {
			address = new Address();
		}
		parameters.put("address", address);

		String localeString = propertyBusiness.getString(Wms2Properties.KEY_REPORT_LOCALE, null);
		Locale locale = Translator.parseLocale(localeString);
		parameters.put(JRParameter.REPORT_LOCALE, locale);
		ResourceBundle bundle = Translator.getBundle(Wms2BundleResolver.class, null, locale);
		parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);

		byte[] data = reportBusiness.createPdfDocument(client, "ShippingPacketlist", Wms2BundleResolver.class,
				reportItems, parameters);
		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("Packetlist");

		return doc;
	}

	private List<StockUnitReportDto> calculateReportItems(Collection<Packet> packets) {
		Map<String, StockUnitReportDto> reportItemMap = new HashMap<>();

		for (Packet packet : packets) {
			registerUnitLoad(reportItemMap, packet.getUnitLoad());
		}

		List<StockUnitReportDto> reportItems = new ArrayList<>();
		reportItems.addAll(reportItemMap.values());

		Collections.sort(reportItems, new StockUnitReportDtoComparator());

		return reportItems;
	}

	private List<StockUnitReportDto> calculateReportItems(UnitLoad unitLoad) {
		Map<String, StockUnitReportDto> reportItemMap = new HashMap<>();

		registerUnitLoad(reportItemMap, unitLoad);

		List<StockUnitReportDto> reportItems = new ArrayList<>();
		reportItems.addAll(reportItemMap.values());
		Collections.sort(reportItems, new StockUnitReportDtoComparator());

		return reportItems;
	}

	private void registerUnitLoad(Map<String, StockUnitReportDto> itemMap, UnitLoad unitLoad) {
		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
		String key = "" + unitLoad.getId();
		registerItem(itemMap, key + "P", "P", unitLoad, null, null);
		for (StockUnit stock : stocksOnUnitLoad) {

			key = "" + unitLoad.getId() + "-" + stock.getItemData().getId();

			registerItem(itemMap, key, "X", unitLoad, stock.getItemData(), null);
			registerItem(itemMap, key + "A", "A", unitLoad, stock.getItemData(), stock.getAmount());
			Lot lot = stock.getLot();
			if (lot != null) {
				String lotKey = key + "L" + lot.getName().hashCode();
				StockUnitReportDto reportItem = registerItem(itemMap, lotKey, "L", unitLoad, stock.getItemData(),
						stock.getAmount());
				reportItem.setLotNumber(lot.getName());
			}
			if (lot != null && lot.getBestBeforeEnd() != null) {
				String bestBeforeKey = key + "B" + lot.getBestBeforeEnd().hashCode();
				StockUnitReportDto reportItem = registerItem(itemMap, bestBeforeKey, "B", unitLoad, stock.getItemData(),
						stock.getAmount());
				reportItem.setBestBefore(lot.getBestBeforeEnd());
			}
			if (!StringUtils.isBlank(stock.getSerialNumber())) {
				String serialKey = key + "L" + stock.getSerialNumber().hashCode();
				StockUnitReportDto reportItem = registerItem(itemMap, serialKey, "S", unitLoad, stock.getItemData(),
						stock.getAmount());
				reportItem.setSerialNumber(stock.getSerialNumber());
			}
		}
	}

	private StockUnitReportDto registerItem(Map<String, StockUnitReportDto> itemMap, String key, String type,
			UnitLoad unitLoad, ItemData itemData, BigDecimal amount) {
		StockUnitReportDto value = itemMap.get(key);
		if (value == null) {
			value = new StockUnitReportDto(type, itemData, amount);
			if (unitLoad != null) {
				String label = unitLoad.getLabelId();
				String suffix = "-" + unitLoad.getId();
				value.setLabel(StringUtils.substringBefore(label, suffix));
			}
			itemMap.put(key, value);
		} else {
			value.addAmount(amount);
		}
		return value;
	}

	private static class StockUnitReportDtoComparator implements Comparator<StockUnitReportDto>, Serializable {
		private static final long serialVersionUID = 1L;

		public int compare(StockUnitReportDto o1, StockUnitReportDto o2) {

			int x = StringUtils.compare(o1.getLabel(), o2.getLabel());
			if (x != 0) {
				return x;
			}

			x = StringUtils.compare(o1.getProductNumber(), o2.getProductNumber());
			if (x != 0) {
				return x;
			}

			x = StringUtils.compare(o1.getType(), o2.getType());
			if (x != 0) {
				return x;
			}

			x = StringUtils.compare(o1.getSerialNumber(), o2.getSerialNumber());
			if (x != 0) {
				return x;
			}

			x = StringUtils.compare(o1.getLotNumber(), o2.getLotNumber());
			if (x != 0) {
				return x;
			}

			if (o1.getAmount() != null && o2.getAmount() != null) {
				return o1.getAmount().compareTo(o2.getAmount());
			}

			return 0;
		}
	}
}
