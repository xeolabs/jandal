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
package com.neocoders.jandal.examples.addressBook.app.frame.addressBook.management;

import com.neocoders.jandal.core.*;
import com.neocoders.jandal.examples.addressBook.service.Address;
import com.neocoders.jandal.examples.addressBook.service.AddressManagementService;

/**
 * Controls the address book proper
 * 
 * @author lindsay
 * 
 */
public class AddressBookManagementController extends Controller {

	public AddressBookManagementController() throws JandalCoreException {
		super("addressBookManagementController");
	}

	protected void onStart() throws JandalCoreException {

		/*
		 * Initial state in which we are managing existing addresses. Note that
		 * we lock the storage service in this state,
		 */
		addInitialState(new State("listing") {
			protected void onEntry() throws JandalCoreException {

				final AddressManagementService ams = (AddressManagementService) getService("addressManagementService");

				setOutput("template", "addressList.ftl");
				setOutput("addressList", ams.getAddresses());

				addViewEventProcessor(new EventProcessor("update") {
					protected void onEvent() throws JandalCoreException {
						doTransition("updating", new Params().add("id",
								getParam("id")));
					}
				});

				addViewEventProcessor(new EventProcessor("create") {
					protected void onEvent() throws JandalCoreException {
						doTransition("creating");
					}
				});

				addViewEventProcessor(new EventProcessor("delete") {
					protected void onEvent() throws JandalCoreException {
						doTransition("deleting", new Params().add("id",
								getParam("id")));
					}
				});

				addViewEventProcessor(new EventProcessor("list") {
					protected void onEvent() throws JandalCoreException {
						doTransition("listing");
					}
				});
			}
		});

		/*
		 * State in which we are updating an existing address.
		 */
		addState(new State("updating") {
			protected void onEntry() throws JandalCoreException {

				final AddressManagementService ams = (AddressManagementService) getService("addressManagementService");

				setOutput("template", "updateAddress.ftl");

				/*
				 * Get the address to update and put it on an output
				 */
				final String id = (String) getParam("id");
				Address address;
				try {
					address = ams.getAddress(id);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				setOutput("name", address.getName());
				setOutput("nameError", "");

				setOutput("email", address.getEmail());
				setOutput("emailError", "");

				setOutput("url", address.getUrl());
				setOutput("urlError", "");

				/*
				 * Process "save" action
				 */
				addViewEventProcessor(new EventProcessor("save") {
					protected void onEvent() throws JandalCoreException {

						final String name = getStringParam("name").trim();
						final String email = getStringParam("email").trim();
						final String url = getStringParam("url").trim();

						boolean errors = false;
						if (name.length() == 0) {
							setOutput("nameError", "Name cannot be empty");
							errors = true;
						}
						if (!errors) {
							try {
								ams.updateAddress(id, name, email, url);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
							doTransition("listing");
						} else {
							setOutput("name", name);
							setOutput("email", email);
							setOutput("url", url);
						}
					}
				});

				/*
				 * Process "cancel" action
				 */
				addViewEventProcessor(new EventProcessor("cancel") {
					protected void onEvent() throws JandalCoreException {
						doTransition("listing");
					}
				});
			}
		});

		/*
		 * State in which we are updating an existing address.
		 */
		addState(new State("creating") {
			protected void onEntry() throws JandalCoreException {

				final AddressManagementService ams = (AddressManagementService) getService("addressManagementService");

				setOutput("template", "createAddress.ftl");

				setOutput("name", "");
				setOutput("nameError", "");

				setOutput("email", "");
				setOutput("emailError", "");

				setOutput("url", "");
				setOutput("urlError", "");

				/*
				 * Process "save" action
				 */
				addViewEventProcessor(new EventProcessor("save") {
					protected void onEvent() throws JandalCoreException {

						final String name = getStringParam("name").trim();
						final String email = getStringParam("email").trim();
						final String url = getStringParam("url").trim();

						boolean errors = false;
						if (name.length() == 0) {
							setOutput("nameError", "Name cannot be empty");
							errors = true;
						}
						if (!errors) {
							try {
								ams.addAddress(name, email, url);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
							doTransition("listing");
						} else {
							setOutput("name", name);
							setOutput("email", email);
							setOutput("url", url);
						}
					}
				});

				/*
				 * Process "cancel" action
				 */
				addViewEventProcessor(new EventProcessor("cancel") {
					protected void onEvent() throws JandalCoreException {
						doTransition("listing");
					}
				});
			}
		});

		/*
		 * State in which we are updating an existing address.
		 */
		addState(new State("deleting") {
			protected void onEntry() throws JandalCoreException {

				final AddressManagementService ams = (AddressManagementService) getService("addressManagementService");

				setOutput("template", "deleteAddress.ftl");

				/*
				 * Get the address to delete and put it on an output
				 */
				final String id = (String) getParam("id");
				Address address;
				try {
					address = ams.getAddress(id);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				setOutput("name", address.getName());
				setOutput("email", address.getEmail());
				setOutput("url", address.getUrl());

				/*
				 * Process "delete" action
				 */
				addViewEventProcessor(new EventProcessor("delete") {
					protected void onEvent() throws JandalCoreException {
						try {
							ams.deleteAddress(id);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						doTransition("listing");
					}
				});

				/*
				 * Process "cancel" action
				 */
				addViewEventProcessor(new EventProcessor("cancel") {
					protected void onEvent() throws JandalCoreException {
						doTransition("listing");
					}
				});
			}

			protected void onExit() throws JandalCoreException {

			}
		});

		/*
		 * State in which an error occurred
		 */
		addState(new State("error") {
			protected void onEntry() throws JandalCoreException {
				setOutput("template", "error.ftl");

				setOutput("message", getParam("message"));

				/*
				 * Process "ok" action
				 */
				addViewEventProcessor(new EventProcessor("ok") {
					protected void onEvent() throws JandalCoreException {
						doTransition("listing");
					}
				});
			}
		});

	}
}
