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
package com.neocoders.jandal.examples.addressBook.service;

import java.util.*;

import com.neocoders.jandal.core.*;

/**
 * Address management service. This is a simple dummy in-memory storage service
 * for demonstration purposes only.
 * 
 * @author lindsay
 * 
 */
public class AddressManagementService extends Service {
	public AddressManagementService(String name, AddressDao dao) {
		super(name);
		this.dao = dao;
		this.setSynchronized(true);
	}

	public Address addAddress(String name, String email, String url) {
		return dao.addAddress(name, email, url);
	}

	public Address getAddress(String id) throws Exception {
		return dao.getAddress(id);
	}

	public Address updateAddress(String id, String name, String email,
			String url) throws Exception {
		return dao.updateAddress(id, name, email, url);
	}

	public Address deleteAddress(String id) throws Exception {
		return dao.deleteAddress(id);
	}

	public List getAddresses() {
		return dao.getAddresses();
	}

	private AddressDao dao;
}
