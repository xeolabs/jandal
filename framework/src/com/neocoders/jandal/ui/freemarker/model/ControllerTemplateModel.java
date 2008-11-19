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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import com.neocoders.jandal.core.*;
import com.neocoders.jandal.ui.freemarker.common.HTTPRequestParamKeys;
import com.neocoders.jandal.ui.freemarker.common.HTTPRequestTypes;
import com.neocoders.jandal.ui.freemarker.session.*;

import freemarker.template.TemplateModelException;

public class ControllerTemplateModel {
	public ControllerTemplateModel() {
	}

	public ControllerTemplateModel(ApplicationSession applicationSession,
			ControllerSession controllerSession, RenderCallback renderCallback) {
		super();
		this.applicationSession = applicationSession;
		this.controllerSession = controllerSession;
		this.renderCallback = renderCallback;
	}

	public final String getRequestUri() {
		return applicationSession.getRequestUri();
	}

	public final String getResourceUrl(String fileName)
			throws TemplateModelException {

		/*
		 * Test if resource can be got
		 */
		try {
			this.controllerSession.getController().getResource(fileName);
		} catch (Exception e) {
			throw new TemplateModelException(e.getMessage(), e);
		}

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
		sb.append(HTTPRequestTypes.RESOURCE_REQUEST);
		sb.append("&");
		sb.append(HTTPRequestParamKeys.PARAM_RESOURCE_NAME);
		sb.append("=");
		sb.append(fileName);
		return sb.toString();
	}

	public final String getTextFileResource(String fileName)
			throws TemplateModelException {
		Resource resource;
		try {
			resource = controllerSession.getController().getResource(fileName);
			InputStream is = resource.getInputStream();
			InputStreamReader in = new InputStreamReader(is);
			BufferedReader bufin = new BufferedReader(in);
			StringBuffer inBuf = new StringBuffer();
			for (String line = bufin.readLine(); line != null; line = bufin
					.readLine()) {
				inBuf.append(line);
				inBuf.append("\n");
			}
			return inBuf.toString();
		} catch (Exception e) {
			throw new TemplateModelException(e.getMessage(), e);
		}
	}

	public final String getProperty(String locator)
			throws TemplateModelException {
		try {
			return controllerSession.getController().getProperty(locator);
		} catch (Exception e) {
			throw new TemplateModelException(e.getMessage(), e);
		}
	}

	public final RequestTool getRequestTool(String name) {
		return new RequestTool(this.controllerSession,
				name);
	}

	public final ApplicationTool getLocalApplicationTool(String name) {
		return new ApplicationTool(this.applicationSession,
				this.controllerSession, name, null);
	}

	public final ApplicationTool getRemoteApplicationTool(String url) {
		return new ApplicationTool(this.applicationSession,
				this.controllerSession, null, url);
	}

	public final FormTool getFormTool() {
		return new FormTool(this.applicationSession, this.controllerSession);
	}

	public final Object getOutput(String name) throws TemplateModelException {
		try {
			Object output = this.controllerSession.getController().getOutput(
					name);
			if (output == null) {
				throw new TemplateModelException(
						"Controller output not found: \"" + name + "\"");
			}
			return output;
		} catch (Exception e) {
			throw new TemplateModelException(e.getMessage(), e);
		}
	}

	public final DownloadTool getDownloadTool(String name)
			throws TemplateModelException {
		Object output = this.getOutput(name);
		Resource download = null;
		try {
			download = (Resource) output;
		} catch (ClassCastException cce) {
			throw new TemplateModelException("Controller Output \"" + name
					+ "\" is not a " + Resource.class.getName() + " - it is a "
					+ output.getClass().getName());
		}
		return new DownloadTool(this.applicationSession, controllerSession,
				name, download);
	}

	public final String getChildView(String childControllerName)
			throws TemplateModelException {
		try {
			ControllerSession childControllerSession = this.controllerSession
					.getChildControllerSessionWithName(childControllerName);
			if (childControllerSession == null) {
				throw new TemplateModelException(
						"Child Controller not found: \"" + childControllerName
								+ "\"");
			}
			return renderCallback.renderController(applicationSession,
					childControllerSession);
		} catch (Exception e) {
			throw new TemplateModelException(e.getMessage(), e);
		}
	}

	private ApplicationSession applicationSession;

	private RenderCallback renderCallback;

	private ControllerSession controllerSession;

}
