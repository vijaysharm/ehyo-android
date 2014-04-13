package com.vijaysharma.ehyo.api.plugins.manifestpermissions;

import java.util.List;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.Service;

public class Permissions implements Plugin {
	@Override
	public String name() {
		return "manifest-permissions";
	}

	@Override
	public List<PluginAction> execute(List<String> args, Service service) {
		ManifestAction action = service.createManifestAction();
		List<PluginAction> result = Lists.newArrayList();

		if (args.contains("--add")) {
			action.addPermission("android.permission.INTERNET");
			result.add(action);
		}
		
		if (args.contains("--remove")) {
			action.removePermission("android.permission.INTERNET");
			result.add(action);
		}
		
		return result;
	}
}
