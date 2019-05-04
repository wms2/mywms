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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Service to access the ModuleRuntime implementations
 * 
 * @author krane
 *
 */
@Singleton
public class ModuleRuntimeBusiness {

	@Inject
	private Instance<ModuleRuntime> moduleRuntimeInstances;

	private List<ModuleRuntime> moduleList = new ArrayList<>();

	@PostConstruct
	private void startup() {
		moduleList = new ArrayList<>();

		for (ModuleRuntime module : moduleRuntimeInstances) {
			if (!moduleList.contains(module)) {
				moduleList.add(module);
			}
		}
	}

	public List<ModuleRuntime> getModuleList() {
		return moduleList;
	}

	@Lock(LockType.WRITE)
	public void registerModule(ModuleRuntime module) {
		if (!moduleList.contains(module)) {
			moduleList.add(module);
		}
	}

}
