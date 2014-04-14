package com.vijaysharma.ehyo.api.plugins.manifestpermissions;

import java.util.List;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.ManifestAction;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.PluginAction;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.utils.OptionSelector;

public class Permissions implements Plugin {
	private final PermissionRegistry registry;
	
	public Permissions() {
		this(new PermissionRegistry());
	}
	
	Permissions(PermissionRegistry registry) {
		this.registry = registry;
	}

	@Override
	public String name() {
		return "manifest-permissions";
	}

	@Override
	public List<PluginAction> execute(List<String> args, Service service) {
		
		List<ProjectManifest> manifests = service.getManifests();
		OptionSelector<ProjectManifest> selector = service.createSelector(ProjectManifest.class);
		List<ProjectManifest> selectedManifests = selector.select(manifests, false);
		
		ManifestAction action = service.createManifestAction();
		List<PluginAction> result = Lists.newArrayList();

		for ( ProjectManifest manifest : selectedManifests ) {
			action.addPermission(manifest, "android.permission.INTERNET");
		}
		result.add(action);
		
		return result;
	}
}
