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
package com.neocoders.jandal.core;

import org.apache.tools.ant.BuildException;

import com.neocoders.jandal.core.JandalCoreException;
import com.neocoders.jandal.core.Params;
import com.neocoders.jandal.core.ServiceSet;
import com.neocoders.jandal.testing.JandalTest;

public class ApplicationFactory {

	public ApplicationFactory(String appClassName, ServiceSet serviceSet) {
		this.appClassName = appClassName;
		this.serviceSet = serviceSet;
	}

	public String getName() {
		return this.name;
	}

	public String getApplicationClassName() {
		return this.appClassName;
	}

	public synchronized Application newApplication() throws JandalCoreException {
		return this.newApplication(new Params());
	}

	public synchronized Application newApplication(Params params)
			throws JandalCoreException {
		Class cls = null;
		try {
			Application app = (Application) getInstance(appClassName);
			app.init(this.serviceSet, params);
			return app;
		} catch (ClassCastException cce) {
			throw new BuildException("Failed to instantiate " + appClassName
					+ " - it is not an implementation of "
					+ JandalTest.class.getName());
		}
	}

	private Object getInstance(String className) throws JandalCoreException {
		final InstantiationThread thread = new InstantiationThread(Thread
				.currentThread(), className);
		thread.start();
		synchronized (this) {
			try {
				Thread.sleep(10000L); // TODO: Config param for this
				throw new JandalCoreException("Failed to instantiate "
						+ className
						+ " - constructor timed out - instantiation exceeded "
						+ 10000 + " milliseconds");
			} catch (InterruptedException e) {
			}
		}
		final Exception exception = thread.getException();
		if (exception != null) {
			throw new JandalCoreException("Failed to instantiate " + className
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

	private String name;

	private String appClassName;

	private ServiceSet serviceSet;

}