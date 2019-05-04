/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.util;


import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 */
public class TransparentPicture {
  Image DblBufferImg;
  PixelGrabber PixelG;
  int PicPixel[];
  int PicBkMem[];
  int BkPixel[];
  Dimension BkDim;
  Rectangle PicArea;

  public TransparentPicture() {
  }

  private void grabPixel(Image PicImg, Image BkImg) {
    ImageIcon icon = new ImageIcon(BkImg);
    BkDim = new Dimension(icon.getIconWidth(), icon.getIconHeight());

    BkPixel = new int[BkDim.width * BkDim.height];

    PixelG = new PixelGrabber(BkImg, 0, 0, BkDim.width, BkDim.height,
                              BkPixel, 0, BkDim.width);

    try {
      PixelG.grabPixels();
    }
    catch (InterruptedException e) {
      return;
    }

//    PicArea=new Rectangle(0,0,40,40);
//    PicArea=new Rectangle(0,0,7,7);

    PicPixel = new int[PicArea.width * PicArea.height];

    PixelG = new PixelGrabber(PicImg, 0, 0, PicArea.width, PicArea.height,
                              PicPixel, 0, PicArea.width);

    try {
      PixelG.grabPixels();
    }
    catch (InterruptedException e) {
      return;
    }

    PicBkMem = new int[PicArea.width * PicArea.height];

  }

  private Image setTransparent(Image PicImg, Image BkImg) {

    int BkOff = BkDim.width * PicArea.y + PicArea.x;
    int PicOff = 0;

    for (int j = 0; j < PicArea.height; j++) {
      System.arraycopy(BkPixel, BkOff, PicBkMem, PicOff, PicArea.width);
      BkOff += BkDim.width;
      PicOff += PicArea.width;
    }

    BkOff = BkDim.width * PicArea.y + PicArea.x;
    PicOff = 0;

    for (int j = 0; j < PicArea.height; j++) {
      for (int i = 0; i < PicArea.width; i++) {

//        if (PicPixel[PicOff + i] != -16777216) //schwarz transparent darstellen.
        if (PicPixel[PicOff + i] != -1) { //weiß transparent darstellen.
          BkPixel[BkOff + i] = PicPixel[PicOff + i];

        }
      }
      BkOff += BkDim.width;
      PicOff += PicArea.width;
    }

    DblBufferImg = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(BkDim.width, BkDim.height,
        BkPixel, 0, BkDim.width));
    return DblBufferImg;
  }

  public Image getTransparentPic(Image PicImg, Image BkImg, Rectangle PicArea) {
    this.PicArea = PicArea;
    grabPixel(PicImg, BkImg);
    return setTransparent(PicImg, BkImg);
//    return null;
  }

}


