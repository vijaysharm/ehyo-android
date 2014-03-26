package com.vijaysharma.ehyo.api.logging;

public interface StyledTextOutput {
	StyledTextOutput println(Object text);
	StyledTextOutput exception(String message, Throwable throwable);
}
