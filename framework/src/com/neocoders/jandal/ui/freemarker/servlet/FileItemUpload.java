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
package com.neocoders.jandal.ui.freemarker.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;

import com.neocoders.jandal.core.Upload;

/**
 * Adapts FileItem to Upload
 * 
 * @author lindsay
 * 
 */
final class FileItemUpload implements Upload {
	public FileItemUpload(FileItem item) {
		this.item = item;
	}

	public String getName() {
		return item.getFieldName();
	}

	public String getClientFileName() {
		return item.getName();
	}

	public String getContentType() {
		return item.getContentType();
	}

	public boolean isInMemory() {
		return item.isInMemory();
	}

	public long getSize() {
		return item.getSize();
	}

	public InputStream getInputStream() throws IOException {
		return item.getInputStream();
	}

	public byte[] toByteArray() {
		return item.get();
	}

	public void write(File file) throws Exception {
		item.write(file);
	}

	public String toString() {
		return "[Upload fileName=\"" + item.getFieldName()
				+ "\", contentType=\"" + item.getContentType() + "\"]";
	}

	private FileItem item;
}