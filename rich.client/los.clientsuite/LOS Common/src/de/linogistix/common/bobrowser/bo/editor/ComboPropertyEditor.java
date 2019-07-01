/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.bobrowser.bo.editor;

import de.linogistix.common.bobrowser.bo.binding.PropertyDescriptorElement;
import de.linogistix.common.res.CommonBundleResolver;
import de.linogistix.common.util.BundleResolve;
import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;

/**
 * @author krane
 *
 */
public class ComboPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {


    private List<Object> valueList;
    private List<String> textList;
    private boolean ro;
    Class valueClass;


    @Override
    public String[] getTags() {
        if( textList == null ) {
            return null;
        }

        if (ro) {
            int idx = valueList.indexOf(getValue());
            if( idx>=0 ) {
                return new String[]{ textList.get(idx) };
            }
            else {
                return new String[]{ getValue().toString() };
            }
        }
        return textList.toArray(new String[textList.size()]);
  }

    @Override
    // get the text representation of the entity value
    public String getAsText() {
        Object o = getValue();
        int idx = valueList.indexOf(o);
        if( idx>=0 ) {
            return textList.get(idx).toString();
        }
        return  o.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    // Set the value of the entity with its text representation
    public void setAsText(String text) throws IllegalArgumentException {
        if (ro){
            return;
        }
        int idx = textList.indexOf(text);
        if( idx < 0 ) {
            for( int i=0; i<textList.size(); i++ ) {
                String s = textList.get(i);
                if( s.toLowerCase().equals(text.toLowerCase())) {
                    idx = i;
                    break;
                }
            }
        }
        if( idx>=0 ) {
            setValue(valueList.get(idx));
            firePropertyChange();
            return;
        }

        if( valueClass==int.class ) {
            if( text == null || text.length()==0 ) {
                text = "0";
            }
            try {
                Integer i = Integer.valueOf(text);
                setValue(i);
                firePropertyChange();
                return;
            }
            catch( Throwable t ) {
                System.out.println("Cannot set value. value="+text+" err="+t.getClass().getSimpleName()+", "+t.getMessage());
                throw new IllegalArgumentException();
            }
        }

        if( valueClass==String.class ) {
            setValue(text);
            firePropertyChange();
            return;
        }

        throw new IllegalArgumentException();
    }


  public void attachEnv(PropertyEnv env) {
       
        Node.Property p = (Node.Property) env.getFeatureDescriptor();
        String access = (String) p.getValue(PropertyDescriptorElement.VALUE_ACCESS);
        if (access != null){
            this.ro = access.equals(PropertyDescriptorElement.VALUE_ACCESS_READONLY);
        } else{
            this.ro = !p.canWrite() ;
        }
        env.getFeatureDescriptor().setValue( "canEditAsText", Boolean.TRUE );
        
        Class bundleResolver = (Class) p.getValue("bundleResolver");
        String bundlePrefix = (String) p.getValue("bundlePrefix");

        if( valueList == null ) {
            try {
                valueList = new ArrayList<Object>();
                textList = new ArrayList<String>();
                List<Object> entries = (List<Object>)p.getValue("valueList");
                for( Object entry : entries ) {
                    String text = null;
                    if(bundlePrefix!=null && bundlePrefix.length()>0) {
                        text = BundleResolve.resolve(new Class[]{bundleResolver,CommonBundleResolver.class},bundlePrefix+"."+p.getName()+"."+entry.toString(), new Object[0], false);
                    } else {
                        text = BundleResolve.resolve(new Class[]{bundleResolver,CommonBundleResolver.class},p.getName()+"."+entry.toString(), new Object[0], false);
                    }
                    if( text == null || text.length()==0 ) {
                        text = entry.toString();
                    }
                    valueList.add(entry);
                    textList.add(text);
                }
            }
            catch( Throwable t ) {
                System.out.println("Cannot read valueList. err="+t.getClass().getSimpleName()+", "+t.getMessage());
            }
        }

        valueClass = p.getValueType();
  }
}
