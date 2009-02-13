package com.neocoders.jandal.ui.freemarker.session;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * Listener that keeps track of the number of sessions that the Web application
 * is currently using and has ever used in its life cycle. So far all this does
 * is destroys the application belonging to each session as it is destroyed, so
 * as to free up any services that the application might hold locks on.
 */

public class JandalFreeMarkerAttributeListener implements
		HttpSessionAttributeListener {

	public void attributeAdded(HttpSessionBindingEvent arg0) {
	}

	public void attributeRemoved(HttpSessionBindingEvent sbe) {
		String skey = sbe.getName();

		if (skey.equals("appSession")) {
			ApplicationSession appSession = (ApplicationSession) sbe.getValue();
			if (appSession != null) {
				/*
				 * Releases any service locks
				 */
				appSession.destroy();
			}
		}

	}

	public void attributeReplaced(HttpSessionBindingEvent arg0) {
	}
}