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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.mywms.model.BasicEntity;
import org.mywms.res.BundleResolver;

import de.wms2.mywms.exception.BusinessException;

/**
 * A EntityManager with some more functionality.<br>
 * - Work with injectable validators<br>
 * - Work with derived datatypes<br>
 * 
 * @author krane
 *
 */
@Stateless
public class PersistenceManager {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	@Inject
	private Instance<EntityValidator<?>> validators;

	private Map<Class<?>, List<EntityValidator<?>>> validatorMap = new HashMap<>();

	private Map<String, Class<?>> entityClassMap = new HashMap<>();

	@PostConstruct
	private void startup() {

		for (EntityValidator<?> validator : validators) {
			Class<?> validatorClass = (Class<?>) ((ParameterizedType) validator.getClass().getGenericInterfaces()[0])
					.getActualTypeArguments()[0];

			List<EntityValidator<?>> validatorList = validatorMap.get(validatorClass);
			if (validatorList == null) {
				validatorList = new ArrayList<>();
				validatorMap.put(validatorClass, validatorList);
			}
			if (!validatorList.contains(validator)) {
				validatorList.add(validator);
			}
		}

		entityClassMap = new HashMap<String, Class<?>>();

		Metamodel metamodel = manager.getMetamodel();

		Set<String> immutableClasses = new HashSet<>();
		for (EntityType<?> entityType : metamodel.getEntities()) {
			Class<?> entityClass = (Class<?>) entityType.getJavaType();

			if (entityClassMap.containsKey(entityClass.getName())) {
				continue;
			}

			Class<?> currentClass = entityClass;
			Class<?> usedClass = entityClass;
			while (!currentClass.equals(Object.class) && !currentClass.isAnnotationPresent(MappedSuperclass.class)) {
				if (immutableClasses.contains(currentClass.getName())) {
					break;
				}
				Class<?> mappedClass = entityClassMap.get(currentClass.getName());
				if (mappedClass != null) {
					if (!mappedClass.isAssignableFrom(usedClass)) {
						usedClass = currentClass;
						immutableClasses.add(currentClass.getName());
					}
				}
				entityClassMap.put(currentClass.getName(), usedClass);
				currentClass = currentClass.getSuperclass();
				;
			}

		}
	}

	private List<EntityValidator<?>> getValidators(Class<?> entityClass) {
		List<EntityValidator<?>> resultList = new ArrayList<>();
		List<EntityValidator<?>> validatorList = validatorMap.get(entityClass);
		if (validatorList != null) {
			resultList.addAll(validatorList);
		}
		validatorList = validatorMap.get(entityClass.getSuperclass());
		if (validatorList != null) {
			resultList.addAll(validatorList);
		}
		return resultList;
	}

	/**
	 * Get the last derivation of a datatype
	 * 
	 * @param baseType The type of the entity
	 */
	@SuppressWarnings("unchecked")
	public <T> Class<T> getDerivedType(Class<T> baseType) {
		Class<?> entityType = baseType;
		while (true) {
			Class<?> mapType = entityClassMap.get(entityType.getName());
			if (mapType != null) {
				return (Class<T>) mapType;
			}
			Class<?> superType = entityType.getSuperclass();
			if (superType.equals(Object.class)) {
				break;
			}
			if (superType.equals(BasicEntity.class)) {
				break;
			}
			entityType = entityType.getSuperclass();
		}
		return baseType;
	}

	public EntityManager getEntityManager() {
		return manager;
	}

	/**
	 * Get the uppermost datatype, which is not BasicEntity or other
	 * MappedSuperclass
	 * 
	 * @param entityType The type of the entity
	 */
	public Class<?> getBaseType(Class<?> entityType) {

		Class<?> parentType = entityType.getSuperclass();
		while (parentType != null && !parentType.equals(Object.class)
				&& !parentType.isAnnotationPresent(MappedSuperclass.class)) {
			entityType = parentType;
			parentType = parentType.getSuperclass();
		}

		return entityType;
	}

	/**
	 * Provide a new instance of the last extension of the class
	 * 
	 * @param baseType the base-type of the requested instance
	 */
	@SuppressWarnings("unchecked")
	public <T extends BasicEntity> T createInstance(Class<T> baseType) {
		String logStr = "createInstance ";

		Class<T> entityType = (Class<T>) entityClassMap.get(baseType.getName());

		if (entityType == null) {
			logger.log(Level.SEVERE, logStr + "No mapping found. class=" + baseType.getName());
			return null;
		}

		try {
			Constructor<?> c = entityType.getDeclaredConstructor();
			c.setAccessible(true);

			T instance = (T) c.newInstance();

			return instance;
		} catch (Throwable e) {
			logger.log(Level.WARNING, logStr + "error instantiating entity. baseType=" + baseType + ", exception="
					+ e.getClass().getName() + ", " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * @see EntityManager#persist(Object)
	 */
	public <ENTITY_TYPE extends BasicEntity> void persist(ENTITY_TYPE entity) {
		manager.persist(entity);
	}

	/**
	 * Call the deployed validators and persist the entity
	 * 
	 * @param entity The entity to persist
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <ENTITY_TYPE extends BasicEntity> void persistValidated(ENTITY_TYPE entity) throws BusinessException {
		if (entity == null) {
			return;
		}

		for (EntityValidator validator : getValidators(entity.getClass())) {
			validator.validateCreate(entity);
		}

		persist(entity);
	}

	/**
	 * @see EntityManager#merge(Object)
	 */
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE update(ENTITY_TYPE entity) {
		entity = manager.merge(entity);
		return entity;
	}

	/**
	 * Call the deployed validators and merge the new entity in the persistence
	 * context
	 * 
	 * @param entityOld The unchanged entity
	 * @param entityNew The changed entity
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE updateValidated(ENTITY_TYPE entityOld, ENTITY_TYPE entityNew)
			throws BusinessException {
		if (entityNew == null) {
			return null;
		}

		for (EntityValidator validator : getValidators(entityNew.getClass())) {
			validator.validateUpdate(entityOld, entityNew);
		}

		return update(entityNew);
	}

	/**
	 * Call the deployed validators
	 * 
	 * @param entityOld The unchanged entity
	 * @param entityNew The changed entity
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <ENTITY_TYPE extends BasicEntity> void validateUpdate(ENTITY_TYPE entityOld, ENTITY_TYPE entityNew)
			throws BusinessException {
		if (entityNew == null) {
			return;
		}
		if (entityOld == null) {
			entityOld = entityNew;
		}

		for (EntityValidator validator : getValidators(entityNew.getClass())) {
			validator.validateUpdate(entityOld, entityNew);
		}

	}

	/**
	 * @see EntityManager#remove(Object)
	 */
	public void remove(BasicEntity entity) {
		manager.remove(entity);
	}

	/**
	 * Call the deployed validators and remove the entity
	 * 
	 * @param entity The entity to remove
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <ENTITY_TYPE extends BasicEntity> void removeValidated(ENTITY_TYPE entity) throws BusinessException {
		if (entity == null) {
			return;
		}

		for (EntityValidator validator : getValidators(entity.getClass())) {
			validator.validateDelete(entity);
		}

		remove(manager.contains(entity) ? entity : manager.merge(entity));
	}

	/**
	 * Remove all entities of the given data types
	 * 
	 * @param entityTypes The data types of which the entities will be removed
	 */
	public void removeAll(Class<?>... entityTypes) {
		for (Class<?> entityType : entityTypes) {
			String jpql = "select entity from " + entityType.getName() + " entity ";
			Query query = manager.createQuery(jpql);
			List<?> entities = query.getResultList();

			for (Object entity : entities) {
				manager.remove(entity);
			}
		}
		manager.flush();
	}

	/**
	 * Call the deployed validators
	 * 
	 * @param entity The entity to remove
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <ENTITY_TYPE extends BasicEntity> void validateRemove(ENTITY_TYPE entity) throws BusinessException {
		if (entity == null) {
			return;
		}

		for (EntityValidator validator : getValidators(entity.getClass())) {
			validator.validateDelete(entity);
		}
	}

	/**
	 * @see EntityManager#find(Class, Object)
	 */
	public <T> T find(Class<T> entityType, Object id) {
		return manager.find(entityType, id);
	}

	/**
	 * Reload the given entity. If it is not existing, an exception is thrown.
	 * 
	 * @param entity       The entity to reload
	 * @param checkVersion If true, the version attribute is checked. In case of
	 *                     conflict, an exception is thrown.
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE reloadNotNull(ENTITY_TYPE entity, boolean checkVersion)
			throws BusinessException {
		String logStr = "reloadNotNull ";
		if (entity == null) {
			logger.log(Level.WARNING, logStr + "entity is null");
			throw new BusinessException(BundleResolver.class, "PeristenceManager.cannotReadEntity");
		}

		int version = entity.getVersion();

		entity = (ENTITY_TYPE) manager.find(getDerivedType(entity.getClass()), entity.getId());
		if (entity == null) {
			logger.log(Level.WARNING, logStr + "Cannot read entity");
			throw new BusinessException(BundleResolver.class, "PeristenceManager.cannotReadEntity");
		}

		if (checkVersion && entity.getVersion() != version) {
			logger.log(Level.WARNING, logStr + "Object version has changed");
			throw new BusinessException(BundleResolver.class, "Validator.versionConflict");
		}

		return entity;
	}

	/**
	 * Reload the given entity. If it is not existing, an exception is thrown.
	 * 
	 * @param entityType The data type of the entity to reload
	 * @param id         The id of the entity to reload
	 */
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE reloadNotNull(Class<ENTITY_TYPE> entityType, Long id)
			throws BusinessException {
		String logStr = "reloadNotNull ";
		if (id == null) {
			logger.log(Level.WARNING, logStr + "id is null");
			throw new BusinessException(BundleResolver.class, "PeristenceManager.cannotReadEntity");
		}

		ENTITY_TYPE entity = (ENTITY_TYPE) manager.find(getDerivedType(entityType), id);
		if (entity == null) {
			logger.log(Level.WARNING, logStr + "Cannot read entity type=" + getDerivedType(entityType) + ", id=" + id);
			throw new BusinessException(BundleResolver.class, "PeristenceManager.cannotReadEntity");
		}

		return entity;
	}

	/**
	 * Reload the given entity. If it is not existing, null is returned.
	 * 
	 * @param entity       The entity to reload
	 * @param checkVersion If true, the version attribute is checked. In case of
	 *                     conflict, an exception is thrown.
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE reload(ENTITY_TYPE entity, boolean checkVersion)
			throws BusinessException {
		String logStr = "reload ";
		if (entity == null) {
			return null;
		}

		int version = 0;
		if (checkVersion) {
			version = entity.getVersion();
		}

		entity = (ENTITY_TYPE) manager.find(getDerivedType(entity.getClass()), entity.getId());

		if (checkVersion && entity.getVersion() != version) {
			logger.log(Level.WARNING, logStr + "Object version has changed");
			throw new BusinessException(BundleResolver.class, "Validator.versionConflict");
		}

		return entity;
	}

	/**
	 * Reload the given entity. If it is not existing, null is returned.
	 * 
	 * @param entityType The data type of the entity to reload
	 * @param id         The id of the entity to reload
	 */
	public <ENTITY_TYPE extends BasicEntity> ENTITY_TYPE reload(Class<ENTITY_TYPE> entityType, Object id) {
		if (id == null) {
			return null;
		}

		ENTITY_TYPE entity = (ENTITY_TYPE) manager.find(getDerivedType(entityType), id);

		return entity;
	}

	/**
	 * Make a clone of the given entity.
	 * <p>
	 * Embedded attributes are not supported by this version
	 * 
	 * @param entity The entity to clone
	 */
	@SuppressWarnings("unchecked")
	public <ENTITY_TYPE> ENTITY_TYPE cloneEntity(ENTITY_TYPE entity) {
		String logStr = "cloneEntity ";

		if (entity == null) {
			return null;
		}

		ENTITY_TYPE clone = null;
		try {
			Class<?> actuClass = entity.getClass();
			clone = (ENTITY_TYPE) actuClass.newInstance();

			while (!actuClass.equals(Object.class)) {
				for (Field field : actuClass.getDeclaredFields()) {
					int modifiers = field.getModifiers();
					if (Modifier.isStatic(modifiers)) {
						continue;
					}
					field.setAccessible(true);
					Object value = field.get(entity);
					field.set(clone, value);
				}
				actuClass = actuClass.getSuperclass();
			}

		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			logger.log(Level.WARNING, logStr + "Error copy data.", e);
		}

		return clone;
	}

	/**
	 * @see EntityManager#clear()
	 */
	public void clear() {
		manager.clear();
	}

	/**
	 * @see EntityManager#createQuery(String)
	 */
	public Query createQuery(String arg0) {
		return manager.createQuery(arg0);
	}

	/**
	 * @see EntityManager#detach(Object)
	 */
	public void detach(Object arg0) {
		manager.detach(arg0);
	}

	/**
	 * @see EntityManager#flush()
	 */
	public void flush() {
		manager.flush();
	}

	/**
	 * @see EntityManager#lock(Object, LockModeType)
	 */
	public void lock(Object entity, LockModeType lockMode) {
		manager.lock(entity, lockMode);
	}

	/**
	 * @see EntityManager#refresh(Object)
	 */
	public void refresh(Object arg0) {
		manager.refresh(arg0);
	}

	/**
	 * @see EntityManager#setFlushMode(FlushModeType)
	 */
	public void setFlushMode(FlushModeType arg0) {
		manager.setFlushMode(arg0);
	}

	/**
	 * @see EntityManager#contains(Object)
	 */
    public boolean contains(Object entity) {
		return manager.contains(entity);
	}

	/**
	 * @see EntityManager#contains(Object)
	 */
    public <T> T merge(T entity) {
		return manager.merge(entity);
	}

}
