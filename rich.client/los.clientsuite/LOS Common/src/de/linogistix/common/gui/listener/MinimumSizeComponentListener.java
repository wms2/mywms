/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.listener;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ComponentEvent;

/**
 * This class is used to not allowing the user to reduce the size of a component
 * below a certain size.  When we want to set a minimum size on an object we
 * just create the object and then we add it as ComponentListener of the object.
 *
 * This is used basically by the QuickSetupDialog dialog.
 *
 */
public class MinimumSizeComponentListener {

    private final Component comp;
    private int minWidth = -1;
    private int minHeight = -1;
    private boolean entry = false;
    
    /**
     * 
     * @param comp Given Component to which the minimumsize will be set.
     */
    public MinimumSizeComponentListener(Component comp) {
        this.comp = comp;
        addListener();     
        resize();                
    }

    
    /**
     * Constructor for the MinimumSizeComponentListener.
     *
     * @param comp the component for which we want to set a minimum size
     * @param minWidth the minimum width for the component
     * @param minHeight the minimum height for the component
     */
    public MinimumSizeComponentListener(Component comp, int minWidth,
            int minHeight) {
        this.comp = comp;
        this.minWidth = minWidth + 2;
        // It seems that we must add two points to the minWidth (the border of
        // the frame)
        if (comp instanceof Window) {
            this.minWidth += 2;
        }
        this.minHeight = minHeight;        
        addListener();
        resize();        
    }
    
    private void addListener() {
        comp.addComponentListener(new java.awt.event.ComponentAdapter() {
           public void componentResized(ComponentEvent e) {
                resize();
           }
     });        
    }
    
    /**
     * resize the component to the new size
     */
    public void resize() {
        //if no params will be given, 
        //so default minWidth, minHeight is the initial size of the component
        int width = comp.getWidth();
        int height = comp.getHeight();
        boolean resize = false;        
        if (entry == false) {
            entry = true;
            if (minWidth == -1) {
                minWidth = comp.getWidth();
            }
            if (minHeight == -1) {
                minHeight = comp.getHeight();
            }
        }        
        if (width < minWidth) {
            resize = true;
            width = minWidth;
        }
        if (height < minHeight) {
            resize = true;
            height = minHeight;
        }
        if (resize && comp != null) {
            comp.setSize(width, height);
        }

    }
}       
