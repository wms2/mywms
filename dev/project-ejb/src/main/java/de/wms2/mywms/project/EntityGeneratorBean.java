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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;

import org.mywms.model.BasicEntity;

import de.linogistix.los.customization.EntityGenerator;

/**
 * @author krane
 *
 */
@Stateless
public class EntityGeneratorBean implements EntityGenerator {
	Logger log = Logger.getLogger(this.getClass().getName());

	@Override
	public <ENTITY extends BasicEntity> ENTITY generateEntity(Class<ENTITY> entityClass) {

		// If you have to create special data types with extended classes just return an
		// instance of the correct class.
		// The default way is to generate an instance of the given class

		try {
			return entityClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Cannot create instance of class=" + entityClass.getName(), e);
		}

		return null;
	}
}
