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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A state within a {@link Controller}. <p/> To implement a state, you must
 * subclass this abstract class and implement the {@link onEntry()} method,
 * which is called when the state becomes active. <p/> A state added as the
 * initial state of its {@link Controller} (see
 * {@link Controller#addInitialState(State)} and
 * {@link Controller#addInitialState(State, Params)}) becomes active
 * immediately, while a state added as a non-initial state (see
 * {@link Controller#addState(State)}) becomes active when transitioned into.
 * <p/> <b>Implementing the {@link onEntry()} Method</b <p/>
 * 
 * 
 * <p/> Within your implementation of this method, you can: <p/>
 * <ol>
 * <li>read parameters passed into the state when it became active,</li>
 * <li>read resource files and properties bundled with the enclosing
 * {@link Controller},</li>
 * <li>write data to the {@link Controller}'s outputs,</li>
 * <li>access {@link Service}s,</li>
 * <li>add {@link EventProcessor}s,</li>
 * <li>add child-{@link Controller}s, and</li>
 * <li>make the state's {@link Controller} transition to another {@link State},
 * and</li>
 * <li>fire events at child and parent {@link Controller}s.
 * </ol> * Note that you must not assume that the State will continue to exist
 * after causing transitions and firing parent/child events. Firing events may
 * indirectly cause a transition out of the {@link State}, while a transition
 * will certainly destroy the State. An exception will be thrown if you call any
 * protected or public method on a State that has been destroyed. <p/> See the
 * documentation on {@link Controller} for more info about bundled resources and
 * how properties resources are cached. <p/> Here's an example {@link onEntry()}
 * method showing the sorts of things that we can do within it:
 * 
 * <pre>
 *                            public void onEntry() throws ExecutionException {
 *                            
 *                            setOutput(&quot;title&quot;, &quot;My awesome state&quot;);
 *                                	setOutput(&quot;message&quot;, getParam(&quot;message&quot;));
 *                            
 *                                	setOutput(&quot;photo&quot;, getResource(&quot;myPhoto.jpg&quot;));
 *                            
 *                                	setOutput(&quot;description&quot;, getControllerProperty(&quot;messages.properties:description&quot;));
 *                            
 *                                	addViewEventProcessor(new EventProcessor(&quot;quit&quot;) {
 *                            		public void onEvent() throws Exception {
 *                            	    		doTransition(&quot;quittingState&quot;, 
 *                            				new Params()
 *                            					.add(&quot;reason&quot;, 
 *                            						getEventParameter(&quot;reason&quot;));
 *                            		}
 *                                });
 *                            
 *                                addChildEventProcessor(new AnotherEventProcessor(&quot;dataRequest&quot;));
 *                            
 *                                addParentEventProcessor(new YetAnotherEventProcessor(&quot;broadcast&quot;));
 *                            
 *                                addChildController(new MyChildController(), new Params().add(&quot;aRandomNumber&quot;,
 *                            	    new Integer(5)));
 *                            }
 * </pre>
 * 
 * This method will
 * <ol>
 * <li>Write a message, taken from a state parameter, to the {@link Controller}'s
 * "message" output.</li>
 * <li>Write an image resource file (wrapped in a {@link Resource}) to the
 * {@link Controller}'s "photo" output.</li>
 * <li>Write a "description" property specified within the controller's bundled
 * "messages.properties" resource file to the {@link Controller}'s
 * "description" output.</li>
 * <li>Add an anonymous {@link EventProcessor} that will respond to "quit" view
 * events targeted at the {@link Controller} by causing the {@link Controller}
 * to make a parameterised transition to the "quittingState" state, where the
 * transition takes with it with the value of the "reason" parameter that was
 * passed in with the event.</li>
 * <li>Add an {@link EventProcessor} to process "dataRequest" events from child
 * {@link Controller}s.</li>
 * <li>Add an {@link EventProcessor} to process "broadcast" events from the
 * parent {@link Controller}.
 * <li>Add a child {@link Controller}, giving it an "aRandomNumber" parameter
 * with the value 5.</li>
 * </ol>
 * Note that you can only bundle resources with {@link Controller}s - read the
 * documentation for that class for the reasons why. <p/>
 * 
 * @author lindsay
 * 
 */
public abstract class State {
	/**
	 * Creates a state.
	 * 
	 * @param name
	 *            Name for the state
	 * @throws JandalCoreException
	 *             Invalid name
	 */
	public State(final String name) throws JandalCoreException {
		Utils.validateName(name, "State name");
		enclosingController = null;
		appContext = null;
		this.name = name;
		onViewEvents = new HashMap();
		onParentEvents = new HashMap();
		onChildEvents = new HashMap();
		childControllerMap = new HashMap();
		childControllerList = new LinkedList();
		stateParams = null;
		this.active = false;
		this.serviceCache = null;
	}

	public Params getParams() {
		return this.stateParams;
	}

	/**
	 * Called by the parent {@link Controller} when this state becomes active.
	 * 
	 * @param serviceCache
	 *            ServiceCache for this state
	 * @param appContext
	 *            Execution context shared by all controllers in the
	 *            {@link Application}.
	 * @param myController
	 *            The enclosing {@link Controller}.
	 * @param stateParams
	 *            Parameters for this state.
	 * @throws JandalCoreException
	 */
	final void enter(final ServiceCache serviceCache,
			final ApplicationExeContext appContext,
			final Controller myController, final Params stateParams)
			throws JandalCoreException {
		if (stateParams == null) {
			throw new JandalCoreException("Params is null");
		}
		this.serviceCache = serviceCache;
		this.appContext = appContext;
		this.enclosingController = myController;
		this.stateParams = stateParams;
		this.active = true;
		onEntry();
	}

	/*
	 * protected ApplicationContext getApplicationContext() { return
	 * this.appContext; }
	 */
	/**
	 * Implement this method to do whatever your state needs to do on entry. See
	 * the class comments for more information.
	 * 
	 * @throws JandalCoreException
	 *             Thrown by your implementation when something goes wrong here.
	 */
	protected abstract void onEntry() throws JandalCoreException;

	/**
	 * Returns the {@link Controller} this state is within.
	 * 
	 * @return The {@link Controller}.
	 * @throws JandalCoreException
	 */
	final Controller getEnclosingController() throws JandalCoreException {
		assertActive();
		return enclosingController;
	}

	/**
	 * Returns the name of this state.
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Returns the value of one of the parameters supplied to this state. Throws
	 * exception if the parameter could not be found - it is in the state's
	 * contract with the caller that they both know what parameters should
	 * exist.
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
		name = Utils.validateName(name, "State parameter name");
		final Object value = stateParams.get(name);
		if (value == null) {
			throw new JandalCoreException("State parameter not found: \""
					+ name + "\"");
		}
		return value;
	}

	/**
	 * Set the value of one of the {@link Controller}'s outputs, creating it
	 * first if not existing yet.
	 * 
	 * @param name
	 *            Name of output.
	 * @param value
	 *            Value for output.
	 * @throws JandalCoreException
	 *             Name not valid.
	 */
	protected final void setOutput(final String name, final Object value)
			throws JandalCoreException {
		assertActive();
		getEnclosingController().setOutput(name, value);
	}

	/**
	 * Destroys this state and everything in it.
	 * 
	 * @throws JandalCoreException
	 * 
	 */
	final void destroy() {
		this.active = false;
		destroyEventProcessors(onViewEvents);
		destroyEventProcessors(onParentEvents);
		destroyEventProcessors(onChildEvents);
		destroyChildControllers();
		this.unlockMyServices();
	}

	private void destroyEventProcessors(final Map map) {
		for (final Iterator i = onChildEvents.keySet().iterator(); i.hasNext();) {
			((EventProcessor) onChildEvents.get(i.next())).destroy();
		}
		map.clear();
	}

	private void destroyChildControllers() {
		for (final Iterator i = childControllerList.iterator(); i.hasNext();) {
			((Controller) i.next()).destroy();
		}
		childControllerList.clear();
		childControllerMap.clear();
	}

	/**
	 * Cause enclosing {@link Controller} to make a parameterised transition to
	 * another {@link State}.
	 * 
	 * @param destStateName
	 *            Name of destination {@link State}.
	 * @param params
	 *            Parameters to accompany the transition.
	 * @throws JandalCoreException
	 *             State no longer active - no more operations allowed in state.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Destination {@link State} not defined within the
	 *             {@link Controller}.
	 * @throws JandalCoreException
	 *             Error occurred on entry into the destination {@link State}.
	 */
	protected final void doTransition(String destStateName, final Params params)
			throws JandalCoreException {
		assertActive();
		getEnclosingController().doTransition(destStateName, params);
	}

	/**
	 * Makes the enclosing {@link Controller} transition to another
	 * {@link State}.
	 * 
	 * @param destStateName
	 *            Name of destination {@link State}.
	 * @throws JandalCoreException
	 *             State no longer active - no more operations allowed in state.
	 * @throws JandalCoreException
	 *             Name invalid.
	 * @throws JandalCoreException
	 *             Destination {@link State} not defined within the
	 *             {@link Controller}.
	 * @throws JandalCoreException
	 *             Error occurred on entry into the destination {@link State}.
	 */
	protected final void doTransition(final String destStateName)
			throws JandalCoreException {
		doTransition(destStateName, new Params());
	}

	/**
	 * Fires a parameterised event down to the child {@link Controller}s of
	 * this state. Child {@link Controller}s don't have to exist, and if they
	 * do, they don't have to process the event.
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
	protected final void fireParentEvent(String eventName, final Params params)
			throws JandalCoreException {
		assertActive();
		eventName = validateName(eventName, "Event name");
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}
		// destroyed = true;
		for (final Iterator i = getChildControllers().iterator(); i.hasNext();) {
			final Controller childController = (Controller) i.next();
			childController.processParentEvent(eventName, params);
		}
	}

	protected final void fireParentEvent(String eventName)
			throws JandalCoreException {
		this.fireParentEvent(eventName, new Params());
	}

	/**
	 * Fires a parameterised event up to the parent {@link Controller} of the
	 * enclosing {@link Controller}. The parent {@link Controller} must exist,
	 * and it must process the event (IE. it must have an {@link EventProcessor}
	 * to handle child events of the given name).
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
	protected final void fireChildEvent(String eventName, final Params params)
			throws JandalCoreException {
		assertActive();
		eventName = validateName(eventName, "Event name");
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}
		final Controller myController = getEnclosingController();
		final Controller parentController = myController.getEnclosingState()
				.getEnclosingController();
		if (parentController == null) {
			throw new JandalCoreException(
					"Event not handled (no parent controller found): "
							+ eventName);
		}
		parentController.processChildEvent(myController.getName(), eventName,
				params);
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

	/**
	 * Fires an unparameterised event up to the parent {@link Controller} of the
	 * enclosing {@link Controller}. The parent {@link Controller} must exist,
	 * and it must process the event (IE. it must have an {@link EventProcessor}
	 * to handle child events of the given name).
	 * 
	 * @param eventName
	 *            Name of event.
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
	protected final void fireChildEvent(String eventName)
			throws JandalCoreException {
		this.fireChildEvent(eventName, new Params());
	}

	/**
	 * Adds an {@link EventProcessor} to process view events fired at the
	 * {@link Controller} when this state is active.
	 * 
	 * @param eventProcessor
	 *            The view-event processor.
	 * @throws JandalCoreException
	 */
	protected final void addViewEventProcessor(
			final EventProcessor eventProcessor) throws JandalCoreException {
		assertActive();
		if (eventProcessor == null) {
			throw new JandalCoreException("EventProcessor is null");
		}
		if (onViewEvents.get(eventProcessor.getName()) != null) {
			throw new JandalCoreException(
					"View-event processor with this name already added: \""
							+ eventProcessor.getName() + "\"");
		}
		onViewEvents.put(eventProcessor.getName(), eventProcessor);
		eventProcessor.setup(this.appContext, this);
	}

	/**
	 * Adds an {@link EventProcessor} to process child events fired at this
	 * state's {@link Controller} when this state is active. See
	 * {@link EventProcessor#fireChildEvent(String, Params)} for information on
	 * the generation of child events.
	 * 
	 * @param eventProcessor
	 *            The child-event processor.
	 * @throws JandalCoreException
	 */
	protected final void addChildEventProcessor(
			final EventProcessor eventProcessor) throws JandalCoreException {
		assertActive();
		if (eventProcessor == null) {
			throw new JandalCoreException("EventProcessor is null");
		}
		if (onChildEvents.get(eventProcessor.getName()) != null) {
			throw new JandalCoreException(
					"Child-event processor with this name already added: \""
							+ eventProcessor.getName() + "\"");
		}
		onChildEvents.put(eventProcessor.getName(), eventProcessor);
		eventProcessor.setup(this.appContext, this);
	}

	/**
	 * Adds an {@link EventProcessor} to process parent events fired at this
	 * state's {@link Controller} when this state is active. See
	 * {@link EventProcessor#fireParentEvent(String, Params)} for information on
	 * the generation of parent events.
	 * 
	 * @param eventProcessor
	 *            The view-event processor.
	 * @throws JandalCoreException
	 */
	protected final void addParentEventProcessor(
			final EventProcessor eventProcessor) throws JandalCoreException {
		assertActive();
		if (eventProcessor == null) {
			throw new JandalCoreException("EventProcessor is null");
		}
		if (onParentEvents.get(eventProcessor.getName()) != null) {
			throw new JandalCoreException(
					"Parent-event processor with this name already added: \""
							+ eventProcessor.getName() + "\"");
		}
		onParentEvents.put(eventProcessor.getName(), eventProcessor);
		eventProcessor.setup(this.appContext, this);
	}

	/**
	 * Add a parameterised child {@link Controller}, which will start as soon
	 * as it is added.
	 * 
	 * @param controller
	 * @param params
	 * @throws JandalCoreException
	 */
	protected final void addChildController(final Controller controller,
			final Params params) throws JandalCoreException {
		assertActive();
		if (controller == null) {
			throw new JandalCoreException("Controller is null");
		}
		if (params == null) {
			throw new JandalCoreException("Params is null");
		}

		if (childControllerMap.get(controller.getName()) != null) {
			throw new JandalCoreException(
					"Child controller with this name already added: \""
							+ controller.getName() + "\"");
		}
		childControllerMap.put(controller.getName(), controller);
		childControllerList.add(controller);
		controller.start(new ServiceCache(this.serviceCache), appContext, this,
				params);
	}

	/**
	 * Add a parameterised child {@link Controller}, which will start as soon
	 * as it is added.
	 * 
	 * @param controller
	 * @param stateParams
	 * @throws JandalCoreException
	 */
	protected final void addChildController(final Controller controller)
			throws JandalCoreException {
		this.addChildController(controller, new Params());
	}

	final Controller getChildController(final String name) {
		return (Controller) childControllerMap.get(name);
	}

	final Set getChildControllers() {
		return new HashSet(childControllerList);
	}

	/**
	 * Deletes the child {@link Controller} with the given name. Ordinarily, you
	 * probably wouldn't want to use this method. For simplicity, it is
	 * recommended that you define a static hierarchy in which
	 * {@link Controller}s wink in out of existence as their parent States are
	 * transitioned in and out of. However, Jandal has no problem with you
	 * deleting {@link Controller}s as part of application execution, so this
	 * method is provided in case you you find a cool way to use it.
	 * 
	 * @param name
	 * @throws JandalCoreException
	 */
	final protected void deleteChildController(String name)
			throws JandalCoreException {
		assertActive();
		name = validateName(name, "Child controller name");
		Controller controller = (Controller) childControllerMap.get(name);
		controller.destroy();
		childControllerMap.remove(name);
		childControllerList.remove(controller);
	}

	/**
	 * Processes a view event.
	 * 
	 * @param name
	 * @param params
	 * @throws JandalCoreException
	 */
	final void processViewEvent(final String name, final Params params)
			throws JandalCoreException {
		final EventProcessor onEvent = (EventProcessor) onViewEvents.get(name);
		if (onEvent == null) {
			throw new JandalCoreException("Could not find "
					+ EventProcessor.class.getSimpleName() + " to process \""
					+ name + "\" view event at State \"" + this.name
					+ "\" of controller \""
					+ this.enclosingController.getPath() + "\"");

		}
		onEvent.setParams(params);
		onEvent.onEvent();
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

	final void processChildEvent(final String sourcePath,
			final String eventName, final Params params)
			throws JandalCoreException {
		final EventProcessor onEvent = (EventProcessor) onChildEvents
				.get(eventName);
		if (onEvent == null) {
			final State superState = enclosingController.getEnclosingState();
			if (superState == null) {
				throw new JandalCoreException(
						"Child controller event not processed: " + eventName);
			}
			final Controller superController = superState
					.getEnclosingController();
			superController.processChildEvent(enclosingController.getName()
					+ "." + sourcePath, eventName, params);
		} else {
			onEvent.setParams(params);
			onEvent.onEvent();
		}
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
	final void processParentEvent(final String eventName, final Params params)
			throws JandalCoreException {
		final EventProcessor onEvent = (EventProcessor) onParentEvents
				.get(eventName);
		if (onEvent != null) {
			onEvent.setParams(params);
			onEvent.onEvent();
		}
	}

	/**
	 * Returns a stream to a resource file bundled with this state's
	 * {@link Controller}.
	 * 
	 * <p/> See the class comments for {@link Controller} for more information.
	 * <p/>
	 * 
	 * @param resourceFileName
	 *            Name of resource file bundled with this controller.
	 * @return Wrapper around input stream through which the file may be read.
	 * 
	 * @throws JandalCoreException
	 *             Resource file could not be found, or problem accessing it.
	 * @throws JandalCoreException
	 *             Name invalid.
	 */
	protected final Resource getResource(final String resourceFileName)
			throws JandalCoreException {
		assertActive();
		return this.enclosingController.getResource(resourceFileName);
	}

	/**
	 * Returns a property in a properties resource file bundled with this
	 * state's {@link Controller}. <p/> The property is found with a locator
	 * string of the form <p/> &lt;fileName&gt;:&lt;propertyName&gt; <p/> See
	 * the class comments for {@link Controller} for more information. <p/>
	 * <b>Note that properties resource files are cached.</b> <p/>
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
	 * 
	 */
	protected final String getProperty(final String locator)
			throws JandalCoreException {
		assertActive();
		return this.enclosingController.getProperty(locator);
	}

	/**
	 * Returns a {@link PropertySet} through which properties, defined in a
	 * properties resource file bundled with this state's {@link Controller},
	 * may be read. <p/> This method will look for the resource file in the same
	 * directory as the class file for this controller. ee the class comments
	 * for {@link Controller} for more information. <p/> <b>Note that properties
	 * resource files are cached.</b> <p/>
	 * 
	 * @param propsFileName
	 *            Name of the resource file.
	 * 
	 * @throws JandalCoreException
	 *             propsFileName invalid.
	 * @throws JandalCoreException
	 *             Resource file could not be found, or problem loading it.
	 * 
	 */
	protected final PropertySet getProperties(final String propsFileName)
			throws JandalCoreException {
		assertActive();
		return this.enclosingController.getProperties(propsFileName);
	}

	/**
	 * Locks and returns a synchronizable {@link Service} that was hopefully
	 * plugged into the {@link Application} when it was instantiated (see
	 * {@link Application#Application(ServiceSet, Params)}), giving up after a
	 * given timeout elapses.
	 * 
	 * @param name
	 *            Name of {@link Service} implementation.
	 * @param timeout
	 *            time in milliseconds after which method gives up and returns
	 *            null
	 * @return The service if lock got on it in timeout period, else null.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             Name not valid
	 * @throws JandalCoreException
	 *             Service not found
	 * @throws JandalCoreException
	 *             Service is not synchronized, so locking does not apply to it
	 */
	protected final Service lockService(final String className, long timeout)
			throws JandalCoreException {
		assertActive();
		return this.serviceCache.lockService(className, timeout);
	}

	/**
	 * Returns a {@link Service} that was hopefully plugged into the
	 * {@link Application} when it was instantiated (see
	 * {@link Application#Application(ServiceSet, Params)}). Throws exception
	 * if the {@link Service} could not be found. If the {@link Service} is
	 * synchronizable then it must be currently locked by either this state or
	 * an enclosing application element.
	 * 
	 * @param name
	 *            Name of {@link Service} implementation.
	 * @param timeout
	 *            time in milliseconds after which method gives up and returns
	 *            null
	 * @return The service.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             className not valid
	 * @throws JandalCoreException
	 *             Service not found
	 * @throws JandalCoreException
	 *             Service is synchronized and not currently locked by this
	 *             state or ancestor element
	 * 
	 */
	protected final Service getService(final String className)
			throws JandalCoreException {
		assertActive();
		return this.serviceCache.getService(className);
	}

	/**
	 * Unlocks a synchronized {@link Service} that was previously locked with
	 * {@link #lockService(String)} of this state. The {@link Service} must be
	 * currently locked by <b>this state</b>, not by an ancestor element.
	 * 
	 * @param className
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             className not valid
	 * @throws JandalCoreException
	 *             Service not found
	 * @throws JandalCoreException
	 *             Service is synchronized and not currently locked by this
	 *             state
	 * @throws JandalCoreException
	 *             Service is not synchronized, so locking does not apply to it
	 */
	protected final void unlockService(final String className)
			throws JandalCoreException {
		assertActive();
		this.serviceCache.releaseService(className);
	}

	private void unlockMyServices() {
		this.serviceCache.releaseLocks();
	}

	private final void assertActive() throws JandalCoreException {
		if (!active) {
			throw new JandalCoreException("State is not active");
		}
	}

	private Controller enclosingController;

	private ApplicationExeContext appContext;

	private final String name;

	private final Map onViewEvents;

	private final Map onChildEvents;

	private final Map onParentEvents;

	private final Map childControllerMap;

	private final List childControllerList;

	private boolean active;

	private Params stateParams;

	private ServiceCache serviceCache;
}
