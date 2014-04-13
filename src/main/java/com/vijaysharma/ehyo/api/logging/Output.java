package com.vijaysharma.ehyo.api.logging;

import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.google.common.base.Strings;
import com.vijaysharma.ehyo.core.utils.UncheckedIoException;

public class Output implements TextOutput {
	public static final TextOutput out = new Output(System.out);
	public static final TextOutput err = new Output(System.err);
	public static final TextOutput debug = new Output(System.out, true);
	
	private final Appendable appendable;
	private final Flushable flushable;
	private final boolean isDebug;
	
	public Output(Appendable appendable) {
		this(appendable, false);
	}

	private Output(Appendable appendable, boolean debug) {
		this.appendable = appendable;
		this.isDebug = debug;
		this.flushable = create(appendable);
	}
	
	@Override
	public TextOutput print(Object text) {
		text(text);
		return this;
	}
	
	@Override
	public TextOutput println(Object text) {
		text(text);
        println();
		return this;
	}
	
	@Override
	public TextOutput exception(String message, Throwable throwable) {
		if ( ! Strings.isNullOrEmpty(message) )
			println(message);

	    StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        throwable.printStackTrace(writer);
        writer.close();
        text(out.toString());
        return this;	
	}
	
    public TextOutput exception(Throwable throwable) {
    	return exception(null, throwable);
    }
    
	public TextOutput println() {
        text(getLineSeparator());
        return this;
    }
	
    public TextOutput text(Object text) {
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
