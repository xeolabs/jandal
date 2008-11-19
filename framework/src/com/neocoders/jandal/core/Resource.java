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

package com.neocoders.jandal.core;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a stream through which a bundled resource file may be read.
 * 
 * @author lindsay
 * 
 */
public abstract class Resource {
	public Resource() {
		this("unnamed");
	}

	/**
	 * Creates a {@link Resource} with explicitly specified content type and
	 * some properties.
	 * 
	 * @param serverFileName
	 *            File name in the file system, as provided by the application
	 *            (or other server-side software).
	 * @param contentType
	 *            MIME type
	 * 
	 * @param properties
	 *            Attributes to associate with the file.
	 */
	public Resource(String fileName, String mimeType, Map properties) {
		this.fileName = fileName;
		this.mimeType = mimeType;

		this.properties = properties;
	}

	/**
	 * Creates a {@link Resource} with content type inferred from file name
	 * extension and some properties.
	 * 
	 * @param serverFileName
	 *            File name in the server's filesystem, as provided by the
	 *            application (or other server-side software).
	 * 
	 * 
	 * @param properties
	 *            Attributes to associate with the file.
	 */
	public Resource(String serverFileName, HashMap properties) {
		this(serverFileName, Utils.getContentType(serverFileName), properties);
	}

	/**
	 * Creates a {@link Resource}.
	 * 
	 * @param serverFileName
	 *            File name in the server's filesystem, as provided by the
	 *            application (or other server-side software).
	 * 
	 */
	public Resource(String serverFileName) {
		this(serverFileName, new HashMap());
	}

	/**
	 * Returns the name of the resource file.
	 * 
	 * @return The file name.
	 */
	public final String getFileName() {
		return this.fileName;
	}

	/**
	 * Returns the MIME type of the resource file, derived from the file name
	 * extension.
	 * 
	 * @return The file name.
	 */
	public final String getMimeType() {
		if (mimeType == null) {
			mimeType = this.getMimeType(fileName);
		}
		return this.mimeType;
	}

	/**
	 * Returns input stream through which the resource file may be read.
	 * 
	 * @return the stream.
	 * @throws JandalCoreException
	 *             Resource file not found - must have been moved since getting
	 *             this {@link Resource} object.
	 * @throws JandalCoreException
	 *             Problem opening resource file.
	 * 
	 */
	public abstract InputStream getInputStream() throws JandalCoreException;

	/**
	 * Writes the resource file to the given output stream.
	 * 
	 * @param out
	 * @throws JandalCoreException
	 *             Resource file not found - must have been moved since getting
	 *             this {@link Resource} object.
	 * @throws JandalCoreException
	 *             IO error accessing or reading teh resource file.
	 */
	public final void write(final OutputStream out) throws JandalCoreException {
		try {
			final byte[] buf = new byte[1025];
			int nRead;
			final InputStream inputStream = this.getInputStream();
			while ((nRead = inputStream.read(buf)) > 0) {
				out.write(buf, 0, nRead);
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			throw new JandalCoreException("Resource file not found: \""
					+ fileName + "\"", e);
		} catch (IOException e) {
			throw new JandalCoreException("IO exception reading resource : \""
					+ fileName + "\"", e);
		}
	}

	// TODO: get mime type in some extensible way
	private static String getMimeType(final String fileName) {
		final int dotIndex = fileName.lastIndexOf(".");
		if (dotIndex == -1) {
			return "unknown";
		}
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName
				.length() - 1);
		ext = ext.toLowerCase();
		if (ext.equals("txt")) {
			return "text/plain";
		} else if (ext.equals("htm") || ext.equals("html")) {
			return "image/html";
		} else if (ext.equals("svg")) {
			return "image/svg";
		} else if (ext.equals("rtx")) {
			return "text/richtext";
		} else if (ext.equals("tsv")) {
			return "text/tab-seperated-values";
		} else if (ext.equals("ps")) {
			return "application/x-postscript";
		} else if (ext.equals("class")) {
			return "application/java";
		} else if (ext.equals("zip")) {
			return "application/zip";
		} else if (ext.equals("gtar") || ext.equals("tar")) {
			return "image/x-tar";
		} else if (ext.equals("dvi")) {
			return "application/dvi";
		} else if (ext.equals("bin")) {
			return "application/octet-stream";
		} else if (ext.equals("pdf")) {
			return "application/pdf";
		} else if (ext.equals("ps")) {
			return "application/postscript";
		} else if (ext.equals("eps")) {
			return "application/postscript";
		} else if (ext.equals("ai")) {
			return "application/postscript";
		} else if (ext.equals("exe")) {
			return "application/octet-stream";
		} else if (ext.equals("gz")) {
			return "application/x-zip";
		} else if (ext.equals("z")) {
			return "application/x-compress";
		} else if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("jpe")) {
			return "image/jpeg";
		} else if (ext.equals("gif")) {
			return "image/gif";
		} else if (ext.equals("tif") || ext.equals("tiff")) {
			return "image/tiff";
		} else if (ext.equals("png")) {
			return "image/png";
		} else if (ext.equals("jpg") || ext.equals("jpeg")) {
			return "image/jpeg";
		} else if (ext.equals("wav")) {
			return "audio/x-wav";
		} else if (ext.equals("qt")) {
			return "video/quicktime";
		} else if (ext.equals("mov")) {
			return "video/quicktime";
		} else if (ext.equals("avi")) {
			return "video/x-msvideo";
		} else if (ext.equals("mpv2")) {
			return "video/mpeg2";
		} else if (ext.equals("wrl")) {
			return "video/x-vrml";
		} else {
			return "unknown";
		}
	}

	/**
	 * Returns a property.
	 * 
	 */
	public String getProperty(String name) {
		return (String) properties.get(name);
	}

	private String fileName;

	private String mimeType;

	private Map properties;
}
