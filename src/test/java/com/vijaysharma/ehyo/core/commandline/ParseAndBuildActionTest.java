package com.vijaysharma.ehyo.core.commandline;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;
import com.vijaysharma.ehyo.core.commandline.ParseAndBuildAction.CommandLineActionsFactory;

public class ParseAndBuildActionTest {
	private CommandLineActionsFactory factory;
	
	@Before
	public void before() {
		factory = mock(CommandLineActionsFactory.class);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void run_throws_when_no_action_is_found() {
		String[] args = {"test"};
		List<CommandLineAction> actions = Mockito.anyList();
		when(factory.create(args)).thenReturn(actions);
		new ParseAndBuildAction( args, factory ).run();
		
		verify(factory, times(1)).create(args);
	}
	
	@Test
	public void run_calls_action_run_on_matching_command_line_action() {
		String[] args = {"test"};
		Action action = mock(Action.class);
		CommandLineAction stub = mock(CommandLineAction.class);
		List<CommandLineAction> actions = Lists.newArrayList(); actions.add(stub);
		
		when(stub.getAction(Mockito.any(OptionSet.class))).thenReturn(action);
		when(factory.create(args)).thenReturn(actions);
		
		new ParseAndBuildAction( args, factory ).run();
		verify(action, times(1)).run();
	}
	
	@Test
	public void run_ignores_other_actions_when_first_is_found() {
		String[] args = {"test"};
		Action action_1 = mock(Action.class);
		Action action_2 = mock(Action.class);
		CommandLineAction stub_1 = mock(CommandLineAction.class);
		CommandLineAction stub_2 = mock(CommandLineAction.class);
		List<CommandLineAction> actions = Lists.newArrayList(); actions.add(stub_1); actions.add(stub_2);
		
		when(stub_1.getAction(Mockito.any(OptionSet.class))).thenReturn(action_1);
		when(stub_2.getAction(Mockito.any(OptionSet.class))).thenReturn(action_2);
		when(factory.create(args)).thenReturn(actions);
		
		new ParseAndBuildAction( args, factory ).run();
		verify(action_1, times(1)).run();
		verify(action_2, never()).run();
	}
}
