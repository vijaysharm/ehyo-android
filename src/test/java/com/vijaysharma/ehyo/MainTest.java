package com.vijaysharma.ehyo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vijaysharma.ehyo.core.Action;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactory;

@RunWith(MockitoJUnitRunner.class)
public class MainTest
{
	private CommandLineFactory factory;
	private Action action;
	
	@Captor
	ArgumentCaptor<List<String>> captor;
	
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
	
	@Test
	public void Main_converts_args_to_List_of_Strings() {
		String[] args = {"arg"};
		when(factory.configure(Mockito.anyList())).thenReturn(action);
		Main main = new Main(args, factory);
		
		main.run();
        verify(factory, times(1)).configure(captor.capture());
        List<String> value = captor.getValue();
        Assert.assertEquals(1, value.size());
        Assert.assertEquals("arg", value.get(0));
	}
}
