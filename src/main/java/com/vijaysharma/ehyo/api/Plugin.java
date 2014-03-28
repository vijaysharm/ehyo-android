package com.vijaysharma.ehyo.api;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public interface Plugin {
	String name();
	void configure(OptionParser parser);
	List<? extends PluginAction> execute(OptionSet options, Service service);
}
