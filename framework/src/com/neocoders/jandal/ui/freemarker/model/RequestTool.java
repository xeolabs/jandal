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

import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

import com.neocoders.jandal.ui.freemarker.common.HTTPRequestTypes;
import com.neocoders.jandal.ui.freemarker.session.*;

import freemarker.template.TemplateModelException;

public class RequestTool {

	public RequestTool() {
	}

	public RequestTool(ControllerSession controllerSession, String eventName) {
		this.controllerSession = controllerSession;
		this.eventName = eventName;
		this.paramVals = new HashMap();
	}

	public RequestTool setParam(String name, String value)
			throws TemplateModelException {
		name = name.trim();
		if (name.length() == 0) {
			throw new TemplateModelException(
					"Client event parameter name is empty");
		}
		if (paramVals.get(name) != null) {
			throw new TemplateModelException(
					"Duplicate client event parameter name: \"" + name + "\"");
		}
		paramVals.put(name, value);
		return this;
	}

	public String getAction() {
		StringBuffer sb = new StringBuffer();
		sb.append("javascript:jandalPostEvent('");

		/*
		 * Controller
		 */
		sb.append(controllerSession.getId());
		sb.append("','");

		/*
		 * Controller Synch Key
		 */
		sb.append(controllerSession.getSynchKey());
		sb.append("','");

		/*
		 * Request type
		 */
		sb.append(HTTPRequestTypes.VIEW_EVENT);
		sb.append("','");

		/*
		 * Event name
		 */
		sb.append(this.eventName);
		sb.append("','");

		/*
		 * No resource name
		 */
		sb.append("','");

		/*
		 * No output name
		 */
		sb.append("','");

		/*
		 * Event arguments
		 */
		sb.append(this.getSerializedArgs());
		sb.append("')");
		return sb.toString();
	}

	private String getSerializedArgs() {
		StringBuffer sb = new StringBuffer();
		for (Iterator i = paramVals.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			String value = (String) paramVals.get(key);
			sb.append(key);
			sb.append("=");
			sb.append(value);
			sb.append(";");
		}
		return sb.toString();
	}

	private ControllerSession controllerSession;

	private String eventName;

	private Map paramVals;
}
