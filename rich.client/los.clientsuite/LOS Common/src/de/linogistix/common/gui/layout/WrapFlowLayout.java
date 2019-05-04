/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.layout;

import java.awt.*;
import javax.swing.*;

/**
* FlowLayout subclass that fully supports wrapping of components.
*/
public class WrapFlowLayout extends FlowLayout
{
	// The preferred size for this container.
	private Dimension preferredLayoutSize;

	/**
	* Constructs a new <code>WrapLayout</code> with a left
	* alignment and a default 5-unit horizontal and vertical gap.
	*/
	public WrapFlowLayout()
	{
		super(LEFT);
	}

	/**
	* Constructs a new <code>FlowLayout</code> with the specified
	* alignment and a default 5-unit horizontal and vertical gap.
	* The value of the alignment argument must be one of
	* <code>WrapLayout</code>, <code>WrapLayout</code>,
	* or <code>WrapLayout</code>.
	* @param align the alignment value
	*/
	public WrapFlowLayout(int align)
	{
		super(align);
	}

	/**
	* Creates a new flow layout manager with the indicated alignment
	* and the indicated horizontal and vertical gaps.
	* <p>
	* The value of the alignment argument must be one of
	* <code>WrapLayout</code>, <code>WrapLayout</code>,
	* or <code>WrapLayout</code>.
	* @param align the alignment value
	* @param hgap the horizontal gap between components
	* @param vgap the vertical gap between components
	*/
	public WrapFlowLayout(int align, int hgap, int vgap)
	{
		super(align, hgap, vgap);
	}

	/**
	* Returns the preferred dimensions for this layout given the
	* <i>visible</i> components in the specified target container.
	* @param target the component which needs to be laid out
	* @return the preferred dimensions to lay out the
	* subcomponents of the specified container
	*/
/*	public Dimension preferredLayoutSize(Container target)
	{
		return layoutSize(target, true);
	}*/

	/**
	* Returns the minimum dimensions needed to layout the <i>visible</i>
	* components contained in the specified target container.
	* @param target the component which needs to be laid out
	* @return the minimum dimensions to lay out the
	* subcomponents of the specified container
	*/
/*	public Dimension minimumLayoutSize(Container target)
	{
		return layoutSize(target, false);
	}*/

        
        

    public Dimension preferredLayoutSize (Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxwidth = target.getWidth() - (insets.left + insets.right + getHgap() * 2);
            int nmembers = target.getComponentCount();
            int x = 0, y = insets.top + getVgap();
            int rowh = 0, start = 0;

            boolean ltr = target.getComponentOrientation().isLeftToRight();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();

                    if ((x == 0) || ((x + d.width) <= maxwidth)) {
                        if (x > 0) {
                            x += getHgap();
                        }
                        x += d.width;
                        rowh = Math.max(rowh, d.height);
                    } else {
                        processComponents(target, insets.left + getHgap(), y, maxwidth - x, rowh, start, i, ltr);
                        x = d.width;
                        y += getVgap() + rowh;
                        rowh = d.height;
                        start = i;
                    }
                }
            }
            processComponents(target, insets.left + getHgap(), y, maxwidth - x, rowh, start, nmembers, ltr);
            y += rowh;
            return new Dimension (maxwidth, y+5);
        }
    }

    private void processComponents(Container target, int x, int y, int width, int height, int rowStart, int rowEnd, boolean ltr) {
        synchronized (target.getTreeLock()) {
            switch (getAlignment()) {
            case LEFT:      x += ltr ? 0 : width; break;
            case CENTER:    x += width / 2;       break;
            case RIGHT:     x += ltr ? width : 0; break;
            case LEADING:   break;
            case TRAILING:  x += width;           break;
            }
            for (int i = rowStart; i < rowEnd; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    x += m.getWidth() + getHgap();
                }
            }
        }
    }        
}
