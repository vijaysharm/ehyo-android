package com.vijaysharma.ehyo.api.plugins.search;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginBundle;

public class SearchLibraryPlugin implements Plugin {

	@Override
	public String name() {
		return "search";
	}

	@Override
	public void configure(OptionParser parser) {

	}

	@Override
	public void execute(OptionSet options, PluginBundle bundle) {

	}
}
