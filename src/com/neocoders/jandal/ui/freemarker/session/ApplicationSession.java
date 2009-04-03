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

import java.util.*;

import com.neocoders.jandal.core.*;

public class ApplicationSession {
	public ApplicationSession(String url, boolean embedded, Application app)
			throws JandalCoreException {
		this.url = url;
		this.embedded = embedded;
		this.controllersMap = new HashMap();
		this.application = app;
		app.addApplicationListener(new ApplicationListener() {

			public void applicationStopping(Application app) {
			}

			public void controllerStarting(Controller controller) {
				ControllerSession cf = new ControllerSession(
						ApplicationSession.this, controller);
				try {
					controllersMap.put(controller.getId(), cf);
					if (controller.isRoot()) {
						rootControllerSession = cf;
					}
				} catch (JandalCoreException e) {
					throw new RuntimeException(e);
				}
			}

			public void controllerStopping(Controller controller) {
				try {
					controllersMap.remove(controller.getId());
				} catch (JandalCoreException e) {
					throw new RuntimeException(e);
				}
			}

			public void applicationDestroying(Application app) {
			}

			public void applicationStarting(Application app) {
			}

			public void controllerUpdated(Controller controller) {
				try {
					((ControllerSession) controllersMap.get(controller.getId()))
							.setRendered(false);
				} catch (JandalCoreException e) {
					throw new RuntimeException(e);
				}
			}
		});
		this.application.start();
	}

	public String getId() {
		return application.getId();
	}

	public String getRequestUri() {
		return url;
	}

	public boolean getEmbedded() {
		return this.embedded;
	}

	public Application getApplication() {
		return application;
	}

	public ControllerSession getRootControllerSession() {
		return (ControllerSession) this.rootControllerSession;
	}

	public ControllerSession getControllerSessionWithId(String id) {
		return (ControllerSession) this.controllersMap.get(id);
	}

	public void destroy() {
		application.destroy();
	}

	private String url;

	private boolean embedded;

	private Application application;

	private Map controllersMap;

	private ControllerSession rootControllerSession;
}
