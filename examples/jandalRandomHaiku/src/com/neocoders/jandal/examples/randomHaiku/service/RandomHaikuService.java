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
package com.neocoders.jandal.examples.randomHaiku.service;

import com.neocoders.jandal.core.Service;

public class RandomHaikuService extends Service {
	public RandomHaikuService(String name) {
		super(name);
		currentHaiku = 0;
	}

	public String getHaiku() {
		currentHaiku = (currentHaiku + 1) % 5;
		switch (currentHaiku) {
		case 0:
			return "a smashed beer bottle\nfills the skate park with diamonds\nthesun in each one";
		case 1:
			return "silent at midnight\non the wall in the bathroom\na giant cranefly";
		case 2:
			return "in morning sunlight\nsteam from the cats yawn\nsmell of salmon";
		case 3:
			return "girlfriend not looking\nhis funny face\nfading";
		case 4:
			return "sudden speed wobble\nthe chips in the asphalt\nsharp and bright";
		case 5:
			return "outside the window\nleaves chattering on the path\nNor-Wester arrives";
		default:
			return "coffee on the bus\nin his rear-view mirror\nthe driver watching";
		}
	}

	private int currentHaiku;
}
