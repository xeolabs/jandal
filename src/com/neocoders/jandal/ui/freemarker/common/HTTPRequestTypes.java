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

import com.neocoders.jandal.core.*;

/**
 * Identifies the HTTP keys that indicate the types of requests made of a
 * {@link guavaFreeMarkerServlet}. Don't use these as field names in your
 * HTML forms. See {@link HTTPRequestTypes} for the keys that identify
 * parameters for the requests.
 * 
 * @author lindsay
 * 
 */
public interface HTTPRequestTypes {
	/**
	 * A client event targeted at an {@link OnClientEvent}.
	 */
	public final static String VIEW_EVENT = "_view_event_request";

	/**
	 * Request for a {@link Download} from an {@link Output}.
	 * 
	 */
	public final static String DOWNLOAD_REQUEST = "_download_request";

	/**
	 * Request for a {@link Resource}.
	 * 
	 */
	public final static String RESOURCE_REQUEST = "_resource_request";
}
