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
package de.wms2.mywms.document;

/**
 * Predefined document types
 * 
 * @author krane
 *
 */
public class DocumentType {

	public static final String PDF = "application/pdf";
	public static final String XML = "text/xml";
	public static final String JPG = "image/jpg";
	public static final String PNG = "image/png";
	public static final String HTML = "text/html";
	public static final String TEXT = "text/plain";
	public static final String UNDEFINED = "application/octet-stream";

	public static final String EXCEL = "application/vnd.ms-excel";
	public static final String WORD = "application/vnd.ms-word";
	public static final String WRITER = "application/vnd.oasis.opendocument.text";
	public static final String CALC = "application/vnd.oasis.opendocument.spreadsheet";
}
