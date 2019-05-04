/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.linogistix.common.gui.component.controls;

/*
 * Copyright (c) 2003, Bodo Tasche (http://www.wannawork.de) All rights reserved. Redistribution and use in
 * source and binary forms, with or without modification, are permitted provided that the following conditions
 * are met: * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
import de.linogistix.common.gui.object.IconType;
import de.linogistix.common.util.GraphicUtil;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.border.Border;


/**
 * This is a Button without a Border and a little Mouse-Over-Effect.
 * 
 * @author Bodo Tasche, Dennis Urech
 */
public class FlatButton
			extends JButton
			implements FocusListener, MouseListener, MouseMotionListener {

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        System.out.println("move");
	setBorder(withFocus);
    }


    public FlatButton() {
            this("button",null);
        }


    	public FlatButton(String text) {
            this(text,null);
        }
        
        public void mytest5() {
            
        }
    /**
	 * Creates the Button.
	 * 
	 * @param text Text in the Button
	 */
	public FlatButton(String text, IconType icon) {
		super(text);
                if (icon != null) {
                    setIcon(GraphicUtil.getInstance().getIcon(icon));
                }
		withFocus = BorderFactory.createEtchedBorder();
		withoutfocus = BorderFactory.createEmptyBorder(2, 2, 2, 2);
		setBorder(withoutfocus);
		// Work-Around for Bug in jgoodies
		if (UIManager.getLookAndFeel().getClass().toString().startsWith("class com.jgoodies")) {
			setOpaque(false);
		}
		else {
			setContentAreaFilled(false);
		}
		addFocusListener(this);
		addMouseListener(this);
                setFocusable(false);
	}
        



	/**
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent event) {
            setBorder(withFocus);
	}

	/**
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent event) {
		setBorder(withoutfocus);
	}

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent event) {
		// nothing to do here
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent event) {
		setBorder(withFocus);
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent event) {
		setBorder(withoutfocus);
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent event) {
		// nothing to do here
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent event) {
		// nothing to do here
	}

	/**
	 * The focus-Border.
	 */
	private Border withFocus;

	/**
	 * The without focus-Border.
	 */
	private Border withoutfocus;
        
        
}

