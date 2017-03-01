package com.spirit21.swagger.converter.loader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

/**
 * 
 * @author dsimon
 *
 */
public class ApiJavadocLoader extends AbstractLoader {
    public ApiJavadocLoader(Log log) {
        super(log);
    }

    /**
     * Find Javadoc that contains basic API information.
     * 
     * @param fileString
     * @return javadoc
     */
    public String getApiJavadocFromJavaFile(String fileString) {
        String apiJavadocRegex = regexes.getApiJavadocRegex();
        Pattern pattern = Pattern.compile(apiJavadocRegex);
        Matcher matcher = pattern.matcher(fileString);
        while (matcher.find()) {
            return fileString.substring(matcher.start(), matcher.end());
        }
        return null;
    }
}
