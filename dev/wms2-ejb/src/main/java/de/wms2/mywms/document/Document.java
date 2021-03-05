/* 
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.mywms.model.BasicEntity;

/**
 * Store documents as blob in database
 * 
 * @author krane
 *
 */
@Entity
@Table
public class Document extends BasicEntity {
	private static final long serialVersionUID = 1L;

	@Column(unique = true, nullable = false)
	private String name;

	private String documentType;

	@Lob
	private byte[] data;

	@Override
	public String toString() {
		if (name != null) {
			return name;
		}
		return super.toString();
	}

	public String getSimpleName() {
		String simpleName = StringUtils.substringAfterLast(name, "/");
		if (StringUtils.isBlank(simpleName)) {
			simpleName = name;
		}

		return simpleName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
