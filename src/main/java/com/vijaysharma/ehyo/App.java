package com.vijaysharma.ehyo;

public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	try {
    		new Main(args).run();
    		System.exit(0);
    	} catch (Exception ex) {
    		System.exit(1);
    	}
    }
}
