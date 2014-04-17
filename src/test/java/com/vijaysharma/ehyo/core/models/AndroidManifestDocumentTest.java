package com.vijaysharma.ehyo.core.models;

import static com.vijaysharma.ehyo.core.models.AndroidManifestDocument.read;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

public class AndroidManifestDocumentTest {
	private String id = "id";
	
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
	@Test
	public void can_read_package() {
		File file = get("/test1.xml");
		AndroidManifestDocument document = read(file, id);
		assertEquals("com.vijay.app", document.getPackage());
	}
	
	@Test
	public void can_read_permissions() {
		File file = get("/test1.xml");
		AndroidManifestDocument document = read(file, id);
		Set<String> permissions = document.getPermissions();
		assertEquals(1, permissions.size());
		assertTrue(permissions.contains("android.permission.INTERNET"));
	}
	
	@Test
	public void can_read_activities() {
//		File file = get("/test2.xml");
//		AndroidManifestDocument document = read(file, id);
//		List<String> ac = document.getActivities();
//		System.out.println(ac);
	}
	
	@Test
	public void can_add_permission() {
		File manifest = newManifest("manifest.xml");
		AndroidManifestDocument document = read(manifest, manifest.getAbsolutePath());
		Set<String> permissions = document.getPermissions();
		assertEquals(0, permissions.size());
		
		document.addPermission("android.permission.INTERNET");
		permissions = document.getPermissions();
		assertEquals(1, permissions.size());
	}
	
	@Test
	public void can_remove_permission() {
		File manifest = newManifest("manifest.xml");
		AndroidManifestDocument document = read(manifest, manifest.getAbsolutePath());
		Set<String> permissions = document.getPermissions();
		assertEquals(0, permissions.size());
		
		document.addPermission("android.permission.INTERNET");
		permissions = document.getPermissions();
		assertEquals(1, permissions.size());
		
		document.removePermission("android.permission.INTERNET");
		permissions = document.getPermissions();
		assertEquals(0, permissions.size());
	}
	
	private File newManifest(String filename) {
		try {
			File file = folder.newFile(filename);
			
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
	        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	        out.write("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"com.vijay.app\">\n");
	        out.write("</manifest>");
	        
	        out.close();
	        return file;
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}
	
	private File get(String filename) {
		URL url = this.getClass().getResource(filename);
		return new File(url.getFile());
	}
}

/*
public class DynamicMessageBundleTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File properties;

    @Before
    public void createTestData() throws IOException {
        properties = folder.newFile("messages.properties");
        BufferedWriter out = new BufferedWriter(new FileWriter(properties));
        out.write("first.name = Arthur\n");
        out.write("last.name = Dent\n");
        out.write("favorite.object = Towel\n");
        out.close();
    }

    @Test
    public void shouldLoadFromPropertiesFile() throws IOException {
       assertThat(properties.exists(), is(true));

       DynamicMessageBundle bundle = new DynamicMessageBundle();
       bundle.load(properties);
       assertThat(bundle.getProperty("first.name"), is("Arthur"));
       assertThat(bundle.getProperty("last.name"), is("Dent"));
       assertThat(bundle.getProperty("favorite.object"), is("Towel"));
    }

    @After
    public void cleanUp() {
       assertThat(properties.exists(), is(true));
    }
}
*/