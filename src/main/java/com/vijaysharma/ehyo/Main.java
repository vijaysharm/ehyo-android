package com.vijaysharma.ehyo;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.vijaysharma.ehyo.api.logging.Output;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactory;

public class Main implements Runnable {
	private final List<String> args;
	private final CommandLineFactory factory;
    public Main(String[] args) {
    	this(args, new CommandLineFactory());
    }
    
    Main(String[] args, CommandLineFactory factory) {
    	this.args = args == null ? Lists.<String>newArrayList() : newArrayList(args);
		this.factory = factory;
	}
    
    @Override
    public void run() {
    	factory.configure(this.args).run();
    }
    
    public static void main(String[] args) {
		String[] main = {
//			"--version",
//			"--plugins", "com.thirdparty.plugin", 
			"--dry-run",
			"--directory", "/Users/vsharma/programming/android/MyApplication"
		};
		
		String[] plugin = {
//			"manifest-permissions",
//			"--add", "access",
//			"--remove", "internet"
				
//			"search-mvn-central",
//			"--lib", "butterknife"
				
			"android-templates"
		};
		
		String[] arguments = concat(main, plugin);
		printArgs(arguments);
		new Main(arguments).run();
	}
    
    private static String[] concat(String[]...strings) {
    	ArrayList<String> result = new ArrayList<String>();
    	for ( String[] s : strings ) {
    		result.addAll(Arrays.asList(s));
    	}
    	return result.toArray(new String[0]);
    }
    
    private static void printArgs(String[] args) {
    	if ( args == null )
    		return;
    	
    	StringBuilder output = new StringBuilder();
    	for ( int index = 0; index < args.length; index++ ) {
    		String arg = args[index];
    		if ( arg.startsWith("--") ) {
    			if ( index != 0 )
    				output.append("\n");
    			output.append(arg);
    		} else {
    			output.append(" " + arg);
    		}
    	}
    	output.append("\n");

    	Output.out.print(output.toString());
    }
}
