/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.processes.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.linogistix.mobile.common.gui.bean.BasicBackingBean;
import de.linogistix.mobile.common.gui.bean.BasicDialogBean;
import de.linogistix.mobile.common.system.JSFHelper;
import de.linogistix.mobileserver.processes.controller.ControllerFacade;
import de.linogistix.mobileserver.processes.controller.MobileFunction;


/**
 *
 * @author krane
 */
public class MenuBean extends BasicBackingBean {
	private static final Logger log = Logger.getLogger(MenuBean.class	.getName());

	
	private List<MenuAction> actions = null;
	private int numItems = 3;
	private int currentPage = 0;
	private ControllerFacade ctrlFacade;
	private boolean isInitialized = false;
	
    public MenuBean() {
    	super();
    	getLocale();
    	init();
    	isInitialized = true;
    }
    
    private void init() {
    	log.info("Init");
    	ctrlFacade = super.getStateless(ControllerFacade.class);

    	numItems = ctrlFacade.getMenuPageSize();
    	
    	actions = new ArrayList<MenuAction>();
    	
    	List<MobileFunction> fctList = ctrlFacade.getFunctions();
    	
    	for( MobileFunction fct : fctList ) {

			log.info("Add dialog " + fct.getClassName() );
			
    		BasicDialogBean dialog;
			try {
		        dialog = (BasicDialogBean) JSFHelper.getInstance().getSessionBeanForce( Class.forName(fct.getClassName()) );
			} catch (Throwable e) {
				log.error("Cannot instanciate dialog. Name="+fct.getClassName()+", Exception: " + e.getMessage(), e);
				continue;
			}
			if( dialog == null ) {
				log.error("Cannot instanciate dialog. Name="+fct.getClassName());
				continue;
			}
			
			dialog.init(fct.getArgs());
			
    		actions.add( new MenuAction( dialog.getTitle(), dialog.getNavigationKey() ) );
    	}
    	
    	
    	currentPage = 0;
    }

    
    /**
     * Just for the stupid picking dialog. It is not able to operate by itself.
     * Kill this method after building a stable picking dialog.
     */
    private void resetProcesses() {
    }
    public String getValue0() {
    	if( ! isInitialized ) {
    		init();
    	}
    	return getValue(0);
    }
    public String getValue1() {
    	return getValue(1);
    }
    public String getValue2() {
    	return getValue(2);
    }
    public String getValue3() {
    	return getValue(3);
    }
    public String getValue(int num) {
    	if(num >= numItems) {
    		return "";
    	}
    	int idx = (currentPage * numItems) + num;
    	return "" + ((actions.size() > idx) ? resolve(actions.get(idx).name) : "");
    }
    
    public String action0() {
    	return action(0);
    }
    public String action1() {
    	return action(1);
    }
    public String action2() {
    	return action(2);
    }
    public String action3() {
    	return action(3);
    }
    public String action(int num) {
    	resetProcesses();
    	int idx = (currentPage * numItems) + num;
    	return "" + ((actions.size() > idx) ? actions.get(idx).action : "");
    }
    
    public boolean getHasNextPage() {
    	int idx = (currentPage * numItems) + numItems;
    	return idx < actions.size();
    }
    
    public boolean getHasPrevPage() {
    	return currentPage > 0;
    }
    
    public String processNextPage() {
    	currentPage++;
    	return "";
    }

    public String processPrevPage() {
    	if( currentPage > 0 ) {
    		currentPage--;
    	}
    	return "";
    }
    
    public String processLogout(){
    	isInitialized = false;
        return "Logout";
    }

}


class MenuAction {
	String name;
	String action;
	
	MenuAction( String name, String action ) {
		this.name = name;
		this.action = action;
	}
}