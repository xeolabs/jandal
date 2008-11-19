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
 * Address storage service. This is a simple dummy in-memory storage service for
 * demonstration purposes only.
 * 
 * @author lindsay
 * 
 */
public class AddressDao {
    public AddressDao() {
	this.addresses = new HashMap();
	this.nextId = 0;
	this.idStack = new Stack();
	/*
	 * Prime with a few initial addresses
	 */
	try {
	    addAddress("Lindsay Kay", "lindsay.stanley.kay@gmail.com",
		    "http://www.neocoders.com");
	    addAddress("Captain Kirk", "captain.kirk@gmail.com",
		    "http://www.startrek.com");
	    addAddress("Horatio Hornblower", "horatio.hornblower@gmail.com",
		    "http://www.hornblower.com");
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
    }

    private String getId() {
	if (idStack.isEmpty()) {
	    return "" + nextId++;
	}
	return (String) idStack.pop();
    }

    private void putId(String id) {
	idStack.push(id);
    }

    public synchronized Address addAddress(String name, String email, String url) {
	String id = getId();
	Address address = new Address(id, name, email, url);
	addresses.put(id, address);
	return address;
    }

    public synchronized Address getAddress(String id) throws Exception {
	Address address = (Address) addresses.get(id);
	if (address == null) {
	    throw new Exception("Address not found");
	}
	return address;
    }

    public synchronized Address updateAddress(String id, String name,
	    String email, String url) throws Exception {
	Address address = (Address) addresses.get(id);
	if (address == null) {
	    throw new Exception("Address not found");
	}
	addresses.remove(id);
	Address newAddress = new Address(id, name, email, url);
	addresses.put(id, newAddress);
	return newAddress;
    }

    public synchronized Address deleteAddress(String id) throws Exception {
	Address address = (Address) addresses.get(id);
	if (address == null) {
	    throw new Exception("Address not found");
	}
	addresses.remove(id);
	putId(id);
	return address;
    }

    public synchronized List getAddresses() {
	List list = new LinkedList();
	for (Iterator i = addresses.keySet().iterator(); i.hasNext();) {
	    list.add(addresses.get(i.next()));
	}
	return list;
    }

    private Stack idStack;

    private int nextId;

    private Map addresses;

}
