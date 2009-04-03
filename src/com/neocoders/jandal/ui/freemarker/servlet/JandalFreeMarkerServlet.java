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

/* (C) NectarWorks 2006 */

package com.neocoders.jandal.ui.freemarker.servlet;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.StringTokenizer;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import java.util.List;

import org.apache.commons.fileupload.*;

import com.neocoders.jandal.core.*;
import com.neocoders.jandal.ui.freemarker.common.*;
import com.neocoders.jandal.ui.freemarker.model.*;
import com.neocoders.jandal.ui.freemarker.servlet.internalExceptions.*;
import com.neocoders.jandal.ui.freemarker.session.*;

import freemarker.template.*;

/**
 * Serves an Application client web interface using FreeMarker templates.
 * 
 * @author lindsay
 * 
 */
public class JandalFreeMarkerServlet extends HttpServlet {

	public static final String DEFAULT_TEMPLATE_CONTEXT_NAME = "context";

	public static final String DEFAULT_TEMPLATE_OUTPUT_NAME = "template";

	private static final String SERVICE_SET_CLASS_INIT_PARAM_KEY = "service-set-class";

	private static final String APPLICATION_CLASS_INIT_PARAM_KEY = "application-class";

	private static final String TEMPLATE_OUTPUT_NAME_INIT_PARAM_KEY = "template-constant-name";

	private static final String TEMPLATE_CONTEXT_NAME_INIT_PARAM_KEY = "template-context-name";

	private static final String WINDOW_TITLE_INIT_PARAM_KEY = "window-title";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {

			/*
			 * Name of ServiceSet implementation - optional, servlet will use a
			 * default empty ServiceSet if none specified
			 */
			this.serviceSet = null;
			String serviceSetClassName = getInitParam(config,
					JandalFreeMarkerServlet.SERVICE_SET_CLASS_INIT_PARAM_KEY,
					null, false);

			if (serviceSetClassName == null) {
				serviceSet = new ServiceSet();
			} else {
				serviceSet = this.newServiceSet(serviceSetClassName);
			}

			/*
			 * Name of Application implementation - mandatory
			 */
			this.applicationClassName = getInitParam(config,
					JandalFreeMarkerServlet.APPLICATION_CLASS_INIT_PARAM_KEY,
					null, true);

			/*
			 * Name of Controller output used by States to indicate their
			 * template file - optional, falls back on default value
			 */
			this.templateOutputName = getInitParam(
					config,
					JandalFreeMarkerServlet.TEMPLATE_OUTPUT_NAME_INIT_PARAM_KEY,
					DEFAULT_TEMPLATE_OUTPUT_NAME, false);

			/*
			 * Name under which each template can find its
			 * ControllerTemplateModel - optional, falls back on default value
			 */
			this.templateContextName = getInitParam(
					config,
					JandalFreeMarkerServlet.TEMPLATE_CONTEXT_NAME_INIT_PARAM_KEY,
					DEFAULT_TEMPLATE_CONTEXT_NAME, false);

			/*
			 * Title for browser window - optional, falls back on title which is
			 * a concatenation of the servlet and application class names
			 */
			this.windowTitle = getInitParam(config,
					JandalFreeMarkerServlet.WINDOW_TITLE_INIT_PARAM_KEY, this
							.getClass().getSimpleName()
							+ ": " + applicationClassName, false);

			this.appFactory = new ApplicationFactory(this.applicationClassName,
					serviceSet);

			/*
			 * Create FreeMarker configuration
			 */
			cfg = new Configuration();
			cfg.setTemplateUpdateDelay(0); // TODO: get from container property
			cfg
					.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
			cfg.setDefaultEncoding("ISO-8859-1");
			cfg.setOutputEncoding("UTF-8");
			cfg.setLocale(Locale.US); // TODO: get from container property
		} catch (Exception e) {
			throw new ServletException(e.getMessage(), e);
		}
	}

	private ServiceSet newServiceSet(final String className) throws Exception {
		try {
			final Class setClass = this.getClass().getClassLoader().loadClass(
					className);
			return (ServiceSet) setClass.newInstance();
		} catch (final Exception e) {
			throw new JandalFreeMarkerServletException(
					"Problem instantiating ServiceSet: " + e.getMessage(), e);
		}
	}

	/**
	 * Convenience method to get a parameter from servlet config. Can be set to
	 * complain if parameter mandatory and missing or empty, or substitute given
	 * fallback value if missing.
	 * 
	 * @param config
	 *            Servlet config
	 * @param key
	 *            Name of parameter to look for
	 * @param defaultVal
	 *            Fallback value for when mandatory is false and param not found
	 *            of has empty value
	 * @param mandatory
	 *            Will complain if not found/empty and this is true
	 * @return Parameter value
	 * @throws Exception
	 */
	private String getInitParam(ServletConfig config, String key,
			String defaultVal, boolean mandatory) throws Exception {
		String val = config.getInitParameter(key);
		if (val == null) {
			if (mandatory) {
				throw new JandalFreeMarkerServletException(
						"Initialisation parameter required: " + key);
			}
			return defaultVal;
		}
		val = val.trim();
		if (val.length() == 0) {
			if (mandatory) {
				throw new JandalFreeMarkerServletException(
						"Initialisation parameter is empty string: " + key);
			}
			return defaultVal;
		}
		return val;
	}

	private long getLongInitParam(ServletConfig config, String key,
			long defaultVal, boolean mandatory)
			throws JandalFreeMarkerServletException {
		String val = config.getInitParameter(key);
		if (val == null) {
			if (mandatory) {
				throw new JandalFreeMarkerServletException(
						"Initialisation parameter required: " + key);
			}
			return defaultVal;
		}
		val = val.trim();
		if (val.length() == 0) {
			if (mandatory) {
				throw new JandalFreeMarkerServletException(
						"Initialisation parameter is empty string: " + key);
			}
			return defaultVal;
		}
		try {
			return Long.parseLong(val);
		} catch (NumberFormatException nfe) {
			throw new JandalFreeMarkerServletException(
					"Initialisation parameter is not a long integer: " + key);
		}
	}

	private int getIntInitParam(ServletConfig config, String key,
			int defaultVal, boolean mandatory)
			throws JandalFreeMarkerServletException {
		String val = config.getInitParameter(key);
		if (val == null) {
			if (mandatory) {
				throw new JandalFreeMarkerServletException(
						"Initialisation parameter required: " + key);
			}
			return defaultVal;
		}
		val = val.trim();
		if (val.length() == 0) {
			if (mandatory) {
				throw new JandalFreeMarkerServletException(
						"Initialisation parameter is empty string: " + key);
			}
			return defaultVal;
		}
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException nfe) {
			throw new JandalFreeMarkerServletException(
					"Initialisation parameter is not an integer: " + key);
		}
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.service(request, response);
	}

	/**
	 * GET - open new session, get refreshed view, fire client event, or get
	 * download or resource.
	 */
	public void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		HttpSession session = request.getSession();
		ApplicationSession appSession = (ApplicationSession) session
				.getAttribute("appSession");

		try {
			RequestParams params = getRequestParams(request);
			if (appSession == null) {
				/*
				 * No application session found
				 */
				try {
					this.handleNewApplicationRequest(request, response, params);
				} catch (Exception e) {
					response.sendError(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
									.getMessage());
				}
			} else {
				/*
				 * Application session found
				 */

				if (params.requestType != null) {
					if (params.requestType.equals(HTTPRequestTypes.VIEW_EVENT)) {
						throw new SC_BAD_REQUEST_Exception(
								"View event request method should be POST, not GET");
					} else if (params.requestType
							.equals(HTTPRequestTypes.DOWNLOAD_REQUEST)) {
						handleDownloadRequest(appSession, params, request,
								response);
					} else if (params.requestType
							.equals(HTTPRequestTypes.RESOURCE_REQUEST)) {
						handleResourceRequest(appSession, params, request,
								response);
					} else {
						throw new Exception("Unknown request type: '"
								+ params.requestType + "'");
					}
				} else {
					/*
					 * No request - just refresh view
					 */
					this
							.handleRefreshViewRequest(appSession, request,
									response);
				}
			}
		} catch (Exception e) {
			/*
			 * Send XML error report to client
			 */
			errorResponse(e, request, response, appSession);
		}
	}

	/**
	 * Uses the Apache Commons File Upload library to get fields and uploads in
	 * HTTP request in a RequestParams.
	 * 
	 */
	private RequestParams getRequestParams(HttpServletRequest request)
			throws Exception {
		RequestParams params = new RequestParams();
		if (ServletFileUpload.isMultipartContent(request)) { // Multipart
			// content
			// with
			// uploads
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// factory.setRepository(new File("/home/lindsay"));
			ServletFileUpload upload = new ServletFileUpload(factory);
			// upload.setSizeMax(100000);
			List items = upload.parseRequest(request);
			for (Iterator i = items.iterator(); i.hasNext();) {
				FileItem item = (FileItem) i.next();
				String key = item.getFieldName();
				if (item.isFormField()) { // Form field parameter
					String value = item.getString();
					setParam(key, value, params);
				} else { // Uploaded file form parameter
					params.viewEventCustomFormParams.put(key,
							new FileItemUpload(item));
				}
			}
		} else { // Not multipart content
			for (Enumeration i = request.getParameterNames(); i
					.hasMoreElements();) {
				String key = ((String) i.nextElement()).trim();
				String value = ((String) request.getParameter(key)).trim();
				setParam(key, value, params);
			}
		}
		return params;
	}

	/**
	 * Called by getRequestParams to set RequestParams member to value of
	 * corresponding field in given Request. Any unrecognised fields are put in
	 * RequestParams.viewEventParams, in the assumption that it must be a
	 * view-event parameter. Any duplicate names in the view-event parameters
	 * causes an exception.
	 * 
	 * @param key
	 *            Name of request parameter
	 * @param value
	 *            Value of request parameter
	 * @param params
	 *            RequestParams we are putting value in
	 * @throws Exception
	 */
	private void setParam(String key, String value, RequestParams params)
			throws Exception {
		key = key.trim();
		if (key.length() == 0) {
			throw new SC_BAD_REQUEST_Exception(
					"Request has parameter with empty name");
		}
		if (key.equals(HTTPRequestParamKeys.PARAM_REQUEST_TYPE)) {
			params.requestType = value;
		} else if (key.equals(HTTPRequestParamKeys.PARAM_CONTROLLER_ID)) {
			params.controllerId = value;
		} else if (key.equals(HTTPRequestParamKeys.PARAM_CONTROLLER_SYNCH_KEY)) {
			params.controllerSynchKey = value;
		} else if (key.equals(HTTPRequestParamKeys.PARAM_VIEW_EVENT_NAME)) {
			params.viewEventName = value;
		} else if (key.equals(HTTPRequestParamKeys.PARAM_EVENT_ARGS)) {
			params.viewEventKeyValParamsStr = value;
		} else if (key.equals(HTTPRequestParamKeys.PARAM_RESOURCE_NAME)) {
			params.resourceName = value;
		} else if (key.equals(HTTPRequestParamKeys.PARAM_OUTPUT_NAME)) {
			params.outputName = value;
		} else if (key.equals(HTTPRequestParamKeys.PARAM_AJAX_ENABLED)) {
			params.ajaxEnabled = value;
		} else if (key.equals(HTTPRequestParamKeys.PARAM_EMBEDDED)) {
			params.embedded = value;
		} else {
			params.viewEventCustomFormParams.put(key, value);
		}
	}

	private void errorResponse(Exception e, HttpServletRequest request,
			HttpServletResponse response, ApplicationSession appSession)
			throws IOException {

		/*
		 * Application session exists - send back an XML error report and
		 * destroy the session
		 */
		if (e instanceof TimeoutException) {
			renderError("reset", e.getMessage(), response);
		} else if (e instanceof OutOfSynchException) {
			return;
			// renderError("out-of-synch", e.getMessage(), response);
		} else if (e instanceof ServerAdminBusyException) {
			renderError("alert", e.getMessage(), response);
			return;
		} else {
			e.printStackTrace();
			renderError("reset", e.getMessage(), response);
		}

		if (appSession != null) {
			appSession.destroy();

			/*
			 * Invalidate HTTP session
			 */
			request.getSession().invalidate();
		}
	}

	private void renderError(String code, String message,
			HttpServletResponse response) {
		try {
			PrintWriter out = new PrintWriter(response.getOutputStream());
			response.setContentType("xml");
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
			sb.append(getResponseXML(code, message));
			out.print(sb.toString());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getResponseXML(String code, String message) {
		StringBuffer sb = new StringBuffer();
		sb.append("<response>");
		sb.append("<code>");
		sb.append(code);
		sb.append("</code>");
		sb.append("<message>");
		sb.append(message);
		sb.append("</message>");
		sb.append("</response>");
		return sb.toString();
	}

	/**
	 * Asserts parameter not null - throws ParameterMissingException if null
	 * 
	 * @param paramKey
	 *            Name of parameter
	 * @param paramVal
	 *            Value to test
	 * 
	 */
	private void assertParamNotNull(String paramKey, String paramVal)
			throws Exception {
		if (paramVal == null) {
			throw new SC_BAD_REQUEST_Exception("Missing parameter: '"
					+ paramKey + "'");
		}
	}

	private ApplicationSession getExistingApplicationSession(
			HttpServletRequest request, RequestParams params) throws Exception {
		HttpSession session = request.getSession();
		ApplicationSession appSession = (ApplicationSession) session
				.getAttribute("appSession");
		if (appSession == null) {
			throw new TimeoutException("Application has timed out");
		}
		return appSession;
	}

	/**
	 * Asserts that continuation key parameter is not null and is consistent
	 * with application session. Throws ParameterMissingException if null,
	 * SC_GONE_Exception if not consistent.
	 * 
	 * 
	 */

	private void checkContinuationKeyParam(String paramVal,
			ControllerSession controllerSession) throws Exception {
		assertParamNotNull(HTTPRequestParamKeys.PARAM_CONTROLLER_SYNCH_KEY,
				paramVal);
		if (!paramVal.equals(controllerSession.getSynchKey())) {
			throw new OutOfSynchException("Page has expired");
		}
	}

	/**
	 * Handles request to open application session. This called by doGet if
	 * session ID is absent from request. Responds by destroying current session
	 * if existing, creating new one, and rendering initial view.
	 * 
	 */
	private void handleNewApplicationRequest(HttpServletRequest request,
			HttpServletResponse response, RequestParams params)
			throws Exception {
		/*
		 * If embedded parameter comes with request, then we will be embedding
		 * the application view in current browser page
		 */
		boolean embedded = (params.embedded != null && params.embedded
				.equalsIgnoreCase("true"));

		/*
		 * Make new application session
		 */
		ApplicationSession appSession = new ApplicationSession(getUrl(request),
				embedded, this.appFactory.newApplication(new Params(
						params.viewEventCustomFormParams)));

		/*
		 * Put in request session
		 */
		HttpSession session = request.getSession();
		session.setAttribute("appSession", appSession);
		/*
		 * Render initial view
		 */
		renderView(appSession, request, response);
	}

	public static String getUrl(HttpServletRequest req) {
		String scheme = req.getScheme(); // http
		String serverName = req.getServerName(); // hostname.com
		int serverPort = req.getServerPort(); // 80
		String contextPath = req.getContextPath(); // /mywebapp
		String servletPath = req.getServletPath(); // /servlet/MyServlet
		String pathInfo = req.getPathInfo(); // /a/b;c=123
		String queryString = req.getQueryString(); // d=789

		// Reconstruct original requesting URL
		String url = scheme + "://" + serverName + ":" + serverPort
				+ contextPath + servletPath;
		/*
		 * if (pathInfo != null) { url += pathInfo; } if (queryString != null) {
		 * url += "?"+queryString; }
		 */
		return url;
	}

	/**
	 * Handles a view event from client. Requires continuation key, controller
	 * path, event name, and string of key-value pairs. Fires event at
	 * application, responds by rendering new view to response stream.
	 * 
	 */
	private void handleViewEventRequest(ApplicationSession applicationSession,
			RequestParams params, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		/*
		 * Check controller path and view event name and string of key-value
		 * pairs
		 */

		assertParamNotNull(HTTPRequestParamKeys.PARAM_CONTROLLER_ID,
				params.controllerId);
		assertParamNotNull(HTTPRequestParamKeys.PARAM_VIEW_EVENT_NAME,
				params.viewEventName);
		assertParamNotNull(HTTPRequestParamKeys.PARAM_EVENT_ARGS,
				params.viewEventKeyValParamsStr);

		/*
		 * Parse key-value pairs into view event parameters
		 */
		parseViewEventArgs(params.viewEventKeyValParamsStr,
				params.viewEventCustomFormParams);

		Application application = applicationSession.getApplication();

		/*
		 * Find target controller
		 */
		ControllerSession controllerSession = applicationSession
				.getControllerSessionWithId(params.controllerId);
		if (controllerSession == null) {
			throw new JandalFreeMarkerServletException(
					"Controller not found: \"" + params.controllerId + "\"");
		}
		/*
		 * Check controller continuation key
		 */
		checkContinuationKeyParam(params.controllerSynchKey, controllerSession);

		/*
		 * Fire the view event
		 */
		controllerSession.getController().fireViewEvent(params.viewEventName,
				new Params(params.viewEventCustomFormParams));

		/*
		 * Render new view.
		 */
		if (params.ajaxEnabled != null) {
			if (params.ajaxEnabled.trim().equalsIgnoreCase("true")) {
				/*
				 * Client requests only views of modified controllers.
				 */
				renderViewAJAX(applicationSession, request, response);
				return;
			}
		}
		/*
		 * Client requests view of entire application.
		 */
		this.renderView(applicationSession, request, response);
	}

	private void parseViewEventArgs(String argsParam, Map viewEventCustomParams)
			throws Exception {
		StringTokenizer strtok = new StringTokenizer(argsParam, ";");
		while (strtok.hasMoreElements()) {
			String pair = (String) strtok.nextElement();
			int equalsIndex = pair.indexOf("=");
			String key = pair.substring(0, equalsIndex).trim();
			String value = pair.substring(equalsIndex + 1, pair.length())
					.trim();
			viewEventCustomParams.put(key, value);
		}
	}

	private void handleDownloadRequest(ApplicationSession applicationSession,
			RequestParams params, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		/*
		 * We want the controller path and output name
		 */
		assertParamNotNull(HTTPRequestParamKeys.PARAM_CONTROLLER_ID,
				params.controllerId);
		assertParamNotNull(HTTPRequestParamKeys.PARAM_OUTPUT_NAME,
				params.outputName);

		/*
		 * Get the controller
		 */
		ControllerSession controllerSession = applicationSession
				.getControllerSessionWithId(params.controllerId);
		if (controllerSession == null) {
			throw new JandalFreeMarkerServletException(
					"Controller not found: \"" + params.controllerId + "\"");
		}

		/*
		 * Check controller continuation key
		 * 
		 */
		checkContinuationKeyParam(params.controllerSynchKey, controllerSession);
		/*
		 * Get the output
		 */
		Object value;
		try {
			value = controllerSession.getController().getOutput(
					params.outputName);
		} catch (JandalCoreException e) {
			throw new JandalFreeMarkerServletException(
					"Problem reading Controller output:" + e.getMessage(), e);
		}

		/*
		 * Convert output to a Resource
		 */
		Resource resource = null;
		try {
			resource = (Resource) value;
		} catch (ClassCastException e) {
			throw new JandalFreeMarkerServletException("Controller "
					+ controllerSession.getController().getPath() + " output "
					+ params.outputName + " is not a "
					+ Resource.class.getName());
		}

		/*
		 * Stream the resource back to the client
		 */
		response.setContentType(resource.getMimeType());
		OutputStream outStream = response.getOutputStream();
		resource.write(outStream);
		outStream.flush();
	}

	private void handleResourceRequest(ApplicationSession applicationSession,
			RequestParams params, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		/*
		 * We want the controller path and resource name
		 */
		assertParamNotNull(HTTPRequestParamKeys.PARAM_CONTROLLER_ID,
				params.controllerId);
		assertParamNotNull(HTTPRequestParamKeys.PARAM_RESOURCE_NAME,
				params.resourceName);

		/*
		 * Get the controller
		 */
		ControllerSession controllerSession = applicationSession
				.getControllerSessionWithId(params.controllerId);
		if (controllerSession == null) {
			throw new JandalFreeMarkerServletException(
					"Controller not found: \"" + params.controllerId + "\"");
		}

		/*
		 * Check controller continuation key
		 * 
		 */
		checkContinuationKeyParam(params.controllerSynchKey, controllerSession);

		/*
		 * Get resource
		 */
		Resource resource = controllerSession.getController().getResource(
				params.resourceName);
		response.setContentType(resource.getMimeType());
		OutputStream out = response.getOutputStream();
		resource.write(out);
		out.flush();
	}

	private void handleRefreshViewRequest(
			ApplicationSession applicationSession, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		renderView(applicationSession, request, response);
	}

	private void renderViewAJAX(ApplicationSession applicationSession,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String output = renderApplicationAJAX(applicationSession);
		PrintWriter out = new PrintWriter(response.getOutputStream());
		response.setContentType("xml");
		out.print(output);
		out.flush();
	}

	private void renderView(ApplicationSession applicationSession,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String output = renderApplication(applicationSession);

		// System.out.println(this.hashCode() + " renderView: app = "
		// + this.applicationClassName + " output = " + output);
		PrintWriter out = new PrintWriter(response.getOutputStream());
		response.setContentType("text/html");

		/*
		 * Do not render header, footer and JavaScript if application view is
		 * embedded in a page
		 */
		boolean embedded = applicationSession.getEmbedded();
		if (!embedded) {
			renderHead(out);
		}
		out.print(output);
		if (!embedded) {
			renderTail(request, out);
		}
		out.flush();
	}

	private final void renderHead(final PrintWriter out)
			throws JandalFreeMarkerServletException {
		out.println("<HTML>");
		out.println("<HEAD><TITLE>");
		out.println(this.getClass().getSimpleName());
		out.println("</TITLE>");
		out.println("</HEAD>");
		out.println("<BODY>");
		out.println(getTextResourceFile("ajax.js"));
	}

	private final void renderFieldAssign(final String fieldName,
			final PrintWriter out) {
		out.print("document._guavaForm.");
		out.print(fieldName);
		out.print(".value=");
		out.print(fieldName);
		out.println(";");
	}

	private final void renderTail(final HttpServletRequest request,
			final PrintWriter out) {
		out.println("</BODY>");
		out.println("</HTML>");
	}

	private final void renderField(final String name, final PrintWriter out) {
		out.print("<input name=\"");
		out.print(name);
		out.println("\" type=\"hidden\" value=\"\"/>");
	}

	/**
	 * POST - client event, download request or resource request within existing
	 * session
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ApplicationSession applicationSession = null;

		try {
			RequestParams params = getRequestParams(request);
			/*
			 * Get application session
			 */
			applicationSession = getExistingApplicationSession(request, params);

			/*
			 * Handle post
			 * 
			 */
			if (params.requestType != null) {
				if (params.requestType.equals(HTTPRequestTypes.VIEW_EVENT)) {
					handleViewEventRequest(applicationSession, params, request,
							response);
				} else if (params.requestType
						.equals(HTTPRequestTypes.DOWNLOAD_REQUEST)) {
					handleDownloadRequest(applicationSession, params, request,
							response);
				} else if (params.requestType
						.equals(HTTPRequestTypes.RESOURCE_REQUEST)) {
					handleResourceRequest(applicationSession, params, request,
							response);
				} else {
					throw new Exception("Unknown POST request type: '"
							+ params.requestType + "'");
				}
			} else {
				/*
				 * POST always requires request parameter
				 */
				throw new JandalFreeMarkerServletException(
						"POST request parameter missing");
			}
		} catch (Exception e) {
			/*
			 * Might have application session if we're lucky
			 */
			this.errorResponse(e, request, response, applicationSession);
		}
	}

	private String renderApplication(ApplicationSession applicationSession)
			throws Exception {
		boolean divTags = true;
		String rootControllerId = applicationSession.getApplication()
				.getRootController().getId();
		ControllerSession rootControllerSession = applicationSession
				.getControllerSessionWithId(rootControllerId);
		return renderController(applicationSession, rootControllerSession,
				divTags);
	}

	private String renderApplicationAJAX(ApplicationSession applicationSession)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		sb.append("<update>");
		String rootControllerId = applicationSession.getApplication()
				.getRootController().getId();
		ControllerSession rootControllerSession = applicationSession
				.getControllerSessionWithId(rootControllerId);
		renderControllerAJAX(sb, applicationSession, rootControllerSession);
		sb.append("</update>");
		return sb.toString();
	}

	private void renderControllerAJAX(StringBuffer sb, ApplicationSession as,
			ControllerSession cs) throws Exception {
		if (!cs.getRendered()) {
			/*
			 * Given controller is fresh - render update for AJAX
			 * 
			 */
			sb.append("<controller>");
			sb.append("<id>" + cs.getController().getId() + "</id>");
			sb.append("<content>");
			sb.append("<![CDATA[");
			/*
			 * DIV tag not needed around views fragment for this controller
			 * subtree because we are inserting it all between existing DIV
			 * tags. Sub-controller views within the fragment will be wrapped by
			 * DIV tags though.
			 */
			boolean divTags = false;
			sb.append(renderController(as, cs, divTags));
			sb.append("]]>");
			sb.append("</content>");
			sb.append("</controller>");
			return;
		}

		/*
		 * Given controller not rendered - now try child controllers
		 */
		for (Iterator i = cs.getController().getChildControllers().iterator(); i
				.hasNext();) {
			Controller child = (Controller) i.next();
			ControllerSession childCs = as.getControllerSessionWithId(child
					.getId());
			renderControllerAJAX(sb, as, childCs);
		}
	}

	private String renderController(final ApplicationSession appSession,
			final ControllerSession controllerSession, boolean divTags)
			throws Exception {
		/*
		 * Wind on the continuation key
		 */
		controllerSession.nextSynchKey();

		ControllerTemplateModel templateTool = new ControllerTemplateModel(
				appSession, controllerSession, new RenderCallback() {

					public String renderController(
							ApplicationSession appSession,
							ControllerSession controllerSession)
							throws Exception {
						return JandalFreeMarkerServlet.this.renderController(
								appSession, controllerSession, true);
					}
				});
		controllerSession.setRendered(true);
		return renderControllerTemplate(appSession, controllerSession,
				templateTool, divTags);
	}

	private String renderControllerTemplate(
			final ApplicationSession applicationSession,
			ControllerSession controllerSession,
			ControllerTemplateModel templateModel, boolean divTags)
			throws Exception {

		Controller controller = controllerSession.getController();

		Object templateName;
		try {
			templateName = controller.getOutput(templateOutputName);
		} catch (JandalCoreException e1) {
			throw new JandalFreeMarkerServletException(
					"Problem reading Controller template name output:"
							+ e1.getMessage(), e1);
		}
		/*
		 * Convert output to string
		 */
		if (!(templateName instanceof String)) {
			throw new JandalFreeMarkerServletException("Controller "
					+ controller.getPath() + " template name output "
					+ templateOutputName + " not a String");
		}
		String templateNameStr = ((String) templateName).trim();

		/*
		 * Going to look for template bundled with Controller's class file
		 */
		cfg.setClassForTemplateLoading(controller.getClass(), "");

		try {
			/*
			 * Process template
			 */
			Template t = cfg.getTemplate(templateNameStr);
			StringWriter writer = new StringWriter();
			Map templateData = new HashMap();

			templateData.put(templateContextName, templateModel);
			t.process(templateData, writer);
			StringBuffer sb = new StringBuffer();
			if (divTags) {
				sb.append("\n<div id=\"" + controller.getId() + "\">\n");
			}
			sb.append(writer.toString());
			if (divTags) {
				sb.append("\n</div>\n");
			}
			return sb.toString();
		} catch (TemplateException e) {

			/*
			 * Syntax error in template
			 */
			throw new JandalFreeMarkerServletException(
					"Problem processing FreeMarker template '"
							+ templateNameStr + "' bundled with Controller "
							+ controller.getPath() + ": " + e.getMessage(), e);
		}
	}

	public String getTextResourceFile(String fileName)
			throws JandalFreeMarkerServletException {
		InputStream is = getClass().getResourceAsStream(fileName);
		if (is == null) {
			throw new JandalFreeMarkerServletException(
					"Resource file not found: " + fileName);
		}
		StringBuffer sb = new StringBuffer();
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			isr.close();
			is.close();
			return sb.toString();
		} catch (Exception e) {
			throw new JandalFreeMarkerServletException(e);
		}
	}

	private String templateOutputName;

	private String templateContextName;

	private String windowTitle;

	private int timeout;

	private int maxSessions;

	private String applicationClassName;

	private ServiceSet serviceSet;

	private Configuration cfg;

	private ApplicationFactory appFactory;

}
