package com.vijaysharma.ehyo.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocument;
import com.vijaysharma.ehyo.core.utils.EFileUtil;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ManifestChangeManager implements ChangeManager<AndroidManifestDocument>{
	private static final Function<AndroidManifest, String> MANIFEST_RENDERER = new Function<AndroidManifest, String>() {
		@Override
		public String apply(AndroidManifest manifest) {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	};
	
	static class ManifestChangeManagerFactory {
		public ManifestChangeManager create(List<AndroidManifest> manifests) {
			return new ManifestChangeManager(transform(manifests), MANIFEST_RENDERER);
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
	private final Function<AndroidManifest, String> renderer;
	
	private ManifestChangeManager(Map<AndroidManifest, AndroidManifestDocument> manifests, Function<AndroidManifest, String> renderer) {
		this.manifests = manifests;
		this.renderer = renderer;
	}
	
	@Override
	public void apply(PluginActionHandler<AndroidManifestDocument> handler) {
		for ( Map.Entry<AndroidManifest, AndroidManifestDocument> manifest : manifests.entrySet() ) {
			handler.modify(manifest.getValue());
		}
	}

	@Override
	public void commit(boolean dryrun) {
		for ( Map.Entry<AndroidManifest, AndroidManifestDocument> manifest : manifests.entrySet() ) {
			try {
				List<String> baseline = toListOfStrings(manifest.getKey().asDocument());
				List<String> changed = toListOfStrings(manifest.getValue());
				Patch diff = DiffUtils.diff(baseline, changed);
				if ( ! diff.getDeltas().isEmpty() ) {
					if ( dryrun ) {
						show(manifest.getKey(), diff);
					} else {
						save(manifest.getKey(), manifest.getValue());
					}					
				}
			} catch (Exception ex) {
				// TODO: What to do here?
			}
		}		
	}
	
	private void show(AndroidManifest manifest, Patch diff) throws IOException {		
		Outputter.out.println("Diff " + renderer.apply(manifest));
		for (Delta delta: diff.getDeltas()) {
			Outputter.out.println(delta);
		}		
	}
	
	private void save(AndroidManifest manifest, AndroidManifestDocument modified) throws IOException {
		Outputter.out.print("Writing " + renderer.apply(manifest) + "... ");
		List<String> changed = toListOfStrings(modified);
		EFileUtil.write(manifest, changed);
		Outputter.out.println("done");
	}
	
	private static List<String> toListOfStrings(AndroidManifestDocument doc) throws IOException {
		return doc.toListOfStrings();
	}
}
