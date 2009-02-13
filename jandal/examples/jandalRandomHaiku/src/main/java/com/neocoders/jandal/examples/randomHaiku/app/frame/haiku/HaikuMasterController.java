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
package com.neocoders.jandal.examples.randomHaiku.app.frame.haiku;

import com.neocoders.jandal.core.Controller;
import com.neocoders.jandal.core.EventProcessor;
import com.neocoders.jandal.core.JandalCoreException;
import com.neocoders.jandal.core.State;
import com.neocoders.jandal.examples.randomHaiku.service.RandomHaikuService;

public class HaikuMasterController extends Controller {

	public HaikuMasterController() throws JandalCoreException {
		super("haikuMaster");
	}

	protected void onStart() throws JandalCoreException {
		this.addInitialState(new State("waiting") {

			protected void onEntry() throws JandalCoreException {
				setOutput("template", "waiting.ftl");

				this.addViewEventProcessor(new EventProcessor("compose") {
					protected void onEvent() throws JandalCoreException {
						doTransition("composed");
					}
				});
			}
		});

		this.addState(new State("composed") {

			protected void onEntry() throws JandalCoreException {
				setOutput("template", "composed.ftl");

				RandomHaikuService service = (RandomHaikuService) getService("randomHaikuService");
				setOutput("haiku", service.getHaiku());

				this.addViewEventProcessor(new EventProcessor("another") {
					protected void onEvent() throws JandalCoreException {
						doTransition("composed");
					}
				});
			}
		});
	}
}
