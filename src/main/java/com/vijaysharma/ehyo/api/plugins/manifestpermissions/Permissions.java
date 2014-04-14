package com.vijaysharma.ehyo.api.plugins.manifestpermissions;

import java.util.List;
import java.util.Set;

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
		List<PluginAction> result = Lists.newArrayList();
		
		String argValue = getArgValue("--add", args);
		if ( argValue != null ) {
			List<ProjectManifest> selectedManifests = selectManifests(service);
			Set<String> permissions = registry.find(argValue);
			
			ManifestAction action = service.createManifestAction();
	
			for ( ProjectManifest manifest : selectedManifests ) {
				for ( String permission : permissions ) {
					action.addPermission(manifest, permission);
				}
			}
			result.add(action);
		}
		
		argValue = getArgValue("--remove", args);
		if ( argValue != null ) {
			List<ProjectManifest> selectedManifests = selectManifests(service);	
			// TODO: Should probably get the set from the manifests themselves
			Set<String> permissions = registry.find(argValue);
			
			ManifestAction action = service.createManifestAction();
			for ( ProjectManifest manifest : selectedManifests ) {
				for ( String permission : permissions ) {
					action.removePermission(manifest, permission);
				}
			}
			result.add(action);
		}

		return result;
	}

	private List<ProjectManifest> selectManifests(Service service) {
		List<ProjectManifest> manifests = service.getManifests();
		OptionSelector<ProjectManifest> selector = service.createSelector(ProjectManifest.class);
		List<ProjectManifest> selectedManifests = selector.select(manifests, false);
		return selectedManifests;
	}
	
	private static String getArgValue(String arg, List<String> args) {
		int libIndex = args.indexOf(arg);
		
		if (libIndex == -1) {
			return null;
		}
		
		int libValueIndex = libIndex + 1;
		if ( libValueIndex >= args.size() )
			return null;
		
		return args.get(libValueIndex);
	}
}
