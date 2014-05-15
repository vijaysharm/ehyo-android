package com.vijaysharma.ehyo.core;

import java.io.File;
import java.io.IOException;

import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

public interface TemplateConfigLoader {
	public Configuration create();
	
	public class ClasspathConfigLoader implements TemplateConfigLoader {
		private String path;
		private Class<?> clazz;

		public ClasspathConfigLoader(Class<?> clazz, String path) {
			this.clazz = clazz;
			this.path = path;
		}
		
		@Override
		public Configuration create() {
			Configuration cfg = new Configuration();
			cfg.setClassForTemplateLoading(clazz, path);
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
			
			return cfg;
		}
	}
	
	public class DiskConfigLoader implements TemplateConfigLoader {
		private final File directory;
		
		public DiskConfigLoader(String path) {
			this.directory = new File(path);
		}
		
		@Override
		public Configuration create() {
			try {
				Configuration cfg = new Configuration();
				cfg.setDirectoryForTemplateLoading(directory);
				cfg.setObjectWrapper(new DefaultObjectWrapper());
				cfg.setDefaultEncoding("UTF-8");
				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
				
				return cfg;
			}
			catch ( IOException ex ) {
				throw new UncheckedIoException(ex);
			}
		}
	}
}
