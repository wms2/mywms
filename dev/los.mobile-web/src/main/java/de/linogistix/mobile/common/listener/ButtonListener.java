/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.listener;

import de.linogistix.mobile.common.gui.bean.NotifyDescriptorExtBean;

/**
 *
 * @author artur
 */
public abstract interface ButtonListener {

    public String buttonClicked(final int buttonId, final NotifyDescriptorExtBean notifyDescriptorBean);
    
}
