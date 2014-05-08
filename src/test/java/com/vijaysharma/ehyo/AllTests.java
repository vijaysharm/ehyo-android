package com.vijaysharma.ehyo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vijaysharma.ehyo.api.ArtifactTest;
import com.vijaysharma.ehyo.api.CommandLineParserTest;
import com.vijaysharma.ehyo.api.utils.OptionSelectorTest;
import com.vijaysharma.ehyo.core.DefaultRecipeDocumentCallbackTest;
import com.vijaysharma.ehyo.core.FileObserverProjectBuilderTest;
import com.vijaysharma.ehyo.core.ManifestActionHandlerTest;
import com.vijaysharma.ehyo.core.PatchApplierTest;
import com.vijaysharma.ehyo.core.ProjectRegistryLoaderIntegrationTest;
import com.vijaysharma.ehyo.core.ProjectRegistryLoaderTest;
import com.vijaysharma.ehyo.core.RecipeDocumentModelTest;
import com.vijaysharma.ehyo.core.RunActionTest;
import com.vijaysharma.ehyo.core.commandline.ApplicationRunActionFactoryTest;
import com.vijaysharma.ehyo.core.commandline.BuiltInActionsTest;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactoryTest;
import com.vijaysharma.ehyo.core.commandline.ParseAndBuildActionTest;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverterTest;
import com.vijaysharma.ehyo.core.commandline.converters.PluginsCommandLineConverterTest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocumentTest;
import com.vijaysharma.ehyo.core.models.GradleBuildDocumentModelTest;
import com.vijaysharma.ehyo.core.models.GradleBuildDocumentTest;
import com.vijaysharma.ehyo.plugins.dependencies.DependenciesTest;
import com.vijaysharma.ehyo.plugins.manifestpermissions.PermissionsTest;
import com.vijaysharma.ehyo.plugins.templates.android.AndroidTemplatesTest;

@RunWith(Suite.class)
@SuiteClasses({
	// Unit tests
	AndroidManifestDocumentTest.class,
	AndroidTemplatesTest.class,
	ApplicationRunActionFactoryTest.class,
	ArtifactTest.class,
	BuiltInActionsTest.class,
	CommandLineFactoryTest.class,
	CommandLineParserTest.class,
	DefaultRecipeDocumentCallbackTest.class,
	DependenciesTest.class,
	DirectoryCommandLineConverterTest.class,
	FileObserverProjectBuilderTest.class,
	GradleBuildDocumentTest.class,
	GradleBuildDocumentModelTest.class,
	OptionSelectorTest.class,
	MainTest.class,
	ManifestActionHandlerTest.class,
	ParseAndBuildActionTest.class,
	PatchApplierTest.class,
	PermissionsTest.class,
	PluginsCommandLineConverterTest.class,
	ProjectRegistryLoaderTest.class,
	RecipeDocumentModelTest.class,
	RunActionTest.class,
	
	// Integration Tests
	ProjectRegistryLoaderIntegrationTest.class
})
public class AllTests {

}
