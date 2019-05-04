/*
 * BasicEntityMergeException.java
 *
 * Created on 26.02.2007, 10:10:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.crud;

import javax.ejb.ApplicationException;
import org.mywms.facade.FacadeException;
import org.mywms.model.BasicEntity;

/**
 * Thrown if two entities can't be merged.
 * 
 * @author trautm
 *
 */
@ApplicationException(rollback = true)
public class BasicEntityMergeException extends FacadeException{

	private static final long serialVersionUID = 1L;

	public static final String RESOURCE_KEY = "BusinessException.EntityCannotBeMerged";
	
	public BasicEntityMergeException(BasicEntity from, BasicEntity to) {
		super("Entity can't be merged", RESOURCE_KEY, new String[]{from.toUniqueString(), to.toUniqueString()});
	}
	
}
