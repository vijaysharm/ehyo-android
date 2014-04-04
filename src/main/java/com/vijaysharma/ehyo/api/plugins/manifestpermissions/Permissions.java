package com.vijaysharma.ehyo.api.plugins.manifestpermissions;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;

public class Permissions implements Plugin {
	private OptionSpecBuilder add;
	private OptionSpecBuilder remove;

	@Override
	public String name() {
		return "manifest-permissions";
	}

	@Override
	public void configure(OptionParser parser) {
		this.add = parser.accepts("add");
		this.remove = parser.accepts("remove");
	}

	@Override
	public List<PluginAction> execute(OptionSet options, Service service) {
		ManifestAction action = service.createManifestAction();
		action.addPermission("android.permission.INTERNET");
		action.addPermission("android.permission.ACCESS_CHECKIN_PROPERTIES");
		
		List<PluginAction> result = Lists.newArrayList();
		result.add(action);
		
		return result;
	}
}
