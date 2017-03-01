package com.spirit21.swagger.converter.parsers;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.JavaFile;
import com.spirit21.swagger.converter.models.Resource;
import com.spirit21.swagger.converter.models.Swagger;
import com.spirit21.swagger.converter.models.SwaggerModel;
import com.spirit21.swagger.converter.models.Tag;

/**
 * Loads and executes all parsers
 * 
 * @author dsimon
 *
 */
public class Parser extends AbstractParser {
    public Parser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
        super(log, loader, tags, definitions);
    }

    /**
     * Loads all parsers and executes each of them passing the Javadoc strings
     * 
     * @param javadoc
     *            Javadoc sections as a list of strings
     * @throws ParserException
     * @throws ClassNotFoundException
     */
    public Swagger parse(List<JavaFile> javaFiles) throws ParserException, ClassNotFoundException {
        List<SwaggerParser> parsers = new ArrayList<>();
        parsers.add(new SwaggerApiParser(log, loader, tags, definitions));
        parsers.add(new ResourceParser(log, loader, tags, definitions));
        List<SwaggerModel> data = new ArrayList<>();
        for (SwaggerParser parser : parsers) {
            addDataFromParser(data, javaFiles, parser);
        }
        return createSwaggerObject(data);
    }

    /**
     * Adds the data from the parser to a given list.
     * 
     * @param destination
     * @param source
     * @throws ParserException
     * @throws ClassNotFoundException
     */
    private void addDataFromParser(List<SwaggerModel> destination, List<JavaFile> javaFiles, SwaggerParser parser)
            throws ParserException, ClassNotFoundException {
        List<SwaggerModel> source = parser.parse(javaFiles);
        if (source != null) {
            destination.addAll(source);
        }
    }

    /**
     * Iterates over the data and appends it to a {@link Swagger} object
     * 
     * @param swaggerObject
     * @param data
     */
    private Swagger createSwaggerObject(List<SwaggerModel> data) {
        Swagger swaggerObject = new Swagger();
        for (SwaggerModel model : data) {
            if (model.getClass().equals(Swagger.class)) {
                mergeSwaggerObjects(swaggerObject, (Swagger) model);
            } else if (model.getClass().equals(Resource.class)) {
                swaggerObject.addResource((Resource) model);
            }
        }
        return swaggerObject;
    }

    /**
     * Copy the primitive attributes from an origin Swagger object to a Swagger
     * object which has the resources set.
     * 
     * @param destination
     *            Swagger object to copy to
     * @param origin
     *            Swagger object to copy from
     */
    private void mergeSwaggerObjects(Swagger destination, Swagger origin) {
        destination.setBasePath(origin.getBasePath());
        destination.setHost(origin.getHost());
        destination.setTitle(origin.getTitle());
        destination.setVersion(origin.getVersion());
        destination.setDescription(origin.getDescription());
        destination.setFileName(origin.getFileName());
    }
}
