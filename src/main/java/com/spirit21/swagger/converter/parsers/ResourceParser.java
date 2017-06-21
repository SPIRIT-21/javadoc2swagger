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
                List<Parameter> pathParameters = getPathParameters(section);
                resource.setPathParameters(pathParameters);
                resource.setOperations(operationParser.findOperationsInJavaFile(file, path, cls));
                return resource;
            }
        }
        return null;
    }

    /**
     * Finds PathParameters
     * 
     * @param section
     * @return Parameter
     */
    private List<Parameter> getPathParameters(String section) {

        List<String> names = findStringsInSectionByRegex("@pathParam \\w+", 11, section);
        List<String> types = findStringsInSectionByRegex("@type \\w+", 6, section);
        /*
         * if there are multiple path parameters with at least one occurring
         * format , all path parameters needs @format to ensure the right order.
         * if no format needed, @format will suffice. thats why (\\w+)?
         */
        List<String> formats = findStringsInSectionByRegex("@format (\\w+)?", 8, section);

        List<Parameter> pathParameters = new ArrayList<Parameter>();

        for (int i = 0; i < names.size(); i++) {

            Parameter param = new Parameter();
            param.setLocation("path");
            param.setName(names.get(i));
            param.setRequired(true);
            param.setType(types.get(i));
            if (!formats.isEmpty()) {
                param.setFormat(formats.get(i));
            }

            pathParameters.add(param);
        }

        return pathParameters;
    }
}
