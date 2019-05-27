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
package de.wms2.mywms.inventory;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.mywms.facade.FacadeException;
import org.mywms.model.BasicClientAssignedEntity;

import de.wms2.mywms.product.ItemData;

/**
 * This class replaces myWMS:Lot
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "itemData_id" }) })
public class Lot extends BasicClientAssignedEntity {
	private static final long serialVersionUID = 1L;

	private String name;

	@Column(nullable = false, name = "lot_date")
	@Temporal(TemporalType.DATE)
	private Date date;

	@ManyToOne(optional = false)
	private ItemData itemData;

	@Temporal(TemporalType.DATE)
	private Date useNotBefore;

	@Temporal(TemporalType.DATE)
	private Date bestBeforeEnd;

	@Column(precision = 15, scale = 2)
	private BigDecimal height;

	@Column(precision = 15, scale = 2)
	private BigDecimal width;

	@Column(precision = 15, scale = 2)
	private BigDecimal depth;

	@Column(precision = 19, scale = 6)
	private BigDecimal volume;

	@Column(precision = 16, scale = 3)
	private BigDecimal weight;

	private String code;

	private String age;

	@PreUpdate
	@PrePersist
	public void proPersist() throws FacadeException {
		if (getAdditionalContent() != null && getAdditionalContent().length() > 255) {
			setAdditionalContent(getAdditionalContent().substring(0, 255));
		}
	}

	@Override
	public String toString() {
		String value = "";
		if (name != null) {
			value += name;
		}
		if (itemData != null) {
			value += itemData.getNumber();
		}
		return value;
	}

	@Override
	public String toUniqueString() {
		if (name != null) {
			return name;
		}
		return super.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public ItemData getItemData() {
		return itemData;
	}

	public void setItemData(ItemData itemData) {
		this.itemData = itemData;
	}

	public Date getUseNotBefore() {
		return useNotBefore;
	}

	public void setUseNotBefore(Date useNotBefore) {
		this.useNotBefore = useNotBefore;
	}

	public Date getBestBeforeEnd() {
		return bestBeforeEnd;
	}

	public void setBestBeforeEnd(Date bestBeforeEnd) {
		this.bestBeforeEnd = bestBeforeEnd;
	}

	public BigDecimal getHeight() {
		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;
	}

	public BigDecimal getWidth() {
		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;
	}

	public BigDecimal getDepth() {
		return depth;
	}

	public void setDepth(BigDecimal depth) {
		this.depth = depth;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

}
