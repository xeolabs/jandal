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
package com.neocoders.jandal.testing;

public class AssertStringOutput {

    public AssertStringOutput(Object outputValue, boolean ignoreCase)
	    throws JandalUnitException {
	this.outputValueStr = convertToString(outputValue);
	this.ignoreCase = ignoreCase;
    }

    public AssertStringOutput(Object outputValue) throws JandalUnitException {
	this(outputValue, false);
    }

    private String convertToString(Object value) throws JandalUnitException {
	try {
	    return (String) value;
	} catch (ClassCastException e) {
	    throw new JandalUnitException("Output value expected to be a "
		    + String.class.getName() + " but it is a "
		    + value.getClass().getName());
	}
    }

    public final AssertStringOutput notEqualTo(final String value)
	    throws JandalUnitException {
	if (ignoreCase) {
	    if (!outputValueStr.equalsIgnoreCase(value)) {
		throw new JandalUnitException(
			"Output value is not equal to reference value (ignoring case)");
	    }
	} else if (!outputValueStr.equals(value)) {
	    throw new JandalUnitException(
		    "Output value is not equal to reference value");
	}
	return this;
    }

    public final AssertStringOutput equalTo(final String value)
	    throws JandalUnitException {
	if (ignoreCase) {
	    if (!outputValueStr.equalsIgnoreCase(value)) {
		throw new JandalUnitException(
			"Output value is not equal to reference value (ignoring case)");
	    }
	} else if (!outputValueStr.equals(value)) {
	    throw new JandalUnitException(
		    "Output value is not equal to reference value");
	}
	return this;
    }

    public final AssertStringOutput greaterThan(final String value)
	    throws JandalUnitException {
	if (ignoreCase) {
	    if (!(outputValueStr.compareToIgnoreCase(value) > 0)) {
		throw new JandalUnitException(
			"Output value is not greater than reference value (ignoring case)");
	    }
	} else if (!(outputValueStr.compareTo(value) > 0)) {
	    throw new JandalUnitException(
		    "Output value is not greater than reference value");
	}
	return this;
    }

    public final AssertStringOutput lessThan(final String value)
	    throws JandalUnitException {
	if (ignoreCase) {
	    if (!(outputValueStr.compareToIgnoreCase(value) < 0)) {
		throw new JandalUnitException(
			"Output value is not less than reference value (ignoring case)");
	    }
	} else if (!(outputValueStr.compareTo(value) < 0)) {
	    throw new JandalUnitException(
		    "Output value is not less than reference value");
	}
	return this;
    }

    public final AssertStringOutput greaterOrEqualTo(final String value)
	    throws JandalUnitException {
	if (ignoreCase) {
	    if (outputValueStr.compareToIgnoreCase(value) > 0) {
		return this;
	    }
	    if (outputValueStr.equalsIgnoreCase(value)) {
		return this;
	    }
	    throw new JandalUnitException(
		    "Output value is not greater than or equal to reference value");
	} else if (!(outputValueStr.compareTo(value) > 0)) {
	    if (outputValueStr.compareTo(value) > 0) {
		return this;
	    }
	    if (outputValueStr.equals(value)) {
		return this;
	    }
	    throw new JandalUnitException(
		    "Output value is not greater than or equal to reference value");
	}
	return this;
    }

    public final AssertStringOutput lessOrEqualTo(final String value)
	    throws JandalUnitException {
	boolean passed = false;
	if (ignoreCase) {
	    if (outputValueStr.compareToIgnoreCase(value) < 0) {
		return this;
	    }
	    if (outputValueStr.equalsIgnoreCase(value)) {
		return this;
	    }
	    if (!passed) {
		throw new JandalUnitException(
			"Output value is not less than or equal to reference value");
	    }
	} else {
	    if (outputValueStr.compareTo(value) < 0) {
		return this;
	    }
	    if (outputValueStr.equals(value)) {
		return this;
	    }
	    throw new JandalUnitException(
		    "Output value is not less than or equal to reference value");
	}
	return this;
    }

    private String outputValueStr;
    private boolean ignoreCase;

}
