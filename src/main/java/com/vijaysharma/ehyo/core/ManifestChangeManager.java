package com.vijaysharma.ehyo.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.utils.EFileUtil;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class ManifestChangeManager {
	static class ManifestChangeManagerFactory {
		public ManifestChangeManager create(List<AndroidManifest> manifests) {
			return new ManifestChangeManager(manifests, MANIFEST_RENDERER);
		}
	}
	
	private static final Function<AndroidManifest, String> MANIFEST_RENDERER = new Function<AndroidManifest, String>() {
		@Override
		public String apply(AndroidManifest manifest) {
			return manifest.getProject() + ":" + manifest.getSourceSet() + ":" + manifest.getFile().getName();
		}
	};
	
	private final Map<AndroidManifest, Document> manifests;
	private final Function<AndroidManifest, String> renderer;
	
	public ManifestChangeManager(List<AndroidManifest> manifests, Function<AndroidManifest, String> renderer) {
		this.manifests = transform(manifests);
		this.renderer = renderer;
	}
	
	private Map<AndroidManifest, Document> transform(List<AndroidManifest> manifests) {
		ImmutableMap.Builder<AndroidManifest, Document> mapping = ImmutableMap.builder();
		for ( AndroidManifest manifest : manifests ) {
			mapping.put(manifest, manifest.asXmlDocument());
		}

		return mapping.build();
	}

	public void apply(PluginActionHandler<?> handler) {
		if ( handler instanceof ManifestActionHandler ) {
			for ( Map.Entry<AndroidManifest, Document> manifest : manifests.entrySet() ) {
				((ManifestActionHandler)handler).modify(manifest.getValue());
			}
		}
	}

	public void commit(boolean dryrun) {
		for ( Map.Entry<AndroidManifest, Document> manifest : manifests.entrySet() ) {
			try {
				List<String> baseline = toListOfStrings(manifest.getKey().asXmlDocument());
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
	
	private void save(AndroidManifest manifest, Document modified) throws IOException {
		Outputter.out.println("Writing " + renderer.apply(manifest));
		List<String> changed = toListOfStrings(modified);
		EFileUtil.write(manifest, changed);
	}
	
	private List<String> toListOfStrings(Document doc) throws IOException {
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		xmlOutput.output(doc, stream);

		return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray()));
	}
}
