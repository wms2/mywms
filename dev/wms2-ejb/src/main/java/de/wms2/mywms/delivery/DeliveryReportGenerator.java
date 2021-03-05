/* 
Copyright 2019-2020 Matthias Krane
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
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.Client;

import de.wms2.mywms.address.Address;
import de.wms2.mywms.document.Document;
import de.wms2.mywms.document.DocumentType;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.inventory.StockUnit;
import de.wms2.mywms.inventory.StockUnitEntityService;
import de.wms2.mywms.inventory.UnitLoad;
import de.wms2.mywms.picking.Packet;
import de.wms2.mywms.picking.PacketEntityService;
import de.wms2.mywms.picking.PickingOrder;
import de.wms2.mywms.picking.PickingOrderLine;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.report.ReportBusiness;
import de.wms2.mywms.shipping.ShippingOrder;
import de.wms2.mywms.shipping.ShippingOrderLine;
import de.wms2.mywms.strategy.OrderState;
import de.wms2.mywms.util.Wms2BundleResolver;

public class DeliveryReportGenerator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String CONTENTLIST_REPORT_NAME = "Contentlist";
	private static final String PICKING_PACKETLIST_REPORT_NAME = "PickingPacketlist";
	private static final String SHIPPING_PACKETLIST_REPORT_NAME = "ShippingPacketlist";
	private static final String DELIVERY_PACKETLIST_REPORT_NAME = "DeliveryPacketlist";
	private static final String DELIVERYNOTE_REPORT_NAME = "Deliverynote";

	@Inject
	private PacketEntityService packetService;
	@Inject
	private ReportBusiness reportBusiness;
	@Inject
	private StockUnitEntityService stockUnitService;

	private static final String ITEM_TYPE_PACKET = "1-PACKET";
	private static final String ITEM_TYPE_ITEMDATA = "2-ITEMDATA";
	private static final String ITEM_TYPE_LOT = "3-LOT";
	private static final String ITEM_TYPE_BESTBEFORE = "4-BESTBEFORE";
	private static final String ITEM_TYPE_LOT_BESTBEFORE = "5-LOT_BESTBEFORE";
	private static final String ITEM_TYPE_SERIAL = "6-SERIAL";
	private static final String ITEM_TYPE_LINE = "7-LINE";

	public Document generateContentList(Packet packet) throws BusinessException {
		return generateContentList(packet, null);
	}

	public Document generateContentList(Packet packet, String reportVersion) throws BusinessException {
		String logStr = "generateContentList ";
		logger.log(Level.INFO, logStr + "packet=" + packet + ", reportVersion=" + reportVersion);

		return generateContentList(packet.getUnitLoad(), packet, reportVersion);
	}

	public Document generateContentList(UnitLoad unitLoad) throws BusinessException {
		return generateContentList(unitLoad, null);
	}

	public Document generateContentList(UnitLoad unitLoad, String reportVersion) throws BusinessException {
		String logStr = "generateContentList ";
		logger.log(Level.INFO, logStr + "unitLoad=" + unitLoad);

		Packet packet = packetService.readFirstByUnitLoad(unitLoad);

		return generateContentList(unitLoad, packet, reportVersion);
	}

	private Document generateContentList(UnitLoad unitLoad, Packet packet, String reportVersion)
			throws BusinessException {
		Client client = unitLoad.getClient();

		DeliveryOrder deliveryOrder = null;
		PickingOrder pickingOrder = null;
		if (packet != null) {
			deliveryOrder = packet.getDeliveryOrder();
			pickingOrder = packet.getPickingOrder();
		}
		Address address = null;
		if (deliveryOrder != null) {
			address = deliveryOrder.getAddress();
		}
		if ((address == null || address.isEmpty()) && pickingOrder != null) {
			address = pickingOrder.getAddress();
		}
		if (address == null) {
			address = new Address();
		}
		String label = unitLoad.getLabelId();
		String suffix = "-" + unitLoad.getId();
		label = StringUtils.substringBefore(label, suffix);

		Map<String, DeliveryReportDto> reportItemMap = new HashMap<>();
		registerItems(reportItemMap, unitLoad, packet, true);
		List<DeliveryReportDto> reportItems = new ArrayList<>();
		reportItems.addAll(reportItemMap.values());
		Collections.sort(reportItems, new UnitLoadReportDtoComparator());

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("unitLoad", unitLoad);
		parameters.put("packet", packet);
		parameters.put("unitLoadType", unitLoad.getUnitLoadType());
		parameters.put("pickingOrder", pickingOrder);
		parameters.put("deliveryOrder", deliveryOrder);
		parameters.put("address", address);
		parameters.put("label", label);

		byte[] data = reportBusiness.createPdfDocument(client, CONTENTLIST_REPORT_NAME, reportVersion,
				Wms2BundleResolver.class, reportItems, parameters);

		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("Contentlist");

		return doc;
	}

	public Document generatePacketList(PickingOrder pickingOrder) throws BusinessException {
		return generatePacketList(pickingOrder, null);
	}

	public Document generatePacketList(PickingOrder pickingOrder, String reportVersion) throws BusinessException {
		String logStr = "generatePacketList ";
		logger.log(Level.INFO, logStr + "pickingOrder=" + pickingOrder + ", reportVersion=" + reportVersion);

		Client client = pickingOrder.getClient();

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

		Map<String, DeliveryReportDto> reportItemMap = new HashMap<>();
		for (Packet packet : pickingOrder.getPackets()) {
			registerItems(reportItemMap, packet.getUnitLoad(), packet, true);
		}
		List<DeliveryReportDto> reportItems = new ArrayList<>();
		reportItems.addAll(reportItemMap.values());
		Collections.sort(reportItems, new UnitLoadReportDtoComparator());

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("pickingOrder", pickingOrder);
		parameters.put("address", address);

		byte[] data = reportBusiness.createPdfDocument(client, PICKING_PACKETLIST_REPORT_NAME, reportVersion,
				Wms2BundleResolver.class, reportItems, parameters);

		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("Packetlist");

		return doc;
	}

	public Document generatePacketList(ShippingOrder shippingOrder) throws BusinessException {
		return generatePacketList(shippingOrder, null);
	}

	public Document generatePacketList(ShippingOrder shippingOrder, String reportVersion) throws BusinessException {
		String logStr = "generatePacketList ";
		logger.log(Level.INFO, logStr + "shippingOrder=" + shippingOrder + ", reportVersion=" + reportVersion);

		Client client = shippingOrder.getClient();

		Address address = null;
		DeliveryOrder deliveryOrder = shippingOrder.getDeliveryOrder();
		if (deliveryOrder != null) {
			address = deliveryOrder.getAddress();
		}

		Address packetAddress = null;
		boolean first = true;
		Map<String, DeliveryReportDto> reportItemMap = new HashMap<>();
		for (ShippingOrderLine shippingOrderLine : shippingOrder.getLines()) {
			Packet packet = shippingOrderLine.getPacket();
			if (packet == null) {
				continue;
			}
			if (first) {
				packetAddress = packet.getAddress();
			}
			if (!Objects.equals(packetAddress, packet.getAddress())) {
				packetAddress = null;
			}
			UnitLoad unitLoad = packet.getUnitLoad();
			registerItems(reportItemMap, unitLoad, packet, true);
			first = false;
		}
		List<DeliveryReportDto> reportItems = new ArrayList<>();
		reportItems.addAll(reportItemMap.values());
		Collections.sort(reportItems, new UnitLoadReportDtoComparator());

		if (address == null) {
			address = packetAddress;
		}
		if (address == null) {
			address = new Address();
		}

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("shippingOrder", shippingOrder);
		parameters.put("address", address);

		byte[] data = reportBusiness.createPdfDocument(client, SHIPPING_PACKETLIST_REPORT_NAME, reportVersion,
				Wms2BundleResolver.class, reportItems, parameters);

		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("Packetlist");

		return doc;
	}

	public Document generatePacketList(DeliveryOrder deliveryOrder) throws BusinessException {
		return generatePacketList(deliveryOrder, null);
	}

	public Document generatePacketList(DeliveryOrder deliveryOrder, String reportVersion) throws BusinessException {
		String logStr = "generatePacketList ";
		logger.log(Level.INFO, logStr + "deliveryOrder=" + deliveryOrder + ", reportVersion=" + reportVersion);

		Client client = deliveryOrder.getClient();

		Address address = deliveryOrder.getAddress();
		if (address == null) {
			address = new Address();
		}

		Map<String, DeliveryReportDto> reportItemMap = new HashMap<>();
		for (Packet packet : packetService.readByDeliveryOrder(deliveryOrder)) {
			registerItems(reportItemMap, packet.getUnitLoad(), packet, true);
		}
		List<DeliveryReportDto> reportItems = new ArrayList<>();
		reportItems.addAll(reportItemMap.values());
		Collections.sort(reportItems, new UnitLoadReportDtoComparator());

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("deliveryOrder", deliveryOrder);
		parameters.put("address", address);

		byte[] data = reportBusiness.createPdfDocument(client, DELIVERY_PACKETLIST_REPORT_NAME, reportVersion,
				Wms2BundleResolver.class, reportItems, parameters);

		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("Packetlist");

		return doc;
	}

	public Document generateDeliverynote(DeliveryOrder order) throws BusinessException {
		return generateDeliverynote(order);
	}

	public Document generateDeliverynote(DeliveryOrder order, String reportVersion) throws BusinessException {
		String logStr = "generateDeliverynote ";
		logger.log(Level.INFO, logStr + "order=" + order + ", reportVersion=" + reportVersion);

		Client client = order.getClient();

		Address address = order.getAddress();
		if (address == null) {
			address = new Address();
		}

		Map<ItemData, BigDecimal> orderAmountMap = new HashMap<>();
		Map<ItemData, BigDecimal> unitLoadAmountMap = new HashMap<>();
		Map<String, DeliveryReportDto> reportItemMap = new HashMap<>();

		for (DeliveryOrderLine orderLine : order.getLines()) {
			registerAmount(orderAmountMap, orderLine.getItemData(), orderLine.getPickedAmount());
		}

		List<Packet> packets = packetService.readByDeliveryOrder(order);
		for (Packet packet : packets) {
			if (packet.getState() > OrderState.FINISHED) {
				continue;
			}
			registerItems(reportItemMap, packet.getUnitLoad(), packet, false);
		}

		for (DeliveryReportDto item : reportItemMap.values()) {
			if (item.getType().equals(ITEM_TYPE_ITEMDATA)) {
				registerAmount(unitLoadAmountMap, item.getItemData(), item.getAmount());
			}
		}

		// Not all picking packets can be resolved. Compare the sum of the amounts with
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

			if (orderAmount.compareTo(unitLoadAmount) > 0 || orderAmount.compareTo(BigDecimal.ZERO) == 0) {
				// More picked amount in order line than in packet
				// Add difference to sheet
				BigDecimal diffAmount = orderAmount.subtract(unitLoadAmount);
				String key = "-" + itemData.getId();
				registerItem(reportItemMap, key, ITEM_TYPE_LINE, null, itemData, null);
				registerItem(reportItemMap, key, ITEM_TYPE_ITEMDATA, null, itemData, diffAmount);

			} else if (orderAmount.compareTo(unitLoadAmount) < 0) {
				// more picked amount in packet than in order line
				// Not possible
				logger.log(Level.WARNING, logStr + "There is more amount in unit loads than order lines. itemData="
						+ itemData + ", unitLoadAmount=" + unitLoadAmount + ", orderLineAmount=" + orderAmount);
			}
		}

		List<DeliveryReportDto> reportItems = new ArrayList<>();
		reportItems.addAll(reportItemMap.values());
		Collections.sort(reportItems, new UnitLoadReportDtoComparator());

		int lineNumber = 0;
		ItemData itemData = null;
		for (DeliveryReportDto reportItem : reportItems) {
			if (itemData == null || !itemData.equals(reportItem.getItemData())) {
				itemData = reportItem.getItemData();
				lineNumber++;
			}
			reportItem.setLineNumber("" + lineNumber);
		}

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("deliveryOrder", order);
		parameters.put("address", address);

		byte[] data = reportBusiness.createPdfDocument(client, DELIVERYNOTE_REPORT_NAME, reportVersion,
				Wms2BundleResolver.class, reportItems, parameters);

		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("Deliverynote");

		return doc;
	}

	private void registerItems(Map<String, DeliveryReportDto> itemMap, UnitLoad unitLoad, Packet packet,
			boolean separateUnitLoad) {
		PickingOrder pickingOrder = null;
		DeliveryOrder deliveryOrder = null;

		String baseKey = "";
		UnitLoad registerUnitLoad = null;
		if (separateUnitLoad && unitLoad != null) {
			registerUnitLoad = unitLoad;
			baseKey += unitLoad.getId();
			DeliveryReportDto packetReportItem = registerItem(itemMap, baseKey, ITEM_TYPE_PACKET, unitLoad, null, null);
			packetReportItem.setExternalId(unitLoad.getExternalId());
		}
		if (packet != null) {
			pickingOrder = packet.getPickingOrder();
			deliveryOrder = packet.getDeliveryOrder();
		}

		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);
		for (StockUnit stock : stocksOnUnitLoad) {

			String key = baseKey + "-" + stock.getItemData().getId();

			registerItem(itemMap, key, ITEM_TYPE_LINE, registerUnitLoad, stock.getItemData(), null);

			DeliveryReportDto itemDatareportItem = registerItem(itemMap, key, ITEM_TYPE_ITEMDATA, registerUnitLoad,
					stock.getItemData(), stock.getAmount());

			if (deliveryOrder != null) {
				// Try to get some additional info of the single items out of the delivery order
				for (DeliveryOrderLine deliveryOrderLine : deliveryOrder.getLines()) {
					if (deliveryOrderLine.getItemData().equals(stock.getItemData())) {
						itemDatareportItem.setPickingHint(deliveryOrderLine.getPickingHint());
						itemDatareportItem.setPackingHint(deliveryOrderLine.getPackingHint());
						itemDatareportItem.setShippingHint(deliveryOrderLine.getShippingHint());
						itemDatareportItem.setUnitPrice(deliveryOrderLine.getUnitPrice());
					}
				}
			}
			if (pickingOrder != null) {
				// Try to get some additional info of the single items out of the picking order
				// The picking order trumps the delivery order
				for (PickingOrderLine pickingOrderLine : pickingOrder.getLines()) {
					if (pickingOrderLine.getItemData().equals(stock.getItemData())) {
						itemDatareportItem.setPickingHint(pickingOrderLine.getPickingHint());
						itemDatareportItem.setPackingHint(pickingOrderLine.getPackingHint());
						itemDatareportItem.setShippingHint(pickingOrderLine.getShippingHint());
						itemDatareportItem.setUnitPrice(pickingOrderLine.getUnitPrice());
					}
				}
			}

			if (!StringUtils.isBlank(stock.getLotNumber())) {
				String lotKey = key + "-" + stock.getLotNumber().hashCode();
				DeliveryReportDto reportItem = registerItem(itemMap, lotKey, ITEM_TYPE_LOT, registerUnitLoad,
						stock.getItemData(), stock.getAmount());
				reportItem.setLotNumber(stock.getLotNumber());
			}
			if (stock.getBestBefore() != null) {
				String bestBeforeKey = key + "-" + stock.getBestBefore().hashCode();
				DeliveryReportDto reportItem = registerItem(itemMap, bestBeforeKey, ITEM_TYPE_BESTBEFORE,
						registerUnitLoad, stock.getItemData(), stock.getAmount());
				reportItem.setBestBefore(stock.getBestBefore());
			}
			if (!StringUtils.isBlank(stock.getLotNumber()) && stock.getBestBefore() != null) {
				String lotBestBeforeKey = key + "-" + stock.getLotNumber().hashCode() + "-"
						+ stock.getBestBefore().hashCode();
				DeliveryReportDto reportItem = registerItem(itemMap, lotBestBeforeKey, ITEM_TYPE_LOT_BESTBEFORE,
						registerUnitLoad, stock.getItemData(), stock.getAmount());
				reportItem.setLotNumber(stock.getLotNumber());
				reportItem.setBestBefore(stock.getBestBefore());
			}
			if (!StringUtils.isBlank(stock.getSerialNumber())) {
				String serialKey = key + "-" + stock.getSerialNumber().hashCode();
				DeliveryReportDto reportItem = registerItem(itemMap, serialKey, ITEM_TYPE_SERIAL, registerUnitLoad,
						stock.getItemData(), stock.getAmount());
				reportItem.setSerialNumber(stock.getSerialNumber());
			}
		}
	}

	private DeliveryReportDto registerItem(Map<String, DeliveryReportDto> itemMap, String key, String type,
			UnitLoad unitLoad, ItemData itemData, BigDecimal amount) {
		DeliveryReportDto value = itemMap.get(key + type);
		if (value == null) {
			value = new DeliveryReportDto(type, itemData, amount);
			value.setUnitLoad(unitLoad);
			if (unitLoad != null) {
				String label = unitLoad.getLabelId();
				String suffix = "-" + unitLoad.getId();
				value.setLabel(StringUtils.substringBefore(label, suffix));
			}
			itemMap.put(key + type, value);
		} else {
			value.addAmount(amount);
		}

		return value;
	}

	private void registerAmount(Map<ItemData, BigDecimal> registeredAmountMap, ItemData itemData, BigDecimal amount) {
		BigDecimal registeredAmount = registeredAmountMap.get(itemData);
		if (registeredAmount == null) {
			registeredAmountMap.put(itemData, amount);
		} else {
			registeredAmountMap.put(itemData, registeredAmount.add(amount));
		}
	}

	private static class UnitLoadReportDtoComparator implements Comparator<DeliveryReportDto>, Serializable {
		private static final long serialVersionUID = 1L;

		public int compare(DeliveryReportDto o1, DeliveryReportDto o2) {

			if (o1.getUnitLoad() != null && o2.getUnitLoad() != null) {
				int x = StringUtils.compare(o1.getUnitLoad().getLabelId(), o2.getUnitLoad().getLabelId());
				if (x != 0) {
					return x;
				}
			}

			if (o1.getItemData() != null && o2.getItemData() != null) {
				int x = StringUtils.compare(o1.getItemData().getNumber(), o2.getItemData().getNumber());
				if (x != 0) {
					return x;
				}
			}

			int x = StringUtils.compare(o1.getType(), o2.getType());
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

	public List<String> readContentListReportVersions(Client client) throws BusinessException {
		return reportBusiness.readReportVersions(CONTENTLIST_REPORT_NAME, client);
	}

	public List<String> readPickingPacketListReportVersions(Client client) throws BusinessException {
		return reportBusiness.readReportVersions(PICKING_PACKETLIST_REPORT_NAME, client);
	}

	public List<String> readShippingPacketListReportVersions(Client client) throws BusinessException {
		return reportBusiness.readReportVersions(SHIPPING_PACKETLIST_REPORT_NAME, client);
	}

	public List<String> readDeliveryPacketListReportVersions(Client client) throws BusinessException {
		return reportBusiness.readReportVersions(DELIVERY_PACKETLIST_REPORT_NAME, client);
	}

	public List<String> readDeliveryNoteReportVersions(Client client) throws BusinessException {
		return reportBusiness.readReportVersions(DELIVERYNOTE_REPORT_NAME, client);
	}
}
