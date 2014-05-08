package com.vijaysharma.ehyo.core;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vijaysharma.ehyo.core.PluginActions.DefaultRecipeDocumentCallback;
import com.vijaysharma.ehyo.core.models.GradleBuild;
import com.vijaysharma.ehyo.core.models.ResourceDocument;
import com.vijaysharma.ehyo.core.models.SourceSet;

public class DefaultRecipeDocumentCallbackTest {
	private DefaultRecipeDocumentCallback callback;
	private PluginActions actions;
	private SourceSet sourceSet;
	private GradleBuild build;
	
	@Before
	public void before() {
		actions = Mockito.mock(PluginActions.class);
		build = Mockito.mock(GradleBuild.class);
		sourceSet = Mockito.mock(SourceSet.class);
		callback = new DefaultRecipeDocumentCallback(actions, build, sourceSet);
	}
	
	@Test
	public void onResourceMerge_adds_contents_to_actions_with_given_path() {
		when(sourceSet.getResourceDirectory()).thenReturn(new File("/root/dir/res"));
		File dest = new File("/root/dir/res/layout/main.xml");
		callback.onMergeResource(null, dest);
		
		verify(actions).mergeResource(Mockito.eq(dest), Mockito.any(ResourceDocument.class));
	}
	
	@Test
	public void onResourceMerge_adds_contents_to_actions_with_modified_path() {
		when(sourceSet.getResourceDirectory()).thenReturn(new File("/root/dir/res"));
		callback.onMergeResource(null, new File("res/layout/main.xml"));
		
		File dest = new File("/root/dir/res/layout/main.xml");
		verify(actions).mergeResource(Mockito.eq(dest), Mockito.any(ResourceDocument.class));
	}
}
