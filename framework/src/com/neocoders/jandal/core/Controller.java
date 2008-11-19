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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A controller within an {@link Application}. <p/> To implement a controller,
 * you must subclass this abstract class and implement the {@link onStart()}
 * method, which will be called as soon as you add your controller to an
 * {@link Application} or {@link State}. <p/> <b>Implementing the
 * {@link onStart()} Method</b> <p/> Within your {@link onStart()}
 * implementation you can
 * <ol>
 * <li>read the parameters you configured the controller with when you added it
 * to its parent {@link Application} or {@link State},</li>
 * <li>read resource files bundled with the controller's class file,</li>
 * <li>access {@link Service}s,</li>
 * <li>create the controller's {@link State}s,</li>
 * <li>make the controller transition into one of its {@link State}s, and</li>
 * <li>fire events at child and parent {@link Controller}s.
 * </ol>
 * Note that you must not assume that the Controller will continue to exist
 * after causing transitions and firing parent/child events. Firing events may
 * indirectly cause a transition out of the {@link State} containing the
 * Controller, while a transition will certainly destroy the Controller. An
 * exception will be thrown if you call any protected or public method on a
 * Controller that has been destroyed. <p/> Controller parameters and resources
 * are read so as to become parameters for the initial {@link State}, as we
 * will soon see. <p/> Note that you can't write outputs in the {@link onStart}
 * method. Reasons for this constraint are:
 * <ol>
 * <li>it prevents you from writing outputs here and then accidentally
 * overwriting them from within {@link State#onEntry()}, and</li>
 * <li>it does not really make sense for a controller to do something like that
 * when it is not yet in any {@link State}. </li>
 * </ol>
 * <p/> When adding {@link State}s to a controller, the first one added is the
 * initial one, which you must add with either {@link addInitialState(State)} or
 * {@link addInitialState(State, Params)}. As soon as you add the initial
 * {@link State}, it is entered (see {@link State#onEntry()}). <p/> You then
 * add the other {@link State}s with {@link addState(State)}, which accepts no
 * parameters. This constraint ensures that the initial {@link State} is the
 * only one able to get parameters when added. The others must not get
 * parameters when added because they will get them later when transitioned
 * into. <p/> Here's an example {@link onStart()} implementation, in which we
 * add three {@link State}s, where the initial one gets two parameters:
 * "message", which has the value "Hi there", and "name", which gets the value
 * of the "userName" controller parameter: <p/>
 * 
 * <pre>
 *                                    protected void onStart() throws ExecutionException {
 *                                    
 *                                        addInitialState(new GreetingState(), 
 *                                        		new Params()
 *                                        			.addParam(
 *                                        				&quot;message&quot;,
 *                                    	    			&quot;&quot;Hi there&quot;&quot;)
 *                                    			.addParam(
 *                                    				&quot;name&quot;, 
 *                                    				getControllerParameter(&quot;userName&quot;));
 *                                    
 *                                        addState(new SomeOtherState());
 *                                        
 *                                        addState(new AnotherState());
 *                                    }
 * </pre>
 * 
 * <b>Resource Bundles</b> <p/> As you just saw, you can bundle resources with
 * your controllers and read them from within {@link onStart()} using the
 * protected {@link getProperty()} and {@link getResource()} methods. <p/> Those
 * methods will look for resources files in the same directory as the
 * controller's class file. Any resources that the controller's {@link State}s
 * and their {@link EventProcessors} will require (from within their
 * {@link State#onEntry()} and {@link EventProcessor#onEvent()} methods) should
 * also be bundled with the controller. <p/> There are two reasons for making
 * controllers look after resources for their {@link State}s and
 * {@link EventProcessor}s:
 * <ol>
 * <li>Controllers are intended to the principle component for re-use in this
 * framework, ie. your {@link State} and {@link EventProcessor} implementations
 * <i>belong</i> to a particular controller implementation and are not really
 * intended to be re-used with another controller implementation - they
 * therefore use the resources of their controller. </li>
 * <li>A controller's {@link State}s and {@link EventProcessor}s are
 * encapsuled and therefore not accessible outside of the controller, and we
 * would like a client to be able to access all bundled resources also.</li>
 * </ol>
 * This means of course that when you need to bundle resources with a
 * controller, the controller unfortunately cannot be an anonymous class. <p/>
 * <b>Caching of Properties Resources</b> <p/> To load a properties resource
 * file every time we call {@link getProperty()} would be very inefficient,
 * therefore controllers cache property files when you first read a property
 * from them. The cache persists for the lifetime of the controller, so if you
 * are tweaking a properties resource file while the {@link Application} is
 * active, you'll need to cause the controller to be destroyed and re-created in
 * order to see the effects. You'll need to cause transitions out of the
 * enclosing {@link State} and then back in again, or restart the
 * {@link Application}. <p/>
 * 
 * @author lindsay
 * 
 */
public abstract class Controller {
	/**
	 * Creates a controller.
	 * 
	 * @param name
	 *            Name for the controller
	 * @throws JandalCoreException
	 */
	public Controller(final String name) throws JandalCoreException {
		Utils.validateName(name, "Controller name");
		exeContext = null;
		enclosingApplication = null;
		enclosingState = null;
		this.name = name;
		states = new HashMap();
		currentState = null;
		params = null;
		outputs = new HashMap();
		resources = null;
		path = null;
		active = false;
		this.serviceCache = null;
	}

	/**
	 * Starts this controller when it is the root - called by
	 * {@link Application} when that starts.
	 * 
	 * @param exeContext
	 *            Execution context shared by all controllers in the
	 *            {@link Application}.
	 * @param myApplication
	 *            The {@link Application} itself.
	 * @param params
	 *            Parameters for this controller.
	 * @throws JandalCoreException
	 */
	final void start(final ServiceCache serviceCache,
			final ApplicationExeContext exeContext,
			final Application myApplication, final Params params)
			throws JandalCoreException {
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}
		this.exeContext = exeContext;
		this.enclosingApplication = myApplication;
		enclosingState = null;
		this.params = params;
		this.active = true;
		this.serviceCache = serviceCache;
		this.exeContext.controllerStarting(this);
		onStart();
		this.exeContext.registerActiveController(this);
	}

	/**
	 * Starts this controller when it is a child of a {@link State} - called by
	 * the {@link State} when that is entered.
	 * 
	 * @param exeContext
	 *            Execution context shared by all controllers in the
	 *            {@link Application}.
	 * @param myState
	 *            The owner {@link State}.
	 * @param params
	 *            Parameters for this controller.
	 * @throws JandalCoreException
	 */
	final void start(final ServiceCache serviceCache,
			final ApplicationExeContext exeContext, final State myState,
			final Params params) throws JandalCoreException {
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}
		this.exeContext = exeContext;
		this.enclosingApplication = null;
		this.enclosingState = myState;
		this.params = params;
		this.active = true;
		this.serviceCache = serviceCache;
		this.exeContext.controllerStarting(this);
		onStart();
		this.exeContext.registerActiveController(this);
	}

	/**
	 * Returns true if this controller is currently active.
	 * 
	 * <p/>A controller is active from the moment it is added to its
	 * {@link Application} or parent {@link State}. The root controller of an
	 * {@link Application} is always active, but a controller that belongs to a
	 * {@link State} will stop active when that {@link State} is transitioned
	 * out of. When that {@link State} is later transitioned back into, there
	 * will be a different controller instance where this one was, so don't hold
	 * on to a dead controller in the hope that it will start active again. Once
	 * they stop active, they are to be discarded. You will have to look this
	 * controller up again with a method like
	 * {@link Application#getControllerOnPath(String)}.
	 * 
	 * @return
	 */
	public final boolean isActive() {
		return active;
	}

	/**
	 * Returns ID of this controller instance. Note that this is transient; if
	 * the controller is destroyed then later instantiated, it will have a
	 * different ID..
	 * 
	 * 
	 */
	public final String getId() throws JandalCoreException {
		return "" + this.hashCode();
	}

	/**
	 * Implement this method to do whatever your controller needs to do when
	 * started. See the class comment for for more information.
	 * 
	 * @throws JandalCoreException
	 *             Thrown by your implementation when something goes wrong here.
	 */
	protected abstract void onStart() throws JandalCoreException;

	/**
	 * Returns the ID of this controller, which is generated from the hash code.
	 * 
	 * @return The ID.
	 */
	/*
	 * public final String getId() { return id; }
	 */
	/**
	 * Returns the name of this controller.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the name of this controller's currently active {@link State}.
	 * 
	 * @throws JandalCoreException
	 * @throws JandalCoreException
	 *             This controller instance is no longer active.
	 */
	public final String getCurrentStateName() throws JandalCoreException {
		assertActive();
		return currentState.getName();
	}

	/**
	 * Fires a view event at this controller. This is ONLY to be called by the
	 * GUI - don't call this from within {@link onStart()} {@link State#onEntry}
	 * or {@link EventHandler#onEvent} or you'll probably get an infinite loop
	 * or something!.
	 * 
	 * @param name
	 *            Name of event.
	 * @param params
	 *            Parameters for event.
	 * @throws JandalCoreException
	 *             Name not valid.
	 * @throws JandalCoreException
	 *             Error occurred during processing of event.
	 * @throws JandalCoreException
	 *             This controller instance is no longer active - its parent
	 *             {@link State} will have been transitioned out of.
	 */
	public final void fireViewEvent(String eventName, final Params params)
			throws JandalCoreException {
		assertActive();
		eventName = Utils.validateName(eventName, "Service class name");
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}
		if (currentState == null) {
			/*
			 * Controller instance should not have been allowed to exist without
			 * at least one State!
			 */
			throw new RuntimeException("Failed to process view event - "
					+ "no states defined for controller");
		}
		currentState.processViewEvent(eventName, params);
	}

	ServiceCache getServiceCache() {
		return this.serviceCache;
	}

	/**
	 * Processes an event from the parent controller.
	 * 
	 * @param eventName
	 *            Event name
	 * @param params
	 *            Parameters for the event
	 * @throws JandalCoreException
	 */
	void processParentEvent(final String eventName, final Params params)
			throws JandalCoreException {
		if (currentState != null) {
			currentState.processParentEvent(eventName, params);
		}
	}

	/**
	 * Processes an event from a child controller.
	 * 
	 * @param eventName
	 *            Event name
	 * @param params
	 *            Parameters for the event
	 * @throws JandalCoreException
	 */
	void processChildEvent(final String sourcePath, final String eventName,
			final Params params) throws JandalCoreException {
		currentState.processChildEvent(sourcePath, eventName, params);
	}

	/**
	 * Set the value of one of this controller's outputs, creating it first if
	 * not existing yet.
	 * 
	 * @param outputName
	 *            Name of output.
	 * @param value
	 *            Value for output.
	 * @throws JandalCoreException
	 *             Name not valid.
	 * @throws JandalCoreException
	 *             Value is null.
	 */
	protected final void setOutput(final String outputName, final Object value)
			throws JandalCoreException {
		assertActive();
		if (value == null) {
			throw new JandalCoreException(
					"Tried to write null value to output \"" + outputName
							+ "\"");
		}
		outputs.put(Utils.validateName(outputName, "Controller output name"),
				value);
		this.exeContext.controllerUpdated(this);
	}

	/**
	 * Returns the value of one of this controller's outputs. <p/>Note that is
	 * <b>very bad form</b> to call this from anywhere within an
	 * {@link Application}!</b><p/>Throws exception if the output was never
	 * written since this controller started.
	 * 
	 * @param name
	 *            Name of output.
	 * @return Value of output.
	 * @throws JandalCoreException
	 *             Name not valid
	 * @throws JandalCoreException
	 *             Output not written
	 * @throws JandalCoreException
	 *             This controller instance is no longer active .
	 */
	public final Object getOutput(String name) throws JandalCoreException {
		assertActive();
		name = Utils.validateName(name, "Controller output name");
		final Object value = outputs.get(name);
		if (value == null) {
			throw new JandalCoreException("Cannot find output \"" + name + "\"");
		}
		return value;
	}

	/**
	 * Returns the child controller with the given name, or null if not found.
	 * 
	 * @param name
	 *            Name of child controller.
	 * @return The child controller if found, else null. *
	 * @throws JandalCoreException
	 *             Name not valid
	 * @throws JandalCoreException
	 *             This controller instance is no longer active.
	 */
	public final Controller getChildController(String name)
			throws JandalCoreException {
		assertActive();
		name = Utils.validateName(name, "Child controller name");
		return currentState.getChildController(name);
	}

	/**
	 * Returns existing child controllers.
	 * 
	 * @throws JandalCoreException
	 *             This controller instance is no longer active.
	 */
	public final Collection getChildControllers() throws JandalCoreException {
		assertActive();
		return this.currentState.getChildControllers();
	}

	/**
	 * Returns the {@link Application} that contains this controller.
	 * 
	 */
	final Application getEnclosingApplication() {
		return enclosingApplication;
	}

	/**
	 * Returns the {@link State} that contains this controller, or null if this
	 * is the root controller.
	 * 
	 * @throws JandalCoreException
	 * 
	 */
	 final State getEnclosingState() throws JandalCoreException {
		assertActive();
		return enclosingState;
	}

	/**
	 * Returns the value of one of the parameters given to this controller when
	 * it was added to its parent {@link Application} or {@link State} (see
	 * {@link State#addChildController(Controller)}). Throws exception if the
	 * parameter could not be found.
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
		assertActive();
		name = Utils.validateName(name, "Controller parameter name");
		final Object value = params.get(name);
		if (value == null) {
			throw new JandalCoreException("Cannot find Controller parameter \""
					+ name + "\"");
		}
		return value;
	}

	/**
	 * Adds the initial {@link State} to this controller, supplying parameters
	 * for it. See the class comment for more information about adding
	 * {@link State}s.
	 * 
	 * @param state
	 *            Initial {@link State} to add.
	 * @param stateParams
	 *            Parameters to pass into the initial {@link State}.
	 * 
	 * @throws JandalCoreException
	 *             The initial {@link State} has already been added.
	 */
	protected final void addInitialState(final State state,
			final Params stateParams) throws JandalCoreException {
		assertActive();
		if (state == null) {
			throw new JandalCoreException("State is null");
		}
		if (stateParams == null) {
			throw new JandalCoreException("Params is null");
		}
		if (!states.isEmpty()) {
			throw new JandalCoreException("Initial state already added");
		}
		currentState = state;
		states.put(state.getName(), state);
		currentState.enter(new ServiceCache(this.serviceCache), exeContext,
				this, stateParams);
		this.exeContext.controllerUpdated(this);
	}

	/**
	 * Adds the initial {@link State} to this controller, with no parameters.
	 * See the class comment for more information about adding {@link State}s.
	 * 
	 * 
	 * @param state
	 *            Initial {@link State} to add.
	 * 
	 * @throws JandalCoreException
	 *             The initial {@link State} has already been added.
	 */
	protected final void addInitialState(final State state)
			throws JandalCoreException {
		assertActive();
		this.addInitialState(state, new Params());
	}

	/**
	 * Adds a non-initial {@link State} to this controller, with no parameters.
	 * See the class comment for more information about adding {@link State}s.
	 * 
	 * @param state
	 *            New {@link State} to add.
	 * @throws JandalCoreException
	 *             No initial {@link State} has been added yet.
	 * @throws JandalCoreException
	 *             {@link State} with same name already added.
	 */
	protected final void addState(final State state) throws JandalCoreException {
		assertActive();
		if (currentState == null) {
			throw new JandalCoreException("No initial state added yet - "
					+ "if this is the intended initial state, "
					+ "add it with method Controller.addInitialState()");
		}
		if (state == null) {
			throw new JandalCoreException("State is null");
		}
		if (states.containsKey(state.getName())) {
			throw new JandalCoreException(
					"State with this name already added: \"" + state.getName()
							+ "\"");
		}
		states.put(state.getName(), state);

	}

	/**
	 * Returns the current active {@link State} of this controller. We're
	 * "hiding" {@link States} from the client/view, so this is not public. The
	 * client/view can get the name of the current {@link State} though.
	 * 
	 * @return The current active {@link State}.
	 */
	final State getCurrentState() {
		return currentState;
	}

	/**
	 * Cause this controller to make a parameterised transition to the given
	 * {@link State}. This controller does not have to be in any {@link State}
	 * yet, so this can be used to choose the initial {@link State} on
	 * activation of this controller.
	 * 
	 * @param destStateName
	 *            Name of destination {@link State}.
	 * @param params
	 *            Parameters to accompany the transition.
	 * @throws JandalCoreException
	 *             Controller no longer active - no more operations allowed in
	 *             controller (see {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Destination {@link State} not defined within this
	 *             {@link Controller}.
	 * @throws JandalCoreException
	 *             Error occurred on entry into the destination {@link State}.
	 */
	final void doTransition(String name, final Params params)
			throws JandalCoreException {
		assertActive();
		name = Utils.validateName(name, "Transition destination state name");
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}
		final State state = (State) states.get(name);
		if (state == null) {
			throw new JandalCoreException(
					"Transition destination state with this name not found: "
							+ name);
		}
		if (this.currentState != null) {
			currentState.destroy();
		}
		currentState = state;
		outputs.clear();
		currentState.enter(new ServiceCache(this.serviceCache), exeContext,
				this, params);
		this.exeContext.controllerUpdated(this);
	}

	/**
	 * Makes this controller transition to the given {@link State}. This
	 * controller does not have to be in any {@link State} yet, so this can be
	 * used to choose the initial {@link State} on activation of this
	 * controller.
	 * 
	 * @param destStateName
	 *            Name of destination {@link State}.
	 * @throws JandalCoreException
	 *             Controller no longer active - no more operations allowed in
	 *             controller (see {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Destination {@link State} not defined within this
	 *             {@link Controller}.
	 * @throws JandalCoreException
	 *             Error occurred on entry into the destination {@link State}.
	 */
	final void doTransition(final String destStateName)
			throws JandalCoreException {
		doTransition(destStateName, new Params());
	}

	/**
	 * Fires a parameterised event down to the child controllers of the current
	 * {@link State} of this controller. Nothing happens if this controller does
	 * not yet have any states. Child controllers don't have to exist, and if
	 * they do, they don't have to process the event.
	 * 
	 * @param eventName
	 *            Name of event.
	 * @param params
	 *            Parameters to accompany the event.
	 * @throws JandalCoreException
	 *             This state no longer active - no more operations allowed in
	 *             state.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Error occurred while child {@link Controller} processed the
	 *             event.
	 */
	final void fireParentEvent(String eventName, final Params params)
			throws JandalCoreException {
		assertActive();
		eventName = validateName(eventName, "Event name");
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}
		if (this.currentState == null) {
			return;
		}
		for (final Iterator i = currentState.getChildControllers().iterator(); i
				.hasNext();) {
			final Controller childController = (Controller) i.next();
			childController.processParentEvent(eventName, params);
		}
	}

	/**
	 * Fires an unparameterised event down to the child controllers of the
	 * current {@link State} of this controller. Nothing happens if this
	 * controller does not yet have any states. Child controllers don't have to
	 * exist, and if they do, they don't have to process the event.
	 * 
	 * @param eventName
	 *            Name of event.
	 * @throws JandalCoreException
	 *             This state no longer active - no more operations allowed in
	 *             state.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Error occurred while child {@link Controller} processed the
	 *             event.
	 */
	final void fireParentEvent(String eventName) throws JandalCoreException {
		this.fireParentEvent(eventName, new Params());
	}

	/**
	 * Fires a parameterised event up to the parent controller of this
	 * controller. The parent controller must exist, and it must process the
	 * event (IE. it must have an {@link EventProcessor} to handle child events
	 * of the given name).
	 * 
	 * @param eventName
	 *            Name of event.
	 * @param params
	 *            Parameters to accompany the event.
	 * @throws JandalCoreException
	 *             State no longer active - no more operations allowed in state.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             No parent {@link Controller} found.
	 * @throws JandalCoreException
	 *             Parent {@link Controller} does not have an
	 *             {@link EventProcessor} to handle child events of the given
	 *             name.
	 * @throws JandalCoreException
	 *             Error occurred while parent {@link Controller} processed the
	 *             event.
	 */
	final void fireChildEvent(String eventName, final Params params)
			throws JandalCoreException {
		assertActive();
		eventName = validateName(eventName, "Event name");
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}
		final Controller parentController = getEnclosingState().getEnclosingController();
		if (parentController == null) {
			throw new JandalCoreException(
					"Event not handled (no parent controller found): "
							+ eventName);
		}
		parentController.processChildEvent(getName(), eventName, params);
	}

	/**
	 * Returns a stream to a resource file bundled with this controller. <p/>
	 * This method will look for the resource file in the same directory as the
	 * class file for this controller. <p/> See the comments for this class for
	 * more information. <p/>
	 * 
	 * @param fileName
	 *            Name of resource file bundled with this controller.
	 * @return Wrapper around input stream through which the file may be read.
	 * 
	 * @throws JandalCoreException
	 *             Resource file could not be found, or problem accessing it.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             This controller instance is no longer active.
	 */
	public final Resource getResource(final String fileName)
			throws JandalCoreException {
		assertActive();
		return getResources().getResource(fileName);
	}

	/**
	 * Returns a property in a properties resource file bundled with this
	 * controller. <p/> This method will look for the resource file in the same
	 * directory as the class file for this controller. <p/> The property is
	 * found with a locator string of the form <p/>
	 * &lt;fileName&gt;:&lt;propertyName&gt; <p/> See the comments for this
	 * class for more information. <p/> <b>Note that properties resource files
	 * are cached.</b> <p/>
	 * 
	 * @param locator
	 *            String specifying the name of the resource file and property,
	 *            as described in the method comment.
	 * @return Value of the property.
	 * 
	 * @throws JandalCoreException
	 *             Locator invalid.
	 * @throws JandalCoreException
	 *             Resource file could not be found, or problem loading it.
	 * @throws JandalCoreException
	 *             This controller instance is no longer active - its parent
	 *             {@link State} will have been transitioned out of.
	 */
	public final String getProperty(final String locator)
			throws JandalCoreException {
		assertActive();
		return getResources().getProperty(locator);
	}

	/**
	 * Returns a {@link PropertySet} through which the properties of a bundled
	 * properties resource file may be read. <p/> This method will look for the
	 * resource file in the same directory as the class file for this
	 * controller. <p/> See the comments for this class for more information.
	 * <p/> <b>Note that properties resource files are cached.</b> <p/>
	 * 
	 * @param propsFileName
	 *            Name of the resource file.
	 * 
	 * @throws JandalCoreException
	 *             propsFileName invalid.
	 * @throws JandalCoreException
	 *             Resource file could not be found, or problem loading it.
	 * @throws JandalCoreException
	 *             This controller instance is no longer active.
	 * 
	 */
	public final PropertySet getProperties(final String propsFileName)
			throws JandalCoreException {
		assertActive();
		return getResources().getProperties(propsFileName);
	}

	/**
	 * Lazy-instantiator for resources accessor.
	 * 
	 */
	private ResourceAccessor getResources() {
		if (resources == null) {
			resources = new ResourceAccessor(this);
		}
		return resources;
	}

	/**
	 * Recursively destroys this controller and all existing sub-controllers,
	 * deregistering them from the execution context.
	 * 
	 * @throws JandalCoreException
	 * 
	 */
	final void destroy() {
		/*
		 * Note that we unset the active flag AFTER we get the path from this
		 * controller.
		 */

		this.unlockMyServices();
		this.exeContext.controllerStopping(this);
		exeContext.deregisterActiveController(this);
		this.active = false;
		if (currentState != null) {
			currentState.destroy();
		}
	}

	/**
	 * Returns the absolute path to this controller within the
	 * {@link Application}. The path will be of the form "bert.ernie.bigbird",
	 * where "bert" is the name of the root controller and "bigbird" is the name
	 * of this controller.
	 * 
	 * @return The path.
	 * @throws JandalCoreException
	 *             This controller instance is no longer active.
	 */
	public final String getPath() throws JandalCoreException {
		assertActive();
		if (path == null) {
			StringBuffer sb = new StringBuffer();
			buildPath(sb, this);
			path = sb.toString();
		}
		return path;
	}

	/**
	 * Returns the absolute path to this controller within the
	 * {@link Application}. The path will be of the form "bert.ernie.bigbird",
	 * where "bert" is the name of the root controller and "bigbird" is the name
	 * of this controller.
	 * 
	 * @return The path.
	 * @throws JandalCoreException
	 *             This controller instance is no longer active.
	 */
	public boolean isRoot() throws JandalCoreException {
		assertActive();
		return (enclosingState == null);
	}

	private void buildPath(StringBuffer sb, Controller c) throws JandalCoreException {
		State state = c.getEnclosingState();
		if (state != null) {
			Controller c2 = state.getEnclosingController();
			buildPath(sb, c2);
		}
		if (sb.length() > 0) {
			sb.append(".");
		}
		sb.append(c.getName());
	}

	private final void assertActive() throws JandalCoreException {
		if (!active) {
			throw new JandalCoreException("Controller instance is not active");
		}
	}

	private void unlockMyServices() {
		this.serviceCache.releaseLocks();
	}

	private String validateName(String name, String description)
			throws JandalCoreException {
		if (name == null) {
			throw new JandalCoreException(description + " is null");
		}
		name = name.trim();
		if (name.length() == 0) {
			throw new JandalCoreException(description + " is zero length");
		}
		return name;
	}

	private Application enclosingApplication;

	private State enclosingState;

	private ApplicationExeContext exeContext;

	private Params params;

	private State currentState;

	private final String name;

	private final Map states;

	private final Map outputs;

	private ResourceAccessor resources;

	private String path;

	private boolean active;

	private ServiceCache serviceCache;

}
