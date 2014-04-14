package com.vijaysharma.ehyo.api.plugins.manifestpermissions;

import java.util.List;
import java.util.Set;

import com.vijaysharma.ehyo.api.Plugin;
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
	public void execute(List<String> args, Service service) {
		String argValue = getArgValue("--add", args);
		if ( argValue != null ) {
			List<ProjectManifest> selectedManifests = selectManifests(service);
			Set<String> permissions = registry.find(argValue);
			
			for ( ProjectManifest manifest : selectedManifests ) {
				manifest.addPermissions(permissions);
			}
		}
		
		argValue = getArgValue("--remove", args);
		if ( argValue != null ) {
			List<ProjectManifest> selectedManifests = selectManifests(service);	
			// TODO: Should probably get the set from the manifests themselves
			Set<String> permissions = registry.find(argValue);
			
			for ( ProjectManifest manifest : selectedManifests ) {
				manifest.removePermissions(permissions);
			}
		}
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
