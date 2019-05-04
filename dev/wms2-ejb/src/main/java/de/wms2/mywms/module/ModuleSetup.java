/* 
Copyright 2014-2019 Matthias Krane

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
package de.wms2.mywms.module;

import java.util.Locale;

import de.wms2.mywms.exception.BusinessException;


/**
 * Setup of a module.
 * <p>
 * Every module has it's own setup service. It is used to generate basic data,
 * demo data or run test procedures.
 * 
 * @author krane
 *
 */
public abstract class ModuleSetup {

	public enum SetupLevel {
		UNINITIALIZED, INITIALIZED, EXPIRED, DEMO_SMALL, DEMO_MEDIUM, DEMO_LARGE
	}

	/**
	 * Get the modules base package.
	 * <p>
	 * The package has to be unique within the application.
	 */
	public abstract String getModulePackage();


	public boolean isActive() {
		return true;
	}

	/**
	 * Give the setup methods an order to be called.<br>
	 * The base modules use values below 10 to be the first modules to be
	 * initialized.<br>
	 * If possible leave this at the default value.
	 */
	public int getOrderIndex() {
		return 99;
	}

	/**
	 * Start the setup for the given level.
	 * 
	 * @param level  The SetupLevel
	 * @param locale The language
	 */
	public abstract void setup(SetupLevel level, Locale locale) throws BusinessException;

	/**
	 * Remove all data of the module
	 */
	public void removeData() throws BusinessException {
	}

	/**
	 * Start the selftest of the module.
	 * <p>
	 * Check whether all setup or other logical requirements are working
	 */
	public boolean runSelfTest() throws BusinessException {
		return true;
	}
}
