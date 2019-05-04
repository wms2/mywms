/*
 * TemplateQueryWhereToken.java
 *
 * Created on 8. Oktober 2006, 00:10
 *
 * Copyright (c) 2006-2013 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;

import java.io.Serializable;
import java.util.Date;

/**
 * Helper to formulize <code>WHERE</code> clauses in TemplateQuery.
 * 
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class TemplateQueryWhereToken implements Serializable {

    private static final long serialVersionUID = 1L;
    public final static String OPERATOR_EQUAL = "=";
    public final static String OPERATOR_NOT_EQUAL = "<>";
    public final static String OPERATOR_SMALLER = "<";
    public final static String OPERATOR_GREATER = ">";
    public final static String OPERATOR_LIKE = "LIKE";
    public final static String OPERATOR_BEFORE = "<";
    public final static String OPERATOR_AFTER = ">";
    public final static String OPERATOR_AT = "=";
    public final static String OPERATOR_TRUE = "= TRUE";
    public final static String OPERATOR_FALSE = "= FALSE";
    public final static String OPERATOR_ISEMPTY = "IS EMPTY";
    public final static String OPERATOR_ISNOTEMPTY = "IS NOT EMPTY";
    public final static String OPERATOR_NOP = "NOP";
    public final static String OPERATOR_CONTAINS = "MEMBER OF";
    public final static String OPERATOR_CONTAINS_NOT = "NOT MEMBER OF";
    public final static String OPERATOR_AND = "AND";
    public final static String OPERATOR_OR = "OR";
    public final static String OPERATOR_MANUAL = "MANUAL";
//    public final static String OPERATOR_MEMBEROF = "MEMBER OF";
//    public final static String OPERATOR_NOTMEMBEROF = "NOT MEMBER OF";
    
    /**
     * the operator for the where clause
     */
    private String operator = OPERATOR_LIKE;
    /**
     * name of the parameter used in where clause
     */
    private String parameter;
    /**
     * value of parameter used in where clause
     */
    private Object value;
    
    private String parameterName;

    private String logicalOperator = OPERATOR_AND;
    
    private boolean useValue = true;
    
    /**
     * Creates a new instance of TemplateQueryWhereToken
     */
    public TemplateQueryWhereToken() {
    }

    public TemplateQueryWhereToken(String operator, String parameter, Object value) {
        setOperator(operator);
        setParameter(parameter);
        setValue(value);
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
        setParameterName(toParameterName(parameter));
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public static String[] getStringOperators() {
        return new String[]{OPERATOR_EQUAL, OPERATOR_NOT_EQUAL, OPERATOR_LIKE};
    }

    public static String[] getDateOperators() {
        return new String[]{OPERATOR_AT, OPERATOR_BEFORE, OPERATOR_AFTER};
    }

    public static String[] getNumericOperators() {
        return new String[]{OPERATOR_EQUAL, OPERATOR_NOT_EQUAL,
            OPERATOR_GREATER, OPERATOR_SMALLER
        };
    }

    public static String[] getBoolOperators() {
        return new String[]{OPERATOR_TRUE, OPERATOR_FALSE};
    }

    public void setParameterName(String parameterName) {
    	if( parameterName.contains(".") ) {
    		useValue = false;
    	}
        this.parameterName = toParameterName(parameterName);
    }

    public String getParameterName() {
        return parameterName;
    }

    public String toParameterName(String parameter) {
        String ret = parameter.replaceAll("\\.", "");
        return ret;
    }

    public boolean isUnaryOperator() {
        return TemplateQueryWhereToken.isUnaryOperator(getOperator());
    }
    
    public static boolean isUnaryOperator(String operator) {
        if (operator == null) {
            throw new IllegalArgumentException();
        } else if (OPERATOR_TRUE.equals(operator)) {
            return true;
        } else if (OPERATOR_FALSE.equals(operator)) {
            return true;
        } else if (OPERATOR_ISEMPTY.equals(operator)) {
            return true;
        } else if (OPERATOR_ISNOTEMPTY.equals(operator)) {
            return true;
        } else {
            return false;
        }
    }

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public String getLogicalOperator() {
		return logicalOperator;
	}
    
    public String getWhereStatement(){
    	StringBuffer s = new StringBuffer();
    	if (getOperator().equals(TemplateQueryWhereToken.OPERATOR_CONTAINS) || getOperator().equals(TemplateQueryWhereToken.OPERATOR_CONTAINS_NOT)) {
            s.append(":");
            s.append((getParameterName()));
            s.append(" ");
            s.append(getOperator());
            s.append(" ");
            s.append(" o.");
            s.append(getParameter());
            s.append(" ");
        } else if (getOperator().equals(TemplateQueryWhereToken.OPERATOR_LIKE)) {
            s.append(" LOWER (o.");
            s.append(getParameter());
            s.append(") ");
            s.append(getOperator());
            s.append(" :");
            s.append((getParameterName()));
            s.append(" ");
        } 
        else if(getOperator().equals(TemplateQueryWhereToken.OPERATOR_AT)
        		&& getValue() instanceof Date)
        {
        	s.append(" o.");
        	s.append(getParameter());
        	s.append(" BETWEEN :").append(getParameterName()+"1 ");
        	s.append("AND :").append(getParameterName()+"2 ");
        }
        else if( getOperator().equals(TemplateQueryWhereToken.OPERATOR_MANUAL) )
        {
        	s.append(getParameter());
        }
        else {
            s.append(" o.");
            s.append(getParameter());
            s.append(" ");
            s.append(getOperator());
            s.append(" ");
            if (!isUnaryOperator()) {
            	if( useValue ) {
                    s.append(":");
                    s.append((getParameterName()));
                    s.append(" ");
            	}
            	else {
	                s.append((getParameterName()));
	                s.append(" ");
                }
            }
        }
    	return new String(s);
    }

    public void transformLikeParameter() {
        
        String val;
        if (! getOperator().equals(TemplateQueryWhereToken.OPERATOR_LIKE)) {
            throw new IllegalArgumentException("Only Like operators allowed");
        }
        val = (String)getValue();        
        setValue(transformLikeParam(val));
    }
    
    public static String transformLikeParam(String param){
        StringBuffer s = new StringBuffer();
        if (! param.startsWith("%")){
            s.append("%");
        }
        s.append(param.toLowerCase());
        if (! param.endsWith("%")){
            s.append("%");
        }
        
        return new String(s);
    }

	public boolean isUseValue() {
		return useValue;
	}
	public void setUseValue(boolean useValue) {
		this.useValue = useValue;
	}

}
