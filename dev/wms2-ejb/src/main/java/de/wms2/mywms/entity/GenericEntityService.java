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
package de.wms2.mywms.entity;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.mywms.model.BasicEntity;

/**
 * Generic service to handle read access to database entities
 * 
 * @author krane
 *
 */
@Stateless
public class GenericEntityService {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private PersistenceManager manager;

	/**
	 * Read all entities of the given type
	 * 
	 * @param entityType The entity type
	 */
	public <ENTITY_TYPE extends BasicEntity> List<ENTITY_TYPE> readList(Class<ENTITY_TYPE> entityType) {
		return readList(entityType, new String[] {}, new Object[] {}, null, null, null);
	}

	/**
	 * Read all entities for the given data
	 * 
	 * @param entityType       The entity type
	 * @param orderByAttribute The name of the attribute which is used to order the
	 *                         results
	 */
	public <ENTITY_TYPE extends BasicEntity> List<ENTITY_TYPE> readList(Class<ENTITY_TYPE> entityType,
			String orderByAttribute) {
		return readList(entityType, new String[] {}, new Object[] {}, new String[] { orderByAttribute }, null, null);
	}

	/**
	 * Read a list of entities for the given data
	 * 
	 * @param entityType       The entity type
	 * @param attributeName    The name of the attribute which is checked
	 * @param attributeValue   The values of the checked attribute
	 * @param orderByAttribute The name of the attribute which is used to order the
	 *                         results
	 */
	public <ENTITY_TYPE extends BasicEntity> List<ENTITY_TYPE> readList(Class<ENTITY_TYPE> entityType,
			String attributeName, Object attributeValue, String orderByAttribute) {
		return readList(entityType, new String[] { attributeName }, new Object[] { attributeValue },
				new String[] { orderByAttribute }, null, null);
	}

	/**
	 * Read a list of entities for the given data
	 * 
	 * @param entityType       The entity type
	 * @param attributeName    The name of the attribute which is checked
	 * @param attributeValue   The values of the checked attribute
	 * @param orderByAttribute The name of the attribute which is used to order the
	 *                         results
	 * @param offset           The first result
	 * @param limit            The maximal number of results
	 */
	public <ENTITY_TYPE extends BasicEntity> List<ENTITY_TYPE> readList(Class<ENTITY_TYPE> entityType,
			String attributeName, Object attributeValue, String orderByAttribute, Integer offset, Integer limit) {
		String[] attributeNames = new String[] {};
		Object[] attributeValues = new Object[] {};
		String[] orderByAttributes = new String[] {};
		if (attributeName != null && attributeValue != null) {
			attributeNames = new String[] { attributeName };
			attributeValues = new Object[] { attributeValue };
		}
		if (orderByAttribute != null) {
			orderByAttributes = new String[] { orderByAttribute };
		}
		return readList(entityType, attributeNames, attributeValues, orderByAttributes, offset, limit);
	}

	/**
	 * Read a list of entities for the given data
	 * 
	 * @param entityType        The entity type
	 * @param attributeNames    The names of the attributes which are checked
	 * @param attributeValues   The values of the checked attributes
	 * @param orderByAttributes The names of the attributes which are used to order
	 *                          the results
	 * @param offset            The first result
	 * @param limit             The maximal number of results
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY_TYPE extends BasicEntity> List<ENTITY_TYPE> readList(Class<ENTITY_TYPE> entityType,
			String[] attributeNames, Object[] attributeValues, String[] orderByAttributes, Integer offset,
			Integer limit) {
		Query query = generateQuery(entityType, "SELECT entity ", attributeNames, attributeValues, orderByAttributes,
				null);
		if (offset != null) {
			query.setFirstResult(offset);
		}
		if (limit != null) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	/**
	 * Read the first entity for the given data
	 * 
	 * @param entityType       The entity type
	 * @param attributeName    The name of the attribute which is checked
	 * @param attributeValue   The values of the checked attribute
	 * @param orderByAttribute The name of the attribute which is used to order the
	 *                         results
	 */
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE readFirst(Class<ENTITY_TYPE> entityType, String attributeName,
			Object attributeValue, String orderByAttribute) {

		return readFirst(entityType, new String[] { attributeName }, new Object[] { attributeValue },
				orderByAttribute == null ? null : new String[] { orderByAttribute });
	}

	/**
	 * Read the first entity for the given data
	 * 
	 * @param entityType        The entity type
	 * @param attributeNames    The names of the attributes which are checked
	 * @param attributeValues   The values of the checked attributes
	 * @param orderByAttributes The names of the attributes which are used to order
	 *                          the results
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE readFirst(Class<ENTITY_TYPE> entityType,
			String[] attributeNames, Object[] attributeValues, String[] orderByAttributes) {
		if (orderByAttributes == null) {
			orderByAttributes = new String[] { "id" };
		}
		Query query = generateQuery(entityType, "SELECT entity ", attributeNames, attributeValues, orderByAttributes,
				null);
		query.setMaxResults(1);

		try {
			return (ENTITY_TYPE) query.getSingleResult();
		} catch (Throwable t) {
		}
		return null;
	}

	public boolean exists(Class<? extends BasicEntity> entityType, String attributeName, Object value) {
		return exists(entityType, new String[] { attributeName }, new Object[] { value }, null);
	}

	/**
	 * Check whether at least one entity exists for the given data
	 * 
	 * @param entityType     The entity type
	 * @param attributeName  The name of the attribute which is checked
	 * @param attributeValue The values of the checked attribute
	 * @param ignoredId      Ignore entities with this id
	 */
	public boolean exists(Class<? extends BasicEntity> entityType, String attributeName, Object attributeValue,
			Object ignoredId) {
		return exists(entityType, new String[] { attributeName }, new Object[] { attributeValue }, ignoredId);
	}

	/**
	 * Check whether at least one entity exists for the given data
	 * 
	 * @param entityType      The entity type
	 * @param attributeNames  The names of the attributes which are checked
	 * @param attributeValues The values of the checked attributes
	 * @param ignoredId       Ignore entities with this id
	 */
	public boolean exists(Class<? extends BasicEntity> entityType, String[] attributeNames, Object[] attributeValues,
			Object ignoredId) {
		Query query = generateQuery(entityType, "SELECT entity.id ", attributeNames, attributeValues, null, ignoredId);
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (Throwable t) {
		}

		return false;
	}

	/**
	 * Check for an existing reference in a collection attribute of an entity
	 * (entityType) to another entity (value)
	 * 
	 * @param entityType    The entity type which references to the value
	 * @param attributeName The collection attribute of the entity type where the
	 *                      reference is checked
	 * @param value         The referenced value
	 */
	public boolean existsReference(Class<? extends BasicEntity> entityType, String attributeName, Object value) {
		String jpql = "SELECT entity.id FROM " + entityType.getName() + " entity where :value in elements(entity."
				+ attributeName + ")";
		Query query = manager.createQuery(jpql);
		query.setParameter("value", value);
		query.setMaxResults(1);
		try {
			query.getSingleResult();
			return true;
		} catch (Throwable t) {
		}

		return false;
	}

	/**
	 * Check whether at least one entity exists for the given data
	 * 
	 * @param entityType The entity type
	 */
	public long count(Class<? extends BasicEntity> entityType) {
		return count(entityType, new String[] {}, new Object[] {});
	}

	/**
	 * Check whether at least one entity exists for the given data
	 * 
	 * @param entityType     The entity type
	 * @param attributeName  The name of the attribute which is checked
	 * @param attributeValue The values of the checked attribute
	 */
	public long count(Class<? extends BasicEntity> entityType, String attributeName, Object attributeValue) {
		return count(entityType, new String[] { attributeName }, new Object[] { attributeValue });
	}

	/**
	 * Check whether at least one entity exists for the given data
	 * 
	 * @param entityType      The entity type
	 * @param attributeNames  The names of the attributes which are checked
	 * @param attributeValues The values of the checked attributes
	 */
	public long count(Class<? extends BasicEntity> entityType, String[] attributeNames, Object[] attributeValues) {
		Query query = generateQuery(entityType, "SELECT count(*) ", attributeNames, attributeValues, null, null);
		try {
			Long num = (Long) query.getSingleResult();
			return (num == null ? 0 : num.longValue());
		} catch (Throwable t) {
		}

		return 0;
	}

	/**
	 * Eager read of an entity
	 * 
	 * @param entityType The entity type
	 * @param entity     The entity to query
	 */
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE eagerRead(Class<ENTITY_TYPE> entityType, ENTITY_TYPE entity) {
		String logStr = "eagerRead ";
		if (entity == null) {
			logger.log(Level.WARNING, logStr + "No entity to reload");
			return null;
		}
		entity = manager.find(entityType, entity.getId());
		if (entity == null) {
			logger.log(Level.WARNING, logStr + "Cannot read entity");
			return null;
		}
		return manager.eagerRead(entity, false);
	}

	/**
	 * Eager read of an entity
	 * 
	 * @param entityType The entity type
	 * @param id         The id of the entity to query
	 */
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE eagerRead(Class<ENTITY_TYPE> entityType, Long id) {
		String logStr = "eagerRead ";
		ENTITY_TYPE entity = manager.find(entityType, id);
		if (entity == null) {
			logger.log(Level.WARNING, logStr + "Cannot read entity");
			return null;
		}
		return manager.eagerRead(entity, false);
	}

	private Query generateQuery(Class<? extends BasicEntity> entityType, String prefix, String[] attributeNames,
			Object[] attributeValues, String[] orderByAttributes, Object ignoredId) {
		String logStr = "generateQuery ";

		if (attributeNames == null) {
			attributeNames = new String[] {};
		}
		if (attributeValues == null) {
			attributeValues = new String[] {};
		}
		if (orderByAttributes == null) {
			orderByAttributes = new String[] {};
		}
		if (attributeNames.length != attributeValues.length) {
			logger.log(Level.WARNING, logStr + "not matching lists of attributes and values");
			return null;
		}

		String jpql = prefix;
		jpql += " FROM " + entityType.getName() + " entity";
		jpql += " WHERE 1=1";

		int i = 0;
		for (String attributeName : attributeNames) {
			jpql += " and entity." + attributeName + "=:value" + i;
			i++;
		}

		if (ignoredId != null) {
			jpql += " and entity.id!=:ignoredId";
		}

		if (orderByAttributes != null && orderByAttributes.length > 0) {
			jpql += " ORDER BY ";
			boolean orderByStarted = false;
			for (String orderByAttribute : orderByAttributes) {
				if (orderByStarted) {
					jpql += ",";
				}
				orderByStarted = true;
				jpql += "entity." + orderByAttribute;
			}
		}
		Query query = manager.createQuery(jpql);
		i = 0;
		for (Object value : attributeValues) {
			query.setParameter("value" + i, value);
			i++;
		}
		if (ignoredId != null) {
			query.setParameter("ignoredId", ignoredId);
		}

		return query;
	}
}
