package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Document;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.api.Artifact;
import com.vijaysharma.ehyo.api.BuildType;
import com.vijaysharma.ehyo.api.Flavor;
import com.vijaysharma.ehyo.api.TemplateParameters;
import com.vijaysharma.ehyo.core.InternalActions.BuildActions;
import com.vijaysharma.ehyo.core.InternalActions.FileActions;
import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.InternalActions.ResourceActions;
import com.vijaysharma.ehyo.core.RecipeDocumentModel.RecipeDocumentCallback;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;
import com.vijaysharma.ehyo.core.models.Dependency;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.ManifestTags.Activity;
import com.vijaysharma.ehyo.core.models.ManifestTags.Receiver;
import com.vijaysharma.ehyo.core.models.ManifestTags.Service;
import com.vijaysharma.ehyo.core.models.Project;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;
import com.vijaysharma.ehyo.core.models.ResourceDocument;
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
	
	private final ImmutableMap.Builder<File, File> copiedFiles = ImmutableMap.builder();
	private final ImmutableMultimap.Builder<File, ResourceDocument> mergedResource = ImmutableMultimap.builder();
	private final ImmutableMultimap.Builder<File, List<String>> createdFiles = ImmutableMultimap.builder();
	
	public void addDependency(GradleBuild build, BuildType type, Flavor flavor, Artifact artifact) {
		Dependency dependency = new Dependency(type, flavor, artifact);
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
	
	public void removeDependency(GradleBuild build, BuildType type, Flavor flavor, Artifact projectId) {
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

	public Set<File> getResourceFiles() {
		Set<File> files = Sets.newHashSet(mergedResource.build().keySet());
		return files;
	}
	
	public void createFile(File file, List<String> contents) {
		createdFiles.put(file, contents);
	}
	
	public void mergeResource(File file, ResourceDocument contents) {
		mergedResource.put(file, contents);
	}
	
	public Map<File,File> getBinaryFiles() {
		return copiedFiles.build();
	}
	public void copyFiles(File from, File to) {
		copiedFiles.put(from, to);
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
	
	public boolean hasBinaryFileChanges() {
		return ! getBinaryFiles().isEmpty();
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
		return  (! createdFiles.build().isEmpty());
	}

	public boolean hasResourceChanges() {
		return ! mergedResource.build().isEmpty();
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
	
	public ResourceActions getResourceActions(final File key) {
		return new ResourceActions() {
			@Override
			public Collection<ResourceDocument> getMergedResources() {
				return mergedResource.build().get(key);
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

		template.apply(mapping, new DefaultRecipeDocumentCallback(this, build, manifest));
	}
	
	static class DefaultRecipeDocumentCallback implements RecipeDocumentCallback {
		private final PluginActions actions;
		private final GradleBuild build;
		private final AndroidManifest manifest;

		public DefaultRecipeDocumentCallback(PluginActions actions, GradleBuild build, AndroidManifest manifest) {
			this.actions = actions;
			this.build = build;
			this.manifest = manifest;
		}
		
		@Override
		public void onCreateJava(List<String> result, File to) {
			File directory = manifest.getSourceDirectory();
			File toDirectory = computeTo(to, directory);
			actions.createFile(toDirectory, result);
		}
		
		@Override
		public void onCreateResource(List<String> result, File to) {
			File directory = manifest.getResourceDirectory();
			File toDirectory = computeTo(to, directory);
			actions.createFile(toDirectory, result);
		}

		@Override
		public void onMergeManifest(Document result, File to) {
			// TODO: Assuming that the manifest that will be modified will be
			// the one that's owned by this action
			AndroidManifestDocument manifestDocument = new AndroidManifestDocument(result);
			
			// TODO: Need to copy everything you can from the template manifest.
			Set<String> permissions = manifestDocument.getPermissions();
			for ( String permission : permissions )
				actions.addPermission(manifest, permission);
			
			List<Activity> activities = manifestDocument.getActivities();
			for ( Activity activity : activities )
				actions.addActivity(manifest, activity);
			
			List<Service> services = manifestDocument.getServices();
			for ( Service service : services )
				actions.addService(manifest, service);
			
			List<Receiver> receivers = manifestDocument.getReceivers();
			for ( Receiver receiver : receivers )
				actions.addReceiver(manifest, receiver);
		}
		
		@Override
		public void onMergeResource(Document result, File to) {
			File directory = manifest.getResourceDirectory();
			File toDirectory = computeTo(to, directory);
			actions.mergeResource(toDirectory, new ResourceDocument(result));
		}

		@Override
		public void onCopy(File from, File to) {
			File directory = manifest.getResourceDirectory();
			File toDirectory = computeTo(to, directory);
			actions.copyFiles(from, toDirectory);
		}

		@Override
		public void onCopyResource(Document result, File to) {
			File directory = manifest.getResourceDirectory();
			File toDirectory = computeTo(to, directory);
			actions.mergeResource(toDirectory, new ResourceDocument(result));
		}
		
		@Override
		public void onDependency(String dependency) {
			// TODO: Need a way to determine which is the best build
			// configuration from the source set.
			Artifact artifact = Artifact.read(dependency);
			actions.addDependency(build, BuildType.COMPILE, null, artifact);
		}
		
		// TODO: This will not work when we start reading the path from the
		// build file.
		private File computeTo(File to, File directory) {
			if (to.getAbsolutePath().startsWith(directory.getAbsolutePath()))
				return to;
			else
				return new File(directory, cleanPath(to));
		}
		
		// TODO: This will not work when we start reading the path from the
		// build file.
		private String cleanPath(File to) {
			String path = to.getPath();
			if (path.startsWith("res/") || path.startsWith("src/")) {
				return path.substring(4, path.length());
			}
			if (path.startsWith("java/")) {
				return path.substring(5, path.length());
			}
			
			return to.getPath();
		}
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
