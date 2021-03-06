package com.vijaysharma.ehyo.core.commandline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vijaysharma.ehyo.api.ArgumentOption;
import com.vijaysharma.ehyo.api.CommandLineParser;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.Action;

@RunWith(MockitoJUnitRunner.class)
public class BuiltInActionsTest {

	@Captor
	private ArgumentCaptor<ArgumentOption<String>> captor;
	
	@Test
	public void configure_sets_version_as_option() {
		BuiltInActions action = new BuiltInActions();
		CommandLineParser parser = mock(CommandLineParser.class);
		action.configure(parser);
		
		verify(parser, times(1)).addOptions(captor.capture());
		ArgumentOption<String> value = captor.getValue();
		assertEquals(true, value.supports("--version"));
	}
	
	@Test
	public void getAction_returns_null_if_option_is_not_present() {
		BuiltInActions action = new BuiltInActions();
		ParsedSet options = mock(ParsedSet.class);
		when(options.has(Mockito.any(ArgumentOption.class))).thenReturn(false);
		assertNull(action.getAction(options));
	}
	
	@Test
	public void getAction_returns_an_action_if_option_is_present() {
		BuiltInActions action = new BuiltInActions();
		ParsedSet options = mock(ParsedSet.class);
		when(options.has(Mockito.any(ArgumentOption.class))).thenReturn(true);
		assertNotNull(action.getAction(options));
	}
	
	@Test
	public void running_run_on_action_prints_version_information() {
		TextOutput out = mock(TextOutput.class);
		BuiltInActions action = new BuiltInActions(out);
		ParsedSet options = mock(ParsedSet.class);
		when(options.has(Mockito.any(ArgumentOption.class))).thenReturn(true);
		Action versionAction = action.getAction(options);
		versionAction.run();
		verify(out, times(1)).println(Mockito.anyString());
	}
}
