package com.spirit21.swagger.converter.parsers;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.JavaFile;
import com.spirit21.swagger.converter.models.Swagger;
import com.spirit21.swagger.converter.models.SwaggerModel;
import com.spirit21.swagger.converter.models.Tag;

/**
 * Parser for getting basic API information in Javadoc.
 * 
 * @author dsimon
 *
 */
public class SwaggerApiParser extends AbstractParser implements SwaggerParser {
    public SwaggerApiParser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
        super(log, loader, tags, definitions);
    }

    @Override
    public List<SwaggerModel> parse(List<JavaFile> javaFiles) throws ParserException {
        List<SwaggerModel> swaggers = new ArrayList<>();
        for (JavaFile file : javaFiles) {
            Swagger swagger = findSwaggerInJavaFile(file.getApiJavadoc());
            if (swagger != null) {
                swaggers.add(swagger);
            }
        }
        if (swaggers.isEmpty()) {
            String msg = "You need to provide information about your API! Title is required.";
            throw new ParserException(msg);
        } else if (swaggers.size() > 1) {
            throw new ParserException("Multiple API information Javadoc sections found!");
        }
        return swaggers;
    }

    /**
     * Searches for basic API information in the Javadoc. If a title and a
     * version are found, a {@link Swagger} object is returned. Otherwise, null
     * will be returned.
     * 
     * @param file
     * @return {@link Swagger} object or null
     */
    private Swagger findSwaggerInJavaFile(String section) {
        if (section != null) {
            Swagger swagger = new Swagger();
            String title = findStringInSectionByRegex("@apiTitle [\\w\\s]+", 10, section);
            String version = findStringInSectionByRegex("@apiVersion [\\w.\\-]+", 12, section);
            String description = findStringInSectionByRegex("@apiDescription [^@]+", 16, section);
            String host = findStringInSectionByRegex("@apiHost [\\w.:\\-\\/\\\\]+", 9, section);
            String basePath = findStringInSectionByRegex("@apiBasePath [\\w.:\\/\\\\]+", 13, section);
            String fileName = findStringInSectionByRegex("@fileName [^\\s]+\\.json", 10, section);
            swagger.setTitle(title);
            swagger.setVersion(version);
            swagger.setDescription(description);
            swagger.setHost(host);
            swagger.setBasePath(basePath);
            swagger.setFileName(fileName);
            if (title != null) {
                return swagger;
            }
        }
        return null;
    }

}
