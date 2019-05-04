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

import java.io.Serializable;

/**
 * This class holds general information about a module.
 * <p>
 * Just extend this class and put it into the cdi context. (deploy it with the
 * main application or explicit register it with the ModuleRuntimeBoundary)
 * 
 * @author krane
 *
 */
public abstract class ModuleRuntime implements Serializable {
	private static final long serialVersionUID = 1L;

	private String specificationVendor;
	private String implementationVendor;
	private String specificationVersion;
	private String implementationVersion;
	private String specificationTitle;
	private String implementationTitle;
	private boolean isMaster = false;

	public ModuleRuntime() {
		Package modulePackage = this.getClass().getPackage();

		specificationTitle = modulePackage.getSpecificationTitle();
		implementationTitle = modulePackage.getImplementationTitle();
		specificationVendor = modulePackage.getSpecificationVendor();
		implementationVendor = modulePackage.getImplementationVendor();
		specificationVersion = modulePackage.getSpecificationVersion();
		implementationVersion = modulePackage.getImplementationVersion();
	}

	public String getModuleName() {
		return (implementationTitle == null ? "" : implementationTitle);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ModuleRuntime)) {
			return false;
		}

		ModuleRuntime otherModule = (ModuleRuntime) other;
		return getModuleName().equals(otherModule.getModuleName());
	}

	@Override
	public int hashCode() {
		return getModuleName().hashCode();
	}

	@Override
	public String toString() {
		return specificationTitle + "/" + implementationTitle;
	}

	public String getSpecificationVendor() {
		return specificationVendor;
	}

	public void setSpecificationVendor(String specVendor) {
		this.specificationVendor = specVendor;
	}

	public String getImplementationVendor() {
		return implementationVendor;
	}

	public void setImplementationVendor(String implVendor) {
		this.implementationVendor = implVendor;
	}

	public String getSpecificationVersion() {
		return specificationVersion;
	}

	public void setSpecificationVersion(String specVersion) {
		this.specificationVersion = specVersion;
	}

	public String getImplementationVersion() {
		return implementationVersion;
	}

	public void setImplementationVersion(String implVersion) {
		this.implementationVersion = implVersion;
	}

	public String getSpecificationTitle() {
		return specificationTitle;
	}

	public void setSpecificationTitle(String specTitle) {
		this.specificationTitle = specTitle;
	}

	public String getImplementationTitle() {
		return implementationTitle;
	}

	public void setImplementationTitle(String implTitle) {
		this.implementationTitle = implTitle;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

}
