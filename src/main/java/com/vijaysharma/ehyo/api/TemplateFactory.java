package com.vijaysharma.ehyo.api;

public interface TemplateFactory {
	Template create(String path);
	Template createDiskTemplate(String templateLocation);
}
