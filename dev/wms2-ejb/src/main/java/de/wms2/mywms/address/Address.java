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
package de.wms2.mywms.address;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.mywms.model.BasicEntity;

/**
 * Address data
 * <p>
 * 
 * @author krane
 */
@Entity
@Table
public class Address extends BasicEntity {
	private static final long serialVersionUID = 1L;

	private String company;
	private String title;
	private String firstName;
	private String lastName;
	private String nameAffix;
	private String street;
	private String streetNumber;
	private String zipCode;
	private String city;
	private String countryCode;
	private String country;

	@Override
	public String toUniqueString() {
		String value = "";
		if (firstName != null) {
			value += firstName;
		}
		if (lastName != null) {
			if (value.length() > 0) {
				value += " ";
			}
			value += lastName;
		}
		if (city != null) {
			if (value.length() > 0) {
				value += ", ";
			}
			value += city;
		}
		if (value.length() > 0) {
			return value;
		}
		return super.toUniqueString();
	}
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNameAffix() {
		return nameAffix;
	}

	public void setNameAffix(String nameAffix) {
		this.nameAffix = nameAffix;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
