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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

class ResourceAccessor {

	public ResourceAccessor(Object owner) {
		super();
		this.owner = owner;
		this.propertySetCache = new HashMap();
	}

	public final Resource getResource(String fileName)
			throws JandalCoreException {
		final String fileName2 = Utils.validateName(fileName,
				"Resource file name");
		return new Resource(fileName2) {
			public InputStream getInputStream() throws JandalCoreException {
				InputStream inputStream = owner.getClass().getResourceAsStream(
						fileName2);
				if (inputStream == null) {
					throw new JandalCoreException("Resource file not found: "
							+ fileName2);
				}
				return inputStream;
			}
		};
	}

	public final String getProperty(final String locator)
			throws JandalCoreException {
		final String[] elems = this.parseLocator(locator);
		final String propsFilePath = elems[0];
		final String propName = elems[1];
		final PropertySet propertySet = this.getProperties(propsFilePath);
		return propertySet.getProperty(propName);
	}

	private String[] parseLocator(final String locator) // TODO: rework so this
			// can handle property
			// keys with ':' in them
			throws JandalCoreException {
		if (locator == null) {
			throw new JandalCoreException("Null property locator");
		}
		final StringTokenizer stok = new StringTokenizer(locator, ":");
		String filePath = null;
		String propertyName = null;
		if (!stok.hasMoreTokens()) {
			throw new JandalCoreException("Property locator is zero length");
		} else {
			filePath = stok.nextToken();
		}
		if (!stok.hasMoreTokens()) {
			throw new JandalCoreException(
					"Property locator has missing property name: \"" + locator
							+ "\"");
		} else {
			propertyName = stok.nextToken();
		}
		if (stok.hasMoreTokens()) {
			throw new JandalCoreException(
					"Property locator has too many elements: \"" + locator
							+ "\"");
		}
		return new String[] { filePath, propertyName };
	}

	public final PropertySet getProperties(final String propsFileName)
			throws JandalCoreException {
		PropertySet propSet = (PropertySet) this.propertySetCache
				.get(propsFileName);
		if (propSet != null) {
			return propSet;
		}
		Properties properties = new Properties();
		final Resource resource = this.getResource(propsFileName);
		try {
			properties.load(resource.getInputStream());
		} catch (IOException e) {
			throw new JandalCoreException(
					"IO exception loading properties file: \"" + propsFileName
							+ "\"", e);
		}
		propSet = new PropertySet(properties);
		this.propertySetCache.put(propsFileName, propSet);
		return propSet;
	}

	private final Object owner;

	private final Map propertySetCache;
}
