package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.core.InternalActions.BuildActions;
import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.BuildType;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.Flavor;
import com.vijaysharma.ehyo.core.models.GradleBuild;

// TODO: Delegate component action changes to smaller classes
public class PluginActions {
	private final ImmutableMultimap.Builder<AndroidManifest, String> addedPermissions = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<AndroidManifest, String> removedPermissions = ImmutableMultimap.builder();
	
	private final ImmutableMultimap.Builder<GradleBuild, Dependency> addedDependencies = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<GradleBuild, Dependency> removedDependencies = ImmutableMultimap.builder();
	
	public void addDependency(GradleBuild build, BuildType type, Flavor flavor, String projectId) {
		Dependency dependency = new Dependency(type, flavor, projectId);
		addedDependencies.put(build, dependency);
	}
	
	public Multimap<GradleBuild, Dependency> getAddedDependencies() {
		return addedDependencies.build();
	}
	
	public void removeDependency(GradleBuild build, BuildType type, Flavor flavor, String projectId) {
		Dependency dependency = new Dependency(type, flavor, projectId);
		removedDependencies.put(build, dependency);
	}
	
	public Multimap<GradleBuild, Dependency> getRemovedDependencies() {
		return removedDependencies.build();
	}
	
	public void addPermission(AndroidManifest manifest, String permission) {
		addedPermissions.put(manifest, permission);
	}
	
	public Multimap<AndroidManifest, String> getAddedPermissions() {
		return addedPermissions.build();
	}
	
	public void removePermission(AndroidManifest manifest, String permission) {
		removedPermissions.put(manifest, permission);
	}
	
	public Multimap<AndroidManifest, String> getRemovedPermissions() {
		return removedPermissions.build();
	}
	
	public boolean hasBuildChanges() {
		return  (! addedDependencies.build().isEmpty()) || (! removedDependencies.build().isEmpty() );
	}
	
	public boolean hasManifestChanges() {
		return  (! addedPermissions.build().isEmpty()) || (! removedPermissions.build().isEmpty() );
	}

	public BuildActions getBuildActions(final GradleBuild key) {
		return new BuildActions() {
			@Override
			public Collection<Dependency> getAddedDependencies() {
				return addedDependencies.build().get(key);
			}
			
			@Override
			public Collection<Dependency> getRemovedDependencies() {
				return removedDependencies.build().get(key);
			}
		};
	}

	public ManifestActions getManifestActions(final AndroidManifest key) {
		return new ManifestActions() {
			@Override
			public Collection<String> getAddedPermissions() {
				return addedPermissions.build().get(key);
			}
			
			@Override
			public Collection<String> getRemovedPermissions() {
				return removedPermissions.build().get(key);
			}
		};
	}
}
