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
package de.wms2.mywms.project;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.linogistix.los.common.facade.VersionFacade;
import de.wms2.mywms.module.ModuleRuntime;
import de.wms2.mywms.module.ModuleRuntimeBusiness;
import de.wms2.mywms.project.module.ProjectRuntimeService;
import de.wms2.mywms.util.Translator;

@Stateless
public class VersionFacadeBean implements VersionFacade {

	@Inject
	private ModuleRuntimeBusiness moduleRuntimeBoundary;

	@Inject
	private ProjectRuntimeService projectRuntime;

	@Override
	public String getInfo() {

		String info = "<b>" + projectRuntime.getImplementationTitle() + "</b> - "
				+ projectRuntime.getImplementationVersion();
		String build = getProperty("build.date");
		if (!StringUtils.isBlank(build)) {
			info += ", Build " + build;
		}
		String user = getProperty("user.name");
		if (!StringUtils.isBlank(user)) {
			info += ", by " + user;
		}

		info += "<br>";

		List<ModuleRuntime> serverModules = moduleRuntimeBoundary.getModuleList();
		Collections.sort(serverModules, new ModuleComparator());

		for (ModuleRuntime serverModule : serverModules) {
			if (serverModule.equals(projectRuntime)) {
				continue;
			}
			info += "<li><b>" + serverModule.getImplementationTitle() + "</b>";
			info += " - " + serverModule.getImplementationVersion() + "</li>";
		}

		return info;
	}

	@Override
	public String getTitle() {
		return projectRuntime.getImplementationTitle();
	}

	@Override
	public String getVersion() {
		return projectRuntime.getImplementationVersion();
	}

	private static String getProperty(String key) {
		String prop = Translator.getString(VersionFacadeBean.class, "translation.VersionBundle", null, key, null,
				Locale.getDefault(), new Object[] {});
		if (prop == null) {
			return "";
		}
		if (prop.startsWith(key)) {
			return "";
		}
		return prop.trim();
	}

	static class ModuleComparator implements Comparator<ModuleRuntime>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(ModuleRuntime arg0, ModuleRuntime arg1) {
			if (arg0 == null) {
				return 1;
			}
			if (arg1 == null) {
				return -1;
			}
			String name0 = arg0.getModuleName();
			if (name0 == null) {
				return -1;
			}
			String name1 = arg1.getModuleName();
			if (name1 == null) {
				return -1;
			}

			return name0.compareTo(name1);
		}
	}
}
