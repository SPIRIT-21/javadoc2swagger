package com.spirit21.swagger.converter.models;

import java.util.List;

/**
 * 
 * @author dsimon
 *
 */
public class Definition {
    private String type;
    private String className;
    private List<Property> properties;
    private List<String> required;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }
}
