package com.vijaysharma.ehyo.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

class TemplateConverter {
	public freemarker.template.Template get(Configuration config, String path) {
		try {
			return config.getTemplate(path);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}
	
	public Document asDocument(freemarker.template.Template template, Map<String, Object> mapping) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			template.process(mapping, new OutputStreamWriter(output));
			return new SAXBuilder().build(new ByteArrayInputStream(output.toByteArray()));
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		} catch (JDOMException e) {
			throw new RuntimeException(e);
		}
	}
	public List<String> asListOfStrings(freemarker.template.Template template, Map<String, Object> mapping) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			template.process(mapping, new OutputStreamWriter(output));
			return IOUtils.readLines(new ByteArrayInputStream(output.toByteArray()));
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new UncheckedIoException(e);
		}
	}
	
	public void dump(freemarker.template.Template template, Map<String, Object> mapping) {
		Writer out = new OutputStreamWriter(System.out);
		try {
			template.process(mapping, out);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Document asDocument(File file) {
		try {
			SAXBuilder builder = new SAXBuilder();
			return builder.build(file);
		} catch (IOException ioe) {
			throw new UncheckedIoException(ioe);
		} catch (JDOMException jde) {
			throw new RuntimeException(jde);
		}
	}
}
