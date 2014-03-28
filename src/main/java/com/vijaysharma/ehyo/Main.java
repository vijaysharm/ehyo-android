package com.vijaysharma.ehyo;

import static com.google.common.base.Joiner.on;

import com.vijaysharma.ehyo.api.logging.Outputter;
import com.vijaysharma.ehyo.core.commandline.CommandLineFactory;

public class Main implements Runnable {
	private final String[] args;
	private final CommandLineFactory factory;
    public Main(String[] args) {
    	this(args, new CommandLineFactory());
    }
    
    Main(String[] args, CommandLineFactory factory) {
    	this.args = args;
		this.factory = factory;
	}
    
    @Override
    public void run() {
    	if (this.args != null) Outputter.debug.println(on(" ").join(this.args));
    	factory.configure(this.args).run();
    }
    
    public static void main(String[] args) {
		String[] arguments = {
//			"--version",
//			"--plugins", "com.thirdparty.plugin", 
			"--plugin", "manifest-permissions",
//			"--about",
			"--directory", "/Users/vsharma/programming/android/MyApplication",
//			"--help",
		};

		new Main(arguments).run();
	}
}
