package com.spirit21.swagger.converter.models;

/**
 * 
 * @author dsimon
 *
 */
public class Method {
    private String javadoc;
    private String httpMethod;
    private String header;

    public Method(String javadoc, String httpMethod, String header) {
        this.javadoc = javadoc;
        this.httpMethod = httpMethod;
        this.header = header;
    }

    public String getJavadoc() {
        return javadoc;
    }

    public void setJavadoc(String javadoc) {
        this.javadoc = javadoc;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
