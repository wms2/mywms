/*
 * Copyright (c) 2007 by Fraunhofer IML, Dortmund.
 * All rights reserved.
 *
 * Project: mywms.as
 */
package org.mywms.service;

import java.io.Serializable;

/**
 * @author Olaf Krause
 * @version $Revision$ provided by $Author$
 */
public class StockUnitInfoTO
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    public int count;
    public int availableStock;
    public int reservedStock;
    public int stock;
}
