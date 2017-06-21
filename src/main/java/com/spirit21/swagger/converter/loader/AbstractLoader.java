package com.spirit21.swagger.converter.loader;

import org.apache.maven.plugin.logging.Log;

/**
 * 
 * @author dsimon
 *
 */
public abstract class AbstractLoader {
    protected Log log;

    public AbstractLoader(Log log) {
        this.log = log;
    }

    /**
     * Removes javadoc starting, continuing and closing characters and replaces
     * sequential white spaces with a single white space
     * 
     * @param str
     *            input string
     * @return formatted string
     */
    protected String removeJavadocCharactersFromString(String str) {
        return str.replace("/**", "").replace("*/", "").replace("*", "").trim().replaceAll("[\\s]+", " ");
    }
}