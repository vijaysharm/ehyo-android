package com.vijaysharma.ehyo.plugins.manifestpermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.Plugin;
import com.vijaysharma.ehyo.api.ProjectManifest;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.UsageException;

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
		return "permissions";
	}

	@Override
	public String usage() {
		StringBuilder usage = new StringBuilder();
		usage.append("usage: ehyo permissions [--add <permission>] [--remove <permission>]\n\n");
		
		usage.append("Examples:\n");
		usage.append("    ehyo permissions --add internet\n");
		usage.append("    ehyo permissions --remove\n\n");
		
		usage.append("Options:\n");
		usage.append("    --add <permission>\n");
		usage.append("        Add the given permission to a manifest.\n");
		usage.append("        Provide a required permission argument used to search and display\n");
		usage.append("        a list of selectable options\n");
		usage.append("    --remove <permission>\n");
		usage.append("        Remove the given permission from a manifest.\n");
		usage.append("        Provide an optional permission argument used to display\n");

		return usage.toString();
	}	

	@Override
	public void execute(List<String> args, Service service) {
		if ( args.isEmpty() )
			throw new UsageException(usage());
		
		boolean containsAdd = args.contains("--add");
		boolean containsRemove = args.contains("--remove");
		
		if ( ! containsAdd && ! containsRemove )
			throw new UsageException(usage());
		
		if ( containsAdd ) {
			String toAdd = getArgValue("--add", args);
			add(toAdd, service);
		}
		
		if ( containsRemove ) {
			String toARemove = getArgValue("--remove", args);
			remove(toARemove, service);
		}
	}
	
	private void remove(String value, Service service) {
		if (Strings.isNullOrEmpty(value)) {
			ArrayListMultimap<String, ProjectManifest> permissions = ArrayListMultimap.create();
			for ( ProjectManifest manifest : service.getManifests() ) {
				Set<String> all = manifest.getPermissions();
				for ( String permission : all )
					permissions.put(permission, manifest);
			}
			
			if ( permissions.isEmpty() )
				throw new GentleMessageException("There are no permissions to remove :(");
				
			List<String> selectedPermissions = service.createSelector("Which permission would you like to remove?", String.class)
					.select(new ArrayList<String>(permissions.keySet()), false);
			
			for ( String permission : selectedPermissions ) {
				List<ProjectManifest> manifests = permissions.get(permission);
				
				List<ProjectManifest> selectedManifests = service.createSelector("Which manifest would you like to remove " + Joiner.on(", ").join(selectedPermissions) + " from?", ProjectManifest.class)
						.select(manifests, false);
				
				for ( ProjectManifest manifest : selectedManifests ) {
					manifest.removePermission(permission);
				}
			}
			
		} else {
			Set<String> toRemove = registry.find(value);
			if ( toRemove.isEmpty() )
				throw new GentleMessageException("No permission found that match '" + value + "'");
			
			List<ProjectManifest> toClean = Lists.newArrayList();
			for ( ProjectManifest manifest : service.getManifests() ) {
				for ( String permission : toRemove ) {
					if ( manifest.getPermissions().contains(permission) )
						toClean.add(manifest);
				}
			}
	
			if ( toClean.isEmpty() )
				throw new GentleMessageException("There are no manifests with permission " + Joiner.on(", ").join(toRemove) );

			List<ProjectManifest> selectedManifests = service.createSelector("Which manifest would you like to remove " + Joiner.on(", ").join(toRemove) + " from?", ProjectManifest.class)
					.select(toClean, false);
			
			for ( ProjectManifest manifest : selectedManifests ) 
				manifest.removePermissions(toRemove);
		}
	}
	
	private void add(String value, Service service) {
		if (Strings.isNullOrEmpty(value))
			throw new UsageException("'--add' has a required argument\n" + usage());
		
		List<String> permissions = Lists.newArrayList(registry.find(value));
		if ( permissions.isEmpty() )
			throw new GentleMessageException("No permission found that match '" + value + "'");

		List<String> selectedPermission = service.createSelector("Which permission would you like to add?", String.class)
				.select(permissions, false);
		
		List<ProjectManifest> manifestsToModify = Lists.newArrayList();
		for ( ProjectManifest manifest : service.getManifests() ) {
			Set<String> all = manifest.getPermissions();
			for ( String permission : selectedPermission ) {
				if ( ! all.contains(permission) )
					manifestsToModify.add(manifest);
			}
		}
		
		if ( manifestsToModify.isEmpty() )
			throw new GentleMessageException("All manifests contain " + Joiner.on(", ").join(selectedPermission));
		
		List<ProjectManifest> selectedManifests = service.createSelector("Which manifest would you like add " + Joiner.on(", ").join(selectedPermission) + " to?", ProjectManifest.class)
				.select(manifestsToModify, false);
		for ( ProjectManifest manifest : selectedManifests ) {
			manifest.addPermissions(Sets.newHashSet(selectedPermission));
		}
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
