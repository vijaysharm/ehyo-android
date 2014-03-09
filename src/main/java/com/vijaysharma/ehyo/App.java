package com.vijaysharma.ehyo;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class App 
{
    public static void main( String[] args )
    {
    	List<String> strings = Lists.newArrayList("Hi", "World!", "It's", "Vijay!");
        System.out.println( Joiner.on(" ").join(strings) );
    }
}
