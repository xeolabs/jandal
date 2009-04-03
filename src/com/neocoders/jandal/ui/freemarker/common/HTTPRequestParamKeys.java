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

/**
 * Identifies HTTP keys for parameters in requests made of a
 * {@link guavaFreeMarkerServlet}. Don't use these as field names in your HTML
 * forms.
 * 
 * @author lindsay
 * 
 */
public interface HTTPRequestParamKeys {

	/**
	 * Controller
	 * 
	 */
	public final static String PARAM_CONTROLLER_ID = "_controller_id";

	/**
	 * Controller synchronisation key
	 * 
	 */
	public final static String PARAM_CONTROLLER_SYNCH_KEY = "_controller_synch_key";

	/**
	 * Request type
	 * 
	 */
	public final static String PARAM_REQUEST_TYPE = "_request_type";

	/**
	 * View event name
	 * 
	 */
	public final static String PARAM_VIEW_EVENT_NAME = "_view_event_name";

	/**
	 * Extra command arguments, as key-value pairs.
	 * 
	 */
	public final static String PARAM_EVENT_ARGS = "_event_args";

	/**
	 * Name of resource bundled with a target {@link Controller}.
	 * 
	 */
	public final static String PARAM_RESOURCE_NAME = "_resource_name";

	/**
	 * Name of output of a target {@link Controller}.
	 * 
	 */
	public final static String PARAM_OUTPUT_NAME = "_output_name";

	/**
	 * Name of flag specifying whether AJAX is to be used in response.
	 * 
	 */
	public final static String PARAM_AJAX_ENABLED = "_ajax_enabled";

	/**
	 * Name of flag specifying whether application view is embedded in page or
	 * not.
	 * 
	 */
	public final static String PARAM_EMBEDDED = "_embedded";

}
