/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.customization;

import javax.ejb.Local;

import org.mywms.model.BasicEntity;

/**
 * @author krane
 *
 */
@Local
public interface EntityGenerator {
	public <E extends BasicEntity> E generateEntity( Class<E> clazz );
}
