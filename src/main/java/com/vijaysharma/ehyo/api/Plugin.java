package com.vijaysharma.ehyo.api;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public interface Plugin {
	String name();
	void configure(OptionParser parser);
	List<PluginAction> execute(OptionSet options, Service service);
}
