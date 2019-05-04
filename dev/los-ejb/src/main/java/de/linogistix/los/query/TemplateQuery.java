/*
 * TemplateQuery.java
 *
 * Created on 8. Oktober 2006, 00:05
 *
 * Copyright (c) 2006-2012 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 *
 */
package de.linogistix.los.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.linogistix.los.query.BODTOConstructorProperty.JoinType;

/**
 * Used for querying BusinessObjects by template.
 * 
 * @see QueryDetail
 * @see TemplateQueryWhereToken
 * @author <a href="http://community.mywms.de/developer.jsp">Andreas Trautmann</a>
 */
public class TemplateQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Class<? extends Object> boClass;
    private List<TemplateQueryFilter> filters = new ArrayList<TemplateQueryFilter>();
    private TemplateQueryFilter defaultFilter;
    private Class<? extends Object> newExprClass;
    
    private List<BODTOConstructorProperty> newExprProperties = new ArrayList<BODTOConstructorProperty>();
    private List<BODTOConstructorProperty> colSumProperties = new ArrayList<BODTOConstructorProperty>();

    private boolean distinct = false;

    
    
    public TemplateQuery() {
    	defaultFilter = addNewFilter();
    }

    public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}


    public TemplateQueryFilter addNewFilter() {
    	TemplateQueryFilter filter = new TemplateQueryFilter();
    	filters.add(filter);
    	return filter;
    }
    
    public String getEntitySimpleClassname() {
        return getBoClass().getSimpleName();
    }



    /**
     * Constructor Expressions in the SELECT Clause. The clazz must have a constructor
     * that has as many arguments as <code>properties</code> has elements. Furthermore
     * the argumemts must match the 
     * entity properties specified by their names in  <code>properties</code>.
     *
     *@param clazz the class of the returned object
     *@param properties the properties of the entity that are used in the constructor
     */
    @SuppressWarnings({ "rawtypes" })
    public void setSelectExpressionNew(Class<BODTO> clazz, List<BODTOConstructorProperty> properties) {

        if (properties == null || properties.size() < 1) {
            throw new IllegalArgumentException();
        }

        this.newExprClass = clazz;
        this.newExprProperties = properties;
    }

    public void setSelectColumnSum(List<BODTOConstructorProperty> properties) {

        if (properties == null || properties.size() < 1) {
            throw new IllegalArgumentException();
        }

        this.colSumProperties = properties;
    }

    public String getStatement() {
        StringBuffer s = new StringBuffer();

        s.append("SELECT ");
        
        if (isDistinct()) s.append(" DISTINCT");
        
        if (this.newExprClass == null) {
            s.append(" o FROM ");
        } else {
            s.append(getConstructorPropsStatement(this.newExprClass, this.newExprProperties));
            s.append(" FROM ");
        }
        s.append(getEntitySimpleClassname());
        s.append(" o ");
        
        for(BODTOConstructorProperty p:newExprProperties){
        	
        	if(p.isNullableReference()){
        		s.append("LEFT JOIN o."+p.getJoinReference()+" ");
        	}
        	else if(p.getJointType()==JoinType.INNER) {
        		s.append("JOIN o."+p.getJoinReference()+" ");
        	}
//        	if(p.getJointType() != null){
//        		s.append( p.getJointType().statement() + " " + "o." + p.getPropertyName() + " AS " +  p.getJoinReference());
//        	}
        }
        s.append( getWhereStatement());
        
        return s.toString();
    }

    
	/**
     * Prepares statement for summing up some columns.
     * @return null if no colSumProperties are configured
     */
    public String getColumnSumStatement(){
    	
    	if (colSumProperties == null || colSumProperties.size() == 0){
    		return null;
    	}
    	
    	StringBuffer s = new StringBuffer();
         s.append("SELECT ");         
         int i = 0;
         for(BODTOConstructorProperty p: colSumProperties){
    		if (i++ > 0)
    			s.append(", ");
    		s.append(" SUM (o." + p.getPropertyName() + ")");
          }
         s.append(" FROM ");
         s.append(getEntitySimpleClassname());
         s.append(" o ");

         s.append(getWhereStatement());
         
//         s.append(" GROUP BY ");
//         
//         i = 0;
//         for(BODTOConstructorProperty p: colSumProperties){
//     		if (i++ > 0)
//     			s.append(", ");
//     		s.append(" o." + p.getPropertyName());
//         }
         
         return s.toString();
    }

    public String getCountStatement() {
        StringBuffer s = new StringBuffer();

        s.append("SELECT ");
        s.append("count(o) FROM ");
        
        s.append(getEntitySimpleClassname());
        s.append(" o ");

        // 14.05.2012, Select and count with identical statements
        for(BODTOConstructorProperty p:newExprProperties){
        	
        	if(p.isNullableReference()){
        		s.append("LEFT JOIN o."+p.getJoinReference()+" ");
        	}
        	else if(p.getJointType()==JoinType.INNER) {
        		s.append("JOIN o."+p.getJoinReference()+" ");
        	}
//        	if(p.getJointType() != null){
//        		s.append( p.getJointType().statement() + " " + "o." + p.getPropertyName() + " AS " +  p.getJoinReference());
//        	}
        }
        
        return s.toString() + getWhereStatement();
    }


    private String getWhereStatement() {
        StringBuffer where = new StringBuffer();
        boolean isNew = true;

        if( filters != null && filters.size() > 0 ) {
        	for( TemplateQueryFilter filter : filters ) {
        		String stmt = filter.getFilterStatement();
        		if( stmt == null || stmt.length() == 0 ) {
        			continue;
        		}
        		if( isNew ) {
        			isNew = false;
        		}
        		else {
        			where.append(" AND ");
        		}
        		where.append( stmt );
        	}
        }
        String whereStr = where.toString().trim();
        if( whereStr.length() > 0 ) {
        	return " WHERE " + whereStr;
        }
        return "";
    }
    
    public void setBoClass(Class<? extends Object> boClass) {
        this.boClass = boClass;
    }

    public Class<? extends Object> getBoClass() {
        return boClass;
    }

    public void addWhereToken(TemplateQueryWhereToken token) {
        defaultFilter.whereTokens.add(token);
    }
    
	public void setWhereTokens(List<TemplateQueryWhereToken> whereTokens) {
		defaultFilter.whereTokens = whereTokens;
	}

	public List<TemplateQueryFilter> getWhereFilter() {
		return filters;
	}

	public List<TemplateQueryWhereToken> getWhereTokens() {
		List<TemplateQueryWhereToken> tokens = new ArrayList<TemplateQueryWhereToken>();
		for( TemplateQueryFilter filter : filters ) {
			tokens.addAll(filter.getWhereTokens());
		}
		return tokens;
	}

	public List<TemplateQueryWhereToken> getWhereTokens(TemplateQueryFilter filter) {
		List<TemplateQueryWhereToken> tokens = new ArrayList<TemplateQueryWhereToken>();
		for( TemplateQueryFilter f : filters ) {
			if( filter.equals(f) )
				tokens.addAll(f.getWhereTokens());
		}
		return tokens;
	}

    public static String getConstructorPropsStatement(Class<? extends Object> newExprClass, 
    												  List<BODTOConstructorProperty> constructorProperties)
    {
    	StringBuffer s = new StringBuffer();
    	s.append(" NEW ");
        s.append(newExprClass.getName());
        s.append("(");
        for (int i = 0; i < constructorProperties.size(); i++) {
            if (i > 0) {
                s.append(", ");
            }
            if (constructorProperties.get(i).getSelectToken() != null){
	            s.append(constructorProperties.get(i).getSelectToken());
            } 
            else if( "".equals(constructorProperties.get(i).getPropertyName()) ) {
	            s.append("o");
            }
            else{
	            s.append("o.");
	            s.append(constructorProperties.get(i).getPropertyName());
            }
        }
        s.append(")");
        
        return new String(s);
    }

	public Class<? extends Object> getNewExprClass() {
		return newExprClass;
	}

	public List<BODTOConstructorProperty> getNewExprProperties() {
		return newExprProperties;
	}
    
    
}
