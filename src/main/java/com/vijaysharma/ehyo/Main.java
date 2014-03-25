package com.vijaysharma.ehyo;

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
    	factory.configure(this.args).run();
    }
    
    public static void main(String[] args) {
		String[] arguments = {
			"--version",
			"--plugins", "", 
			"--plugin", "list",
			"--directory", ".",
			"--help",
		};
		
		new Main(arguments).run();
	}
}


//initializeLoggers();

//OptionSet options = parser.parse( args );
//if ( options.has(help) ) {
//	parser.printHelpOn( System.out );
//} else {
//	l.info("loding plugins");
//	List<Plugin> plugins = loadPlugins();
//	
//	if ( options.has(list) ) {
//		
//	} else {
//    	File file = directory.value(options);
//    	if ( ! file.isDirectory() ) {
//    		l.error( "Expected a directory. Got {}", directory );
//    		System.exit(-1);
//    	}
//    	
//    	String root = file.getName();
//    	
//    	FileObserverProjectBuilder observer = new FileObserverProjectBuilder( root );
//		showFiles(file.listFiles(), observer);
//		
//		ProjectRegistry registry = observer.build();        		
//	}
//}
//}
//
//public static void initializeLoggers() throws Exception {
//Reflections reflections = new Reflections("com.vijaysharma.ehyo", new FieldAnnotationsScanner());
//Set<Field> set = reflections.getFieldsAnnotatedWith(IsLogger.class);
//for (Field field : set) {
//	field.set(null, LoggerFactory.getLogger(field.getDeclaringClass()));
//}	
//}
//
//public static List<Plugin> loadPlugins() throws Exception {
//List<Plugin> plugins = Lists.newArrayList();
//Reflections reflections = new Reflections("com.vijaysharma.ehyo");
//Set<Class<? extends Plugin>> subTypesOf = reflections.getSubTypesOf(Plugin.class);
//for ( Class<? extends Plugin> pluginClass : subTypesOf ) {
//	plugins.add(pluginClass.newInstance());
//}
//
//return plugins;
//}
