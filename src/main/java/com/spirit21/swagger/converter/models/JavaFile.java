package com.spirit21.swagger.converter.models;

import java.util.List;

/**
 * 
 * @author dsimon
 *
 */
public class JavaFile {
    private String fileName;
    private String packageName;
    private List<String> imports;
    private List<Method> functions;
    private String apiJavadoc;
    private List<String> classAnnotations;
    private String classJavadoc;

    public List<String> getImports() {
        return imports;
    }

    public void setImports(List<String> imports) {
        this.imports = imports;
    }

    public List<Method> getFunctions() {
        return functions;
    }

    public void setFunctions(List<Method> functions) {
        this.functions = functions;
    }

    public String getApiJavadoc() {
        return apiJavadoc;
    }

    public void setApiJavadoc(String apiJavadoc) {
        this.apiJavadoc = apiJavadoc;
    }

    public List<String> getClassAnnotations() {
        return classAnnotations;
    }

    public void setClassAnnotations(List<String> classAnnotations) {
        this.classAnnotations = classAnnotations;
    }

    public String getClassJavadoc() {
        return classJavadoc;
    }

    public void setClassJavadoc(String classJavadoc) {
        this.classJavadoc = classJavadoc;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
