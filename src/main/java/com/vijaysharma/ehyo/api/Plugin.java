package com.vijaysharma.ehyo.api;

import java.util.List;

public interface Plugin {
	String name();
	List<PluginAction> execute(List<String> args, Service service);
}
