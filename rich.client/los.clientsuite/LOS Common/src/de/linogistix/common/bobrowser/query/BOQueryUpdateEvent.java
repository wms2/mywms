/*
 * BOQueryUpdateEvent.java
 *
 * Created on 28. May 2012, 06:42
 *
 * Copyright (c) 2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query;

import java.beans.PropertyChangeEvent;

/**
 *
 * @author andreas
 */
public class BOQueryUpdateEvent extends PropertyChangeEvent{

    public static final String PROP_UPDATE = "PROP_UPDATE";

    public BOQueryUpdateEvent(BOQueryModel model, String propertyName, Object oldValue, Object newValue){
        super(model, PROP_UPDATE, oldValue, newValue);
    }

}
