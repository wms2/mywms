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

import de.linogistix.los.inventory.query.dto.PackagingUnitTO;
import de.linogistix.los.query.BusinessObjectQueryBean;
import de.linogistix.los.query.TemplateQueryWhereToken;
import de.wms2.mywms.product.PackagingUnit;

/**
 * @author krane
 *
 */
@Stateless
public class PackagingUnitQueryBean extends BusinessObjectQueryBean<PackagingUnit> implements PackagingUnitQueryRemote {

	private static final String[] dtoProps = new String[] { "id", "version", "name", "itemData.number",
			"itemData.name" };

	@Override
	protected String[] getBODTOConstructorProps() {
		return dtoProps;
	}

	@Override
	public String getUniqueNameProp() {
		return "name";
	}

	@Override
	public Class<PackagingUnitTO> getBODTOClass() {
		return PackagingUnitTO.class;
	}

	@Override
	protected List<TemplateQueryWhereToken> getAutoCompletionTokens(String value) {
		List<TemplateQueryWhereToken> ret = new ArrayList<TemplateQueryWhereToken>();

		TemplateQueryWhereToken number = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE, "name",
				value);
		number.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(number);

		TemplateQueryWhereToken iName = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE,
				"itemData.name", value);
		iName.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(iName);

		TemplateQueryWhereToken iNumber = new TemplateQueryWhereToken(TemplateQueryWhereToken.OPERATOR_LIKE,
				"itemData.number", value);
		iNumber.setLogicalOperator(TemplateQueryWhereToken.OPERATOR_OR);
		ret.add(iNumber);

		return ret;
	}

}
