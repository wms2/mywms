/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import javax.ejb.Local;

@Local
public interface LOSSequenceTransactionService {
	
	/**
	 * The assigned class is used as key to differ between sequences. <br>
	 * For the first call a new sequence will be created initialized with 0.
	 *  
	 * @param sequanceName. The name of the sequence
	 * @return next value in the sequence of unique numbers starting with 0.
	 */
    public long getNextNoNewTransaction(String sequenceName);
	
	/**
	 * Reset the sequence belonging to the assigned class to 0.
	 * The next call to getNextSequenceNumber() will return value 1;
	 * 
	 * @param sequanceName. The name of the sequence
	 */
	public void resetSequenceInNewTransaction(String sequenceName);

}
