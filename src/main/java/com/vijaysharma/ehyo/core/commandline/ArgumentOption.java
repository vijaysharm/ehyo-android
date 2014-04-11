package com.vijaysharma.ehyo.core.commandline;

import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

public class ArgumentOption<T> {
	public static class ArgumentOptionBuilder<T> {
		
		private final String[] expectedArgs;
		private Class<T> requiredArgType;
		private T value;

		public ArgumentOptionBuilder(String...args) {
			this.expectedArgs = args;
		}
		
		public ArgumentOptionBuilder<T> withRequiredArg(Class<T> clazz) {
			this.requiredArgType = clazz;
			return this;
		}
		
		public ArgumentOptionBuilder<T> defaultsTo( T value ) {
			this.value = value;
			return this;
		}
		
		public ArgumentOption<T> build() {
			ImmutableSet.Builder<String> args = ImmutableSet.builder();
			for ( String arg : expectedArgs ) {
				if ( arg.length() == 1 )
					args.add("-" + arg);
				else
					args.add("--" + arg);
			}
			
			return new ArgumentOption<T>(args.build(), requiredArgType, value);
		}
	}
	
	private final Set<String> args;
	private final T defaultArgValue;
	private final Class<T> requiredArgType;
	
	private ArgumentOption(Set<String> expectedArgs, Class<T> requiredArgType, T defaultArgValue) {
		this.args = expectedArgs;
		this.requiredArgType = requiredArgType;
		this.defaultArgValue = defaultArgValue;
	}
	
	public boolean supports(String arg) {
		return args.contains(arg);
	}
	
	public boolean hasRequiredArg() {
		return this.requiredArgType != null;
	}
	
	public T getDefaultArgValue() {
		return defaultArgValue;
	}
	
	public Class<T> getRequiredArgType() {
		return requiredArgType;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + Joiner.on(", ").join(args);
	}
}
