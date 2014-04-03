package com.vijaysharma.ehyo.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.logging.Outputter;

public class FileSelector<T> {
	private final Scanner scanner;
	private final Function<T, String> renderer;
	
	public FileSelector(Function<T, String> renderer) {
		this(renderer, System.in);
	}
	
	FileSelector(Function<T, String> renderer, InputStream in) {
		this.renderer = renderer;
		this.scanner = new Scanner(new InputStreamReader(in));
	}
	
	public List<T> select(List<T> items) {
		StringBuilder dialog = new StringBuilder();
		int max = items.size() + 1;
		
		dialog.append("Which of the following would you like to modify\n");
		
		for ( int index = 0; index < items.size(); index++ ) {
			T item = items.get(index);
			dialog.append("[" + (index + 1) + "] " + renderer.apply(item) + "\n");
		}

		dialog.append("[" + max + "] Apply to all\n");
		dialog.append("Select: ");
		Outputter.out.println(dialog.toString());
		
		int selection = read(1, max);

		if ( selection < 1 || selection > max)
			return Lists.newArrayList();

		if ( selection == max )
			return items;
		
		return Lists.newArrayList(items.get(selection - 1));
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
