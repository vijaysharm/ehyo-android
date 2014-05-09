package com.vijaysharma.ehyo.core.models;

import static com.google.common.base.Joiner.on;
import static com.vijaysharma.ehyo.core.utils.EStringUtil.makeFirstLetterUpperCase;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

public class ProjectRegistryBuilderUtil {
	private Set<File> manifests;
	private Multimap<String, SourceSetDocument> sourceSetDocuments;

	public ProjectRegistryBuilderUtil(Set<File> manifests, Multimap<String, SourceSetDocument> multimap) {
		this.manifests = manifests;
		this.sourceSetDocuments = multimap;
	}

	public void addSourceSet(String id, File parent, ImmutableMap.Builder<SourceSetType, SourceSet> sourceSet) {
		addSourceSet(id, parent, sourceSet, sourceSetDocuments.get(id));
	}
	
	public void addSourceSetWithTest(String name, File parent, ImmutableMap.Builder<SourceSetType, SourceSet> sourceSet) {
		String testName = "androidTest" + makeFirstLetterUpperCase(name);
		addSourceSet(name, parent, sourceSet, sourceSetDocuments.get(name));
		addSourceSet(testName, parent, sourceSet, sourceSetDocuments.get(testName));
	}
	
	// TODO: Read the document for where the manifest, source, and resource
	// directory are.
	private void addSourceSet(String id, File parent, ImmutableMap.Builder<SourceSetType, SourceSet> sourceSet, Collection<SourceSetDocument> document) {
		File directory = new File (parent, sourceSetPath(id));
		if ( directory.exists() ) {
			String project = parent.getName();
			AndroidManifest manifest = getManifest(directory, document);
			File sourceDirectory = getSourceDirectory(directory, document);
			File resourceDirectory = getResourceDirectory(directory, document);
			SourceSetType type = new SourceSetType(id);
			SourceSet source = new SourceSet(project, 
											 type, 
											 manifest,
											 sourceDirectory,
											 resourceDirectory);
			sourceSet.put(type, source);
		}
	}

	// TODO: Read the document for where the source directory is
	private File getSourceDirectory(File directory, Collection<SourceSetDocument> document) {
		return new File(directory, "java");
	}

	// TODO: Read the document for where the resource directory is
	private File getResourceDirectory(File directory, Collection<SourceSetDocument> document) {
		return new File(directory, "res");
	}

	// TODO: Read the document for where the manifest is
	private AndroidManifest getManifest(File directory, Collection<SourceSetDocument> document) {
		File manifestFile = new File(directory, "AndroidManifest.xml");
		if (manifests.contains(manifestFile)) {
			AndroidManifestDocument manifestDocument = AndroidManifestDocument.read(manifestFile); 
			Set<String> permissions = manifestDocument.getPermissions();
			String packageName = manifestDocument.getPackage();
			
			return new AndroidManifest(manifestFile, packageName, permissions);
		}

		return null;
	}

	private String sourceSetPath(String id) {
		return on(File.separator).join("src", id);
	}
}
