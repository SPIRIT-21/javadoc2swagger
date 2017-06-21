package com.spirit21.swagger.converter.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.Regex;
import com.spirit21.swagger.converter.models.Method;

/**
 * 
 * @author dsimon
 *
 */
public class MethodLoader extends AbstractLoader {
    public MethodLoader(Log log) {
        super(log);
    }

    private Integer javadocEnd;

    /**
     * Find methods that define an operation.
     * 
     * @param file
     * @return {@link Method} object
     */
    public List<Method> getMethodsFromJavaFile(String file) {
        List<Method> methods = new ArrayList<>();
        String sectionRegex = Regex.JAVADOC + "[\\s]*" + Regex.HTTP_METHOD + "([\\s]*" + Regex.ANNOTATION + ")*[\\s]*"
                + Regex.METHOD;
        Pattern pattern = Pattern.compile(sectionRegex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(file);
        while (matcher.find()) {
            String section = file.substring(matcher.start(), matcher.end());
            String javadocSection = findJavadocSectionByRegexInSection(section, Regex.JAVADOC);
            List<String> annotations = findAnnotationsByRegexInSection(section, Regex.ANNOTATION);
            String header = findMethodByRegexInSection(section, Regex.METHOD);
            String httpMethod = findHttpMethodInAnnotations(annotations);
            methods.add(new Method(removeJavadocCharactersFromString(javadocSection), httpMethod, header));
        }
        return methods;
    }

    private String findHttpMethodInAnnotations(List<String> annotations) {
        for (String annotation : annotations) {
            switch (annotation) {
            case "@GET":
            case "@POST":
            case "@PUT":
            case "@DELETE":
                return annotation.substring(1).toLowerCase();
            default:
                break;
            }
        }
        return null;
    }

    /**
     * Searches for the first occurrence of a string matching the given regex on
     * the given section
     * 
     * @param section
     * @param regex
     * @return first matching string
     */
    private String findJavadocSectionByRegexInSection(String section, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(section);
        if (matcher.find()) {
            javadocEnd = matcher.end();
            return section.substring(matcher.start(), matcher.end());
        }
        return "";
    }

    /**
     * Searches for annotations and adds each to a list of strings
     * 
     * @param section
     * @param regex
     * @return list of annotation strings
     */
    private List<String> findAnnotationsByRegexInSection(String section, String regex) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(section);
        while (matcher.find()) {
            if (matcher.end() > javadocEnd) {
                matches.add(section.substring(matcher.start(), matcher.end()));
            }
        }
        return matches;
    }

    /**
     * Searches for the first occurrence of a string matching the given regex on
     * the given section and comes after the javadoc block
     * 
     * @param section
     * @param regex
     * @return first matching string
     */
    private String findMethodByRegexInSection(String section, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(section);
        while (matcher.find()) {
            if (matcher.start() > javadocEnd) {
                return section.substring(matcher.start(), matcher.end());
            }
        }
        return "";
    }
}
