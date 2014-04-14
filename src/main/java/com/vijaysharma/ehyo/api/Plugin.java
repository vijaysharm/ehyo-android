package com.vijaysharma.ehyo.api;

import java.util.List;

public interface Plugin {
	String name();
	void execute(List<String> args, Service service);
}
