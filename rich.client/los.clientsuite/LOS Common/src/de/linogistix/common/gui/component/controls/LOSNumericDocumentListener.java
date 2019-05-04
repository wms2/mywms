/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import javax.swing.text.AttributeSet;

public interface LOSNumericDocumentListener {

	public abstract void insertFailed(LOSNumericDocument doc, int offset, String str, AttributeSet a);
	
	public abstract void insertSucceeded(LOSNumericDocument doc, String resultOfInsert, int offset);
}
