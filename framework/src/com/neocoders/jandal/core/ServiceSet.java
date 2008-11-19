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
 * Set of services for use by {@link Application}s.
 * 
 * @author lindsay
 * 
 */
public class ServiceSet {
	/**
	 * Creates new empty service set.
	 * 
	 * 
	 */
	public ServiceSet() {
		this.services = new HashMap();
		this.servicesList = new LinkedList();
	}

	/**
	 * Notifies all services of the startup of an application that is using this
	 * service set.
	 * 
	 * @param app
	 *            Id of application that has started up.
	 */
	final synchronized void applicationStarted(String appId) {
		for (Iterator i = servicesList.iterator(); i.hasNext();) {
			((Service) i.next()).applicationStarted(appId);
		}
	}

	/**
	 * Notifies all services of the stopping of an application that was using
	 * this service set. This is useful for causing services to release any
	 * resources that they are holding for the application.
	 * 
	 * @param appId
	 *            Id of application that has stopped.
	 */
	final synchronized void applicationStopped(String appId) {
		for (Iterator i = servicesList.iterator(); i.hasNext();) {
			((Service) i.next()).applicationStopped(appId);
			((Service) i.next()).getLock().releaseIfHeld(appId);
		}
	}

	/**
	 * Stores a service under a name. *
	 * 
	 * @param name
	 *            Name under which to store the service.
	 * @param service
	 *            Service to store.
	 */
	public final void addService(String name, Service service) {
		this.services.put(name, service);
	}

	/**
	 * Gets the service stored under the given name.
	 * 
	 * @param name
	 *            Name of the service to get.
	 * @return The service.
	 * @throws JandalCoreException
	 *             service name invalid.
	 */
	public final Service getService(String name) throws JandalCoreException {
		return (Service) services.get(Utils.validateName(name, "Service name"));
	}

	/**
	 * Returns true if the service with the given name is contained.
	 * 
	 * @param name
	 * @return
	 * @throws JandalCoreException
	 */
	public final boolean containsService(String name)
			throws JandalCoreException {
		name = Utils.validateName(name, "Service name");
		return this.services.containsKey(name);
	}

	/**
	 * Returns the names of the services in this set.
	 * 
	 * @return
	 */
	public final Set getServiceNames() {
		return new HashSet(services.keySet());
	}

	private Map services;

	private List servicesList;
}
