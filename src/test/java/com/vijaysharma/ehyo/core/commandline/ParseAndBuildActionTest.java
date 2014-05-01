package com.vijaysharma.ehyo.core.commandline;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.CommandLineParser.ParsedSet;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.actions.CommandLineAction;
import com.vijaysharma.ehyo.core.commandline.ParseAndBuildAction.CommandLineActionsFactory;

public class ParseAndBuildActionTest {
	private CommandLineActionsFactory factory;
	private TextOutput out;
	private ParseAndBuildAction action;
	
	@Before
	public void before() {
		factory = mock(CommandLineActionsFactory.class);
		out = mock(TextOutput.class);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void run_throws_when_no_action_is_found() {
		List<String> args = newArrayList("test");
		List<CommandLineAction> actions = Lists.newArrayList();
		when(factory.create()).thenReturn(actions);
		new ParseAndBuildAction( args, factory, out ).run();
		
		verify(factory, times(1)).create();
	}
	
	@Test
	public void run_calls_action_run_on_matching_command_line_action() {
		List<String> args = newArrayList("test");
		Action action = mock(Action.class);
		CommandLineAction stub = mock(CommandLineAction.class);
		List<CommandLineAction> actions = Lists.newArrayList(); actions.add(stub);
		
		when(stub.getAction(Mockito.any(ParsedSet.class))).thenReturn(action);
		when(factory.create()).thenReturn(actions);
		
		new ParseAndBuildAction( args, factory, out ).run();
		verify(action, times(1)).run();
	}

	@Test
	public void run_ignores_other_actions_when_first_is_found() {
		List<String> args = newArrayList("test");
		Action action_1 = mock(Action.class);
		Action action_2 = mock(Action.class);
		CommandLineAction stub_1 = mock(CommandLineAction.class);
		CommandLineAction stub_2 = mock(CommandLineAction.class);
		List<CommandLineAction> actions = Lists.newArrayList(); actions.add(stub_1); actions.add(stub_2);
		
		when(stub_1.getAction(Mockito.any(ParsedSet.class))).thenReturn(action_1);
		when(stub_2.getAction(Mockito.any(ParsedSet.class))).thenReturn(action_2);
		when(factory.create()).thenReturn(actions);
		
		new ParseAndBuildAction( args, factory, out ).run();
		verify(action_1, times(1)).run();
		verify(action_2, never()).run();
	}
	
	@Test
	public void run_handles_exceptions_thrown() {
		List<String> args = newArrayList("test");
		Action action = mock(Action.class);
		CommandLineAction stub = mock(CommandLineAction.class);
		List<CommandLineAction> actions = Lists.newArrayList(); actions.add(stub);
		RuntimeException toBeThrown = new RuntimeException();
		
		when(factory.create()).thenReturn(actions);
		when(stub.getAction(Mockito.any(ParsedSet.class))).thenReturn(action);
		Mockito.doThrow(toBeThrown).when(action).run();
		
		new ParseAndBuildAction( args, factory, out ).run();
		verify(out, times(1)).exception(Mockito.anyString(), Mockito.eq(toBeThrown));
	}
	
	@Test
	public void CommandLineActionsFactory_adds_BuiltInActions_and_ApplicationRunActionFactory() {
		CommandLineActionsFactory factory = new CommandLineActionsFactory();
		List<CommandLineAction> actions = factory.create();
		
		assertEquals(2, actions.size());
		assertEquals(BuiltInActions.class, actions.get(0).getClass());
		assertEquals(ApplicationRunActionFactory.class, actions.get(1).getClass());
	}
}
