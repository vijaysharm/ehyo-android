package com.vijaysharma.ehyo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vijaysharma.ehyo.api.utils.OptionSelectorTest;
import com.vijaysharma.ehyo.core.FileObserverProjectBuilderTest;
import com.vijaysharma.ehyo.core.PatchApplierTest;
import com.vijaysharma.ehyo.core.ProjectRegistryLoaderTest;
import com.vijaysharma.ehyo.core.RunActionTest;
import com.vijaysharma.ehyo.core.commandline.ApplicationRunActionFactoryTest;
import com.vijaysharma.ehyo.core.commandline.BuiltInActionsTest;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactoryTest;
import com.vijaysharma.ehyo.core.commandline.CommandLineParserTest;
import com.vijaysharma.ehyo.core.commandline.ParseAndBuildActionTest;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverterTest;
import com.vijaysharma.ehyo.core.commandline.converters.PluginsCommandLineConverterTest;
import com.vijaysharma.ehyo.core.models.AndroidManifestDocumentTest;

@RunWith(Suite.class)
@SuiteClasses({
	AndroidManifestDocumentTest.class,
	ApplicationRunActionFactoryTest.class,
	BuiltInActionsTest.class,
	CommandLineFactoryTest.class,
	CommandLineParserTest.class,
	DirectoryCommandLineConverterTest.class,
	FileObserverProjectBuilderTest.class,
	OptionSelectorTest.class,
	MainTest.class,
	ParseAndBuildActionTest.class,
	PatchApplierTest.class,
	PluginsCommandLineConverterTest.class,
	ProjectRegistryLoaderTest.class,
	RunActionTest.class,
})
public class AllTests {

}
