package com.neocoders.jandal.examples.numberguess.app;

import com.neocoders.jandal.core.*;

public class CorrectState extends State {
	public CorrectState() throws JandalCoreException {
		super("correctState");
	}

	public void onEntry() throws JandalCoreException {

		setOutput("template", "correct.ftl");

		addViewEventProcessor(new EventProcessor("playAgain") {
			public void onEvent() throws JandalCoreException {
				doTransition("guessingState");
			}
		});
	}
}
