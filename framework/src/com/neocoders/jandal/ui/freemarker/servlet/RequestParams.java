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

import java.util.HashMap;
import java.util.Map;

/**
 * Set of request parameters.
 */
final class RequestParams {


	public String controllerSynchKey = null;

	public String viewEventName = null;

	public String controllerId = null;

	public String resourceName = null;

	public String outputName = null;

	public String requestType = null;

	/**
	 * When true, we are getting initial session for an application that is
	 * embedded within a page.
	 */
	public String embedded = null;

	/**
	 * When true, we render only views of the controllers that have changed.
	 * When false, render the whole application view.
	 */
	public String ajaxEnabled = null;

	/**
	 * View event params supplied in name=value;name2=value2 format
	 */
	public String viewEventKeyValParamsStr = null;

	/**
	 * View event params that are taken from unrecognised POSTED form fields
	 */
	public Map viewEventCustomFormParams = new HashMap();
}