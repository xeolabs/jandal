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

import java.util.Iterator;

/**
 * Processes a <i>view</i>, <i>child</i> or <i>parent</i> event for the
 * current {@link State} of a {@link Controller}.
 * 
 * <p/>You define one of these on a {@link State} to process each type of event
 * that the {@link Controller} may receive when the {@link State} is active,
 * where an event type is distinguished by the source type (IE. view or
 * parent/child {@link Controller}) and the name that the event will have.
 * 
 * 
 * <p/> To implement an event processor, you must subclass this abstract class
 * and implement the {@link onEvent()} method, which is called when the
 * corresponding event type arrives at the {@link State}.
 * 
 * 
 * <p/> Within your {@link onEvent()} implementation you can use
 * EventProcessor's protected methods to:
 * <ol>
 * <li>read any parameters that accompany the event,</li>
 * <li>read resource files bundled with the controller's class file,</li>
 * <li>write {@link Controller} outputs,</li>
 * <li>access services in the {@link ServiceSet} that the {@link Application}
 * has been provided with at runtime,</li>
 * <li>make the {@link Controller} transition to another {@link State}, and</li>
 * <li>fire events at child and parent {@link Controller}s.
 * </ol>
 * Note that you must not assume that the EventProcessor will continue to exist
 * after causing transitions and firing parent/child events. Firing events may
 * indirectly cause a transition out of the {@link State} containing the
 * EventProcessor, while a transition will certainly destroy the EventProcessor.
 * An exception will be thrown if you call any of the protected method methods
 * on an EventProcessor that has been destroyed.
 * 
 * <p/><b>Examples</b><p/>Here is an example in which several hypothetical
 * processors are added to a {@link State}, from within an implementation of
 * the {@link State}'s {@link State#onEntry()} method:
 * 
 * <pre>
 *          public void onEntry() throws ExecutionException {
 *          	
 *          	addViewEventProcessor(new EventProcessor(&quot;login&quot;) {
 *          
 *          		public void onEvent() throws ExecutionException {
 *          			String name = getStringParam(&quot;name&quot;);
 *          			String password = getStringParam(&quot;password&quot;);
 *          
 *          			MyLoginService service 
 *          				= (MyLoginService)getService(&quot;com.acme.MyLoginService&quot;);
 *          
 *          			boolean success = service.doLogin(name, password);
 *          
 *          			if (success) {
 *          				doTransition(&quot;welcome&quot;, 
 *          					new Params()
 *          						.add(&quot;greeting&quot;, 
 *          							getProperty(&quot;messages.properties:greeting&quot;));
 *          
 *          			}
 *          			else {
 *          				setOutput(&quot;errorMessage&quot;, &quot;Login failed&quot;);
 *          			}
 *          		}
 *          	});
 *          
 *          	addViewEventProcessor(new EventProcessor(&quot;getPicture&quot;) {
 *          
 *          		public void onEvent() throws ExecutionException {
 *          			
 *          			Resource imageFile = getResource(&quot;picture.jpg&quot;);
 *          
 *          			setOutput(&quot;picture&quot;, imageFile);
 *          		}
 *          	});
 *          
 *            	addChildEventProcessor(new EventProcessor(&quot;quit&quot;) {
 *          
 *          		public void onEvent() throws ExecutionException {
 *          
 *          			String msg = getProperty(&quot;messages.properties:confirmMsg&quot;);
 *          
 *          			doTransition(&quot;confirmState&quot;, 
 *          					new Params()
 *          						.add(&quot;message&quot;, msg));
 *          		}
 *          	});
 *          
 *          	addParentEventProcessor(new EventProcessor(&quot;parentAnnouncement&quot;) {
 *          
 *          		public void onEvent() throws ExecutionException {
 *          			
 *          			String msg = getStringParam(&quot;message&quot;);
 *          
 *          			setOutput(&quot;message&quot;, msg);
 *          
 *          			fireParentEvent(&quot;parentAnnouncement&quot;, 
 *          					new Params().add(&quot;message&quot;, msg);
 *          		}
 *          	});
 *          }
 * </pre>
 * 
 * The first processor will process a "login" event coming from the view. The
 * event will be accompanied by "name" and "password" parameters. The processor
 * gets a MyLoginService that has been supplied in the {@link ServiceSet} for
 * this {@link Application} and attempts to login with it. On success, it causes
 * the {@link Controller} to make a transition to a "welcome" state,
 * parameterised with a greeting message taken from a "greeting" property in a
 * "messages.properties" properties file file bundled with the
 * {@link Controller}. On failure, it sets an error message on the
 * {@link Controller}'s "errorMessage" output. <p/> The second processor will
 * process a "getPicture" event coming from the view by outputting an image file
 * that is bundled with the {@link Controller}. See how the image is wrapped in
 * a {@link Resource}, which provides a stream through which the file may be
 * read. <p/> The third processor processes a "quit" event coming from a child
 * {@link Controller} by making a transition to a "confirmState" {@link State},
 * parameterised with a message taken from a "confirmMsg" property in a
 * "messages.properties" properties file bundled with the {@link Controller}.
 * <p/>The fourth processor will process a "parentAnnouncement" event by
 * outputting the "message" parameter and relaying the event, along with the
 * parameter, down to child {@link Controller}s.
 * 
 * 
 * 
 * @author lindsay
 * 
 */
public abstract class EventProcessor {
	/**
	 * Creates event processor for the event of the given name.
	 * 
	 * @param name
	 *            Name of event.
	 * @throws JandalCoreException
	 *             Name not valid
	 */
	public EventProcessor(final String name) throws JandalCoreException {
		super();
		appContext = null;
		myState = null;
		this.name = Utils.validateName(name, "Event processor name");
		active = false;
	}

	/**
	 * Called by this processor's {@link State} to set this processor up with a
	 * context and a parent link when we add it to the {@link State}.
	 * 
	 */
	final void setup(ApplicationExeContext appContext, final State myState) {
		this.appContext = appContext;
		this.myState = myState;
		this.active = true;
	}

	final State getMyState() {
		return myState;
	}

	final String getName() {
		return name;
	}

	/**
	 * Called by this processor's {@link State} to inject parameters just before
	 * the {@link State} calls this processor's {@link onEvent()}.
	 * 
	 * @param params
	 */
	final void setParams(final Params params) {
		this.params = params;
	}

	/**
	 * Returns the value of one of the parameters passed in with the event.
	 * Throws exception if the parameter could not be found - it is in the
	 * {@link EventProcessor}'s contract with the view that they both know what
	 * parameters should have been supplied with the event.
	 * 
	 * @param name
	 *            Name of parameter.
	 * @return Value of parameter.
	 * @throws JandalCoreException
	 *             Name not valid
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             Parameter not found
	 */
	protected final Object getParam(String name) throws JandalCoreException {
		assertActive();
		name = Utils.validateName(name, "Event parameter name");
		final Object value = params.get(name);
		if (value == null) {
			throw new JandalCoreException("Event parameter not found: \""
					+ name + "\"");
		}
		return value;
	}

	/**
	 * Returns the value of a String-type parameter passed in with the event.
	 * Throws exception if the parameter could not be found or is not a String -
	 * it is in the {@link EventProcessor}'s contract with the view that they
	 * both know what parameters should have been supplied with the event, and
	 * what type they are.
	 * 
	 * @param name
	 *            Name of parameter.
	 * @return Value of parameter.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             Name not valid
	 * @throws JandalCoreException
	 *             Parameter not found
	 * @throws JandalCoreException
	 *             Parameter value is not a String
	 */
	protected final String getStringParam(final String name)
			throws JandalCoreException {
		assertActive();
		final Object value = getParam(name);
		try {
			return (String) value;
		} catch (final ClassCastException cce) {
			throw new JandalCoreException("Event parameter type mismatch \""
					+ name + "\" not a String - it is a "
					+ value.getClass().getName() + " instead");
		}
	}

	void processEvent() throws JandalCoreException {
		this.onEvent();
		/*
		 * 
		 */
	//	appContext.controllerUpdated(this.myState.getEnclosingController());
	}

	/**
	 * Implement this method to do whatever this processor needs to do when it
	 * receives its event. In this method you can process the event using the
	 * protected methods of this class. </br></br>Note that some of those
	 * methods, such as
	 * <ul>
	 * <li>{@link #fireParentEvent(String, Params)},</li>
	 * <li>{@link #fireChildEvent(String, Params)} and</li>
	 * <li>{@link #doTransition(String)}</li>
	 * </ul>
	 * may cause state transitions in {@link Controller}s higher up, which
	 * would destroy this event processor element. For that reason, any other
	 * methods of this class called after any of those three will throw an
	 * exception if this processor has been destroyed. It's therefore best to
	 * make a call to any of these three methods the last thing you do when
	 * handling an event.
	 * </p>
	 * See class comments for more information.
	 * 
	 * @throws JandalCoreException
	 *             Error while processing event.
	 */
	protected abstract void onEvent() throws JandalCoreException;

	/**
	 * Sets an output of the enclosing {@link Controller}, creating it first if
	 * it does not yet exist.
	 * 
	 * @param name
	 *            Name of the output.
	 * @param value
	 *            Value for the output.
	 * @throws JandalCoreException
	 *             Name not valid.
	 */
	protected final void setOutput(String name, final Object value)
			throws JandalCoreException {
		assertActive();
		getMyState().getEnclosingController().setOutput(name, value);
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
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
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
		active = false;
		getMyState().getEnclosingController().doTransition(destStateName,
				params);
	}

	/**
	 * Makes the enclosing {@link Controller} transition to another
	 * {@link State}.
	 * 
	 * @param destStateName
	 *            Name of destination {@link State}.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
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
	 * Fires a parameterised event down to the child {@link Controller}s of the
	 * enclosing {@link Controller}. Child {@link Controller}s don't have to
	 * exist, and if they do, they don't have to process the event.
	 * 
	 * @param eventName
	 *            Name of event.
	 * @param params
	 *            Parameters to accompany the event.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             Name invalid.
	 * 
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
		for (final Iterator i = getMyState().getChildControllers().iterator(); i
				.hasNext();) {
			final Controller childController = (Controller) i.next();
			childController.processParentEvent(eventName, params);
		}
	}

	/**
	 * Fires an unparameterised event down to the child {@link Controller}s of
	 * the enclosing {@link Controller}. Child {@link Controller}s don't have
	 * to exist, and if they do, they don't have to process the event.
	 * 
	 * @param eventName
	 *            Name of event.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
	 * @throws JandalCoreException
	 *             Name invalid.
	 * 
	 * @throws JandalCoreException
	 *             Error occurred while child {@link Controller} processed the
	 *             event.
	 */
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
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for reason).
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
		final Controller myController = getMyState().getEnclosingController();
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

	protected final void fireChildEvent(String eventName)
			throws JandalCoreException {
		this.fireChildEvent(eventName, new Params());
	}

	/**
	 * Returns a stream to a bundled resource file. </br></br>This method will
	 * search for the resource file bundled with the class file that contains
	 * the definition of this event processor.
	 * 
	 * @param resourceFileName
	 *            Name of resource file.
	 * @return Wrapper around input stream through which the file may be read.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for the reason).
	 * @throws JandalCoreException
	 *             Resource file could not be found, or problem accessing it.
	 * @throws JandalCoreException
	 *             Name invalid.
	 */
	protected final Resource getResource(final String resourceFileName)
			throws JandalCoreException {
		assertActive();
		return this.myState.getResource(resourceFileName);
	}

	/**
	 * Returns a property in a bundled properties resource file. </br></br>This
	 * method will search for the resource file bundled with the class file that
	 * contains the definition of this event processor. </br></br>The property
	 * is found with a locator string of the form</br><br>
	 * &lt;fileName&gt;:&lt;propertyName&gt; <br>
	 * <br>
	 * <b>Note: The property resource file will be cached by this event
	 * processor.</b> Therefore if you change the contents of the file while
	 * the {@link Application} is running, you will need to cause transitions
	 * out of the enclosing {@link State} and then back in again in order to see
	 * the change.
	 * 
	 * @param locator
	 *            String specifying the name of the resource file and property,
	 *            as described in the method comment.
	 * @return Value of the property.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for the reason).
	 * @throws JandalCoreException
	 *             Locator invalid.
	 * @throws JandalCoreException
	 *             Resource file could not be found, or problem loading it.
	 * 
	 */
	protected final String getProperty(final String locator)
			throws JandalCoreException {
		assertActive();
		return this.myState.getProperty(locator);
	}

	/**
	 * Returns a {@link PropertySet} through which the properties of a bundled
	 * properties resource file may be read. </br></br>This method will search
	 * for the resource file bundled with the class file that contains the
	 * definition of this event processor.</br></br><b>Note: The property
	 * resource file will be cached by this event processor.</b> Therefore if
	 * you change the contents of the file while the {@link Application} is
	 * running, you will need to cause transitions out of the enclosing
	 * {@link State} and then back in again in order to see the change.
	 * 
	 * @param propsFileName
	 *            Name of the resource file.
	 * @throws JandalCoreException
	 *             No more operations allowed in processor (see
	 *             {@link EventProcessor#onEvent()} for the reason).
	 * @throws JandalCoreException
	 *             propsFileName invalid.
	 * @throws JandalCoreException
	 *             Resource file could not be found, or problem loading it.
	 * 
	 */
	protected final PropertySet getProperties(final String propsFileName)
			throws JandalCoreException {
		assertActive();
		return this.myState.getProperties(propsFileName);
	}

	private void assertActive() throws JandalCoreException {
		if (!active) {
			throw new JandalCoreException("EventProcessor no longer active");
		}
	}

	void destroy() {
		active = false;
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
		return this.myState.lockService(className, timeout);
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
		return this.myState.getService(className);
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
		this.myState.unlockService(className);
	}

	private ApplicationExeContext appContext;

	private State myState;

	private final String name;

	private Params params;

	/**
	 * False as soon as this processor has been destroyed as a result of a
	 * transition.
	 */
	private boolean active;

}
