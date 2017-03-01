package com.spirit21.swagger.converter.parsers;

/**
 * 
 * @author dsimon
 *
 */
@SuppressWarnings("serial")
public class ParserException extends Exception {
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(String message) {
        super(message);
    }
}
