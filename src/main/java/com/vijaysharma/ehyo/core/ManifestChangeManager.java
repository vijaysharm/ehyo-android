package com.vijaysharma.ehyo.core;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.InternalActions.InternalManifestAction;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;
import com.vijaysharma.ehyo.core.models.ProjectRegistry;

public class ManifestChangeManager implements ChangeManager<AndroidManifestDocument>{
	private static final Function<AndroidManifest, String> MANIFEST_RENDERER = new Function<AndroidManifest, String>() {
		@Override
		public String apply(AndroidManifest manifest) {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	};
	
	static class ManifestChangeManagerFactory {
		public ManifestChangeManager create(ProjectRegistry registry, Set<InternalManifestAction> manifestActions) {
			PatchApplier<AndroidManifest, AndroidManifestDocument> patcher = new PatchApplier<AndroidManifest, AndroidManifestDocument>(MANIFEST_RENDERER);
			return new ManifestChangeManager(transform(registry, manifestActions), patcher);
		}
		
		private static Map<AndroidManifest, AndroidManifestDocument> transform(ProjectRegistry registry, Set<InternalManifestAction> manifests) {
			ImmutableMap.Builder<AndroidManifest, AndroidManifestDocument> mapping = ImmutableMap.builder();
			for ( InternalManifestAction action : manifests ) {
				Set<String> manifestIds = action.getAddedPermissions().keySet();
				for ( String id : manifestIds ) {
					AndroidManifest manifest = registry.getAndroidManifest(id);
					mapping.put(manifest, manifest.asDocument());
				}
			}

			return mapping.build();
		}
	}
	
	private final Map<AndroidManifest, AndroidManifestDocument> manifests;
	private final PatchApplier<AndroidManifest, AndroidManifestDocument> patcher;
	
	private ManifestChangeManager(Map<AndroidManifest, AndroidManifestDocument> manifests, 
								  PatchApplier<AndroidManifest, AndroidManifestDocument> patcher) {
		this.manifests = manifests;
		this.patcher = patcher;
	}
	
	@Override
	public void apply(PluginActionHandler<AndroidManifestDocument> handler) {
		for ( Map.Entry<AndroidManifest, AndroidManifestDocument> manifest : manifests.entrySet() ) {
			handler.modify(manifest.getValue());
		}
	}

	@Override
	public void commit(boolean dryrun) {
		patcher.apply(manifests, dryrun);	
	}
}
