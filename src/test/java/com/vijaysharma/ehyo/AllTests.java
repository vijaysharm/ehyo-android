package com.vijaysharma.ehyo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vijaysharma.ehyo.api.utils.OptionSelectorTest;
import com.vijaysharma.ehyo.core.FileObserverProjectBuilderTest;
import com.vijaysharma.ehyo.core.ManifestActionHandlerTest;
import com.vijaysharma.ehyo.core.PatchApplierTest;
import com.vijaysharma.ehyo.core.ProjectRegistryLoaderTest;
import com.vijaysharma.ehyo.core.RecipeDocumentModelTest;
import com.vijaysharma.ehyo.core.RunActionTest;
import com.vijaysharma.ehyo.core.commandline.ApplicationRunActionFactoryTest;
import com.vijaysharma.ehyo.core.commandline.BuiltInActionsTest;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactoryTest;
import com.vijaysharma.ehyo.core.commandline.CommandLineParserTest;
import com.vijaysharma.ehyo.core.commandline.ParseAndBuildActionTest;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverterTest;
import com.vijaysharma.ehyo.core.commandline.converters.PluginsCommandLineConverterTest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocumentTest;
import com.vijaysharma.ehyo.core.models.BuildTypeTest;
import com.vijaysharma.ehyo.core.models.GradleBuildDocumentModelTest;
import com.vijaysharma.ehyo.core.models.GradleBuildDocumentTest;

@RunWith(Suite.class)
@SuiteClasses({
	AndroidManifestDocumentTest.class,
	ApplicationRunActionFactoryTest.class,
	BuiltInActionsTest.class,
	BuildTypeTest.class,
	CommandLineFactoryTest.class,
	CommandLineParserTest.class,
	DirectoryCommandLineConverterTest.class,
	FileObserverProjectBuilderTest.class,
	GradleBuildDocumentTest.class,
	GradleBuildDocumentModelTest.class,
	OptionSelectorTest.class,
	MainTest.class,
	ManifestActionHandlerTest.class,
	ParseAndBuildActionTest.class,
	PatchApplierTest.class,
	PluginsCommandLineConverterTest.class,
	ProjectRegistryLoaderTest.class,
	RecipeDocumentModelTest.class,
	RunActionTest.class,
})
public class AllTests {

}
