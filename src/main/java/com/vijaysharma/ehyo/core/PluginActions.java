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

public class PluginActions {
	private final ImmutableMultimap.Builder<String, String> addedPermissions = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<String, String> removedPermissions = ImmutableMultimap.builder();
	
	private final ImmutableMultimap.Builder<String, BuildActionDependencyValue> addedDependencies = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<String, BuildActionDependencyValue> removedDependencies = ImmutableMultimap.builder();
	
	public void addDependency(BuildConfiguration config, String dependency) {
		DefaultBuildConfiguration configuration = (DefaultBuildConfiguration) config;
		BuildActionDependencyValue value = new BuildActionDependencyValue(configuration, dependency);
		addedDependencies.put(configuration.getBuild().getId(), value);
	}
	
	public Multimap<String, BuildActionDependencyValue> getAddedDependencies() {
		return addedDependencies.build();
	}
	
	public void removeDependency(BuildConfiguration config, String dependency) {
		DefaultBuildConfiguration configuration = (DefaultBuildConfiguration) config;
		BuildActionDependencyValue value = new BuildActionDependencyValue(configuration, dependency);
		removedDependencies.put(configuration.getBuild().getId(), value);
	}
	
	public Multimap<String, BuildActionDependencyValue> getRemovedDependencies() {
		return removedDependencies.build();
	}
	
	public void addPermission(AndroidManifest manifest, String permission) {
		addedPermissions.put(manifest.getId(), permission);
	}
	
	public Multimap<String, String> getAddedPermissions() {
		return addedPermissions.build();
	}
	
	public void removePermission(AndroidManifest manifest, String permission) {
		removedPermissions.put(manifest.getId(), permission);
	}
	
	public Multimap<String, String> getRemovedPermissions() {
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
				return addedDependencies.build().get(key.getId());
			}
		};
	}

	public ManifestActions getManifestActions(final AndroidManifest key) {
		return new ManifestActions() {
			@Override
			public Collection<String> getAddedPermissions() {
				return addedPermissions.build().get(key.getId());
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
