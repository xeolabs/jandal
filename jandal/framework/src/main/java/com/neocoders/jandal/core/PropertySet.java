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

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * A read-only wrapper for a properties resource file.
 * 
 * @author lindsay
 * 
 */
public class PropertySet {
	PropertySet(final Properties props) {
		this.props = props;
	}

	/**
	 * Returns the value of a property in this set. Throws an exception if the
	 * property is not in the set.
	 * 
	 * @param name
	 *            Name of property in this set.
	 * @return Value of the property.
	 * 
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Property not found in this set.
	 * 
	 */
	public final String getProperty(String name) throws JandalCoreException {
		name = Utils.validateName(name, "Property name");
		String value = this.props.getProperty(name);
		if (value == null) {
			throw new JandalCoreException("Property not found: \"" + name
					+ "\"");
		}
		return value;
	}

	/**
	 * Returns the value of a property in this set, or if not found, the given
	 * default backup value.
	 * 
	 * @param name
	 *            Name of property in this set. the backup value is not allowed
	 *            to be null.
	 * @return Value of the property.
	 * 
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Property not found in this set.
	 * 
	 */
	public final String getProperty(String name, final String defaultValue)
			throws JandalCoreException {
		name = Utils.validateName(name, "Property name");
		if (defaultValue == null) {
			throw new JandalCoreException("Default property value is null");
		}
		return this.props.getProperty(name, defaultValue);
	}

	/**
	 * Returns the names of all the properties in this set.
	 * 
	 * @return The names.
	 */
	public final Set propertyNames() {
		return new HashSet(this.props.keySet());
	}

	private Properties props;
}
