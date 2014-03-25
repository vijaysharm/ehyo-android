package com.vijaysharma.ehyo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vijaysharma.ehyo.core.commandline.ApplicationRunActionFactoryTest;
import com.vijaysharma.ehyo.core.commandline.BuiltInActionsTest;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactoryTest;
import com.vijaysharma.ehyo.core.commandline.ParseAndBuildActionTest;

@RunWith(Suite.class)
@SuiteClasses({
	ApplicationRunActionFactoryTest.class,
	BuiltInActionsTest.class,
	CommandLineFactoryTest.class,
	MainTest.class,
	ParseAndBuildActionTest.class,
})
public class AllTests {

}
