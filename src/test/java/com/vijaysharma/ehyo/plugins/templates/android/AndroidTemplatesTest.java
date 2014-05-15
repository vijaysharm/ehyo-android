package com.vijaysharma.ehyo.plugins.templates.android;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.GentleMessageException;
import com.vijaysharma.ehyo.api.ProjectSourceSet;
import com.vijaysharma.ehyo.api.Service;
import com.vijaysharma.ehyo.api.Template;
import com.vijaysharma.ehyo.api.TemplateProperty;
import com.vijaysharma.ehyo.api.UsageException;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.api.utils.OptionSelector;
import com.vijaysharma.ehyo.api.utils.Questioner;
import com.vijaysharma.ehyo.api.utils.Questioner.AnswerFactory;
import com.vijaysharma.ehyo.plugins.templates.android.AndroidTemplates.DefaultQuestion;
import com.vijaysharma.ehyo.plugins.templates.android.TemplateRegistry.TemplateItem;

@SuppressWarnings("unchecked")
public class AndroidTemplatesTest {
	private TextOutput out;
	private TemplateRegistry registry;
	private AndroidTemplates command;
	private Service service;
	
	@Before
	public void before() {
		out = mock(TextOutput.class);
		registry = mock(TemplateRegistry.class);
		service = mock(Service.class);
		command = new AndroidTemplates(registry, out);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_when_args_are_empty() {
		List<String> args = Lists.newArrayList();
		command.execute(args, service);
	}
	
	@Test(expected=UsageException.class)
	public void execute_throws_if_no_acceptable_arguments_are_given() {
		List<String> args = Lists.newArrayList("arg");
		command.execute(args, service);
	}
	
//	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_no_templates_found_to_list_with_dash_l() {
		List<String> args = Lists.newArrayList("-l");
		command.execute(args, service);
	}
	
	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_no_templates_found_to_list() {
		List<String> args = Lists.newArrayList("--list");
		command.execute(args, service);
	}
	
//	@Test
	public void execute_lists_templates() {
		List<String> args = Lists.newArrayList("--list");
		TemplateItem item = mock(TemplateItem.class);
		List<TemplateItem> templates = Lists.newArrayList(item);
		
		when(registry.find()).thenReturn(templates);
		when(item.getName()).thenReturn("template");
		
		command.execute(args, service);
		
		verify(out).println("Templates Found\n"+
							"---------------\n" + 
							"    template\n");
	}
	
//	@Test(expected=UsageException.class)
	public void execute_throws_when_dash_a_has_no_args() {
		List<String> args = Lists.newArrayList("-a");
		command.execute(args, service);
	}
	
//	@Test(expected=UsageException.class)
	public void execute_throws_when_apply_has_no_args() {
		List<String> args = Lists.newArrayList("--apply");
		command.execute(args, service);
	}
	
//	@Test(expected=GentleMessageException.class)
	public void execute_throws_when_no_templates_found_to_apply() {
		List<String> args = Lists.newArrayList("--apply", "boo");
		command.execute(args, service);
	}
	
//	@Test
	public void execute_calls_applyTemplate_on_project_source_set() {
		List<String> args = Lists.newArrayList("--apply", "template");
		TemplateItem item = mock(TemplateItem.class);
		List<TemplateItem> items = Lists.newArrayList(item);
		OptionSelector<TemplateItem> templateItemSelector = mock(OptionSelector.class);
		Questioner<DefaultQuestion, TemplateProperty> questioner = mock(Questioner.class);
		Template template = mock(Template.class);
		TemplateProperty property = mock(TemplateProperty.class);
		List<TemplateProperty> properties = Lists.newArrayList(property);
		OptionSelector<ProjectSourceSet> configSelector = mock(OptionSelector.class);
		ProjectSourceSet sourceSet = mock(ProjectSourceSet.class);
		List<ProjectSourceSet> sourceSets = Lists.newArrayList(sourceSet);
		
		when(item.getFullPath()).thenReturn("template");
		when(registry.find(Mockito.anyString())).thenReturn(items);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(TemplateItem.class))).thenReturn(templateItemSelector);
		when(templateItemSelector.selectOne(items)).thenReturn(item);
		when(service.loadTemplate("template")).thenReturn(template);
		when(service.createPrompt(Mockito.any(AnswerFactory.class))).thenReturn(questioner);
		when(questioner.prompt(Mockito.anyList())).thenReturn(properties);
		when(service.getSourceSets()).thenReturn(sourceSets);
		when(service.createSelector(Mockito.anyString(), Mockito.eq(ProjectSourceSet.class))).thenReturn(configSelector);
		when(configSelector.selectOne(sourceSets)).thenReturn(sourceSet);
		
		command.execute(args, service);
		verify(sourceSet).applyTemplate(template, properties);
	}
}
