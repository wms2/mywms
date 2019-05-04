/*
 * BOQueryByTemplateWrapper.java
 *
 * Created on 15. Januar 2007, 01:04
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.common.bobrowser.query.gui.object;

import de.linogistix.common.bobrowser.util.TypeResolver;
import de.linogistix.los.query.BODTO;
import de.linogistix.los.query.TemplateQueryWhereToken;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mywms.model.BasicEntity;
import org.openide.nodes.Node;

/**
 * Wraps {@link BOQueryByTemplateProperty}
 *
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class BOQueryByTemplateWrapper {

    private static final Logger log = Logger.getLogger(BOQueryByTemplateWrapper.class.getName());
    /**
     * The original PropertySupport
     */
    private Node.Property property;
    /**
     * The operator like in {@link TemplateQueryWhereToken}
     */
    private QueryOperator operator = QueryOperatorNop.OPERATOR_NOP;

    public BOQueryByTemplateWrapper(QueryOperator operator, Node.Property orig) {
        setOperator(operator);

        if ((BasicEntity.class.isAssignableFrom(orig.getValueType())) ||
                (BODTO.class.isAssignableFrom(orig.getValueType()))) {

        }

        setProperty(orig);
    }

    //-----------------------------------------------------------------------------
    
    /**
     * @return empty instance of derived BasicEntity if type #isBusinessObjectType
     */
    public BasicEntity getBasicEntityInstance() {
        if (isBusinessObjectType()) {
            try {
                return (BasicEntity) getProperty().getValueType().newInstance();
            } catch (InstantiationException ex) {
                log.severe(ex.getMessage());
                log.log(Level.INFO, ex.getMessage(), ex);
            } catch (IllegalAccessException ex) {
                log.severe(ex.getMessage());
                log.log(Level.INFO, ex.getMessage(), ex);
            }
        }
        return null;
    }

    //----------------------------------------------------------------------------
    public boolean isNumericType() {
        return TypeResolver.isNumericType(getProperty().getValueType());

    }

    public boolean isLongType() {
        return TypeResolver.isLongType(getProperty().getValueType());
    }

    public boolean isIntegerType() {
        return TypeResolver.isIntegerType(getProperty().getValueType());
    }

    public boolean isByteType() {
        return TypeResolver.isByteType(getProperty().getValueType());
    }

    public boolean isFloatType() {
        return TypeResolver.isFloatType(getProperty().getValueType());
    }

    public boolean isDoubleType() {
        return TypeResolver.isDoubleType(getProperty().getValueType());
    }

    public boolean isDateType() {
        return TypeResolver.isDateType(getProperty().getValueType());
    }

    public boolean isBoolType() {
        return TypeResolver.isBooleanType(getProperty().getValueType());
    }

    public boolean isStringType() {
        return TypeResolver.isStringType(getProperty().getValueType());
    }

    /**
     * @return true <code>if (isStringType() || isNumericType())</code>
     */
    public boolean isPrimitiveType() {
        return TypeResolver.isPrimitiveType(getProperty().getValueType());
    }

    /**
     * 
     * 
     * @return true if getProperty() type is instanceof BasicEntity
     */
    public boolean isBusinessObjectType() {
        return TypeResolver.isBusinessObjectType(getProperty().getValueType());
    }
    
    public boolean isCollectionType() {
        return TypeResolver.isCollectionType(getProperty().getValueType());
    }

    public boolean isEnumType() {
        return TypeResolver.isEnumType(getProperty().getValueType());
    }
    
    public Node.Property getProperty() {
        return property;
    }

    public void setProperty(Node.Property source) {
        this.property = source;
    }

    public QueryOperator getOperator() {
        return operator;
    }

    public void setOperator(QueryOperator operator) {
        this.operator = operator;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("operator: ");
        b.append(operator.toString());
        b.append("; property: ");
        if (property != null) {
            b.append(property.getName());
            b.append("=");
            try {
                b.append("" + property.getValue());
            } catch (Exception ex) {
                b.append(ex.getMessage());
            }
        }

        return new String(b);
    }
}
