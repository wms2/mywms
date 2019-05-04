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
public enum QueryOperatorCollection implements QueryOperator {

  OPERATOR_NOP(TemplateQueryWhereToken.OPERATOR_NOP),
  OPERATOR_CONTAINS(TemplateQueryWhereToken.OPERATOR_CONTAINS),
  OPERATOR_CONTAINS_NOT(TemplateQueryWhereToken.OPERATOR_CONTAINS_NOT),
  OPERATOR_IS_EMPTY(TemplateQueryWhereToken.OPERATOR_ISEMPTY);
  
  private final String operator;

  QueryOperatorCollection(String operator) {
    this.operator = operator;
  }

  public QueryOperator getDefault() {
    return OPERATOR_CONTAINS;
  }

  public String getOperator() {
    return this.operator;
  }

    public boolean isNOP() {
        return this == OPERATOR_NOP;
    }
}
