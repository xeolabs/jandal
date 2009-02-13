/*
 * Copyright (C) 2007 Lindsay S. Kay, All rights Reserved.
 *
 * This software is provided "as-is", without any express or implied warranty. In no event will the 
 * author be held liable for any damages arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose, including commercial 
 * applications, and to alter it and redistribute if freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented: you must not claim that you wrote 
 * 	the original software. if you use this software in a product, an acknowledgement in the product 
 * 	documentation would be appreciated but is not required.
 * 
 *  2. Altered source versions must be plainly marked as such, and must not be misrepresented 
 * 	as the original software.
 * 
  * 3. This notice must not be removed or altered from any source distribution.
 */
package com.neocoders.jandal.examples.imageGenerator.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.neocoders.jandal.core.JandalCoreException;
import com.neocoders.jandal.core.Resource;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Image extends Resource {
	public Image(String message) {
		super("image/jpg");
		this.message = message;
	}

	public InputStream getInputStream() throws JandalCoreException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(stream);

		BufferedImage bi = new BufferedImage(300, 100,
				BufferedImage.TYPE_BYTE_INDEXED);

		Graphics graphics = bi.createGraphics();
		graphics.setColor(new Color(190, 255, 190));
		graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		graphics.setColor(new Color(0, 0, 0));
		graphics.setFont(new Font("helvetica", Font.BOLD, 30));
		graphics.drawString(message, 10, 40);

		try {
			encoder.encode(bi);
		} catch (Exception e) {
			throw new JandalCoreException("Error creating banner image: "
					+ e.getMessage(), e);
		}
		return new ByteArrayInputStream(stream.toByteArray());
	}

	private String message;
}
