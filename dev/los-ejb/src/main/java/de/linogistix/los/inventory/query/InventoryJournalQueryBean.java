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
package de.linogistix.los.inventory.query;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import de.linogistix.los.inventory.query.dto.InventoryJournalTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.inventory.InventoryJournal;

@Stateless
public class InventoryJournalQueryBean extends BusinessObjectQueryBean<InventoryJournal>
		implements InventoryJournalQueryRemote {

	private static final String[] dtoProps = new String[] { "id", "version", "id", "productNumber", "lotNumber",
			"amount", "stockUnitAmount", "fromStorageLocation", "fromUnitLoad", "toStorageLocation", "toUnitLoad",
			"activityCode", "recordType", "created", "unitLoadType" };

	@Override
	public String getUniqueNameProp() {
		return "id";
	}

	@Override
	public Class<InventoryJournalTO> getBODTOClass() {
		return InventoryJournalTO.class;
	}

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {

		List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

		TemplateQueryWhereToken itemData = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE,
				"productNumber", value);
		itemData.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(itemData);

		TemplateQueryWhereToken toSl = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE,
				"toStorageLocation", value);
		toSl.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(toSl);

		TemplateQueryWhereToken toUl = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "toUnitLoad",
				value);
		toUl.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(toUl);

		TemplateQueryWhereToken activityCode = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE,
				"activityCode", value);
		activityCode.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(activityCode);

		return ret;
	}

}
