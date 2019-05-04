/*
 * BOEditor.java
 *
 * Created on 11. September 2006, 13:33
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.bo.BOEntityNodeReadOnly;
import de.linogistix.common.bobrowser.api.BOLookup;
import de.linogistix.common.bobrowser.bo.BO;
import de.linogistix.common.util.ExceptionAnnotator;
import de.linogistix.los.query.BODTO;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.model.BasicEntity;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.Lookup;

/**
 * Editor for picking a value of type BasicEntity from a BOQueryTopComponent.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOEditorChoose extends BOEditorReadOnly /*implements InplaceEditor.Factory*/ {
    
    private static final Logger log = Logger.getLogger(BOEditorChoose.class.getName());
 
    private InplaceEditor inplaceEditor = null;
    /**
     * Creates a new instance of BOEditor
     */
    public BOEditorChoose() {

    }
    public void attachEnv(PropertyEnv propertyEnv) {
        super.attachEnv(propertyEnv);    
    }
    

    public Component getCustomEditor() {

        try {
            if (getTypeHint() == null) {
                ExceptionAnnotator.annotate(new BOEditorTypeException());
            } else {
                return new BOEditorChoosePanel(this);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return null;


    }

    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     * 
     * @param value either an instance of {@link BasicEntity} or {@link BODTO}.
     */
    public void setValue(Object value) {
        try {
            if (value != null) {
                if (value instanceof BasicEntity) {
                    this.setBoBeanNode(new BOEntityNodeReadOnly((BasicEntity) value));
                } else if (value instanceof BODTO) {
                    if (getTypeHint() != null) {
                        BODTO boDTO = (BODTO) value;
                        BOLookup l = (BOLookup) Lookup.getDefault().lookup(BOLookup.class);
                        BO bo = (BO) l.lookup(getTypeHint());
                        value = bo.getQueryService().queryById(boDTO.getId());
                        this.setBoBeanNode(new BOEntityNodeReadOnly((BasicEntity) value));
                    }
                } else if (value instanceof String && value.equals("???")){ 
                    log.warning("setValue called with ???");
                }
            } else {
                setBoBeanNode(null);
            }
        } catch (Throwable ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        firePropertyChange();
    }

//    public InplaceEditor getInplaceEditor() {
//        if (inplaceEditor == null){
//            inplaceEditor = new BOInplaceEditorAutoFiltering();
//        }
//        return inplaceEditor;
//    }

    
    
  
}
