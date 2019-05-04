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
public enum QueryOperatorBO implements QueryOperator {

  OPERATOR_NOP(TemplateQueryWhereToken.OPERATOR_NOP),
  OPERATOR_IS(TemplateQueryWhereToken.OPERATOR_EQUAL),
  OPERATOR_IS_NOT(TemplateQueryWhereToken.OPERATOR_NOT_EQUAL);
  
  private final String operator;

  QueryOperatorBO(String operator) {
    this.operator = operator;
  }

  public QueryOperator getDefault() {
    return OPERATOR_IS;
  }

  public String getOperator() {
    return this.operator;
  }

    public boolean isNOP() {
        return this == OPERATOR_NOP;
    }
}
