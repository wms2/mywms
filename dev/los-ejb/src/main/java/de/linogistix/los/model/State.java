/*
 * Copyright (c) 2009-2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */

package de.linogistix.los.model;


/**
 * @author krane
 *
 */
public class State {
	
	/**
	 * Just created. Do nothing with it.
	 */
	public static final int RAW = 0;
	/**
	 * Released for automatic calculation.
	 */
	public static final int RELEASED = 100;
	/**
	 * Assigned to a pick-from stock or location.
	 */
	public static final int ASSIGNED = 200;
	/**
	 * Released to be handled by the user process (automatic assignment to a user).
	 */
	public static final int PROCESSABLE = 300;
	/**
	 * Has been reserved for a user to process it.
	 */
	public static final int RESERVED = 400;
	/**
	 * The user has accepted the order and will handle it.
	 */
	public static final int STARTED = 500;
	/**
	 * The processing cannot continue, but is not finished.
	 */
	public static final int PENDING = 550;
	/**
	 * The material has been taken.
	 */
	public static final int PICKED = 600;
	/**
	 * The operation is finished.
	 */
	public static final int FINISHED = 700;
	/**
	 * The operation is canceled. Something went wrong. 
	 */
	public static final int CANCELED = 800;
	/**
	 * The operation is done and possible post processing actions have been done. 
	 */
	public static final int POSTPROCESSED = 900;
	/**
	 * The order is nearly deleted. No more operations are necessary.
	 * It can be deleted. 
	 */
	public static final int DELETED = 1000;
	
}
