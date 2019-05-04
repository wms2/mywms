/*
 *
 * Copyright (c) 2006 LinogistiX GmbH. All rights reserved.
 *
 *<a href="http://www.linogistix.com/">browse for licence information</a>
 */
package de.linogistix.common.bobrowser.query.gui.object;

import de.linogistix.los.query.TemplateQueryWhereToken;

/**
 *
 * @author trautm
 */
public enum QueryOperatorNumeric implements QueryOperator{
  OPERATOR_NOP(TemplateQueryWhereToken.OPERATOR_NOP),
  OPERATOR_EQUAL(TemplateQueryWhereToken.OPERATOR_EQUAL),
  OPERATOR_SMALLER(TemplateQueryWhereToken.OPERATOR_SMALLER),
  OPERATOR_GREATER(TemplateQueryWhereToken.OPERATOR_GREATER);
  
  private final String operator;
 
 QueryOperatorNumeric(String operator){
   this.operator  = operator;
 }
         
  public String getOperator() {
    return this.operator;
  }

  public QueryOperator getDefault() {
    return OPERATOR_EQUAL;
  }
  
  public boolean isNOP() {
        return this == OPERATOR_NOP;
    }
  
}
