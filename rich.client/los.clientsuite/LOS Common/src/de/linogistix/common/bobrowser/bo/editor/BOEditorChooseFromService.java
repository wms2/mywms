/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.services.J2EEServiceLocator;
import de.linogistix.common.services.J2EEServiceLocatorException;
import de.linogistix.common.util.ExceptionAnnotator;
import java.awt.Component;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.mywms.model.BasicEntity;
import org.openide.NotifyDescriptor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.util.Lookup;

/**
 *
 * @author trautm
 */
public class BOEditorChooseFromService extends BOEditorChoose{

    public static final String EDITOR_SERVICE_CLASS_KEY = "editorServiceClass";
    public static final String EDITOR_SERVICE_METHOD_KEY = "editorServiceMethodname";
    public static final String EDITOR_SERVICE_PARAM_KEY = "editorServiceParameters";
    
    Method toInvoke;
    
    Object[] params;
    
    Object service;
    
    @Override
    public void attachEnv(PropertyEnv propertyEnv) {
        super.attachEnv(propertyEnv);  
        
        Class c = (Class)propertyEnv.getFeatureDescriptor().getValue(EDITOR_SERVICE_CLASS_KEY);
        String s = (String)propertyEnv.getFeatureDescriptor().getValue(EDITOR_SERVICE_METHOD_KEY);
        Object[] p = (Object[])propertyEnv.getFeatureDescriptor().getValue(EDITOR_SERVICE_PARAM_KEY);
        
        this.toInvoke = initInvokeMethod(c,s,p);
    }
    
    protected Method initInvokeMethod(Class c, String methodname, Object[] params){
        try {
            Class[] classes;
            J2EEServiceLocator loc = Lookup.getDefault().lookup(J2EEServiceLocator.class);
            this.service = loc.getStateless(c);
            this.params = params;
            if (params != null) {
                classes = new Class[params.length];
                int i = 0;
                for (Object o : params) {
                    classes[i++] = o.getClass();
                }
            } else {
                classes = new Class[0];
            }
            
            return this.service.getClass().getMethod(methodname, classes);
            
        } catch (NoSuchMethodException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch (SecurityException ex) {
            ExceptionAnnotator.annotate(ex);
        } catch (J2EEServiceLocatorException ex) {
            ExceptionAnnotator.annotate(ex);
        }
        return null;
    }
    
    @Override
    public Component getCustomEditor() {

        NotifyDescriptor d;

        try {
            if (getTypeHint() == null) {
                ExceptionAnnotator.annotate(new BOEditorTypeException());
            } else {
                return new BOEditorChooseFromServicePanel(this);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return null;

    }
    
    public List invokeService(){
        List list;
        try{
            list = (List)toInvoke.invoke(service, params);
            return list;
        } catch (Throwable t){
            ExceptionAnnotator.annotate(t);
            return new ArrayList();
        }
    }


    
}
