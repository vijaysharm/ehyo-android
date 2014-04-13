package com.vijaysharma.ehyo.api.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.logging.Output;

public class OptionSelector<T> {
	private final Scanner scanner;
	private final Function<T, String> renderer;
	private String header;
	
	public OptionSelector(String header, Function<T, String> renderer) {
		this(header, renderer, System.in);
	}
	
	OptionSelector(String header, Function<T, String> renderer, InputStream in) {
		this.renderer = renderer;
		this.header = header;
		this.scanner = new Scanner(new InputStreamReader(in));
	}
	
	public List<T> select(List<T> items, boolean multiselect) {
		StringBuilder dialog = new StringBuilder();
		int max = items.size() + (multiselect ? 1 : 0);
		
		dialog.append(header + "\n");
		
		for ( int index = 0; index < items.size(); index++ ) {
			T item = items.get(index);
			dialog.append("[" + (index + 1) + "] " + renderer.apply(item) + "\n");
		}

		if ( multiselect ) dialog.append("[" + max + "] Apply to all\n");
		
		dialog.append("Select: ");
		Output.out.println(dialog.toString());
		
		int selection = read(1, max);

		if ( selection < 1 || selection > max)
			return Lists.newArrayList();

		if ( selection == max )
			return items;
		
		return Lists.newArrayList(items.get(selection - 1));	
	}
	
	public List<T> select(List<T> items) {
		return this.select(items, true);
	}

	private int read(int min, int max) {
		String selection = scanner.next();
		try {
			int parseInt = Integer.parseInt(selection);
			if ( parseInt < min || parseInt > max )
				return -1;

			return parseInt;
		} catch ( NumberFormatException nfe ) {
			return -1;
		}
	}
}
