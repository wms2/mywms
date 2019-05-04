/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.mywms.model.BasicEntity;
import org.openide.nodes.PropertySupport;

/**
 *
 * @author trautm
 */
public class BasicEntityPropertyRW<T extends BasicEntity> extends PropertySupport.ReadWrite<T> {

        T value;
        Class service;
        String methodname;
        Object[] params;
        private List listeners = Collections.synchronizedList(new LinkedList());

        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            listeners.add(pcl);
        }

        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            listeners.remove(pcl);
        }

        private void fire(String propertyName, Object old, Object nue) {
            //Passing 0 below on purpose, so you only synchronize for one atomic call:
            PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
            for (int i = 0; i < pcls.length; i++) {
                pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
            }
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return new BOEditorChooseFromService();
        }

        BasicEntityPropertyRW(String name, Class<T> type, String displayName, T value) {
            super(name, type, displayName, "");
            this.value = value;
        }

        BasicEntityPropertyRW(String name, Class<T> type, String displayName, T value, Class service, String method, Object[] params) {
            this(name, type, displayName, value);
            this.service = service;
            this.methodname = method;
            this.params = params;

            setValue(BOEditorChooseFromService.EDITOR_SERVICE_CLASS_KEY, this.service);
            setValue(BOEditorChooseFromService.EDITOR_SERVICE_METHOD_KEY, this.methodname);
            setValue(BOEditorChooseFromService.EDITOR_SERVICE_PARAM_KEY, this.params);

        }

        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(T arg0) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            boolean fireyes = false;
            T old = null;
            if (arg0 != null && (!arg0.equals(this.value))) {
                fireyes = true;
                old = this.value;
            } else if (arg0 == null && this.value != null) {
                fireyes = true;
                old = this.value;
            }

            this.value = arg0;

            if (fireyes) {
                fire(getName(), old, this.value);
            }
        }
    }
