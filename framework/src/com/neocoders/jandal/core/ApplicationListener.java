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
 * Notifies of events on {@link Application}s. Since this is a <i>pull-MVC</i>
 * framework, where the view "pulls" everything it needs from a
 * {@link Controller} all at once, the events coming from a {@link Controller}
 * are just enough to let the view know when it needs to resynchronise itself
 * with the {@link Controller}, IE. when the {@link Controller} has updated
 * (changed state and/or written output).
 * 
 * @author lindsay
 * 
 */
public interface ApplicationListener {

	/**
	 * The given {@link Application} is about to start up.
	 */
	public void applicationStarting(Application app);

	/**
	 * The given {@link Controller} has started up.
	 */
	public void controllerStarting(Controller controller);

	/**
	 * The given {@link Controller} has stopped.
	 */
	public void controllerStopping(Controller controller);

	/**
	 * The given {@link Controller} has changed active {@link State} or written
	 * output. When the listener is a view, this indicates to the view that it
	 * will need to resynchronise itself with the {@link Controller} when it
	 * next renders.
	 */
	public void controllerUpdated(Controller controller);

	/**
	 * The given {@link Application} is about to stop;
	 */
	public void applicationStopping(Application app);

	/**
	 * The given {@link Application} is about to be destroyed
	 */
	public void applicationDestroying(Application app);
}
