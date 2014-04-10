package com.vijaysharma.ehyo.api.utils;

public interface OptionSelectorFactory {
	<T> OptionSelector<T> create(Class<T> clazz);
}
