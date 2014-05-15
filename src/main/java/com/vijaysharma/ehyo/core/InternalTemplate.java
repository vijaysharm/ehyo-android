package com.vijaysharma.ehyo.core;

import java.util.Map;

import com.vijaysharma.ehyo.api.Template;
import com.vijaysharma.ehyo.core.RecipeDocumentModel.RecipeDocumentCallback;

public interface InternalTemplate extends Template {
	public void apply(Map<String, Object> properties, RecipeDocumentCallback callback);
}
