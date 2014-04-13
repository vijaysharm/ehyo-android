package com.vijaysharma.ehyo.core.commandline;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


public class CommandLineParser {
	private final ImmutableList.Builder<ArgumentOption<?>> options = ImmutableList.builder();
	private static final Object NULL = new Object();
	
	public CommandLineParser(List<ArgumentOption<?>> options) {
		this.options.addAll(options);
	}
	
	public CommandLineParser() {
		
	}
	
	public void addOptions(ArgumentOption<?>...ops) {
		if ( ops == null || ops.length == 0 )
			return;
		
		for ( ArgumentOption<?> op : ops ) {
			options.add(op);
		}
	}
	
	public ParsedSet parse(List<String> args) {
		ImmutableMap.Builder<ArgumentOption<?>, Object> parsed = ImmutableMap.builder();
		ImmutableList<ArgumentOption<?>> ops = options.build();
		for ( Iterator<String> it = args.iterator(); it.hasNext(); ) {
			String arg = it.next();
			for ( ArgumentOption<?> op : ops ) {
				if ( op.supports(arg) ) {
					it.remove();
					if (op.hasRequiredArg()) {
						if (! it.hasNext() )
							throw new IllegalArgumentException(arg + " has required argument");
						parsed.put(op, it.next());
						it.remove();
					} else {
						parsed.put(op, NULL);
					}
					
					break;
				}
			}
		}
		
		return new ParsedSet(parsed.build(), args);
	}
	
	public static class ParsedSet {
		private final ImmutableMap<ArgumentOption<?>, Object> parsed;
		private final List<String> args;

		public ParsedSet(ImmutableMap<ArgumentOption<?>, Object> parsed, List<String> args) {
			this.parsed = parsed;
			this.args = args;
		}

		public boolean has(ArgumentOption<?> option) {
			return parsed.containsKey(option);
		}
		
		public List<String> getRemainingArgs() {
			return args;
		}
		
		public <T> T value(ArgumentOption<T> option) {
			Object object = parsed.get(option);
			if ( object == null ) {
				return option.getDefaultArgValue();
			}
			
			if ( object == NULL ) {
				throw new IllegalArgumentException("Option: " + option + " was seen without argument" );
			}
			
			return newInstanceFromValue(option, object);
		}

		private <T> T newInstanceFromValue(ArgumentOption<T> option, Object value) {
			try {
				Class<T> clazz = option.getRequiredArgType();
				Constructor<T> constructor = clazz.getConstructor(String.class);
				return constructor.newInstance(value);
			} catch (Exception e) {
//				throw new IllegalArgumentException("Unable to cast [" + value + "] to [" + clazz.getSimpleName() + "]");
				return option.getDefaultArgValue();
			}
		}
	}
}