package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;

public class ManifestChangeManager implements ChangeManager<PluginActions> {
	private static final Function<AndroidManifest, AndroidManifestDocument> FACTORY = new Function<AndroidManifest, AndroidManifestDocument>() {
		@Override
		public AndroidManifestDocument apply(AndroidManifest input) {
			return AndroidManifestDocument.read(input.getFile());
		}
	};
	
	// TODO: Replace with ObjectFactory
	static class ManifestChangeManagerFactory {
		public ManifestChangeManager create() {
			return new ManifestChangeManager(new PatchApplier<AndroidManifest, AndroidManifestDocument>(FACTORY));
		}
	}
	
	private final PluginActionHandlerFactory factory;
	private final PatchApplier<AndroidManifest, AndroidManifestDocument> patcher;
	private final ImmutableMap.Builder<AndroidManifest, AndroidManifestDocument> changes;
	private final Function<AndroidManifest, AndroidManifestDocument> producer;
	
	private ManifestChangeManager(PatchApplier<AndroidManifest, AndroidManifestDocument> patcher) {
		this( patcher, new PluginActionHandlerFactory(), FACTORY );
	}
	
	ManifestChangeManager(PatchApplier<AndroidManifest, AndroidManifestDocument> patcher,
						  PluginActionHandlerFactory factory,
						  Function<AndroidManifest, AndroidManifestDocument> producer) {
		this.patcher = patcher;
		this.factory = factory;
		this.changes = ImmutableMap.builder();
		this.producer = producer;
	}
	
	@Override
	public void apply(PluginActions actions) {
		Set<AndroidManifest> manifests = Sets.newHashSet(actions.getAddedPermissions().keySet());
		manifests.addAll(actions.getRemovedPermissions().keySet());
		
		for ( AndroidManifest manifest : manifests ) {
			changes.put(manifest, producer.apply(manifest));
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
