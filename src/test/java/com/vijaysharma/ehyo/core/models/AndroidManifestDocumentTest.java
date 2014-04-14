package com.vijaysharma.ehyo.core.models;

import static com.vijaysharma.ehyo.core.models.AndroidManifestDocument.read;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class AndroidManifestDocumentTest {
	private String id = "id";
	
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
		File file = get("/test2.xml");
		AndroidManifestDocument document = read(file, id);
		List<String> ac = document.getActivities();
		System.out.println(ac);
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