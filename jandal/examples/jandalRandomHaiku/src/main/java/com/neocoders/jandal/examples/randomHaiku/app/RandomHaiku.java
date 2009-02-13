package com.neocoders.jandal.examples.randomHaiku.app;

import com.neocoders.jandal.core.Application;
import com.neocoders.jandal.core.JandalCoreException;
import com.neocoders.jandal.examples.randomHaiku.app.frame.FrameController;

public class RandomHaiku extends Application {

	protected void onStart() throws JandalCoreException {
		this.setRootController(new FrameController("parentController"));
	}
}
