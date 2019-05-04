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
public enum QueryOperatorNop implements QueryOperator{
  OPERATOR_NOP(TemplateQueryWhereToken.OPERATOR_NOP);
          
 private final String operator;
 
 QueryOperatorNop(String operator){
   this.operator  = operator;
 }

  public String getOperator() {
    return this.operator;
  }

  public QueryOperator getDefault() {
    return OPERATOR_NOP;
  }
  
  public boolean isNOP() {
        return this == OPERATOR_NOP;
    }
}
