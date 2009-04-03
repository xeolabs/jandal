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

/**
 * Base class for Jandal applications. In your sub-classes, you must implement
 * the {@link onStart()} method, which is called from within Application's
 * constructors to immediately start it up on instantiation.
 * 
 * <p/> Application has a constructor through which it may configured with a
 * {@link ServiceSet} and parameters, along with variations of the constructor,
 * in which it supplies its own a default {@link ServiceSet} and/or empty
 * parameters.
 * 
 * 
 * <p/><b>Implementing onStart()</b><p/>
 * 
 * In your implementation of the {@link onStart()} method you must add a root
 * {@link Controller} to your Application. The root {@link Controller} is
 * mandatory because, since an Application is to start the moment it is created,
 * it cannot start if it doesn't have one of those.
 * 
 * 
 * <p/>Here's a plain vanilla example in which we define an application with a
 * non-parameterised root {@link Controller}:
 * 
 * <pre>
 * class MyApp extends Application {
 * 
 * 	public void onStart() throws ExecutionException {
 * 
 * 		setRootController(new MyController());
 * 	}
 * }
 * </pre>
 * 
 * <p/> Here we define an application with a root {@link Controller} thats takes
 * a single "description" parameter:
 * 
 * <pre>
 * class MyApp extends Application {
 * 
 * 	public void onStart() throws ExecutionException {
 *                                                    
 *                                                    	setRootController(new MyNextController(), 
 *                                                    		new Params().add(&quot;description&quot;, &quot;My Awesome Application&quot;);
 *                                                        }
 * }
 * </pre>
 * 
 * <p/> Here we define another application with a parameterised root
 * {@link Controller}. This time, the {@link Controller}'s parameter is taken
 * from an "appDescription" Application parameter, which was given (hopefully)
 * plugged into the Application's when it was instantiated:
 * 
 * <pre>
 * class MyApp extends Application {
 * 
 * 	public void onStart() throws ExecutionException {
 *                                                    
 *                                                    	setRootController(new MyLatestController(), 
 *                                                    		new Params().add(&quot;description&quot;, getParam(&quot;appDescription&quot;);
 *                                                        }
 * }
 * </pre>
 * 
 * As soon as you add the root {@link Controller} the Application calls
 * {@link Controller#onStart()} to start it up. Read the comments on the
 * {@link Controller} class for information about that.
 * 
 * <p/><b>Getting Controllers by name-path</b><p/> Although not shown in the
 * examples above, {@link Controller}s have names, and you can obtain a
 * {@link Controller} from an an Application's tree of currently running
 * {@link Controller}s with the {@link #getControllerOnPath(String path)}
 * method, which takes a path of names pointing down to the one you want. Note
 * that the root {@link Controller} is always running.
 * 
 * <p/><b>Restarting</b><p/> You can restart an {@link Application} at any
 * time with {@link #restart()}, which resets the running {@link Controller}
 * tree; the root {@link Controller} is reset to its initial {@link State}, and
 * child-{@link Controller}s of that {@link State} will be running, each in
 * their inital {@link State}s, and so on.
 * 
 * <p/><b>Extra notes</b><p/> <p/>There is no listener implemented as yet for
 * activities within an Application, as this is a <b><i>pull-MVC</i></b>
 * framework, designed for use with a servlet (IE. JandalFreeMarkerServlet) that
 * will inspect the {@link Controller} tree on each client request to see what
 * {@link Controller}s are running, what their current {@link State}s are, and
 * pull data from their outputs.
 * 
 * @author lindsay
 * 
 */
public abstract class Application {

	void init(ServiceSet serviceSet, Params params) {
		if (params == null) {
			throw new IllegalArgumentException("Params is null");
		}
		this.exeContext = new ApplicationExeContext(serviceSet, this);
		this.params = params;
		this.active = false;
	}

	/**
	 * Returns ID unique to this instance.
	 * 
	 * @return
	 */
	public final String getId() {
		return "" + this.hashCode();
	}

	/**
	 * Starts this application. Throws {@link JandalCoreException} if something
	 * goes amiss while starting.
	 * 
	 * @throws JandalCoreException
	 *             Problem originating within your implementation of
	 *             {@link #onStart()}.
	 * @throws JandalCoreException
	 *             No root {@link Controller} was added within your
	 *             implementation of {@link #onStart()}.
	 */
	public final void start() throws JandalCoreException {
		if (active) {
			this.exeContext.applicationStopping();
			this.rootController.destroy();
			this.active = false;
			this.exeContext.applicationStarting();
			this.onStart();
			if (this.rootController == null) {
				throw new JandalCoreException(
						"Application could not start because it has no root Controller");
			}
			this.active = true;
		} else {
			this.exeContext.applicationStarting();
			this.onStart();
			if (this.rootController == null) {
				throw new JandalCoreException(
						"Application could not start because it has no root Controller");
			}
			this.active = true;
		}
	}

	/**
	 * Returns the value of one of the parameters given to this application when
	 * it was started. Throws exception if the parameter could not be found - it
	 * is in the application's contract with the caller that they both know what
	 * parameters should exist.
	 * 
	 * @param name
	 *            Name of parameter.
	 * @return Value of parameter.
	 * @throws JandalCoreException
	 *             Name not valid
	 * @throws JandalCoreException
	 *             Parameter not found
	 */
	protected final Object getParam(String name) throws JandalCoreException {
		name = Utils.validateName(name, "Application parameter name");
		Object value = params.get(name);
		if (value == null) {
			throw new JandalCoreException("Application parameter not found: \""
					+ name + "\"");
		}
		return value;
	}

	/**
	 * Restarts this application. Throws {@link JandalCoreException} if
	 * something goes amiss while restarting.
	 * 
	 * @throws JandalCoreException
	 */
	public final void restart() throws JandalCoreException {
		this.assertActive();
		this.exeContext.applicationStopping();
		this.rootController.destroy();
		this.active = false;
		this.exeContext.applicationStarting();
		this.onStart();
		if (this.rootController == null) {
			throw new JandalCoreException(
					"Application could not start because it has no root Controller");
		}
		active = true;
	}

	/**
	 * Implement this to do things when this application starts, IE. create the
	 * root {@link Controller} with {@link setRootController(Controller)}.
	 * 
	 * @throws JandalCoreException
	 */
	protected abstract void onStart() throws JandalCoreException;

	/**
	 * Sets this application's root {@link Controller}, providing it with
	 * parameters and replacing any root that already exists.
	 * 
	 * @throws JandalCoreException
	 * 
	 */
	protected final void setRootController(Controller controller, Params params)
			throws JandalCoreException {
		if (controller == null) {
			throw new JandalCoreException("Root controller cannot be null");
		}
		this.rootController = controller;
		this.rootController.start(new ServiceCache(this.getId(),
				this.exeContext.getServiceSet()), exeContext, this, params);
	}

	/**
	 * Sets this application's root {@link Controller}, replacing any root that
	 * already exists.
	 * 
	 * @throws JandalCoreException
	 * 
	 */
	protected final void setRootController(Controller controller)
			throws JandalCoreException {
		this.setRootController(controller, new Params());
	}

	/**
	 * Returns this application's root {@link Controller}. The Application must
	 * be running otherwise an exception will be thrown.
	 * 
	 * @throws JandalCoreException
	 */
	public final Controller getRootController() throws JandalCoreException {
		assertActive();
		return rootController;
	}

	/**
	 * Returns the {@link Controller} located on the given path. The
	 * {@link Controller} is found only if it is in the tree of currently
	 * running {@link Controller}s, otherwise this method returns null. The
	 * Application must be running otherwise an exception will be thrown.
	 * 
	 * @param path
	 *            Absolute path to {@link Controller} of the form
	 *            "alpha.baker.charlie".
	 * @throws JandalCoreException
	 *             Application not running
	 */
	public final Controller getControllerOnPath(String path)
			throws JandalCoreException {
		assertActive();
		/*
		 * Controllers generate their paths and register themselves with the
		 * execution context when they start, then deregister themselves when
		 * they are destroyed.
		 */
		return this.exeContext.getControllerOnPath(path);
	}

	/**
	 * Returns the {@link Controller} with given ID. The {@link Controller} is
	 * found only if it is in the tree of currently running {@link Controller}s,
	 * otherwise this method returns null. IDs are transient since they are
	 * generated from hashcodes. Therefore, the ID of a {@link Controller} at a
	 * given location within the tree will change if that {@link Controller} is
	 * destroyed and recreated. The Application must be running otherwise an
	 * exception will be thrown.
	 * 
	 * @param controllerId
	 *            ID of {@link Controller} .
	 * @throws JandalCoreException
	 *             Application not running
	 */
	public final Controller getControllerWithId(String controllerId)
			throws JandalCoreException {
		assertActive();
		return this.exeContext.getControllerWithId(controllerId);
	}

	private void assertActive() throws JandalCoreException {
		if (!active) {
			throw new JandalCoreException("Application not running");
		}
	}

	/**
	 * Stops this application.
	 */
	public final void stop() {
		if (active) {
			this.exeContext.applicationStopping();
			this.rootController.destroy();
			this.active = false;
		}
	}

	/**
	 * Stops and destroys this application.
	 */
	public final void destroy() {
		if (active) {
			this.exeContext.applicationStopping();
			this.exeContext.applicationDestroying();
			this.rootController.destroy();
		} else {
			this.exeContext.applicationDestroying();
		}
		this.active = false;
	}

	/**
	 * Adds an {@link ApplicationListener} to be notified of events accurring on
	 * this application.
	 */
	public final void addApplicationListener(ApplicationListener appListener) {
		this.exeContext.addApplicationListener(appListener);
	}

	/**
	 * Removes an {@link ApplicationListener} that was added previously..
	 */
	public final void removeApplicationListener(ApplicationListener appListener) {
		this.exeContext.removeApplicationListener(appListener);
	}

	private ApplicationExeContext exeContext;

	private Controller rootController;

	private Params params;

	private boolean active;
}
