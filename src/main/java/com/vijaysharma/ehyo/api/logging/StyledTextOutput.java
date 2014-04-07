package com.vijaysharma.ehyo.api.logging;

public interface StyledTextOutput {
	StyledTextOutput print(Object text);
	StyledTextOutput println(Object text);
	StyledTextOutput exception(String message, Throwable throwable);
}
