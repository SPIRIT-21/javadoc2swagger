package com.spirit21.swagger.converter.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.json.JSONException;
import org.json.JSONObject;

import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.Swagger;
import com.spirit21.swagger.converter.models.Tag;

/**
 * Contains function needed for creating a Swagger definition
 * 
 * @author dsimon
 *
 */
public class SwaggerWriter extends AbstractWriter {
    DefinitionWriter definitionWriter = new DefinitionWriter(log);
    TagWriter tagWriter = new TagWriter(log);
    ResourceWriter resourceWriter = new ResourceWriter(log);

    public SwaggerWriter(Log log) {
        super(log);
    }

    /**
     * Creates a Swagger definition with information provided in the
     * {@link Swagger} object
     * 
     * @param swaggerObject
     *            object in the {@link Swagger} format
     * @param basePath
     *            base path of the project
     * @param tags
     *            list of all tags
     * @param definitions
     *            list of all definitions
     * @throws WriterException
     *             Error while writing the Swagger JSON file
     * @throws IOException
     *             I/O Exception is thrown
     */
    public void createSwaggerDefinition(Swagger swaggerObject, String basePath, List<Tag> tags,
            List<Definition> definitions) throws WriterException, IOException {

        JSONObject swaggerJson = new JSONObject();
        swaggerJson.put("swagger", "2.0");
        swaggerJson.put("basePath", swaggerObject.getBasePath());
        swaggerJson.put("host", swaggerObject.getHost());
        swaggerJson.put("info", mapInfo(swaggerObject));
        swaggerJson.put("tags", tagWriter.mapTagDefinitions(tags));
        swaggerJson.put("paths", resourceWriter.mapResources(swaggerObject.getResources()));
        swaggerJson.put("definitions", definitionWriter.mapDefinitions(definitions));
        String fileName = swaggerObject.getFileName();
        if (fileName == null) {
            fileName = "swagger.json";
        }
        try {
            write(swaggerJson, basePath, fileName);
        } catch (IOException e) {
            log.warn("Swagger file could not be created. Please try removing illegal characters from the file name.");
            fileName = "swagger.json";
            write(swaggerJson, basePath, fileName);
        }
    }

    /**
     * Writes a given JSON object to a file
     * 
     * @param swaggerJson
     *            JSON object containing the whole swagger definition
     * @param basePath
     *            base path of the projects
     * @param fileName
     *            file name
     * @throws IOException
     * @throws WriterException
     *             Error while writing the Swagger JSON file
     */
    private void write(JSONObject swaggerJson, String basePath, String fileName) throws IOException, WriterException {
        try (Writer fw = getWriter(basePath, fileName)) {
            fw.write(swaggerJson.toString(4));
        } catch (JSONException e) {
            throw new WriterException("Error creating the JSON string!", e);
        }
    }

    /**
     * Creates the file writer
     * 
     * @param basePath
     *            base path of the projects
     * @param fileName
     *            file name
     * @return file writer
     * @throws IOException
     */
    private Writer getWriter(String basePath, String fileName) throws IOException {
        File swaggerPath = new File(basePath, "../swagger");
        swaggerPath.mkdirs();
        log.info("Creating file " + fileName);
        File f = new File(swaggerPath, fileName);
        f.createNewFile();
        return new FileWriter(f);
    }

    /**
     * Creates JSON for the info object of the Swagger definition
     * 
     * @param swaggerObject
     *            {@link Swagger} object
     * @return JSON for info
     */
    private JSONObject mapInfo(Swagger swaggerObject) {
        JSONObject info = new JSONObject();
        info.put("version", swaggerObject.getVersion());
        info.put("title", swaggerObject.getTitle());
        String description = swaggerObject.getDescription();
        if (description != null) {
            info.put("description", description);
        }
        return info;
    }
}
