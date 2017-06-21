package com.spirit21.swagger.converter.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.Regex;
import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.JavaFile;
import com.spirit21.swagger.converter.models.Parameter;
import com.spirit21.swagger.converter.models.Resource;
import com.spirit21.swagger.converter.models.SwaggerModel;
import com.spirit21.swagger.converter.models.Tag;

/**
 * 
 * @author dsimon
 *
 */
public class ResourceParser extends AbstractParser implements SwaggerParser {

    public ResourceParser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
        super(log, loader, tags, definitions);
    }

    @Override
    public List<SwaggerModel> parse(List<JavaFile> javaFiles) throws ParserException, ClassNotFoundException {
        List<SwaggerModel> resources = new ArrayList<>();
        for (JavaFile file : javaFiles) {
            Class<?> cls = loader.loadClass(
                    file.getPackageName() + "." + file.getFileName().substring(0, file.getFileName().length() - 5));
            Resource newResource = findResourceInJavaFile(file, cls);
            if (newResource != null) {
                resources.add(newResource);
            }
        }
        if (resources.isEmpty()) {
            log.warn("No Swagger resources found in Javadoc!");
        }
        return resources;
    }

    /**
     * Searches for a resource in a {@link JavaFile} and sets all fields if a
     * resource was found.
     * 
     * @param file
     * @return {@link Resource}
     * @throws ParserException
     */
    private Resource findResourceInJavaFile(JavaFile file, Class<?> cls) throws ParserException {
        OperationParser operationParser = new OperationParser(log, loader, tags, definitions);
        String section = file.getClassJavadoc();
        if (section != null) {
            Pattern pattern = Pattern.compile(Regex.PATH, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(section);
            if (matcher.find()) {
                String path = section.substring(matcher.start() + 6, matcher.end());
                Resource resource = new Resource();
                resource.setPath(path);
                Parameter pathParameter = getPathParameter(section);
                resource.setPathParameter(pathParameter);
                resource.setOperations(operationParser.findOperationsInJavaFile(file, path, cls));
                return resource;
            }
        }
        return null;
    }

    /**
     * Finds PathParameter
     * 
     * @param section
     * @return Parameter
     */
    private Parameter getPathParameter(String section) {
        String name = findStringInSectionByRegex("@pathParam \\w+", 11, section);
        String type = findStringInSectionByRegex("@type \\w+", 6, section);
        String format = findStringInSectionByRegex("@format \\w+", 8, section);
        if (name != null && type != null) {
            Parameter param = new Parameter();
            param.setLocation("path");
            param.setName(name);
            param.setRequired(true);
            param.setType(type);
            if (format != null) {
                param.setFormat(format);
            }
            return param;
        }
        return null;
    }
}
