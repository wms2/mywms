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
import de.wms2.mywms.address.Address;

public class AddressTO extends BODTO<Address> {

	public static final long serialVersionUID = 1L;

	public String name;

	public AddressTO(Address address) {
		this(address.getId(), address.getVersion(), address.getFirstName(), address.getLastName(), address.getCity());
	}

	public AddressTO(Long id, int version, String firstName, String lastName, String city) {
		super(id, version, id);

		name = "";
		if (firstName != null) {
			name += firstName;
		}
		if (lastName != null) {
			if (name.length() > 0) {
				name += " ";
			}
			name += lastName;
		}
		if (city != null) {
			if (name.length() > 0) {
				name += ", ";
			}
			name += city;
		}
		if (name.length() == 0) {
			name += id;
		}
		setClassName(Address.class.getName());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
