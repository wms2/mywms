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
public enum QueryOperatorDate implements QueryOperator {

  OPERATOR_NOP(TemplateQueryWhereToken.OPERATOR_NOP),
  OPERATOR_BEFORE(TemplateQueryWhereToken.OPERATOR_BEFORE),
  OPERATOR_AFTER(TemplateQueryWhereToken.OPERATOR_AFTER),
  OPERATOR_AT(TemplateQueryWhereToken.OPERATOR_AT);
  private final String operator;

  QueryOperatorDate(String operator) {
    this.operator = operator;
  }

  public QueryOperator getDefault() {
    return OPERATOR_AT;
  }

  public String getOperator() {
    return this.operator;
  }
  
  public boolean isNOP() {
        return this == OPERATOR_NOP;
    }

}
