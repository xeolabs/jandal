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
package com.neocoders.jandal.examples.treeMap.frame.cell;

import com.neocoders.jandal.core.*;

public class CellController extends Controller {

	public CellController(String name, final int width, final int height,
			boolean axis, final String colour) throws JandalCoreException {
		super(name);
		this.width = width;
		this.height = height;
		this.axis = axis;
		this.colour = colour;
	}

	protected void onStart() throws JandalCoreException {
		this.addInitialState(new State("undivided") {

			protected void onEntry() throws JandalCoreException {
				setOutput("template", "undivided.ftl");
				setOutput("colour", colour);
				setOutput("width", "" + width);
				setOutput("height", "" + height);

				this.addViewEventProcessor(new EventProcessor("clicked") {
					protected void onEvent() throws JandalCoreException {
						if (axis) {
							doTransition("horDivided");
						} else {
							doTransition("vertDivided");
						}
					}
				});
			}
		});

		this.addState(new State("horDivided") {
			protected void onEntry() throws JandalCoreException {
				setOutput("template", "horDivided.ftl");
				setOutput("width", "" + width);
				setOutput("height", "" + height);

				/*
				 * Child controllers for the left and right sub-cells
				 */
				addChildController(new CellController("a", width / 2, height,
						!axis, getRandomColourHexString()));
				addChildController(new CellController("b", width / 2, height,
						!axis, getRandomColourHexString()));
			}
		});

		this.addState(new State("vertDivided") {
			protected void onEntry() throws JandalCoreException {
				setOutput("template", "vertDivided.ftl");
				setOutput("width", "" + width);
				setOutput("height", "" + height);

				/*
				 * Child controllers for the top and bottom sub-cells
				 */
				addChildController(new CellController("a", width, height / 2,
						!axis, getRandomColourHexString()));
				addChildController(new CellController("b", width, height / 2,
						!axis, getRandomColourHexString()));

			}
		});
	}

	public String getRandomColourHexString() {
		return "#" + Integer.toString(random(), 16)
				+ Integer.toString(random(), 16)
				+ Integer.toString(random(), 16);
	}

	private final int random() {
		return (Math.abs((int) (Math.random() * 1000.0))) % 255;
	}

	private int width;

	private int height;

	private boolean axis;

	private String colour;
}