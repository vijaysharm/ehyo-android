package com.vijaysharma.ehyo.core.models;

import static com.vijaysharma.ehyo.core.models.GradleBuildDocument.read;
import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GradleBuildDocumentTest {
	
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void can_read_dependencies_without_flavors() throws IOException {
    	File file = fromFile("/test1.build.gradle");
    	GradleBuildDocument document = read(file);
    	List<Dependency> dependencies = document.getDependencies();
    	assertEquals(1, dependencies.size());
    	
    	Dependency dependency = dependencies.get(0);
    	assertEquals(BuildType.COMPILE, dependency.getBuildType());
    	assertEquals(null, dependency.getFlavor());
    	assertEquals("com.android.support:appcompat-v7:+", dependency.getDependency());
    }
	
	private File fromFile(String filename) {
		URL url = this.getClass().getResource(filename);
		return new File(url.getFile());
	}
}
