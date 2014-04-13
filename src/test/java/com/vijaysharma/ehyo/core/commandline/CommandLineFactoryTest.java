package com.vijaysharma.ehyo.core.commandline;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactory.ActionFactory;

public class CommandLineFactoryTest {
	private ActionFactory factory;
	private Action action;
	
	@Before
	public void before() {
		factory = mock(ActionFactory.class);
		action = mock(Action.class);
	}
	
	@Test
	public void configure_calls_create() {
		CommandLineFactory clf = new CommandLineFactory(factory);
		clf.configure(null);
		
		verify(factory, times(1)).create(null);
	}
	
	@Test
	public void configure_returns_factory_created_action() {
		Mockito.when(factory.create(null)).thenReturn(action);
		CommandLineFactory clf = new CommandLineFactory(factory);
		Action actualAction = clf.configure(null);
		
		Assert.assertEquals(action, actualAction);
	}
	
	@Test
	public void configure_returns_a_ParseAndBuildAction_object() {
		CommandLineFactory clf = new CommandLineFactory();
		Action action = clf.configure(null);
		Assert.assertEquals(ParseAndBuildAction.class, action.getClass());
	}
}
