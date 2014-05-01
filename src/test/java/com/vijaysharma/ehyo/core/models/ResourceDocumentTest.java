package com.vijaysharma.ehyo.core.models;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ResourceDocumentTest {
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void can_read_non_existing_file() throws IOException {
		File newFile = new File("resource.xml");
		ResourceDocument.read(newFile);
	}
}
