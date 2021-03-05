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
package de.wms2.mywms.report;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.product.ItemDataState;

/**
 * Report forms
 * <p>
 * This class is based on myWMS-LOS:LOSJasperReport
 * 
 * @author krane
 *
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "reportVersion", "client_id" }) })
public class Report extends BasicClientAssignedEntity {

	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_REPORT_VERSION = "Default";

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String reportVersion = DEFAULT_REPORT_VERSION;

	@Column(nullable = false)
	private int state = ItemDataState.ACTIVE;

	@Override
	public String toString() {
		return name + "(" + getClient() + ")";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReportVersion() {
		return reportVersion;
	}

	public void setReportVersion(String reportVersion) {
		this.reportVersion = reportVersion;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
