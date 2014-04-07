package com.vijaysharma.ehyo.api.logging;

import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.common.base.Strings;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

public class Outputter implements StyledTextOutput {
	public static final StyledTextOutput out = new Outputter(System.out);
	public static final StyledTextOutput err = new Outputter(System.err);
	public static final StyledTextOutput debug = new Outputter(System.out, true);
	
	private final Appendable appendable;
	private final Flushable flushable;
	private final boolean isDebug;
	
	public Outputter(Appendable appendable) {
		this(appendable, false);
	}

	private Outputter(Appendable appendable, boolean debug) {
		this.appendable = appendable;
		this.isDebug = debug;
		this.flushable = create(appendable);
	}
	
	@Override
	public StyledTextOutput print(Object text) {
		text(text);
		return this;
	}
	
	@Override
	public StyledTextOutput println(Object text) {
		text(text);
        println();
		return this;
	}
	
	@Override
	public StyledTextOutput exception(String message, Throwable throwable) {
		if ( ! Strings.isNullOrEmpty(message) )
			println(message);

	    StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        throwable.printStackTrace(writer);
        writer.close();
        text(out.toString());
        return this;	
	}
	
    public StyledTextOutput exception(Throwable throwable) {
    	return exception(null, throwable);
    }
    
	public StyledTextOutput println() {
        text(getLineSeparator());
        return this;
    }
	
    public StyledTextOutput text(Object text) {
        append(text == null ? "null" : text.toString());
        return this;
    }
    
	private static Flushable create(Appendable appendable) {
        if (appendable instanceof Flushable) {
            return (Flushable) appendable;
        }

        return new Flushable() { public void flush() throws IOException {}};
	}
    
    private void append(CharSequence output) {
//    	if ( ! isDebug )
        try {
            appendable.append(output);
            flushable.flush();
        } catch (IOException e) {
            throw new UncheckedIoException(e);
        }
    }
    
    private static String getLineSeparator() {
        return System.getProperty("line.separator");
    }
}
