package com.spirit21.swagger.converter.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.Tag;

/**
 * Abstract class for Javadoc parsers. Provides functions needed in multiple
 * parsers.
 * 
 * @author dsimon
 *
 */
public abstract class AbstractParser {
    protected Log log;
    protected ClassLoader loader;
    protected List<Tag> tags;
    protected List<Definition> definitions;

    public AbstractParser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
        this.log = log;
        this.loader = loader;
        this.tags = tags;
        this.definitions = definitions;
    }

    /**
     * Finds a char sequence in a string by a regular expression. Returns the
     * char sequence with an offset.
     * 
     * @param regex
     *            regular expression
     * @param offset
     *            number of characters to cut the found string at the beginning
     * @param section
     *            string in where to search for matches
     * @return String
     */
    protected String findStringInSectionByRegex(String regex, int offset, String section) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(section);
        if (matcher.find()) {
            String ret = section.substring(matcher.start() + offset, matcher.end());
            ret = ret.trim().replace("\n", "");
            return ret;
        }
        return null;
    }

    /**
     * Finds all char sequences in a string by a regular expression. Returns the
     * list of char sequences with an offset.
     * 
     * @param regex
     *            regular expression
     * @param offset
     *            number of characters to cut the found string at the beginning
     * @param section
     *            string in where to search for matches
     * @return list of String
     */
    protected List<String> findStringsInSectionByRegex(String regex, int offset, String section) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(section);

        List<String> findings = new ArrayList<String>();

        while (matcher.find()) {
            String ret = section.substring(matcher.start() + offset, matcher.end());
            ret = ret.trim().replace("\n", "");
            if (ret.isEmpty()) {
                ret = null;
            }
            findings.add(ret);
        }
        return findings;
    }

    /**
     * Removes link tags and code blocks from string
     * 
     * @param str
     *            input string
     * @return formatted string
     */
    protected String removeUnwantedCharactersFromJavadocDescription(String str) {
        return str.replace("{@link ", "").replace("}", "").replace("<code>", "").replace("</code>", "");
    }

    /**
     * Extracts a class name by removing the javadoc link tag
     * 
     * @param link
     *            link tag as String
     * @return class name
     */
    protected String getClassNameFromLinkTag(String link) {
        return link.substring(7, link.length() - 1);
    }
}
