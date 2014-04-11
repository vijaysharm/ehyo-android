package com.vijaysharma.ehyo.core;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;

public class ManifestChangeManager implements ChangeManager<AndroidManifestDocument>{
	private static final Function<AndroidManifest, String> MANIFEST_RENDERER = new Function<AndroidManifest, String>() {
		@Override
		public String apply(AndroidManifest manifest) {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	};
	
	static class ManifestChangeManagerFactory {
		public ManifestChangeManager create(List<AndroidManifest> manifests) {
			PatchApplier<AndroidManifest, AndroidManifestDocument> patcher = new PatchApplier<AndroidManifest, AndroidManifestDocument>(MANIFEST_RENDERER);
			return new ManifestChangeManager(transform(manifests), patcher);
		}
		
		private static Map<AndroidManifest, AndroidManifestDocument> transform(List<AndroidManifest> manifests) {
			ImmutableMap.Builder<AndroidManifest, AndroidManifestDocument> mapping = ImmutableMap.builder();
			for ( AndroidManifest manifest : manifests ) {
				mapping.put(manifest, manifest.asDocument());
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
