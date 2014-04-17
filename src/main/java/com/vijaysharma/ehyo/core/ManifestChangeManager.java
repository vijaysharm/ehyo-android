package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;

public class ManifestChangeManager implements ChangeManager<PluginActions>{
	private static final Function<AndroidManifest, String> MANIFEST_RENDERER = new Function<AndroidManifest, String>() {
		@Override
		public String apply(AndroidManifest manifest) {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	};
	
	// TODO: Replace with ObjectFactory
	static class ManifestChangeManagerFactory {
		public ManifestChangeManager create() {
			return new ManifestChangeManager(new PatchApplier<AndroidManifest, AndroidManifestDocument>(MANIFEST_RENDERER));
		}
	}
	
	private final PluginActionHandlerFactory factory;
	private final PatchApplier<AndroidManifest, AndroidManifestDocument> patcher;
	private final ImmutableMap.Builder<AndroidManifest, AndroidManifestDocument> changes;
	
	private ManifestChangeManager(PatchApplier<AndroidManifest, AndroidManifestDocument> patcher) {
		this( patcher, new PluginActionHandlerFactory() );
	}
	
	ManifestChangeManager(PatchApplier<AndroidManifest, AndroidManifestDocument> patcher,
						  PluginActionHandlerFactory factory) {
		this.patcher = patcher;
		this.factory = factory;
		this.changes = ImmutableMap.builder();
	}
	
	@Override
	public void apply(PluginActions actions) {
		// TODO: You have to check manifest IDs from ALL points that can be modified
		Set<AndroidManifest> manifests = Sets.newHashSet(actions.getAddedPermissions().keySet());
		manifests.addAll(actions.getRemovedPermissions().keySet());
		for ( AndroidManifest manifest : manifests ) {
			changes.put(manifest, manifest.asDocument());
		}
		
		ManifestActionHandler handler = factory.createManifestActionHandler();
		for ( Map.Entry<AndroidManifest, AndroidManifestDocument> manifest : changes.build().entrySet() ) {
			handler.modify(manifest.getValue(), actions.getManifestActions(manifest.getKey()));
		}
	}

	@Override
	public void commit(boolean dryrun) {
		patcher.apply(changes.build(), dryrun);	
	}
}
