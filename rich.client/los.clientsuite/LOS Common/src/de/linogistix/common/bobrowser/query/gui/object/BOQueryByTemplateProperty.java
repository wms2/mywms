/*
 * BOQueryByTemplateProperty.java
 *
 * Created on 15. Januar 2007, 02:37
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query.gui.object;

import de.linogistix.common.bobrowser.query.gui.component.BOQueryByEditor;
import de.linogistix.common.bobrowser.util.TypeResolver;
import de.linogistix.los.query.TemplateQueryWhereToken;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;

/**
 * Wraps the original {@link PropertyDescriptor} in field #getProperty().
 * The #value of a BOQueryByTemplateProperty
 * instance is the query string given via PropertyPanel.
 * 
 * This class has helper methods to determine the type of getProperty(). See isXXXType().
 * 
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOQueryByTemplateProperty extends PropertySupport {

    private BOQueryByTemplateWrapper propertyWrapper;

    /** Creates a new instance of BOQueryByTemplateProperty
     *
     * @param source The original PropertyDescriptor
     */
    public BOQueryByTemplateProperty(Node.Property source) {
        super(
                source.getName(),
                BOQueryByTemplateWrapper.class,
                source.getDisplayName(),
                source.getShortDescription(),
                true,
                true);

        BOQueryByTemplateSupport support = new BOQueryByTemplateSupport(source);

        this.propertyWrapper = new BOQueryByTemplateWrapper(QueryOperatorNop.OPERATOR_NOP, support);

        setExpert(getSource().isExpert());
        setHidden(getSource().isHidden());
        setPreferred(getSource().isPreferred());

    }

    public boolean canRead() {
        return true;
    }

    public boolean canWrite() {
        return true;
    }

    public void setValue(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        this.propertyWrapper = (BOQueryByTemplateWrapper) object;
    }

    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        return this.getPropertyWrapper();
    }

    @Override
    public void setValue(String attributeName, Object value) {
        super.setValue(attributeName, value);
        this.propertyWrapper.getProperty().setValue(attributeName, value);
    }
    
    

    /**
     * @return The operator like in {@link TemplateQueryWhereToken}
     */
    public QueryOperator getOperator() {
        return getPropertyWrapper().getOperator();
    }

    public void setOperator(QueryOperator operator) {
        getPropertyWrapper().setOperator(operator);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj instanceof BOQueryByTemplateProperty) {
            BOQueryByTemplateProperty p = (BOQueryByTemplateProperty) obj;
            return getSource().equals(p.getSource());
        } else {
            return false;
        }
    }

    public Node.Property getSource() {
        return this.getPropertyWrapper().getProperty();
    }

    public BOQueryByTemplateWrapper getPropertyWrapper() {
        return this.propertyWrapper;
    }

    public PropertyEditor getPropertyEditor() {
        return new BOQueryByEditor();
    }

    /**
     *
     */
    static final class BOQueryByTemplateSupport extends PropertySupport.ReadWrite {

        Logger log = Logger.getLogger(BOQueryByTemplateSupport.class.getName());
        Object value;
        Node.Property nodeProp;

        public BOQueryByTemplateSupport(Node.Property orig) {
            super(orig.getName(), orig.getValueType(), orig.getDisplayName(), orig.getShortDescription());

            this.nodeProp = orig;
            
            try {
                if (TypeResolver.isStringType(orig.getValueType())) {
                    setValue("");
                    setValue("nullValue", "");
                }
                if (TypeResolver.isNumericType(orig.getValueType())) {
                    setValue(0);
                    setValue("nullValue", "");
                }
                if (TypeResolver.isBusinessObjectType(orig.getValueType())) {
                    setValue(null);
                    setValue("nullValue", "");
                }
            } catch (Throwable t) {
                log.log(Level.SEVERE, t.getMessage(), t);
            }
        }

        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        public void setValue(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            this.value = object;
//      log.info("!!! set value to " + object);
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return nodeProp.getPropertyEditor();
        }

        @Override
        public Object getValue(String attributeName) {
            Object value = super.getValue(attributeName);
            if (value == null){
                value = nodeProp.getValue(attributeName);
            }
            
            return value;
            
        }
        
        
        
        
    }
}
