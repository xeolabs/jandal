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

package com.neocoders.jandal.ui.freemarker.model;

import com.neocoders.jandal.ui.freemarker.common.HTTPRequestParamKeys;
import com.neocoders.jandal.ui.freemarker.common.HTTPRequestTypes;
import com.neocoders.jandal.ui.freemarker.session.*;

public class FormTool {

	public FormTool() {
	}

	FormTool(ApplicationSession applicationSession,
			ControllerSession controllerSession) {
		this.applicationSession = applicationSession;
		this.controllerSession = controllerSession;
	}

	public String getFormOpenTag(String eventName) {
		StringBuffer sb = new StringBuffer();
		String formId = controllerSession.getId() + ":" + eventName
				+ this.hashCode();
		sb.append("<form method = \"POST\" action=\"javascript:jandalPostForm("
				+ "document.getElementById('" + formId + "'));\" name=\""
				+ formId + "\" id=\"" + formId + "\">\n");

		/*
		 * Controller
		 * 
		 */
		sb.append("<input name=\"");
		sb.append(HTTPRequestParamKeys.PARAM_CONTROLLER_ID);
		sb.append("\" type=\"hidden\" value=\"");
		sb.append(controllerSession.getId());
		sb.append("\"/>\n");

		/*
		 * Continuation key
		 * 
		 */
		sb.append("<input name=\"");
		sb.append(HTTPRequestParamKeys.PARAM_CONTROLLER_SYNCH_KEY);
		sb.append("\" type=\"hidden\" value=\""); // Continuation key
		sb.append(controllerSession.getSynchKey());
		sb.append("\"/>\n");

		/*
		 * Request type - client event
		 * 
		 */
		sb.append("<input name=\"");
		sb.append(HTTPRequestParamKeys.PARAM_REQUEST_TYPE);
		sb.append("\" type=\"hidden\" value=\""); // Session ID
		sb.append(HTTPRequestTypes.VIEW_EVENT);
		sb.append("\"/>\n");

		/*
		 * Event name
		 * 
		 */
		sb.append("<input name=\"");
		sb.append(HTTPRequestParamKeys.PARAM_VIEW_EVENT_NAME);
		sb.append("\" type=\"hidden\" value=\"");
		sb.append(eventName);
		sb.append("\"/>\n");

		/*
		 * Comma-separated event args - empty because the args in this case are
		 * supplied via the application form fields.
		 * 
		 */
		sb.append("<input name=\"");
		sb.append(HTTPRequestParamKeys.PARAM_EVENT_ARGS);
		sb.append("\" type=\"hidden\" value=\"\"/>\n");
		return sb.toString();
	}

	public String getFormCloseTag() {
		return "</form>";
	}

	private ApplicationSession applicationSession;

	private ControllerSession controllerSession;

}
