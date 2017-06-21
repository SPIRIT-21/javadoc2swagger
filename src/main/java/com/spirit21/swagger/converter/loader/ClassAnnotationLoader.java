package com.spirit21.swagger.converter.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.Regex;

/**
 * 
 * @author dsimon
 *
 */
public class ClassAnnotationLoader extends AbstractLoader {
    public ClassAnnotationLoader(Log log) {
        super(log);
    }

    /**
     * Finds the section with the class definition and its annotations
     * 
     * @param fileString
     *            file as a single string
     * @return List of annotations
     */
    public List<String> getClassAnnotationsFromJavaFile(String fileString) {
        List<String> annotations = new ArrayList<>();
        String reg = "(" + Regex.ANNOTATION + "[\\s]*)+" + Regex.CLASS;
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(fileString);
        while (matcher.find()) {
            String section = fileString.substring(matcher.start(), matcher.end());
            Pattern annotationPattern = Pattern.compile(Regex.ANNOTATION);
            Matcher annotationMatcher = annotationPattern.matcher(section);
            while (annotationMatcher.find()) {
                annotations.add(section.substring(annotationMatcher.start(), annotationMatcher.end()).trim());
            }
        }
        return annotations;
    }
}
