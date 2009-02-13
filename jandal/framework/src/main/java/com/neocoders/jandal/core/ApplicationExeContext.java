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

import java.util.*;

/**
 * Provides application-context information and other resources to all elements
 * within an {@link Application}.
 * 
 * @author lindsay
 * 
 */
 class ApplicationExeContext {
	public ApplicationExeContext(ServiceSet serviceSet, Application app) {
		this.serviceSet = serviceSet;
		this.app = app;
		this.controllerPathMap = new HashMap();
		this.controllerIdMap = new HashMap();
		this.appListeners = new LinkedList();
	}

	/**
	 * Returns ID of the {@link Application} instance.
	 * 
	 * @return
	 */
	public String getAppId() {
		return this.app.getId();
	}

	public ServiceSet getServiceSet() {
		return serviceSet;
	}

	public Application getApplication() {
		return this.app;
	}

	/**
	 * Registers a controller that has become active. It must be active when
	 * this is called.
	 * 
	 */
	public void registerActiveController(Controller controller) {
		try {
			this.controllerPathMap.put(controller.getPath(), controller);
			this.controllerIdMap.put(controller.getId(), controller);
		} catch (JandalCoreException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deregisters a controller that is about to become inactive. It must be
	 * active when this is called.
	 * 
	 */
	public void deregisterActiveController(Controller controller) {
		try {
			this.controllerPathMap.remove(controller.getPath());
			this.controllerIdMap.remove(controller.getId());
		} catch (JandalCoreException e) {
			throw new RuntimeException(e);
		}
	}

	public Controller getControllerOnPath(String path) {
		return (Controller) this.controllerPathMap.get(path);
	}

	public Controller getControllerWithId(String controllerId) {
		return (Controller) this.controllerIdMap.get(controllerId);
	}

	public void addApplicationListener(ApplicationListener appListener) {
		if (!this.appListeners.contains(appListener)) {
			this.appListeners.add(appListener);
		}
	}

	public void removeApplicationListener(ApplicationListener appListener) {
		if (this.appListeners.contains(appListener)) {
			this.appListeners.remove(appListener);
		}
	}

	public void applicationStarting() {
		for (Iterator i = this.appListeners.iterator(); i.hasNext();) {
			((ApplicationListener) i.next()).applicationStarting(this.app);
		}
	}

	public void controllerStarting(Controller controller) {
		for (Iterator i = this.appListeners.iterator(); i.hasNext();) {
			((ApplicationListener) i.next()).controllerStarting(controller);
		}
	}

	public void controllerStopping(Controller controller) {
		for (Iterator i = this.appListeners.iterator(); i.hasNext();) {
			((ApplicationListener) i.next()).controllerStopping(controller);
		}
	}

	public void controllerUpdated(Controller controller) {
		for (Iterator i = this.appListeners.iterator(); i.hasNext();) {
			((ApplicationListener) i.next()).controllerUpdated(controller);
		}
	}

	public void applicationStopping() {
		for (Iterator i = this.appListeners.iterator(); i.hasNext();) {
			((ApplicationListener) i.next()).applicationStopping(this.app);
		}
	}

	public void applicationDestroying() {
		for (Iterator i = this.appListeners.iterator(); i.hasNext();) {
			((ApplicationListener) i.next()).applicationDestroying(this.app);
		}
	}

	private Application app;

	private ServiceSet serviceSet;

	private Map controllerPathMap;

	private Map controllerIdMap;

	private List appListeners;
}
