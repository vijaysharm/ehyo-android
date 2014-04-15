package com.vijaysharma.ehyo.core;


public interface ChangeManager<T> {
	void apply(T actions);
	void commit(boolean dryrun);
}
