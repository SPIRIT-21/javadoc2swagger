package com.spirit21.swagger.converter.writers;

import org.apache.maven.plugin.logging.Log;

/**
 * 
 * @author dsimon
 *
 */
public abstract class AbstractWriter {
    protected Log log;

    public AbstractWriter(Log log) {
        this.log = log;
    }

    /**
     * appends the prefix for definitions in swagger files
     * 
     * @param in
     *            input string
     * @return merged strings
     */
    protected String appendDefinitionPrefix(String in) {
        String prefix = "#/definitions/";
        return prefix.concat(in);
    }
}
