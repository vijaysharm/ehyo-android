package com.vijaysharma.ehyo.core;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.base.CaseFormat;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class TemplateMethods {
	/**
	 * Should replace MainActivity to activity_main 
	 */
	static class ActivityToLayout implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
//			return "[TODO: activityToLayout " + arguments.get(0) + "]";
			SimpleScalar scalar = (SimpleScalar) arguments.get(0);
			String line = scalar.getAsString();
			line = line.replace("Activity", "");
			
			return "activity_" + CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, line);			
		}
	}
	
	static class ExtractLetters implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
//			return "[TODO: extractLetters " + arguments.get(0) + "]";
			SimpleScalar line = (SimpleScalar) arguments.get(0);
			return line.getAsString().replaceAll(" ", "");
		}
		
	}
	
	static class EscapeXmlString implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
//			return "[TODO: escapeXmlString " + arguments.get(0) + "]";
			SimpleScalar line = (SimpleScalar) arguments.get(0);
			return StringEscapeUtils.escapeXml11(line.getAsString());				
		}
	}
	
	static class CamelCaseToUnderscore implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
//			return "[TODO: camelCaseToUnderscore " + arguments.get(0) + "]";
			SimpleScalar scalar = (SimpleScalar) arguments.get(0);
			String line = scalar.getAsString();
			return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, line);
		}
	}
	
	/**
	 * Should replace MainActivity to main 
	 */
	static class ClassToResource implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
//			return "[TODO: classToResource " + arguments.get(0) + "]";
			SimpleScalar scalar = (SimpleScalar) arguments.get(0);
			String line = scalar.getAsString();
			line = line.replace("Activity", "");

			return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, line);
		}
	}
	
	static class SlashedPackageName implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
//			return "[TODO: slashed " + arguments.get(0) + "]";
			SimpleScalar line = (SimpleScalar) arguments.get(0);
			return line.getAsString().replaceAll("\\.", File.separator);
		}
	}
	
	static class EscapeXmlAttribute implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
//			return "[TODO: escape " + arguments.get(0) + "]";
			SimpleScalar line = (SimpleScalar) arguments.get(0);
			return StringEscapeUtils.unescapeXml(line.getAsString());	
		}
	}
}
