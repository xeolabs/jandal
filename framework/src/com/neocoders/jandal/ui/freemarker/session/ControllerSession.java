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
package com.neocoders.jandal.ui.freemarker.session;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.neocoders.jandal.core.*;

public class ControllerSession {
	public ControllerSession(ApplicationSession appSession,
			Controller controller) {
		this.appSession = appSession;
		this.parentControllerSession = null;
		this.controller = controller;
		this.nextSynchKey = 0L;
	}

	public ControllerSession(ControllerSession parentControllerSession,
			Controller controller) {
		this.appSession = parentControllerSession.getApplicationSession();
		this.parentControllerSession = parentControllerSession;
		this.controller = controller;
		this.nextSynchKey = 0L;
	}

	public String getId() {
		try {
			return controller.getId();
		} catch (JandalCoreException e) {
			throw new RuntimeException(e);
		}
	}

	public String getName() {
		return controller.getName();
	}

	public String getPath() {
		try {
			return controller.getPath();
		} catch (JandalCoreException e) {
			throw new RuntimeException(e);
		}
	}

	public ApplicationSession getApplicationSession() {
		return this.appSession;
	}

	public Collection getChildControllerSessions() {
		List list = new LinkedList();
		try {
			for (Iterator i = controller.getChildControllers().iterator(); i
					.hasNext();) {
				Controller childController = (Controller) i.next();
				list.add(this.appSession.getControllerSessionWithId(childController
						.getId()));
			}
		} catch (JandalCoreException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	public ControllerSession getChildControllerSessionWithName(String name) {
		try {
			Controller childController = controller.getChildController(name);
			if (childController == null) {
				return null;
			}
			return this.appSession
					.getControllerSessionWithId(childController.getId());
		} catch (JandalCoreException e) {
			throw new RuntimeException(e);
		}
	}

	public Controller getController() {
		return this.controller;
	}

	public boolean getRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public void nextSynchKey() {
		this.nextSynchKey++;
	}

	public String getSynchKey() {
		return "" + nextSynchKey;
	}

	private ApplicationSession appSession;

	private ControllerSession parentControllerSession;

	private Controller controller;

	private boolean rendered;

	private long nextSynchKey;
}
