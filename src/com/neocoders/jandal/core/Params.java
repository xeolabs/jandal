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

import java.util.*;

/**
 * Packages parameter values for {@link Application}s, {@link Controller}s,
 * {@link State}s and {@link EVentProcessor}s.
 * 
 * <p/> This is easier to use than a HashMap, and it checks for errors, such as:
 * <ul>
 * <li>illegal parameter names,</li>
 * <li>duplicate parameter names, when you add to the map, and</li>
 * <li>missing parameters, when you try to get them from the map.</li>
 * </ul>
 * 
 * @author lindsay
 * 
 */
public class Params {
	/**
	 * Creates new parameter set.
	 * 
	 * 
	 */
	public Params() {
		params = new HashMap();

	}

	public Params(Params params) {
		this.params = new HashMap(params.params);
	}

	public Params(Map map) {
		this.params = new HashMap(map);
	}

	/**
	 * Creates a new parameter set, starting it off with an initial parameter.
	 * The parameter value is not allowed to be null.
	 * 
	 * @param name
	 *            Name of new parameter.
	 * @param value
	 *            Value for new parameter.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Parameter with given name already exists in set.
	 * @throws JandalCoreException
	 *             Parameter value is null.
	 */
	public Params(String name, Object value) throws JandalCoreException {
		this();
		add(name, value);
	}

	/**
	 * Creates a new parameter in this set. The parameter value is not allowed
	 * to be null.
	 * 
	 * @param name
	 *            Name of new parameter.
	 * @param value
	 *            Value for new parameter.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Parameter value is null.
	 */
	public final Params add(String name, Object value)
			throws JandalCoreException {
		name = Utils.validateName(name, "Parameter name");
		if (value == null) {
			throw new IllegalArgumentException("Parameter value is null: \""
					+ name + "\"");
		}
		params.put(name, value);
		return this;
	}

	/**
	 * Removes a parameter from this set.
	 * 
	 * @param name
	 *            Name of parameter.
	 * @param value
	 *            Value for new parameter.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Parameter not found.
	 */
	public final Params remove(String name) throws JandalCoreException {
		name = Utils.validateName(name, "Parameter name");
		if (!params.containsKey(name)) {
			throw new IllegalArgumentException("Parameter not found: \"" + name
					+ "\"");
		}
		params.remove(name);
		return this;
	}

	/**
	 * Returns the names of the parameters in this set.
	 * 
	 * @return Set of names.
	 */
	public Set getNames() {
		return new HashSet(params.keySet());
	}

	/**
	 * Returns a parameter in this set. Throws exception if parameter not in
	 * this set.
	 * 
	 * @param name
	 *            Name of parameter to get.
	 * @return The Parameter.
	 * @throws JandalCoreException
	 *             Name is invalid.
	 * @throws JandalCoreException
	 *             Parameter with given name is not in this set.
	 */
	public final Object get(String name) throws JandalCoreException {
		if (name == null) {
			throw new JandalCoreException("Parameter name is null");
		}
		name = name.trim();
		if (name.length() == 0) {
			throw new JandalCoreException("Parameter name is zero length");
		}
		return params.get(name);
	}

	/**
	 * Returns true if parameter with given name is contained.
	 */
	public final boolean contains(String name) {
		return (params.containsKey(name));
	}

	private Map params;

}