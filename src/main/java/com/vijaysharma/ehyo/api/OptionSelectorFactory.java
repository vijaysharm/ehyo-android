package com.vijaysharma.ehyo.api;

import com.vijaysharma.ehyo.api.utils.OptionSelector;

public interface OptionSelectorFactory {
	<T> OptionSelector<T> create(Class<T> clazz);
}