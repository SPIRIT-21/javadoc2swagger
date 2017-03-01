package com.spirit21.swagger.converter.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.Resource;
import com.spirit21.swagger.converter.models.Response;
import com.spirit21.swagger.converter.models.Tag;

/**
 * 
 * @author dsimon
 *
 */
public class ResponseParser extends AbstractParser {

    public ResponseParser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
        super(log, loader, tags, definitions);
    }

    /**
     * Searches for responses in a String.
     * 
     * @param section
     *            javadoc section
     * @param imports
     *            imports of a java file
     * @param fileName
     *            java file name
     * @return List of {@link Resource}
     * @throws ParserException
     *             Error while the parsing process
     */
    public List<Response> findResponsesInJavadocSection(String section, List<String> imports, String fileName)
            throws ParserException {
        List<Response> responses = new ArrayList<>();
        String regex = "@responseCode [0-9]{1,3}([\\s]|@responseSchema \\{@link \\w+\\}|@responseType [a-zA-Z]+|@responseMessage [^@]+)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(section);
        while (matcher.find()) {
            String responseSection = section.substring(matcher.start(), matcher.end());
            responses.add(generateResponseFromSection(responseSection, imports, fileName));
        }
        return responses;
    }

    /**
     * Searches for information about a resource in the given section and sets
     * the fields for a resource.
     * 
     * @param section
     *            javadoc section
     * @param imports
     *            imports of a java file
     * @param fileName
     *            java file name
     * @return {@link Resource}
     * @throws ParserException
     *             Error while the parsing process
     */
    private Response generateResponseFromSection(String section, List<String> imports, String fileName)
            throws ParserException {
        DefinitionParser definitionParser = new DefinitionParser(log, loader, tags, definitions);
        Response response = new Response();
        String schema = findStringInSectionByRegex("@responseSchema \\{@link \\w+\\}", 16, section);
        if (schema != null) {
            String title = definitionParser.createDefinitionIfNotExists(schema, imports, fileName);
            Definition definition = definitionParser.getDefinitionByClassName(title);
            response.setDefinition(definition);
        }
        response.setStatusCode(Integer.parseInt(findStringInSectionByRegex("@responseCode [0-9]{1,3}", 14, section)));
        response.setMessage(findStringInSectionByRegex("@responseMessage [^@]+", 17, section));
        response.setResponseType(findStringInSectionByRegex("@responseType [a-zA-Z]+", 14, section));
        return response;
    }
}
