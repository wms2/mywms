/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import org.mywms.model.BasicEntity;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author trautm
 */
public class BasicEntityPropertyRO<T extends BasicEntity> extends PropertySupport.ReadOnly<T> {

        T value;

        @Override
        public PropertyEditor getPropertyEditor() {
            return new BOEditorReadOnly();
        }

        BasicEntityPropertyRO(String name, Class<T> type, String displayName, T value) {
            super(name, type, displayName, "");
            this.value = value;
        }

        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }
    }
