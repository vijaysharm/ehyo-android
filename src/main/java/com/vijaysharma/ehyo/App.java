package com.vijaysharma.ehyo;

import org.slf4j.LoggerFactory;

import com.vijaysharma.ehyo.core.models.GradleSettings;

public class App 
{
    public static void main( String[] args )
    {
    	String directory = "/Users/vsharma/programming/android/MyApplication";
    	GradleSettings settings = GradleSettings.read(directory);
    	LoggerFactory.getLogger(App.class).info("GetOnThat");
    }
}
