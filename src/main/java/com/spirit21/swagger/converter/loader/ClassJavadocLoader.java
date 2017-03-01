package com.spirit21.swagger.converter.loader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

/**
 * 
 * @author dsimon
 *
 */
public class ClassJavadocLoader extends AbstractLoader {
    public ClassJavadocLoader(Log log) {
        super(log);
    }

    /**
     * Searches for the Javadoc section that belongs to the class definition and
     * returns it, if it was found
     * 
     * @param fileString
     * @return Javadoc section as String
     */
    public String getClassJavadocFromJavaFile(String fileString) {
        String javadocRegex = regexes.getJavadocRegex();
        String regex = javadocRegex + "[\\s]*(" + regexes.getAnnotationRegex() + "[\\s]*)+[\\s]*"
                + regexes.getClassRegex();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileString);
        while (matcher.find()) {
            String section = fileString.substring(matcher.start(), matcher.end());
            Pattern javadocPattern = Pattern.compile(javadocRegex);
            Matcher javadocMatcher = javadocPattern.matcher(section);
            while (javadocMatcher.find()) {
                return section.substring(javadocMatcher.start(), javadocMatcher.end());
            }
        }
        return null;
    }
}
