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
package com.neocoders.jandal.examples.chitChat;

import com.neocoders.jandal.core.Controller;
import com.neocoders.jandal.core.EventProcessor;
import com.neocoders.jandal.core.JandalCoreException;
import com.neocoders.jandal.core.Params;
import com.neocoders.jandal.core.State;

public class ChatClientController extends Controller {

	public ChatClientController(String name) throws JandalCoreException {
		super(name);
	}

	protected void onStart() throws JandalCoreException {
		messages = new StringBuffer();

		setOutput("name", ChatClientController.this.getName());
		setOutput("messages", "(no messages yet)");

		addInitialState(new State("initialState") {
			protected void onEntry() throws JandalCoreException {
				setOutput("template", "chatClient.ftl");

				addParentEventProcessor(new EventProcessor("messagePosted") {
					protected void onEvent() throws JandalCoreException {
						String sender = getStringParam("sender");
						String message = getStringParam("message");
						messages.append(sender + ": " + message);
						messages.append("\n");
						setOutput("messages", messages.toString());
					}
				});
				addViewEventProcessor(new EventProcessor("messagePosted") {
					protected void onEvent() throws JandalCoreException {
						fireChildEvent("messagePosted", new Params().add(
								"sender", ChatClientController.this.getName())
								.add("message", getStringParam("message")));
					}
				});
			}
		});
	}

	private StringBuffer messages;
}
