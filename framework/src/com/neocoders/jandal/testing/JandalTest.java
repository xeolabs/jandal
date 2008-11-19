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

import java.util.Iterator;

import java.util.Set;

import com.neocoders.jandal.core.*;

/*
 * 
 */
public abstract class JandalTest {

	public JandalTest() {
		init("", true);
	}

	public JandalTest(final String localPath) throws JandalUnitException {
		Utils.validateNameOkEmpty(localPath, "localPath");
		init(localPath, true);
	}

	public JandalTest(boolean recover) {
		init("", recover);
	}

	public JandalTest(final String localPath, boolean recover)
			throws JandalUnitException {
		Utils.validateNameOkEmpty(localPath, "baseControllerPath");
		init(localPath, recover);
	}

	private void init(final String localPath, boolean recover) {
		this.localPath = localPath;
		this.absPath = "";
		context = null;
		indent = 0;
		this.recover = recover;
	}

	/*
	 * Runs a test suite on an application. We pass in an application factory
	 * rather than an application to allow the testing framework to be extended
	 * in future to testing mutliple concurrent application instances.
	 */
	public final void run(final ApplicationFactory appFactory, Logger logger) {
		this.context = new Context(appFactory, logger);
		internalLog("");
		try {
			doRun();
		} catch (Exception e) {
			/*
			 * Exceptions logged
			 */
			logStackTrace(e);
			this.internalLog("");
			log(e);
		}
	}

	final void setup(final Context context, final String parentAbsPath,
			final int indent) {
		this.context = context;
		StringBuffer sb = new StringBuffer();
		sb.append(parentAbsPath);
		if (this.localPath.length() > 0) {
			if (sb.length() > 0) {
				sb.append(".");
			}
			sb.append(this.localPath);
		}
		this.absPath = sb.toString();
		this.indent = indent;
	}

	final boolean getRecover() {
		return this.recover;
	}

	protected void runChildTest(final JandalTest test) throws Exception {
		test.setup(context, absPath, indent + 1);
		internalLog("");
		test.doRun();
	}

	private void doRun() throws Exception {
		// indent--;
		internalLog(JandalTest.class.getSimpleName() + " {");
		indent++;
		if (absPath.length() > 0) {
			internalLog("baseControllerPath: " + absPath + "");
		}
		internalLog("");

		/*
		 * 
		 */

		try {
			onRun();
		} catch (Exception e) {

			if (!recover) {
				throw e;
			}
			logStackTrace(e);
			this.internalLog("");
			log(e);
			log("Recovering and continuing with parent "
					+ JandalTest.class.getSimpleName());
		}
		/*
		 * 
		 */
		indent--;
		internalLog("}");
		// indent++;
	}

	private boolean logStackTrace(Throwable ex, boolean displayAll) {
		if (null == ex) {
			this.internalLog("Null stack trace reference! Bailing...");
			return false;
		}
		this.internalLog("The stack according to printStackTrace():\n");
		ex.printStackTrace();
		this.internalLog("");
		StackTraceElement[] stackElements = ex.getStackTrace();
		if (displayAll) {
			this.internalLog("The " + stackElements.length + " element"
					+ ((stackElements.length == 1) ? "" : "s")
					+ " of the stack trace:\n");
		} else {
			this.internalLog("The top element of a " + stackElements.length
					+ " element stack trace:\n");
		}

		for (int lcv = 0; lcv < stackElements.length; lcv++) {
			this.internalLog("Filename: " + stackElements[lcv].getFileName());
			this.internalLog("Line number: "
					+ stackElements[lcv].getLineNumber());
			String className = stackElements[lcv].getClassName();
			String packageName = extractPackageName(className);
			String simpleClassName = extractSimpleClassName(className);
			this.internalLog("Package name: "
					+ ("".equals(packageName) ? "[default package]"
							: packageName));
			this.internalLog("Full class name: " + className);
			this.internalLog("Simple class name: " + simpleClassName);
			this.internalLog("Unmunged class name: "
					+ unmungeSimpleClassName(simpleClassName));
			this.internalLog("Direct class name: "
					+ extractDirectClassName(simpleClassName));
			this.internalLog("Method name: "
					+ stackElements[lcv].getMethodName());
			this.internalLog("Native method?: "
					+ stackElements[lcv].isNativeMethod());
			this.internalLog("toString(): " + stackElements[lcv].toString());
			this.internalLog("");
			if (!displayAll)
				return true;
		}
		this.internalLog("");
		return true;
	} // End of displayStackTraceInformation().

	private static String extractPackageName(String fullClassName) {
		if ((null == fullClassName) || ("".equals(fullClassName)))
			return "";
		int lastDot = fullClassName.lastIndexOf('.');
		if (0 >= lastDot)
			return "";

		return fullClassName.substring(0, lastDot);
	}

	public static String extractSimpleClassName(String fullClassName) {
		if ((null == fullClassName) || ("".equals(fullClassName)))
			return "";
		int lastDot = fullClassName.lastIndexOf('.');
		if (0 > lastDot)
			return fullClassName;

		return fullClassName.substring(++lastDot);
	}

	public static String extractDirectClassName(String simpleClassName) {
		if ((null == simpleClassName) || ("".equals(simpleClassName)))
			return "";
		int lastSign = simpleClassName.lastIndexOf('$');
		if (0 > lastSign)
			return simpleClassName;
		return simpleClassName.substring(++lastSign);
	}

	public static String unmungeSimpleClassName(String simpleClassName) {
		if ((null == simpleClassName) || ("".equals(simpleClassName)))
			return "";

		// Nested classes are set apart from top-level classes by using
		// the dollar sign '$' instead of a period '.' as the separator
		// between them and the top-level class that they sit
		// underneath. Let's undo that.
		return simpleClassName.replace('$', '.');
	}

	private void logStackTrace(Throwable t) {
		internalLog(t.getClass().getName() + ": " + t.getMessage());
		StackTraceElement[] stackTrace = t.getStackTrace();
		for (int i = 0; i < stackTrace.length; i++) {
			internalLog(stackTrace[i].toString());
		}
	}

	/*
	 * private void logStackTrace(Throwable e) { int c = 0; Throwable t = e;
	 * while (t != null) { String message = t.getMessage();
	 * internalLog(e.getClass().getName() + " - " + t.getMessage());
	 * StackTraceElement[] stackTrace = t.getStackTrace(); for (int i = 0; i <
	 * stackTrace.length; i++) { internalLog("" + stackTrace[i].getLineNumber() + " - " +
	 * stackTrace[i].toString()); } t = t.getCause(); this.indent++; } while (c >
	 * 0) { indent--; c--; } }
	 */
	protected abstract void onRun() throws Exception;

	protected void startApplication() throws JandalUnitException,
			JandalCoreException {
		this.startApplication(new Params());
	}

	protected void startApplication(final Params params)
			throws JandalUnitException, JandalCoreException {

		/*
		 * Do logging
		 */
		internalLog("startApplication");
		internalLog("");

		/*
		 * Check is params is null
		 */
		if (params == null) {
			throw new IllegalArgumentException("Params is null");
		}

		/*
		 * Check if Application exists already
		 */
		if (context.application != null) {
			throw new JandalUnitException("Application already exists");
		}
		/*
		 * Attempt instantiation
		 */
		context.application = context.appFactory.newApplication(params);
		context.application.start();
	}

	protected void destroyApplication() throws JandalUnitException {
		internalLog("destroyApplication");
		internalLog("");
		if (context.application == null) {
			throw new JandalUnitException("Application does not exist");
		}
		context.application.destroy();
		context.application = null;
	}

	protected void restartApplication() throws JandalCoreException,
			JandalUnitException {
		internalLog("restartApplication");
		internalLog("");
		getApplication().restart();
	}

	protected String getState(String relControllerPath)
			throws JandalUnitException {
		relControllerPath = Utils.validateNameOkEmpty(relControllerPath,
				"relControllerPath");
		internalLog("getState");
		if (relControllerPath.length() > 0) {
			internalLog("    relControllerPath = " + relControllerPath);
		}
		final Controller controller = getController(relControllerPath);
		String stateName = null;
		try {
			stateName = controller.getCurrentStateName();
		} catch (JandalCoreException e) {
			/*
			 * We know that the controller is running
			 */
			throw new RuntimeException(e);
		}
		if (stateName == null) {
			throw new JandalUnitException("Controller \""
					+ getAbsPath(relControllerPath) + "\" has no States");
		}
		return stateName;
	}

	protected final Resource getResource(String relControllerPath,
			String fileName) throws JandalUnitException, JandalCoreException {
		relControllerPath = Utils.validateNameOkEmpty(relControllerPath,
				"relControllerPath");
		internalLog("getResource");
		if (relControllerPath.length() > 0) {
			internalLog("    relControllerPath = " + relControllerPath);
		}
		internalLog("    fileName = " + fileName);
		internalLog("");
		final Controller controller = getController(relControllerPath);
		return controller.getResource(fileName);
	}

	protected final String getProperty(String relControllerPath, String locator)
			throws JandalUnitException, JandalCoreException {
		relControllerPath = Utils.validateNameOkEmpty(relControllerPath,
				"relControllerPath");
		internalLog("getProperty");
		if (relControllerPath.length() > 0) {
			internalLog("    relControllerPath = " + relControllerPath);
		}
		internalLog("    locator = " + locator);
		internalLog("");
		final Controller controller = getController(relControllerPath);
		return controller.getProperty(locator);
	}

	protected final PropertySet getProperties(String relControllerPath,
			String fileName) throws JandalUnitException, JandalCoreException {
		relControllerPath = Utils.validateNameOkEmpty(relControllerPath,
				"relControllerPath");
		internalLog("getProperty");
		if (relControllerPath.length() > 0) {
			internalLog("    relControllerPath = " + relControllerPath);
		}
		internalLog("    fileName = " + fileName);
		internalLog("");
		final Controller controller = getController(relControllerPath);
		return controller.getProperties(fileName);
	}

	protected void assertState(String relControllerPath,
			String expectedStateName) throws JandalUnitException {
		expectedStateName = Utils.validateName(expectedStateName,
				"expectedStateName");
		/*
		 * 
		 */
		internalLog("assertState");
		if (relControllerPath.length() > 0) {
			internalLog("    relControllerPath = " + relControllerPath);
		}
		internalLog("    state = " + expectedStateName);
		internalLog("");
		/*
		 * 
		 */
		final Controller controller = getController(relControllerPath);
		String stateName = null;
		try {
			stateName = controller.getCurrentStateName();
		} catch (JandalCoreException e) {
			/*
			 * We know that the controller is running
			 */
			throw new RuntimeException(e);
		}
		if (stateName == null) {
			throw new JandalUnitException("Controller \""
					+ getAbsPath(relControllerPath) + "\" has no States");
		}
		if (!stateName.equals(expectedStateName)) {
			throw new JandalUnitException("Controller \""
					+ getAbsPath(relControllerPath)
					+ "\" is not in expected \"" + expectedStateName
					+ "\" State - active State is \"" + stateName + "\"");
		}
	}

	protected void assertState(final String stateName)
			throws JandalUnitException {
		this.assertState("", stateName);
	}

	protected Object getOutput(final String relControllerPath,
			final String outputName) throws JandalCoreException,
			JandalUnitException {
		/*
		 * 
		 */
		Utils.validateNameOkEmpty(relControllerPath, "relControllerPath");
		Utils.validateNameOkEmpty(outputName, "outputName");
		/*
		 * 
		 */
		if (relControllerPath.length() == 0) {
			internalLog("getOutput(outputName=\"" + outputName + "\")");
		} else {
			internalLog("getOutput(relControllerPath=\"" + relControllerPath
					+ "\", outputName=\"" + outputName + "\")");
		}
		internalLog("");
		/*
		 * 
		 */
		return getController(relControllerPath).getOutput(outputName);
	}

	protected Object getOutput(final String name) throws JandalCoreException,
			JandalUnitException {
		return this.getOutput("", name);
	}

	protected void fireViewEvent(final String relControllerPath,
			final String eventName, final Params params)
			throws JandalCoreException, JandalUnitException {
		/*
		 * 
		 */
		Utils.validateNameOkEmpty(relControllerPath, "relControllerPath");
		Utils.validateNameOkEmpty(eventName, "eventName");
		if (params == null) {
			throw new JandalUnitException("params is null");
		}

		/*
		 * 
		 */
		internalLog("");
		internalLog("fireViewEvent");
		if (relControllerPath.length() > 0) {
			internalLog("    " + relControllerPath);
		}
		internalLog("    eventName = " + eventName);
		final Set paramNames = params.getNames();
		if (!paramNames.isEmpty()) {
			internalLog("    parameters:");
			for (final Iterator i = params.getNames().iterator(); i.hasNext();) {
				final String name = (String) i.next();
				internalLog("        " + name + " = " + params.get(name));
			}
		}
		internalLog("");
		/*
		 * 
		 */
		getController(relControllerPath).fireViewEvent(eventName, params);
	}

	protected final void fireViewEvent(final String eventName,
			final Params params) throws JandalCoreException,
			JandalUnitException {
		this.fireViewEvent("", eventName, params);
	}

	protected final void fireViewEvent(final String eventName)
			throws JandalCoreException, JandalUnitException {
		this.fireViewEvent("", eventName, new Params());
	}

	protected final void checkTemplate(String outputName,
			String expectedFileName) throws Exception {
		this.checkTemplate("", outputName, expectedFileName);
	}

	protected final void checkTemplate(final String relControllerPath,
			String outputName, String expectedFileName) throws Exception {
		/*
		 * Check arguments
		 */
		Utils.validateNameOkEmpty(relControllerPath, "relControllerPath");
		Utils.validateNameOkEmpty(outputName, "outputName");

		/*
		 * Log message
		 */
		internalLog("checkTemplate");
		if (relControllerPath.length() > 0) {
			internalLog("    relControllerPath = " + relControllerPath);
		}
		internalLog("    outputName = " + outputName);
		internalLog("    expectedFileName = " + expectedFileName);
		internalLog("");

		/*
		 * Get the output
		 */
		Object value = getController(relControllerPath).getOutput(outputName);

		/*
		 * Convert output to string
		 */
		String templateFileName = null;
		try {
			templateFileName = (String) value;
		} catch (ClassCastException cce) {
			throw new JandalUnitException(
					"Output expected to be a String type, but it is a "
							+ value.getClass().getName());
		}
		/*
		 * Open and close template file stream just to see if there are any
		 * problems
		 */
		// this.getResource(relControllerPath, templateFileName).write(new
		// StringWriter());
	}

	private Controller getController(final String relControllerPath)
			throws JandalUnitException {
		/*
		 * 
		 */
		Utils.validateNameOkEmpty(relControllerPath, "relControllerPath");
		/*
		 * 
		 */
		final Application application = getApplication();
		Controller controller = null;
		final String absPath = getAbsPath(relControllerPath);
		try {
			controller = application.getControllerOnPath(absPath);
		} catch (final JandalCoreException e) {
			throw new JandalUnitException(
					"Error getting controller on path : \"" + absPath + "\": "
							+ e.getMessage(), e);
		}
		if (controller == null) {
			throw new JandalUnitException("Can't find controller on path \""
					+ absPath + "\"");
		}
		return controller;
	}

	private Application getApplication() throws JandalUnitException {
		if (context.application == null) {
			throw new JandalUnitException("Application does not exist");
		}
		return context.application;
	}

	private String getAbsPath(String relControllerPath) {
		StringBuffer sb = new StringBuffer();
		relControllerPath = relControllerPath.trim();
		if (relControllerPath.length() == 0) {
			return this.absPath;
		} else if (absPath.length() == 0) {
			return absPath;
		} else {
			return absPath + "." + relControllerPath;
		}
	}

	protected void log(final String message) {
		internalLog("//// " + message);
	}

	private void internalLog(final String message) {
		context.logger.logTrace(getIndent() + message);
	}

	private String getIndent() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			sb.append("    ");
		}
		return sb.toString();
	}

	private void log(Exception e) {
		context.logger.logException(e);
	}

	/*
	 * public static void log(Level level, String message) { Thread
	 * currentThread = Thread.currentThread(); StackTraceElement[] sts =
	 * currentThread.getStackTrace(); if (sts != null && sts.length > 3) {
	 * StackTraceElement ste = sts[3]; String fileName = ste.getFileName();
	 * String className = ste.getClassName(); String methodName =
	 * ste.getMethodName(); int lineNum = ste.getLineNumber(); LogRecord record =
	 * new LogRecord(level, message + "At line " + lineNum + " in file " +
	 * fileName); record.setSourceClassName(className); record.setThreadID((int)
	 * currentThread.getId()); record.setSourceMethodName(methodName);
	 * logger_.log(record); } else { logger_.log(level, message); } }
	 */

	private int indent;

	private String absPath;

	private String localPath;

	private Context context;

	private boolean recover;
}
