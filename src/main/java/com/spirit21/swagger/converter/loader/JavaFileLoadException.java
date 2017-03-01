package com.spirit21.swagger.converter.loader;

/**
 * 
 * @author dsimon
 *
 */
@SuppressWarnings("serial")
public class JavaFileLoadException extends Exception {
    public JavaFileLoadException(String causeMessage, Throwable cause) {
        super(causeMessage, cause);
    }
}
