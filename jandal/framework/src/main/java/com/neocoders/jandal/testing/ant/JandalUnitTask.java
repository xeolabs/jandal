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
package com.neocoders.jandal.testing.ant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.neocoders.jandal.core.*;

import com.neocoders.jandal.testing.*;
import com.neocoders.jandal.ui.freemarker.servlet.JandalFreeMarkerServlet;

/**
 * ANT task to run a {@link JandalTest} on a {@link Context}. You specify the
 * name of the {@link JandalTest} implementation, while for the
 * {@link Context} you either specify an implementation or a path to an XML
 * file from which the task loads the {@link Context}.
 * <p>
 * If you provide the task with a report file path, then the task will write the
 * test report to the file, otherwise it will log it to ANT's standard output.
 * 
 * @author lindsay
 * 
 */
public class JandalUnitTask extends Task {
	public JandalUnitTask() {
		super();
		this.testClassName = null;
		this.appClassName = null;
		this.serviceSetClassName = null;
		this.reportFilePath = null;
	}

	/**
	 * Specifies name of mandatory {@link JandalTest} implementation to run. The
	 * implementation must be available on the class path.
	 * 
	 */
	public void setTestClass(String testClassName) {
		this.testClassName = testClassName;
	}

	/**
	 * Specifies name of the mandatory {@link Application} implementation to run
	 * tests on. The implementation must be available on the class path.
	 * 
	 */
	public void setApplicationClass(String appClassName) {
		this.appClassName = appClassName;
	}

	/**
	 * Specifies name of the {@link ServiceSet} implementation that the
	 * {@link Application} under test might source {@link Service}s from.
	 * 
	 */
	public void setServiceSetClass(String serviceSetClassName) {
		this.serviceSetClassName = serviceSetClassName;
	}

	/**
	 * Specifies the optional file that the test report should be written to. If
	 * you don't specify this, the report is written to ANT standard output.
	 * 
	 * @param reportFilePath
	 *            Absolute path to report file.
	 */
	public void setReportFile(String reportFilePath) {
		this.reportFilePath = reportFilePath;
	}

	/**
	 * Runs the task.
	 */
	public void execute() throws BuildException {
		super.execute();

		/*
		 * Validate testClass param
		 */
		if (testClassName == null) {
			throw new BuildException("testClass not specified");
		}
		testClassName = testClassName.trim();
		if (testClassName.length() == 0) {
			throw new BuildException("testClass is empty string");
		}

		/*
		 * Validate applicationClass param
		 */
		if (appClassName == null) {
			throw new BuildException("applicationClass not specified");
		}
		appClassName = appClassName.trim();
		if (appClassName.length() == 0) {
			throw new BuildException("applicationClass is empty string");
		}

		/*
		 * Validate serviceSetClass param
		 */
		if (serviceSetClassName != null) {
			serviceSetClassName = serviceSetClassName.trim();
			if (serviceSetClassName.length() == 0) {
				throw new BuildException("serviceSetClassName is empty string");
			}
		}

		/*
		 * Validate report file path param
		 */
		if (reportFilePath != null) {
			reportFilePath = reportFilePath.trim();
			if (reportFilePath.length() == 0) {
				throw new BuildException("reportFilePath is empty string");
			}
		}

		/*
		 * Name of ServiceSet implementation - optional, servlet will use a
		 * default empty ServiceSet if none specified
		 */
		ServiceSet serviceSet = null;
		if (serviceSetClassName == null) {
			serviceSet = new ServiceSet();
		} else {
			try {
				serviceSet = (ServiceSet) getInstance(serviceSetClassName);
			} catch (ClassCastException cce) {
				throw new BuildException("Failed to instantiate "
						+ serviceSetClassName
						+ " - it is not an implementation of "
						+ ServiceSet.class.getName());
			}
		}

		/*
		 * Create factory
		 */
		ApplicationFactory appFactory = new ApplicationFactory(
				this.appClassName, serviceSet);

		/*
		 * Create tests
		 * 
		 */
		JandalTest jandalTest = null;
		try {
			jandalTest = (JandalTest) getInstance(testClassName);
		} catch (ClassCastException cce) {
			throw new BuildException("Failed to instantiate " + testClassName
					+ " - it is not an implementation of "
					+ JandalTest.class.getName());
		}

		/*
		 * Set up logger
		 */
		MyLogger myLogger = new MyLogger();

		/*
		 * Run tests on the container, logging to the logger
		 */
		try {
			jandalTest.run(appFactory, myLogger);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		/*
		 * Dump the logger to the report file
		 */
		String reportTxt = myLogger.getReport();
		if (this.reportFilePath != null) {
			log("Executing tests, writing report to " + reportFilePath + "\n\n");
			this.writeReportFile(reportTxt);
		} else {
			log("Executing tests, trace follows\n\n");
			this.log(reportTxt);
		}

		/*
		 * Spit the dummy if we logged any exceptions during our tests
		 */
		if (myLogger.getNumExceptions() == 1) {
			throw new BuildException("Tests failed - "
					+ "there was 1 error logged - see report");
		} else if (myLogger.getNumExceptions() > 0) {
			throw new BuildException("Tests failed - there were "
					+ myLogger.getNumExceptions()
					+ " errors logged - see report");
		}
	}

	private static class MyLogger implements Logger {
		public MyLogger() {
			this.countExceptions = 0;
			sb = new StringBuffer();
		}

		public void logTrace(String message) {
			sb.append(message);
			sb.append("\n");
		}

		public void logException(Exception e) {
			countExceptions++;
		}

		public String getReport() {
			return sb.toString();
		}

		public int getNumExceptions() {
			return countExceptions;
		}

		private int countExceptions;

		private StringBuffer sb;
	}

	private void writeReportFile(String str) {
		if (reportFilePath != null) {
			File file = new File(reportFilePath);
			if (file.exists()) {
				file.delete();
			}
			try {
				file.createNewFile();
				FileWriter writer = new FileWriter(file);
				PrintWriter pw = new PrintWriter(writer);
				pw.println(str);
				pw.close();
			} catch (IOException e) {
				throw new BuildException("Failed to create report file '"
						+ reportFilePath + "': " + e.getMessage());
			}
		}
	}

	private Object getInstance(String className) throws BuildException {
		final InstantiationThread thread = new InstantiationThread(Thread
				.currentThread(), className);
		thread.start();
		synchronized (this) {
			try {
				Thread.sleep(10000L); // TODO: Config param for this
				throw new BuildException("Failed to instantiate " + className
						+ " - constructor timed out - instantiation exceeded "
						+ 10000 + " milliseconds");
			} catch (InterruptedException e) {
			}
		}
		final Exception exception = thread.getException();
		if (exception != null) {
			throw new BuildException("Failed to instantiate " + className
					+ " - " + exception.toString(), exception);
		}

		return thread.getInstance();

	}

	/*
	 * Thread in which object is instantiated, so that we can check for timeout
	 * in parent thread
	 */
	private static class InstantiationThread extends Thread {
		public InstantiationThread(final Thread parent, final String className) {
			super();
			this.parent = parent;
			this.className = className;
			this.instance = null;
			this.exception = null;
		}

		public void run() {
			try {
				final ClassLoader loader = this.getClass().getClassLoader();
				final Class c = loader.loadClass(className);
				this.instance = c.newInstance();
			} catch (final Exception e) {
				trimStackTrace(e);
				this.exception = e;
			}
			parent.interrupt();
		}

		/**
		 * Trims off the last stack trace element in order to hide this class
		 * from the stack trace. End effect is improved framework transparency.
		 * 
		 * @param e
		 */
		private void trimStackTrace(Exception e) {
			StackTraceElement[] trace = e.getStackTrace();
			StackTraceElement[] trace2 = new StackTraceElement[trace.length - 1];
			for (int i = 0; i < trace.length - 1; i++) {
				trace2[i] = trace[i];
			}
			e.setStackTrace(trace2);
		}

		public Object getInstance() {
			return instance;
		}

		public Exception getException() {
			return exception;
		}

		private Thread parent;

		private String className;

		private Object instance;

		private Exception exception;
	}

	private String reportFilePath;

	private String testClassName;

	private String appClassName;

	private String serviceSetClassName;

}
