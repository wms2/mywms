/*
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo.editor;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 * Shows any Object using Object's <code>toString()</code> in <code>getAsText()</code>.
 * <code>setAsText()</code> does nothing. This way read only behaviour is
 * emulated.
 *
 * @author trautm
 */
public class PlainObjectReadOnlyEditor extends PropertyEditorSupport implements ExPropertyEditor {

    boolean i18n;
    PropertyEnv env;

    @Override
    public void setAsText(String text) throws java.lang.IllegalArgumentException {
        //
    }

    @Override
    public String getAsText() {
        Object o;

        o = getValue();
        if (o != null) {
            return o.toString();
        } else {
            return "-/-";
        }
    }

    public void attachEnv(PropertyEnv arg0) {
        this.env = arg0;
        if (this.env.getFeatureDescriptor().getValue("i18n") != null) {
            this.i18n = (Boolean) this.env.getFeatureDescriptor().getValue("i18n");
        }
    }

    @Override
    public boolean isPaintable() {
        return true;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        gfx.drawString(getAsText(), box.x + 5, box.y + 15);
    }




}
