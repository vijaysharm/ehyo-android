package com.vijaysharma.ehyo.api.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import joptsimple.internal.Strings;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.api.logging.TextOutput;
import com.vijaysharma.ehyo.api.utils.Questioner.Question;

public class Questioner<T extends Question, K> {
	private final Scanner scanner;
	private final TextOutput out;
	private final AnswerFactory<T, K> factory;
	
	public Questioner(AnswerFactory<T, K> factory) {
		this(factory, System.in, Output.out);
	}
	
	Questioner(AnswerFactory<T, K> factory, InputStream in, TextOutput out) {
		this.factory = factory;
		this.scanner = new Scanner(new InputStreamReader(in));
		this.out = out;
	}
	
	public List<K> prompt(List<T> questions) {
		ImmutableList.Builder<K> answers = ImmutableList.builder();
		
		for ( T question : questions ) {
			K answer = ask( question );
			answers.add(answer);
		}
		
		return answers.build();
	}
	
	private K ask(T question) {
		if ("enum".equalsIgnoreCase(question.getType())) {
			String reply = askOptionQuestion(question);
			return factory.apply(question, reply);
		} else {
			out.println(buildQuestion(question));
			String reply = read(question);
		
			return factory.apply(question, reply);
		}
	}

	private String read(T question) {
		String value = scanner.nextLine();
		if (Strings.isNullOrEmpty(value))
			value = question.getValue();
		
		return value;
	}

	private String buildQuestion(T question) {
		StringBuilder q = new StringBuilder();
		q.append(question.getName());
		
		if ( ! Strings.isNullOrEmpty(question.getValue()))
			q.append(" [" + question.getValue() + "]");
			
		q.append(": ");
		
		return q.toString();
	}

	private String askOptionQuestion(final T question) {
		Function<String, String> renderer = new Function<String, String>() {
			@Override
			public String apply(String input) {
				return question.getOptions().get(input);
			}
		};
		OptionSelector<String> op = new OptionSelector<String>(question.getName(), renderer);
		return op.selectOne(Lists.newArrayList(question.getOptions().keySet()));
	}
	
	public static interface Question {
		public String getValue();
		public String getName();
		public String getType();
		public Map<String, String> getOptions();
	}
	
	public static interface AnswerFactory<T, K> {
		K apply(T input, String value);
	}
}
