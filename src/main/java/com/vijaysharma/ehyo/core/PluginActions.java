package com.vijaysharma.ehyo.core;

import java.util.Collection;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vijaysharma.ehyo.api.BuildConfiguration;
import com.vijaysharma.ehyo.core.InternalActions.BuildActions;
import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.RunActionInternals.DefaultBuildConfiguration;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;

// TODO: Delegate component action changes to smaller classes
public class PluginActions {
	private final ImmutableMultimap.Builder<AndroidManifest, String> addedPermissions = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<AndroidManifest, String> removedPermissions = ImmutableMultimap.builder();
	
	private final ImmutableMultimap.Builder<GradleBuild, BuildActionDependencyValue> addedDependencies = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<GradleBuild, BuildActionDependencyValue> removedDependencies = ImmutableMultimap.builder();
	
	public void addDependency(BuildConfiguration config, String dependency) {
		DefaultBuildConfiguration configuration = (DefaultBuildConfiguration) config;
		BuildActionDependencyValue value = new BuildActionDependencyValue(configuration, dependency);
		addedDependencies.put(configuration.getBuild(), value);
	}
	
	public Multimap<GradleBuild, BuildActionDependencyValue> getAddedDependencies() {
		return addedDependencies.build();
	}
	
	public void removeDependency(BuildConfiguration config, String dependency) {
		DefaultBuildConfiguration configuration = (DefaultBuildConfiguration) config;
		BuildActionDependencyValue value = new BuildActionDependencyValue(configuration, dependency);
		removedDependencies.put(configuration.getBuild(), value);
	}
	
	public Multimap<GradleBuild, BuildActionDependencyValue> getRemovedDependencies() {
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
			public Collection<BuildActionDependencyValue> getAddedDependencies() {
				return addedDependencies.build().get(key);
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
	
	static class BuildActionDependencyValue {
		private final DefaultBuildConfiguration configuration;
		private final String dependency;
		
		public BuildActionDependencyValue(DefaultBuildConfiguration config, String dependency) {
			this.configuration = config;
			this.dependency = dependency;
		}
		
		public String getDependency() {
			return dependency;
		}
		
		public DefaultBuildConfiguration getConfiguration() {
			return configuration;
		}
	}	
}
