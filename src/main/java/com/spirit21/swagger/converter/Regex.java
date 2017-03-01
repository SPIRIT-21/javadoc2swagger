package com.spirit21.swagger.converter;

/**
 * 
 * @author dsimon
 *
 */
public class Regex {
    private String javadocRegex = "\\/\\*\\*((?!\\*\\/).)*\\*\\/";
    private String annotationRegex = "@[a-zA-Z]+(\\([^@)]*\\))?";
    private String methodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^{]*\\{";
    private String importRegex = "import [^;]*;";
    private String apiJavadocRegex = "\\/\\*\\*(?=.*@apiTitle).+\\*\\/";
    private String classRegex = "public class \\w+";
    private String pathRegex = "@path [^\\n ]*";
    private String descriptionRegex = "([^@{]|\\{@|\\{)*";
    private String httpMethodRegex = "(@GET|@POST|@PUT|@DELETE|@PUT)";

    public String getHttpMethodRegex() {
        return httpMethodRegex;
    }

    public String getJavadocRegex() {
        return javadocRegex;
    }

    public String getAnnotationRegex() {
        return annotationRegex;
    }

    public String getMethodRegex() {
        return methodRegex;
    }

    public String getImportRegex() {
        return importRegex;
    }

    public String getApiJavadocRegex() {
        return apiJavadocRegex;
    }

    public String getClassRegex() {
        return classRegex;
    }

    public String getPathRegex() {
        return pathRegex;
    }

    public String getDescriptionRegex() {
        return descriptionRegex;
    }
}
