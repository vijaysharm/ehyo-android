package com.vijaysharma.ehyo.core;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vijaysharma.ehyo.core.PluginActions.DefaultRecipeDocumentCallback;
import com.vijaysharma.ehyo.core.models.AndroidManifest;
import com.vijaysharma.ehyo.core.models.GradleBuild;

public class DefaultRecipeDocumentCallbackTest {
	private DefaultRecipeDocumentCallback callback;
	private PluginActions actions;
	private AndroidManifest manifest;
	private GradleBuild build;
	
	@Before
	public void before() {
		actions = Mockito.mock(PluginActions.class);
		manifest = Mockito.mock(AndroidManifest.class);
		build = Mockito.mock(GradleBuild.class);
		callback = new DefaultRecipeDocumentCallback(actions, build, manifest);
	}
	
	@Test
	public void onResourceMerge_adds_contents_to_actions_with_given_path() {
		when(manifest.getResourceDirectory()).thenReturn(new File("/root/dir/res"));
		File dest = new File("/root/dir/res/layout/main.xml");
		callback.onMergeResource(null, dest);
		
		verify(actions).mergeResource(dest, null);
	}
	
	@Test
	public void onResourceMerge_adds_contents_to_actions_with_modified_path() {
		when(manifest.getResourceDirectory()).thenReturn(new File("/root/dir/res"));
		callback.onMergeResource(null, new File("res/layout/main.xml"));
		
		verify(actions).mergeResource(new File("/root/dir/res/layout/main.xml"), null);
	}
}
