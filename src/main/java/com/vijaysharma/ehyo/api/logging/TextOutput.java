package com.vijaysharma.ehyo.api.logging;

public interface TextOutput {
	TextOutput print(Object text);
	TextOutput println(Object text);
	TextOutput exception(String message, Throwable throwable);
}
