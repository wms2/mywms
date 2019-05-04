/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author trautm
 */
public class PropertyRW<T> extends PropertySupport.ReadWrite<T> {

        T value;

        public PropertyRW(String name, Class<T> type, String displayName, T value) {
            super(name, type, displayName, "");
            this.value = value;
        }

        @Override
        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(T arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            this.value = arg0;
        }
    }