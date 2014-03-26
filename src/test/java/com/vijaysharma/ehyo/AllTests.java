package com.vijaysharma.ehyo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vijaysharma.ehyo.core.commandline.ApplicationRunActionFactoryTest;
import com.vijaysharma.ehyo.core.commandline.BuiltInActionsTest;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactoryTest;
import com.vijaysharma.ehyo.core.commandline.ParseAndBuildActionTest;
import com.vijaysharma.ehyo.core.commandline.converters.DirectoryCommandLineConverterTest;
import com.vijaysharma.ehyo.core.commandline.converters.PluginsCommandLineConverterTest;

@RunWith(Suite.class)
@SuiteClasses({
	ApplicationRunActionFactoryTest.class,
	BuiltInActionsTest.class,
	CommandLineFactoryTest.class,
	DirectoryCommandLineConverterTest.class,
	MainTest.class,
	ParseAndBuildActionTest.class,
	PluginsCommandLineConverterTest.class,
})
public class AllTests {

}
