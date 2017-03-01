package com.spirit21.swagger.converter.parsers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.Regex;
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
    protected Regex regexes = new Regex();
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
     * @param offset
     * @param section
     * @return String
     */
    protected String findStringInSectionByRegex(String regex, int offset, String section) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(section);
        while (matcher.find()) {
            String ret = section.substring(matcher.start() + offset, matcher.end());
            ret = ret.trim().replace("\n", "");
            return ret;
        }
        return null;
    }

    /**
     * Removes link tags and code blocks from string
     * 
     * @param str
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
