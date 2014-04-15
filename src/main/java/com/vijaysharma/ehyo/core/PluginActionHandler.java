package com.vijaysharma.ehyo.core;

public interface PluginActionHandler<T, K> {
	public void modify(T item, K actions);
}
