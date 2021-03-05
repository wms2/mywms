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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.mywms.model.Client;

import de.wms2.mywms.document.Document;
import de.wms2.mywms.document.DocumentBusiness;
import de.wms2.mywms.document.DocumentType;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.product.ItemData;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.report.ReportBusiness;
import de.wms2.mywms.util.Translator;
import de.wms2.mywms.util.Wms2BundleResolver;
import de.wms2.mywms.util.Wms2Properties;
import net.sf.jasperreports.engine.JRParameter;

public class StockUnitReportGenerator {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final String STOCKUNIT_REPORT_NAME = "StockUnitLabel";

	@Inject
	private ReportBusiness reportBusiness;
	@Inject
	private StockUnitEntityService stockUnitService;
	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@Inject
	private DocumentBusiness documentBusiness;

	public Document generateReport(UnitLoad unitLoad) throws BusinessException {
		return generateReport(unitLoad, null);
	}

	public Document generateReport(UnitLoad unitLoad, String reportVersion) throws BusinessException {
		String logStr = "generateReport ";
		logger.log(Level.INFO, logStr + "unitLoad=" + unitLoad + ", reportVersion=" + reportVersion);

		Client client = unitLoad.getClient();

		List<StockUnitReportDto> reportItems = new ArrayList<>();
		List<StockUnit> stocksOnUnitLoad = stockUnitService.readByUnitLoad(unitLoad);

		for (StockUnit stockUnit : stocksOnUnitLoad) {
			StockUnitReportDto dto = new StockUnitReportDto(stockUnit);

			Document itemImage = documentBusiness.readImage(ItemData.class, stockUnit.getItemData().getId());
			if (itemImage != null) {
				dto.setImage(itemImage);
			}

			reportItems.add(dto);
		}

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("printDate", new Date());
		parameters.put("unitLoad", unitLoad);

		String localeString = propertyBusiness.getString(Wms2Properties.KEY_REPORT_LOCALE, null);
		Locale locale = Translator.parseLocale(localeString);
		parameters.put(JRParameter.REPORT_LOCALE, locale);
		ResourceBundle bundle = Translator.getBundle(Wms2BundleResolver.class, null, locale);
		parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, bundle);

		byte[] data = reportBusiness.createPdfDocument(client, STOCKUNIT_REPORT_NAME, reportVersion,
				Wms2BundleResolver.class, reportItems, parameters);
		Document doc = new Document();
		doc.setData(data);
		doc.setDocumentType(DocumentType.PDF);
		doc.setName("label");

		return doc;
	}

	public List<String> readReportVersions(Client client) throws BusinessException {
		return reportBusiness.readReportVersions(STOCKUNIT_REPORT_NAME, client);
	}
}
