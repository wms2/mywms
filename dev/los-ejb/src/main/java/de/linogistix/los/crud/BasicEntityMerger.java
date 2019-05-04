/*
 * BasicEntityMerger.java
 *
 * Created on 26.02.2007, 10:10:29
 *
 * Copyright (c) 2006/2007 LinogistiX GmbH. All rights reserved.
 *
 * <a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */

package de.linogistix.los.crud;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import javax.persistence.EntityManager;

import org.mywms.model.BasicEntity;

/**
 * Merges one instance of BasicEntity into another.
 * 
 * Why is this? Because when providing an instance as template for
 * creating a new entity, this has no ID field set.
 * 
 * @author trautm
 *
 */
public class BasicEntityMerger<T extends BasicEntity> {

  EntityManager manager = null;
  
  public BasicEntityMerger(){
    
  }
  
  public BasicEntityMerger(EntityManager manager){
    
  }
  
	/**
	 * Overide to gain performance. This method uses reflections. In an extended class where
	 * the type <code><T></code> of BasicEntity is known you can copy property values directly.
	 * 
	 */
	public void mergeInto(T from, T to) throws BasicEntityMergeException{

		try{
			BeanInfo infoTo = Introspector.getBeanInfo(from.getClass());
			
			PropertyDescriptor[] d = infoTo.getPropertyDescriptors();
		
			for (int i=0; i<d.length; i++) {
				try{
					mergePropertyInto(from,to, d[i]);
				} catch (Throwable ex){
					continue;
				}
			}
		} catch (Throwable ex){
			throw new BasicEntityMergeException(from, to);
		}

		return;

	}

	protected void mergePropertyInto(
			T from,
			T to,
			PropertyDescriptor d
			) throws IllegalAccessException, InvocationTargetException {
 
		if (skipMergeIntoProperty(d)){
			return ;
		}

		Object value = d.getReadMethod().invoke(from, new Object[0]);
		if (d.getWriteMethod() != null){
			d.getWriteMethod().invoke(to, new Object[]{value});
		} 
    else{
			//log.debug("read-only property: " + d.getName());
		}
	}
	
	/**
	 * Ignores given property. Standard implementation ignores properties
	 * <ul>
	 * <li>class property obtained by <code>getClass</code>
	 * </ul>id property obtained by <code>getId</code>
	 * 
	 * @param d the inspected property.
	 * @return true if this property should be ignored
	 */
	protected boolean skipMergeIntoProperty(PropertyDescriptor d){
		if (d.getPropertyType().equals(Class.class)){
			//log.debug("skip: " + d.getName());
			return true;
		}
		else if (d.getName().equalsIgnoreCase("id")){
			//log.debug("skip: " + d.getName());
			return true;
		}
		//log.debug("process: " + d.getName() + " with class type " + d.getPropertyType() );
		return false;
	}
}
