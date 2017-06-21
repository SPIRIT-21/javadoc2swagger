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
public class ImportLoader extends AbstractLoader {
    public ImportLoader(Log log) {
        super(log);
    }

    /**
     * Tests a given pattern on a given string and returns an array of all
     * matches.
     * 
     * @param pattern
     * @param file
     * @return List of string
     */
    public List<String> getImportsFromFile(String file) {
        Pattern pattern = Pattern.compile(Regex.IMPORT);
        List<String> sections = new ArrayList<>();
        Matcher matcher = pattern.matcher(file);
        while (matcher.find()) {
            sections.add(file.substring(matcher.start() + 7, matcher.end() - 1));
        }
        return sections;
    }
}
