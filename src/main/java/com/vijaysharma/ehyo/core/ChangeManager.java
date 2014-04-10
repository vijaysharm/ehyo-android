package com.vijaysharma.ehyo.core;


public interface ChangeManager<T> {
	void apply(PluginActionHandler<T> handler);
	void commit(boolean dryrun);
}
