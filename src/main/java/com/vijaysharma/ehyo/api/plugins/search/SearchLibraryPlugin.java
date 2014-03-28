package com.vijaysharma.ehyo.api.plugins.search;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;

public class SearchLibraryPlugin implements Plugin {

	@Override
	public String name() {
		return "search";
	}

	@Override
	public void configure(OptionParser parser) {

	}

	@Override
	public List<? extends PluginAction> execute(OptionSet options, Service service) {
		return Lists.newArrayList();
	}
}
