/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class RGB2GIF
{
   static final int BITS = 12;

   static final int HSIZE = 5003; // 80% occupancy

   private static int n_bits; // number of bits/code
   private static int maxbits = BITS; // user settable max # bits/code
   private static int maxcode; // maximum code, given n_bits
   private static int maxmaxcode = 1 << BITS; // should NEVER generate this code

   private static int[] htab = new int[HSIZE];
   private static int[] codetab = new int[HSIZE];

   private static int free_ent = 0; // first unused entry

   // block compression parameters -- after all codes are used up,
   // and compression rate changes, start over.
   private static boolean clear_flg = false;
   private static int g_init_bits;

   private static int EOFCode;
   private static int cur_accum = 0;
   private static int cur_bits = 0;

   private static int masks[] = {
         0x0000,
         0x0001,
         0x0003,
         0x0007,
         0x000F,
         0x001F,
         0x003F,
         0x007F,
         0x00FF,
         0x01FF,
         0x03FF,
         0x07FF,
         0x0FFF,
         0x1FFF,
         0x3FFF,
         0x7FFF,
         0xFFFF };

   /** Number of characters so far in this 'packet' */
   private static int a_count;

   /** Define the storage for the packet accumulator */
   private static byte[] accum = new byte[256];
   
   /**
    * reset code table
    */
   private static final void clearHashTable(int hsize) {
      for (int i = 0; i < hsize; ++i)
         htab[i] = -1;
   }
      
   private static final void output(int code, OutputStream outs) throws IOException
   {
      cur_accum &= masks[cur_bits];

      if (cur_bits > 0)
         cur_accum |= (code << cur_bits);
      else
         cur_accum = code;

      cur_bits += n_bits;

      while (cur_bits >= 8)
      {
         accum[a_count++] = ((byte) (cur_accum & 0xff));
         if (a_count >= 254)            
         {
            outs.write(a_count);
            outs.write(accum, 0, a_count);
            a_count = 0;
         }
         cur_accum >>= 8;
         cur_bits -= 8;
      }

      // If the next entry is going to be too big for the code size,
      // then increase it, if possible.
      if (free_ent > maxcode || clear_flg) {
         if (clear_flg) {
            int n_bits1 = n_bits = g_init_bits;
            maxcode = (1 << n_bits1) - 1;
            clear_flg = false;
         } else {
            ++n_bits;
            if (n_bits == maxbits)
               maxcode = maxmaxcode;
            else
               maxcode = (1 << n_bits) - 1;
         }
      }

      if (code == EOFCode)
      {
         // At EOF, write the rest of the buffer.
         while (cur_bits > 0) {
            accum[a_count++] = ((byte) (cur_accum & 0xff));
            if (a_count >= 254)
            {
               outs.write(a_count);
               outs.write(accum, 0, a_count);
               a_count = 0;
            }
            cur_accum >>= 8;
            cur_bits -= 8;
         }

         if (a_count > 0)
         {
            outs.write(a_count);
            outs.write(accum, 0, a_count);
            a_count = 0;
         }
      }
   }
      
   /**
    *Para: pictureWidth, pictureHeight, RGBArray[]
    *int pictureWidth: width of the picture
    *int pictureHeight: height of the picture
    *int RGBArray[]: every element express an RGB value
    */
   final static byte[] m_gifhead = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0, 0, 0, 0, 0, 0, 0};
   final static byte[] m_giftransport_datahead = {0x21, (byte)0xF9, 4, 1, 0, 0, 0, 0, 0x2c, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   
   /**
    * Make a GIF File from an RGB array
    * @param pictureWidth
    * @param pictureHeight
    * @param RGBArray
    * @return
    */
   public static final byte[] makeGIF(int pictureWidth, int pictureHeight, int RGBArray[])
   {
      // initialize palette      
      int paletteRGB[] = new int[256];
      for(int i=0; i<256; i++)
         paletteRGB[i] = 0xF0000F;
      // the transparent color is set to purple (ff00ff)
      paletteRGB[0] = 0xFF00FF;
   
      int palNumber = 0;
      for(int i=0; i<pictureWidth * pictureHeight; i++)
      {
         if(RGBArray[i] == 0xFF00FF)
            RGBArray[i] = 0;
         else
         {
            for(int j=0; j<palNumber+1; j++)
            {
               if(RGBArray[i] == paletteRGB[j])
               {
                  RGBArray[i]=j;
                  break;
               }
               if(RGBArray[i] != paletteRGB[j]&&j == palNumber)
               {
                  palNumber++;
                  paletteRGB[palNumber]=RGBArray[i];
                  RGBArray[i]=palNumber;
                  break;
               }
            }
         }
      }   
            
      int imgSize = pictureWidth * pictureHeight + 768;
      byte gis[]=new byte[imgSize];
      for(int i=0; i<256; i++)
      {
         gis[i*3]=(byte)((paletteRGB[i]&0xFF0000)>>16);
         gis[i*3+1]=(byte)((paletteRGB[i]&0x00FF00)>>8);
         gis[i*3+2]=(byte)(paletteRGB[i]&0x0000FF);
      }
      for(int i=768, j=0; i<imgSize; i++, j++)
      {
         gis[i] = (byte)RGBArray[j];
      }
                  
        byte pels[] = new byte[pictureWidth * pictureHeight];
        int cplen = 255;
        int d = 2;
        while (cplen > d) {
            d <<= 1;
        }
        cplen *= 3;

        m_giftransport_datahead[13] = m_gifhead[6] = (byte)pictureWidth;
        m_giftransport_datahead[15] = m_gifhead[8] = (byte)pictureHeight;
        m_gifhead[10] = (byte)0xF7;
   
        int point = 0;
        int z = 768;
        System.arraycopy(gis, z, pels, point, pictureWidth*pictureHeight);

        cplen = d*3;
       
        try{
           ByteArrayOutputStream out = new ByteArrayOutputStream();
       
           //encode the data
           {
              int initCodeSize = 8; // color depth (must be greater than 2)
              out.write(initCodeSize); // write "initial code size" byte

            int nPixelCount = pictureWidth * pictureHeight;
            int curPixel = 0;
      
            // compress and write the pixel data
            initCodeSize += 1;
            int fcode;
            int i /* = 0 */;
            int c;
            int ent;
            int disp;
            int hsize_reg;
            int hshift;
      
            // Set up the globals:  g_init_bits - initial number of bits
            g_init_bits = initCodeSize;
      
            // Set up the necessary values
            clear_flg = false;
            n_bits = g_init_bits;
            maxcode = (1 << n_bits) - 1;
      
            int clearCode = 1 << (initCodeSize - 1);
            EOFCode = clearCode + 1;
            free_ent = clearCode + 2;
      
            a_count = 0; // clear packet
      
            ent = RGBArray[curPixel++]&0xff;
      
            hshift = 0;
            for (fcode = HSIZE; fcode < 65536; fcode *= 2)
               ++hshift;
            hshift = 8 - hshift; // set hash code range bound
      
            hsize_reg = HSIZE;
            clearHashTable(hsize_reg); // clear hash table
            cur_bits = 0; 				//crash: added reinitialization       
            output(clearCode, out);
      
            outer_loop : while (curPixel < nPixelCount)
            {
               c = RGBArray[curPixel++]&0xff;
               fcode = (c << maxbits) + ent;
               i = (c << hshift) ^ ent; // xor hashing
      
               if (htab[i] == fcode) {
                  ent = codetab[i];
                  continue;
               } else if (htab[i] >= 0) // non-empty slot
                  {
                  disp = hsize_reg - i; // secondary hash (after G. Knott)
                  if (i == 0)
                     disp = 1;
                  do {
                     if ((i -= disp) < 0)
                        i += hsize_reg;
      
                     if (htab[i] == fcode) {
                        ent = codetab[i];
                        continue outer_loop;
                     }
                  } while (htab[i] >= 0);
               }
               output(ent, out);
               ent = c;
               if (free_ent < maxmaxcode) {
                  codetab[i] = free_ent++; // code -> hashtable
                  htab[i] = fcode;
               }
               else
               {
                  clearHashTable(HSIZE);
                  free_ent = clearCode + 2;
                  clear_flg = true;            
                  output(clearCode, out);
               }
            }
            // Put out the final code.
            output(ent, out);
            output(EOFCode, out);
      
            out.write(0); // write block terminator
           }
              
           int ret = 0;          
           final int retLength = cplen + m_gifhead.length + m_giftransport_datahead.length + out.size() + 1;// - (((gis[3] & 0x01) > 0) ? 0 : 8);
           final byte retArray[] = new byte[retLength];
           System.arraycopy(m_gifhead, 0, retArray, ret, m_gifhead.length);
           ret += m_gifhead.length;
           System.arraycopy(gis, 0, retArray, ret, 256*3);
           ret += cplen;
           System.arraycopy(m_giftransport_datahead, 0, retArray, ret, m_giftransport_datahead.length);
           ret += m_giftransport_datahead.length;
      
           System.arraycopy(out.toByteArray(), 0, retArray, ret, out.size());
           ret += out.size();
           try
           {
              out.close();
            }catch(Exception e){}
           out = null;
           pels = null;
           retArray[ret] = 0x3b;   // eof flag
           return retArray;
          
        }
        catch(Exception exc)
        {
        }
       
        return null;
   }
}