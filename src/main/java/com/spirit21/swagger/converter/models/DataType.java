package com.spirit21.swagger.converter.models;

/**
 * 
 * @author dsimon
 *
 */
public class DataType {
    private String type;
    private String format;

    public DataType(String type) {
        this.type = type;
    }

    public DataType(String type, String format) {
        this.type = type;
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
