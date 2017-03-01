package com.spirit21.swagger.converter.models;

/**
 * 
 * @author dsimon
 *
 */
public class Property {
    private String name;
    private String type;
    private String format;
    private String reference;
    private String genericType;
    private String genericFormat;

    public String getGenericType() {
        return genericType;
    }

    public void setGenericType(String genericType) {
        this.genericType = genericType;
    }

    public String getGenericFormat() {
        return genericFormat;
    }

    public void setGenericFormat(String genericFormat) {
        this.genericFormat = genericFormat;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
