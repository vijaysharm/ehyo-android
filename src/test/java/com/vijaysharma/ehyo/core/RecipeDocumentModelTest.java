package com.vijaysharma.ehyo.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Joiner;
import com.vijaysharma.ehyo.core.RecipeDocumentModel.RecipeDocumentCallback;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

import freemarker.template.Configuration;

public class RecipeDocumentModelTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
	private Configuration config;
	private Map<String, Object> properties;
	private TemplateConverter converter;
	private RecipeDocumentCallback callback;
	private File root;
	
	@Before
	public void before() {
		config = mock(Configuration.class);
		converter = mock(TemplateConverter.class);
		properties = mock(Map.class);
		callback = mock(RecipeDocumentCallback.class);
		root = folder.newFolder("template");
	}
	
	@Test
	public void onManifestMerge_receives_to_path_when_defined() {
		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<merge from=\"AndroidManifest.xml.ftl\" to=\"/somepath/AndroidManifest.xml\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onMergeManifest(null, new File("/somepath/AndroidManifest.xml"));
	}

	@Test
	public void onManifestMerge_receives_cleaned_from_path_when_to_isnt_defined() {
		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<merge from=\"AndroidManifest.xml.ftl\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onMergeManifest(null, new File("AndroidManifest.xml"));
	}

	@Test
	public void onManifestMerge_receives_from_path_when_to_isnt_defined() {
		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<merge from=\"AndroidManifest.xml\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onMergeManifest(null, new File("AndroidManifest.xml"));
	}
	
	@Test
	public void onResourceMerge_receives_to_path_when_defined() {
		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<merge from=\"res/values/refs.xml.ftl\" to=\"/path/to/values/refs.xml\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onMergeResource(null, new File("/path/to/values/refs.xml"));
	}
	
	@Test
	public void onResourceMerge_receives_cleaned_from_path_when_to_isnt_defined() {
		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<merge from=\"res/values/strings.xml.ftl\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onMergeResource(null, new File("res/values/strings.xml"));
	}
	
	@Test
	public void onResourceMerge_receives_from_path_when_to_isnt_defined() {
		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<merge from=\"res/values/strings.xml\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onMergeResource(null, new File("res/values/strings.xml"));
	}
	
	@Test
	public void onInstantiate_is_called_for_resource_files() {
		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<instantiate from=\"res/layout/fragment_grid.xml\" to=\"/some/path/res/layout/fragment_grid.xml\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onCreateResource(new LinkedList<String>(), new File("/some/path/res/layout/fragment_grid.xml"));
	}
	
	@Test
	public void onInstantiate_is_called_for_java_files() {
		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
		    "<instantiate from=\"src/app_package/dummy/DummyContent.java.ftl\" to=\"/some/path/dummy/DummyContent.java\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onCreateJava(new LinkedList<String>(), new File("/some/path/dummy/DummyContent.java"));
	}
	
	@Test
	public void onCopy_is_called_with_full_path_to_copied_file() throws IOException {
		createFileInPath("template/root/res/layout-v17/dream.xml");

		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<copy from=\"res/layout-v17/dream.xml\" to=\"/some/path/layout-v17/dream.xml\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onCopyResource(null, new File("/some/path/layout-v17/dream.xml"));
	}

	@Test
	public void onCopy_is_called_for_every_files() throws IOException {
		createFileInPath("template/root/res/layout-v17/dream1.xml");
		createFileInPath("template/root/res/layout-v17/dream2.xml");

		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<copy from=\"res/layout-v17\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onCopyResource(
			null, 
			new File("res/layout-v17/dream1.xml")
		);
		
		verify(callback).onCopyResource(
			null, 
			new File("res/layout-v17/dream2.xml")
		);		
	}
	
	@Test
	public void onCopy_is_called_for_every_deep_files() throws IOException {
		createFileInPath("template/root/res/layout-v17/dream1.xml");
		createFileInPath("template/root/res/layout-v17/dir/dream2.xml");

		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<copy from=\"res/layout-v17\" />",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onCopyResource(
			null,
			new File("res/layout-v17/dream1.xml")
		);
		
		verify(callback).onCopyResource(
			null, 
			new File("res/layout-v17/dir/dream2.xml")
		);		
	}
	
	@Test
	public void onCopy_is_called_for_every_files_with_to_give_to_path() throws IOException {
		createFileInPath("template/root/res/layout-v17/dream1.xml");
		createFileInPath("template/root/res/layout-v17/dream2.xml");

		List<String> document = Arrays.asList(
			"<?xml version=\"1.0\"?>",
			"<recipe>",
			"<copy from=\"res/layout-v17\" to=\"/some/path/res/drawable-xxhdpi\"/>",
			"</recipe>"
		);
		
		RecipeDocumentModel model = new RecipeDocumentModel(root, load(document), config, properties, converter);
		model.read(callback);
		
		verify(callback).onCopyResource(
			null, 
			new File("/some/path/res/drawable-xxhdpi/dream1.xml")
		);
		
		verify(callback).onCopyResource(
			null, 
			new File("/some/path/res/drawable-xxhdpi/dream2.xml")
		);		
	}
	
	private void createFileInPath(String path) throws IOException {
		String[] split = path.split("/");
		for ( int i = 1; i <= split.length; i++ ) {
			String s = Joiner.on("/").join(Arrays.copyOf(split, i));
			if ( i == split.length ) {
				folder.newFile(s);
			} else {
				folder.newFolder(s);
			}
		}
	}
	
	private Document load(List<String> document) {
		try {
			InputStream inputStream = IOUtils.toInputStream(Joiner.on("").join(document));
			SAXBuilder builder = new SAXBuilder();
			return builder.build(inputStream);
		} catch (IOException ioe) {
			throw new UncheckedIoException(ioe);
		} catch (JDOMException jde) {
			throw new RuntimeException(jde);
		}
	}
}
