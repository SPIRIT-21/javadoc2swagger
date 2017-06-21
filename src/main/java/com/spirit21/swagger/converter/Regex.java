package com.spirit21.swagger.converter;

/**
 * 
 * @author dsimon
 *
 */
public class Regex {

    public final static String PACKAGE = "package [^;]*;";
    public final static String JAVADOC = "\\/\\*\\*((?!\\*\\/).)*\\*\\/";
    public final static String ANNOTATION = "@[a-zA-Z]+(\\([^@)]*\\))?";
    public final static String METHOD = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^{]*\\{";
    public final static String IMPORT = "import [^;]*;";
    public final static String API_JAVADOC = "\\/\\*\\*(?=.*@apiTitle).+\\*\\/";
    public final static String CLASS = "public class \\w+";
    public final static String PATH = "@path [^\\n ]*";
    public final static String DESCRIPTION = "([^@{]|\\{@|\\{)*";
    public final static String HTTP_METHOD = "(@GET|@POST|@PUT|@DELETE|@PUT)";
    public final static String IGNORE_JAVAFILE = "@swagger:ignore_javafile";

}
