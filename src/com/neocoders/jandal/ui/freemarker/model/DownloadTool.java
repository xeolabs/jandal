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

import com.neocoders.jandal.core.*;
import com.neocoders.jandal.ui.freemarker.common.HTTPRequestParamKeys;
import com.neocoders.jandal.ui.freemarker.common.HTTPRequestTypes;
import com.neocoders.jandal.ui.freemarker.session.*;

import freemarker.template.TemplateModelException;

public class DownloadTool {

	DownloadTool(ApplicationSession applicationSession,
			ControllerSession controllerSession, String outputName,
			Resource download) {
		this.applicationSession = applicationSession;
		this.controllerSession = controllerSession;
		this.download = download;
		this.outputName = outputName;
	}

	public String getFileName() {
		return download.getFileName();
	}

	public String getMimeType() {
		return download.getMimeType();
	}

	public String getAction() {

		StringBuffer sb = new StringBuffer();
		sb.append("javascript:_jandalPostEvent('");

	

		/*
		 * Controller
		 */
		sb.append(controllerSession.getId());
		sb.append("','");

		/*
		 * Controller sychronisation key
		 * 
		 */
		sb.append(controllerSession.getSynchKey());
		sb.append("','");

		/*
		 * Request type
		 * 
		 */
		sb.append(HTTPRequestTypes.DOWNLOAD_REQUEST);
		sb.append("','");

		/*
		 * No view event name
		 * 
		 */
		sb.append("','");
		/*
		 * No resource name
		 * 
		 */
		sb.append("','");
		sb.append(outputName);
		sb.append("','");
		/*
		 * No event args
		 * 
		 */
		sb.append("')");
		return sb.toString();
	}

	public final String getDownloadUrl() throws TemplateModelException {

		/*
		 * Render URL
		 */
		StringBuffer sb = new StringBuffer();
		sb.append(applicationSession.getRequestUri());
		sb.append("?");
		sb.append(HTTPRequestParamKeys.PARAM_CONTROLLER_ID);
		sb.append("=");
		sb.append(controllerSession.getId());
		sb.append("&");
		sb.append(HTTPRequestParamKeys.PARAM_CONTROLLER_SYNCH_KEY);
		sb.append("=");
		sb.append(controllerSession.getSynchKey());
		sb.append("&");
		sb.append(HTTPRequestParamKeys.PARAM_REQUEST_TYPE);
		sb.append("=");
		sb.append(HTTPRequestTypes.DOWNLOAD_REQUEST);
		sb.append("&");
		sb.append(HTTPRequestParamKeys.PARAM_OUTPUT_NAME);
		sb.append("=");
		sb.append(this.outputName);
		return sb.toString();
	}

	private ApplicationSession applicationSession;

	private ControllerSession controllerSession;

	private Resource download;

	private String outputName;
}
