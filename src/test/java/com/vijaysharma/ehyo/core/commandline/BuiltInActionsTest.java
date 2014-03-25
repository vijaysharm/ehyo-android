package com.vijaysharma.ehyo.core.commandline;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.junit.Test;
import org.mockito.Mockito;

public class BuiltInActionsTest {
	@Test
	public void configure_sets_version_as_option() {
		BuiltInActions action = new BuiltInActions();
		OptionParser parser = mock(OptionParser.class);
		action.configure(parser);
		verify(parser, times(1)).accepts("version");
	}
	
	@Test
	public void getAction_returns_null_if_option_is_not_present() {
		BuiltInActions action = new BuiltInActions();
		OptionSet options = mock(OptionSet.class);
		when(options.has(Mockito.any(OptionSpec.class))).thenReturn(false);
		assertNull(action.getAction(options));
	}
	
	@Test
	public void getAction_returns_an_action_if_option_is_present() {
		BuiltInActions action = new BuiltInActions();
		OptionSet options = mock(OptionSet.class);
		when(options.has(Mockito.any(OptionSpec.class))).thenReturn(true);
		assertNotNull(action.getAction(options));
	}
}
