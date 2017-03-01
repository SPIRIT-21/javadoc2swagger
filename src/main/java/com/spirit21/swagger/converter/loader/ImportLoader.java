package com.spirit21.swagger.converter.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

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
     * @param file
     *            file as string
     * @return List of string
     */
    public List<String> getImportsFromFile(String file) {
        String regex = regexes.getImportRegex();
        Pattern pattern = Pattern.compile(regex);
        List<String> sections = new ArrayList<>();
        Matcher matcher = pattern.matcher(file);
        while (matcher.find()) {
            sections.add(file.substring(matcher.start() + 7, matcher.end() - 1));
        }
        return sections;
    }
}
