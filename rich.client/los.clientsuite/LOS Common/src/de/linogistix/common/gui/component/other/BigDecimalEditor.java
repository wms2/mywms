/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.other;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

/**
 *
 * @author trautm
 */
public class BigDecimalEditor extends PropertyEditorSupport {

    public String getAsText() {
        return getValue() != null ? getValue().toString() : "";
    }

    public void setAsText(String s) throws java.lang.IllegalArgumentException {
        try {
            if (s != null && s.length() > 0) {
                setValue(new BigDecimal(s));
            } else{
                setValue(new BigDecimal(0));
            }
        } catch (NumberFormatException e) {
            setValue((BigDecimal) getValue());
            throw new IllegalArgumentException(s);
        }
    }

    public String getJavaInitializationString() {
        return ("new java.math.BigDecimal(" + getValue()!=null?getValue().toString():"0" + ")");
    }
}
