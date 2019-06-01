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
package de.linogistix.los.inventory.query.dto;

import de.linogistix.los.query.BODTO;
import de.wms2.mywms.product.PackagingUnit;

/**
 * @author krane
 *
 */
public class PackagingUnitTO extends BODTO<PackagingUnit> {

	private static final long serialVersionUID = 1L;

	private String itemDataNumber;
	private String itemDataName;

	public PackagingUnitTO(PackagingUnit packagingUnit) {
		super(packagingUnit.getId(), packagingUnit.getVersion(), packagingUnit.getName());
		this.itemDataName = packagingUnit.getItemData().getName();
		this.itemDataNumber = packagingUnit.getItemData().getNumber();
	}

	public PackagingUnitTO(Long id, int version, String name) {
		super(id, version, name);
	}

	public PackagingUnitTO(Long id, int version, String name, String itemDataNumber, String itemDataName) {
		super(id, version, name);
		this.itemDataName = itemDataName;
		this.itemDataNumber = itemDataNumber;
	}

	public String getItemDataNumber() {
		return itemDataNumber;
	}

	public void setItemDataNumber(String itemDataNumber) {
		this.itemDataNumber = itemDataNumber;
	}

	public String getItemDataName() {
		return itemDataName;
	}

	public void setItemDataName(String itemDataName) {
		this.itemDataName = itemDataName;
	}
}
