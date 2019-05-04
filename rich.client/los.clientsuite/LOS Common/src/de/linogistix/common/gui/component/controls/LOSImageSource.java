/*
 * Copyright (c) 2011-2012 LinogistiX GmbH
 *
 *  www.linogistix.com
 *
 *  Project myWMS-LOS
 */

package de.linogistix.common.gui.component.controls;

import java.awt.image.BufferedImage;

/**
 *
 * @author krane
 */
public interface LOSImageSource {

    public BufferedImage getImage();
    public String getTitle();
}
