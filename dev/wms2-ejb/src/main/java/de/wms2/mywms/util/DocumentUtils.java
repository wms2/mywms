/* 
Copyright 2019 Matthias Krane

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
package de.wms2.mywms.util;

import java.util.HashMap;
import java.util.Map;

import de.wms2.mywms.document.DocumentType;

/**
 * Utilities to handle Documents
 * 
 * @author krane
 *
 */
public class DocumentUtils {

	private static Map<String, String> extensionMap = new HashMap<String, String>();
	private static Map<String, String> typeMap = new HashMap<String, String>();

	static {
		typeMap.put(DocumentType.WRITER, "odt");
		typeMap.put(DocumentType.CALC, "ods");
		typeMap.put(DocumentType.WORD, "doc");
		typeMap.put(DocumentType.EXCEL, "xls");
		typeMap.put(DocumentType.PDF, "pdf");
		typeMap.put(DocumentType.JPG, "jpg");
		typeMap.put(DocumentType.PNG, "png");
		typeMap.put(DocumentType.HTML, "htm");
		typeMap.put(DocumentType.TEXT, "txt");
		typeMap.put(DocumentType.XML, "xml");

		extensionMap.put("odt", DocumentType.WRITER);
		extensionMap.put("ods", DocumentType.CALC);
		extensionMap.put("doc", DocumentType.WORD);
		extensionMap.put("xls", DocumentType.EXCEL);
		extensionMap.put("pdf", DocumentType.PDF);
		extensionMap.put("jpg", DocumentType.JPG);
		extensionMap.put("png", DocumentType.PNG);
		extensionMap.put("htm", DocumentType.HTML);
		extensionMap.put("html", DocumentType.HTML);
		extensionMap.put("txt", DocumentType.TEXT);
		extensionMap.put("xml", DocumentType.XML);
		extensionMap.put("jrxml", DocumentType.XML);
	}

	public static String resolveExtension(String documentType) {
		String extension = typeMap.get(documentType);
		return extension;
	}

	public static String resolveDocumentType(String extension) {
		String type = extensionMap.get(extension);
		return type;
	}

	public static String resolveDocumentType(String extension, String defaultValue) {

		String type = extensionMap.get(extension);
		if (type == null) {
			return defaultValue;
		}
		return type;
	}

}
