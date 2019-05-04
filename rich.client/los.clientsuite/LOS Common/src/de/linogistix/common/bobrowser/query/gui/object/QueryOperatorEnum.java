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
public enum QueryOperatorEnum implements QueryOperator{
  OPERATOR_NOP(TemplateQueryWhereToken.OPERATOR_NOP),
  OPERATOR_EQUAL(TemplateQueryWhereToken.OPERATOR_EQUAL),
  OPERATOR_NOT_EQUAL(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL);
   
  private final String operator;
  
 QueryOperatorEnum(String operator){
   this.operator = operator;
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
