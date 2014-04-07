package com.vijaysharma.ehyo.api.plugins.search;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import retrofit.RestAdapter;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.plugins.search.models.QueryByNameResponse;

public class SearchLibraryPlugin implements Plugin {

	@Override
	public String name() {
		return "search";
	}

	@Override
	public void configure(OptionParser parser) {

	}

	@Override
	public List<PluginAction> execute(OptionSet options, Service service) {
		RestAdapter restAdapter = new RestAdapter.Builder()
	    	.setEndpoint("http://search.maven.org")
	    	.build();
		
		MavenService maven = restAdapter.create(MavenService.class);
		QueryByNameResponse name = maven.searchByName("guice");
//		name.getResponse().;
		return Lists.newArrayList();
	}
}
