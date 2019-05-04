/*
 * Copyright (c) 2006 - 2013 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.util.businessservice;

import javax.ejb.Local;
import javax.persistence.TableGenerator;

/**
 * This service acts like a {@link TableGenerator} to generate unique serial numbers for a certain key.
 *  
 * 
 * @author Jordan
 *
 */
@Local
public interface LOSSequenceGeneratorService {

	/**
	 * The assigned class is used as key to differ between sequences. <br>
	 * For the first call a new sequence will be created initialized with 0.
	 *  
	 * @param assignedClass used as key to identify the sequence.
	 * @return next value in the sequence of unique numbers starting with 0.
	 */
	@SuppressWarnings("rawtypes")
	public long getNextSequenceNumber(Class assignedClass);
	public long getNextSequenceNumber(String seqName);

	/**
	 * Reset the sequence belonging to the assigned class to 0.
	 * The next call to getNextSequenceNumber() will return value 1;
	 * 
	 * @param assignedClass used as key to identify the sequence.
	 */
	@SuppressWarnings("rawtypes")
	public void resetSequence(Class assignedClass);
	public void resetSequence(String seqName);

}
