package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.InternalActions.ManifestActions;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class ManifestChangeManager implements ChangeManager<ManifestActions>{
	private static final Function<AndroidManifest, String> MANIFEST_RENDERER = new Function<AndroidManifest, String>() {
		@Override
		public String apply(AndroidManifest manifest) {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	};
	
	static class ManifestChangeManagerFactory {
		public ManifestChangeManager create(ProjectRegistry registry) {
			return new ManifestChangeManager(registry, new PatchApplier<AndroidManifest, AndroidManifestDocument>(MANIFEST_RENDERER));
		}
	}
	
	private final ProjectRegistry registry;
	private final PluginActionHandlerFactory factory;
	private final PatchApplier<AndroidManifest, AndroidManifestDocument> patcher;
	private final ImmutableMap.Builder<AndroidManifest, AndroidManifestDocument> changes;
	
	private ManifestChangeManager(ProjectRegistry registry, 
								  PatchApplier<AndroidManifest, AndroidManifestDocument> patcher) {
		this( registry, patcher, new PluginActionHandlerFactory() );
	}
	
	ManifestChangeManager(ProjectRegistry registry, 
						  PatchApplier<AndroidManifest, AndroidManifestDocument> patcher,
						  PluginActionHandlerFactory factory) {
		this.registry = registry;
		this.patcher = patcher;
		this.factory = factory;
		this.changes = ImmutableMap.builder();
	}
	
	@Override
	public void apply(ManifestActions actions) {
		// TODO: You have to check manifest IDs from ALL points that can be modified
		Set<String> manifestIds = actions.getAddedPermissions().keySet();
		for ( String id : manifestIds ) {
			AndroidManifest manifest = registry.getAndroidManifest(id);
			changes.put(manifest, manifest.asDocument());
		}
		
		ManifestActionHandler handler = factory.createManifestActionHandler();
		for ( Map.Entry<AndroidManifest, AndroidManifestDocument> manifest : changes.build().entrySet() ) {
			handler.modify(manifest.getValue(), actions.from(manifest.getKey()));
		}
	}

	@Override
	public void commit(boolean dryrun) {
		patcher.apply(changes.build(), dryrun);	
	}
}
