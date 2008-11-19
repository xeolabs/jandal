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
import java.util.Iterator;
import java.util.Map;

class ServiceCache {
	public ServiceCache(String appId, ServiceSet serviceSet) {
		this.parent = null;
		this.appId = appId;
		this.serviceSet = serviceSet;
		this.lockedServices = new HashMap();
	}

	public ServiceCache(ServiceCache parent) {
		this.parent = parent;
		this.appId = parent.appId;
		this.serviceSet = parent.serviceSet;
		this.lockedServices = new HashMap();
	}

	public Service lockService(String serviceName, long timeout)
			throws JandalCoreException {
		Service service = tryLockService(serviceName);
		while (service == null) {
			try {
				Thread.sleep(100);
				timeout -= 100;
				if (timeout <= 0) {
					return null;
				}
			} catch (Exception e) {
			}
			service = tryLockService(serviceName);
		}
		return service;
	}

	public Service tryLockService(final String serviceName)
			throws JandalCoreException {

		/*
		 * Get service, exception if not found
		 */
		Service service = serviceSet.getService(serviceName);
		if (service == null) {
			throw new JandalCoreException("Service not found: " + serviceName);
		}
		/*
		 * Exception if service does not require synchronisation
		 */
		if (!service.isSynchronized()) {
			throw new JandalCoreException("Service not lockable: "
					+ serviceName);
		}

		/*
		 * Exception if service currently locked here or higher in the tree
		 */
		if (this.hasLock(service)) {
			throw new JandalCoreException(
					"Service already locked by current application element: "
							+ serviceName);
		}
		for (ServiceCache sc = this.parent; sc != null; sc = sc.parent) {
			if (sc.hasLock(service)) {
				throw new JandalCoreException(
						"Service already locked by ancestor application element: "
								+ serviceName);
			}
		}
		/*
		 * Try to get immediate lock on service
		 */
		boolean locked = lockService(service);
		if (!locked) {
			return null;
		}
		return service;
	}

	private boolean hasLock(Service service) {
		return (lockedServices.containsKey(service.hashCode()));
	}

	private boolean lockService(Service service) {
		if (!service.getLock().tryAcquire(appId)) {
			return false;
		}
		lockedServices.put(service.hashCode(), service);
		return true;
	}

	private void unlockService(Service service) {
		service.getLock().release();
		lockedServices.remove(service.hashCode());
	}

	public void releaseLocks() {
		for (Iterator i = lockedServices.keySet().iterator(); i.hasNext();) {
			Service service = (Service) lockedServices.get(i.next());
			unlockService(service);
		}
	}

	public final Service getService(final String serviceName)
			throws JandalCoreException {
		Service service = serviceSet.getService(serviceName);
		if (service == null) {
			throw new JandalCoreException("Service not found: " + serviceName);
		}
		if (service.isSynchronized()) {
			for (ServiceCache sc = this; sc != null; sc = sc.parent) {
				if (sc.hasLock(service)) {
					return service;
				}
			}
			throw new JandalCoreException(
					"Cannot get service - "
							+ "service is synchronised, so you must have a lock on it first: "
							+ serviceName);
		}
		return service;
	}

	public final void releaseService(final String serviceName)
			throws JandalCoreException {
		Service service = serviceSet.getService(serviceName);
		if (service == null) {
			throw new JandalCoreException("Service not found: " + serviceName);
		}
		if (!service.isSynchronized()) {
			throw new JandalCoreException(
					"Cannot unlock service - service is not locked: "
							+ serviceName);
		}
		if (!hasLock(service)) {
			throw new JandalCoreException("Cannot unlock service here - "
					+ "this element does not hold a lock on it: " + serviceName);
		}
		unlockService(service);
	}

	private ServiceCache parent;

	private String appId;

	private ServiceSet serviceSet;

	private Map lockedServices;
}
