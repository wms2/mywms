/* 
Copyright 2019 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Utilities for image handling
 * 
 * @author krane
 *
 */
public class ImageUtils {

	private final static String FORMAT_PNG = "png";

	/**
	 * Scale an image to a given size.
	 * <p>
	 * Images are only scaled to a smaller size than the original.
	 * <p>
	 * The size is given as a max value. It checks both width and height.
	 * 
	 * @param data        The images binary data
	 * @param maxSize     The maximal size (width or height)
	 * @param highQuality A high quality transformation needs more calculation time
	 * @throws IOException
	 */
	public static byte[] scaleImage(byte[] data, int maxSize, boolean highQuality) throws IOException {
		BufferedImage img = null;

		if (data == null || data.length == 0) {
			return null;
		}

		img = ImageIO.read(new ByteArrayInputStream(data));

		if (img != null && (img.getWidth() > maxSize || img.getHeight() > maxSize)) {
			double fctWidth = maxSize;
			fctWidth /= img.getWidth();
			double fctHeigth = maxSize;
			fctHeigth /= img.getHeight();
			double fct = fctWidth > fctHeigth ? fctHeigth : fctWidth;
			if (fct > 0) {
				AffineTransform tx = new AffineTransform();
				tx.scale(fct, fct);
				AffineTransformOp op = new AffineTransformOp(tx,
						highQuality ? AffineTransformOp.TYPE_BICUBIC : AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				img = op.filter(img, null);
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, FORMAT_PNG, out);
		byte[] bytes = out.toByteArray();

		return bytes;
	}
}
