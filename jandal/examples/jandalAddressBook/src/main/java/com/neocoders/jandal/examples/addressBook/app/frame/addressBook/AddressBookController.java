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
package com.neocoders.jandal.examples.addressBook.app.frame.addressBook;

import com.neocoders.jandal.core.*;
import com.neocoders.jandal.examples.addressBook.app.frame.addressBook.management.AddressBookManagementController;
import com.neocoders.jandal.examples.addressBook.service.*;

/**
 * Controls the actual address book. This controller has two main states,
 * "browsing", in which the address book is being browsed, and "managing", in
 * which it is being managed. The "managing" state has a child-controller,
 * AddressBookManagementController, that controls the actual address book
 * management.
 * </p>
 * We start in the "browsing" state. On entry, that state gets the address list
 * from AddressBrowsingService and puts it on an output. It can access that
 * service without locking it because the service is not synchronised. Then on a
 * "manage" view event we transition to "managing". The "managing" state then
 * attempts to get a lock on the AddressManagementService, which is a
 * synchronised service that provides address querying, creation, removal, and
 * update. If that fails, then somebody else is currently in the "managing"
 * state and holding the lock, so we transition to a third "managementBusy"
 * state where we can try to reenter the "managing" state later.
 * </p>
 * When we are finally in the "managing" state and have the lock, then
 * AddressBookManagementController is started. The "managing" state holds the
 * lock for as long as it is active, allowing AddressBookManagementController
 * free access to AddressManagementService. Anybody else entering the "managing"
 * state during this time is redirected to the "managementBusy" state as
 * described above.
 * </p>
 * When we're done managing the addresses, a "browse" view event takes us back
 * to the "browsing" state.
 * </p>
 * 
 * 
 * @author lindsay
 * 
 */
public class AddressBookController extends Controller {

	public AddressBookController() throws JandalCoreException {
		super("addressBookController");
	}

	protected void onStart() throws JandalCoreException {

		/*
		 * Initial state in which we are browsing the address list.
		 */
		addInitialState(new State("browsing") {
			protected void onEntry() throws JandalCoreException {

				setOutput("template", "browsing.ftl");

				/*
				 * Get address list from address browsing service and put on
				 * output. This service is not synchronised, so we don't need to
				 * get a lock on it first.
				 */
				AddressBrowsingService abs = (AddressBrowsingService) getService("addressBrowsingService");
				setOutput("addressList", abs.getAddresses());

				/*
				 * A "manage" view event takes us into the "managing" state.
				 */
				addViewEventProcessor(new EventProcessor("manage") {
					protected void onEvent() throws JandalCoreException {
						doTransition("managing");
					}
				});
			}
		});

		/*
		 * State in which we entered the "managing" state but were then
		 * redirected here because AddressBookManagementController is locked.
		 * Note that this state is defined before the "managing" state because
		 * otherwise the doTransition in that state's onEntry would not be able
		 * to find it.
		 */
		addState(new State("managementBusy") {
			protected void onEntry() throws JandalCoreException {
				setOutput("template", "busy.ftl");

				/*
				 * A "browse" view event take us into the "browsing" state.
				 */
				addViewEventProcessor(new EventProcessor("browse") {
					protected void onEvent() throws JandalCoreException {
						doTransition("browsing");
					}
				});

				/*
				 * A "manage" view event attempts once more to take us into the
				 * "managing" state.
				 */
				addViewEventProcessor(new EventProcessor("manage") {
					protected void onEvent() throws JandalCoreException {
						doTransition("managing");
					}
				});
			}
		});

		/*
		 * State in which we are managing addresses. This state has a child
		 * controller, AddressBookManagementController, which does the actual
		 * management. On entry, this state attempts to lock the
		 * AddressManagementService for the child controller to have exclusive
		 * access to. When exited, this state releases the lock automatically.
		 * If this state fails to acquire the lock, then some other instance of
		 * this application has it, so this states makes a transition to
		 * "managementBusy" state.
		 */
		addState(new State("managing") {
			protected void onEntry() throws JandalCoreException {

				if (lockService("addressManagementService", 100L) == null) {
					doTransition("managementBusy");
					return;
				}

				setOutput("template", "managing.ftl");

				/*
				 * Child controller where the address management is actually
				 * done.
				 */
				addChildController(new AddressBookManagementController());

				/*
				 * A "browse" view event takes us back to "browsing" state.
				 */
				addViewEventProcessor(new EventProcessor("browse") {
					protected void onEvent() throws JandalCoreException {
						doTransition("browsing");
					}
				});
			}
		});
	}
}
