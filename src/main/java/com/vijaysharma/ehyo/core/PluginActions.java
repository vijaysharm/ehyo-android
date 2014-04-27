package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Document;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.core.DefaultTemplate.RecipeDocumentCallback;
import com.vijaysharma.ehyo.core.InternalActions.BuildActions;
import com.vijaysharma.ehyo.core.InternalActions.FileActions;
import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.ManifestTags.Activity;
import com.vijaysharma.ehyo.core.models.ManifestTags.Receiver;
import com.vijaysharma.ehyo.core.models.ManifestTags.Service;
import com.vijaysharma.ehyo.core.models.Project;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.models.SourceSet;

// TODO: Delegate component action changes to smaller classes
public class PluginActions {
	private final ImmutableMultimap.Builder<AndroidManifest, String> addedPermissions = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<AndroidManifest, String> removedPermissions = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<AndroidManifest, Activity> addedActivities = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<AndroidManifest, Service> addedServices = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<AndroidManifest, Receiver> addedReceivers = ImmutableMultimap.builder();
	
	private final ImmutableMultimap.Builder<GradleBuild, Dependency> addedDependencies = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<GradleBuild, Dependency> removedDependencies = ImmutableMultimap.builder();
	
	private final ImmutableMultimap.Builder<File, List<String>> mergedFiles = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<File, List<String>> createdFiles = ImmutableMultimap.builder();
	
	public void addDependency(GradleBuild build, BuildType type, Flavor flavor, String projectId) {
		Dependency dependency = new Dependency(type, flavor, projectId);
		addedDependencies.put(build, dependency);
	}
	
	public Set<GradleBuild> getBuilds() {
		Set<GradleBuild> builds = Sets.newHashSet(getAddedDependencies().keySet());
		builds.addAll(getRemovedDependencies().keySet());
		
		return builds;
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
	
	public void addActivity(AndroidManifest manifest, Activity activity) {
		addedActivities.put(manifest, activity);
	}
	public Multimap<AndroidManifest, Activity> getAddedActivities() {
		return addedActivities.build();
	}
	
	public void addService(AndroidManifest manifest, Service service) {
		addedServices.put(manifest, service);
	}
	public Multimap<AndroidManifest, Service> getAddedServices() {
		return addedServices.build();
	}
	
	public void addReceiver(AndroidManifest manifest, Receiver receiver) {
		addedReceivers.put(manifest, receiver);
	}
	public Multimap<AndroidManifest, Receiver> getAddedReceivers() {
		return addedReceivers.build();
	}
	
	public Set<File> getFiles() {
		Set<File> files = Sets.newHashSet(createdFiles.build().keySet());
		return files;
	}
	
	public void createFile(File file, List<String> contents) {
		createdFiles.put(file, contents);
	}
	
	public Multimap<File, List<String>> getMergedFiles() {
		return mergedFiles.build();
	}
	
	public boolean hasBuildChanges() {
		return  (! addedDependencies.build().isEmpty()) || 
				(! removedDependencies.build().isEmpty());
	}
	
	public boolean hasManifestChanges() {
		return  (! addedPermissions.build().isEmpty()) || 
				(! removedPermissions.build().isEmpty()) ||
				(! addedActivities.build().isEmpty()) ||
				(! addedServices.build().isEmpty()) ||
				(! addedReceivers.build().isEmpty());
	}
	
	public Set<AndroidManifest> getManifests() {
		Set<AndroidManifest> manifests = Sets.newHashSet(getAddedPermissions().keySet());
		manifests.addAll(getRemovedPermissions().keySet());
		manifests.addAll(getAddedActivities().keySet());
		manifests.addAll(getAddedServices().keySet());
		manifests.addAll(getAddedReceivers().keySet());
		
		return manifests;
	}

	public boolean hasFileChanges() {
		return  (! mergedFiles.build().isEmpty()) ||
				(! createdFiles.build().isEmpty());
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
			
			@Override
			public Collection<Activity> getAddedActivities() {
				return addedActivities.build().get(key);
			}

			@Override
			public Collection<Service> getAddedServices() {
				return addedServices.build().get(key);
			}

			@Override
			public Collection<Receiver> getAddedReceivers() {
				return addedReceivers.build().get(key);
			}
		};
	}
	
	public FileActions getFileActions(final File key) {
		return new FileActions() {
			@Override
			public Collection<List<String>> getCreatedFiles() {
				return createdFiles.build().get(key);
			}
		};
	}

	// TODO: Can't say I'm a fan of this API... 4 arguments??
	// TODO: Need to read in the buildApi and the minApiLevel from the build
	public void applyTemplate(DefaultTemplate template, SourceSet sourceSet, List<TemplateParameters> parameters, ProjectRegistry registry) {
		Project project = registry.getProject(sourceSet.getProject());
		final GradleBuild build = project.getBuild();
		final AndroidManifest manifest = sourceSet.getManifests();
		
		final Map<String, Object> mapping = Maps.newHashMap();
		for ( TemplateParameters param : parameters ) {
			mapping.put(param.getId(), param.getDefaultValue());
		}
		
		// TODO: This might be acceptable until ehyo supports creating new projects
		mapping.put("isNewProject", false);
		mapping.put("buildApi", 16);
		mapping.put("minApiLevel", 16);
		
		mapping.put("manifestDir", manifest.getFile().getParentFile().getAbsolutePath());
		mapping.put("manifestOut", manifest.getFile().getParentFile().getAbsolutePath());
		mapping.put("srcDir", manifest.getSourceDirectory().getAbsolutePath());
		mapping.put("resDir", manifest.getResourceDirectory().getAbsolutePath());
		mapping.put("packageName", manifest.getPackageName());

		template.apply(mapping, new RecipeDocumentCallback() {
			@Override
			public void onInstantiate(File from, List<String> result, File to) {
//				System.out.println("instantiate from: " + from);
//				System.out.println(result);
//				System.out.println("to: " + to + "\n");
				createdFiles.put(to, result);
			}

			@Override
			public void onManifestMerge(File from, Document result, File to) {
				if ( ! manifest.getFile().equals(to) )
					throw new IllegalStateException("Expected " + to + " to be " + manifest.getFile());
				AndroidManifestDocument manifestDocument = new AndroidManifestDocument(result);
				
				Set<String> permissions = manifestDocument.getPermissions();
				for ( String permission : permissions )
					addPermission(manifest, permission);
				
				List<Activity> activities = manifestDocument.getActivities();
				for ( Activity activity : activities )
					addActivity(manifest, activity);
				
				List<Service> services = manifestDocument.getServices();
				for ( Service service : services )
					addService(manifest, service);
				
				List<Receiver> receivers = manifestDocument.getReceivers();
				for ( Receiver receiver : receivers ) {
					addReceiver(manifest, receiver);
				}
			}
			
			@Override
			public void onMerge(File from, List<String> result, File to) {
//				System.out.println("merge from: " + from);
//				System.out.println(result);				
//				System.out.println("to: " + to + "\n");
				mergedFiles.put(to, result);
			}

			@Override
			public void onCopy(File from, List<String> result, File to) {
//				System.out.println("copy from: " + from);
//				System.out.println(result);
//				System.out.println("to: " + to + "\n");
				createdFiles.put(to, result);
			}

			@Override
			public void onDependency(String dependency) {
				// TODO: Need a way to determine which is the best build
				// configuration from the source set.
				addDependency(build, BuildType.COMPILE, null, dependency);
			}
		});
	}
	
	static class TemplateAction {
		private List<TemplateParameters> parameters;
		private DefaultTemplate template;
		private final SourceSet sourceSet;
		
		public TemplateAction(DefaultTemplate template, SourceSet sourceSet, List<TemplateParameters> parameters) {
			this.template = template;
			this.parameters = parameters;
			this.sourceSet = sourceSet;
		}
		
		public SourceSet getSourceSet() {
			return sourceSet;
		}
		
		public List<TemplateParameters> getParameters() {
			return parameters;
		}
		
		public DefaultTemplate getTemplate() {
			return template;
		}
	}
}
