package de.linogistix.mobile.common.gui.bean;

public abstract class BasicDialogBean extends BasicBackingBean {

	/**
	 * @return The key of the faces-config.xml file
	 */
	public abstract String getNavigationKey();
	
	/**
	 * @return The title of the dialog.
	 */
	public abstract String getTitle();

	/**
	 * Initialization of the dialog. 
	 * Overwrite this method to use the arguments given by the MenuBean 
	 */
	public void init( String[] args ){
	}
}
