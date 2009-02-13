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

package com.neocoders.jandal.ui.freemarker.common;

import java.util.LinkedList;
import java.util.List;

/**
 * Exception thrown by an element of the JandalFreeMarker library.
 * 
 * @author lindsay
 * 
 */
public class JandalFreeMarkerServletException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -3691825560702289214L;

    public JandalFreeMarkerServletException(String message, String description,
	    Throwable cause) {
	super(message, cause);
	this.message = message;
	this.description = description;
	this.tipList = new LinkedList();
    }

    public JandalFreeMarkerServletException(String message, Throwable throwable) {
	this(message, null, throwable);
    }

    public JandalFreeMarkerServletException(String message, String description) {
	this(message, description, null);
    }

    public JandalFreeMarkerServletException(String message) {
	this(message, null, null);
    }

    public JandalFreeMarkerServletException(Throwable throwable) {
	this(null, null, throwable);
    }

    /**
     * Returns the brief description.
     * 
     */
    public final String getMessage() {
	return message;
    }

    /**
     * Returns the detailed description.
     * 
     */
    public final String getDescription() {
	return description;
    }

    public JandalFreeMarkerServletException setDescription(String description) {
	this.description = description;
	return this;
    }

    /**
     * Adds a tip to accompany the {@link Message}. When the {@link Message}
     * indicates an error, a tip might suggest a way to fix it.
     * 
     * @param tip
     *                The suggestion.
     * @return This {@link Message}.
     */
    public JandalFreeMarkerServletException addTip(String tip) {
	this.tipList.add(tip);
	return this;
    }

    /**
     * Returns accompanying tips.
     * 
     * @return List of strings.
     */
    public final List getTips() {
	return new LinkedList(tipList);
    }

    private String message;

    private String description;

    private List tipList;
}
