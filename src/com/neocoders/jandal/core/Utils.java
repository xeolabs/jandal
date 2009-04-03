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

class Utils {

    public static String validateName(String name, String description)
	    throws JandalCoreException {
	if (name == null) {
	    throw new JandalCoreException(description + " is null");
	}
	name = name.trim();
	if (name.length() == 0) {
	    throw new JandalCoreException(description + " is zero length");
	}
	return name;
    }

    public static String getContentType(String fileName) {
	int dotIndex = fileName.lastIndexOf(".");
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
}
