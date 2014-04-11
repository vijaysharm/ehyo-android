package com.vijaysharma.ehyo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactory;

public class MainTest
{
	private CommandLineFactory factory;
	private Action action;
	
	@Before
	public void before() {
		factory = mock(CommandLineFactory.class);
		action = mock(Action.class);
	}
	
	@Test
    public void run_calls_configure_on_factory() {
		when(factory.configure(Mockito.anyList())).thenReturn(action);
		
        Main main = new Main(null, factory);
        main.run();
        verify(factory, times(1)).configure(Mockito.anyList());
    }
	
	@Test
    public void run_calls_run_on_action() {
		when(factory.configure(Mockito.anyList())).thenReturn(action);
		
        Main main = new Main(null, factory);
        main.run();
        verify(action, times(1)).run();
    }
}
