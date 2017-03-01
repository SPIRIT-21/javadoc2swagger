package com.spirit21.swagger.converter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.loader.JavaFileLoader;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.JavaFile;
import com.spirit21.swagger.converter.models.Swagger;
import com.spirit21.swagger.converter.models.Tag;
import com.spirit21.swagger.converter.parsers.Parser;
import com.spirit21.swagger.converter.writers.SwaggerWriter;

/**
 * Loads and starts the Maven plugin
 * 
 * @author dsimon
 *
 */
@Mojo(name = "generateswagger")
public class Plugin extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;
    Log log;

    /**
     * Loads the javadoc from the project, creates a {@link Swagger} object and
     * creates a Swagger definition
     */
    @Override
    public void execute() throws MojoExecutionException {
        LocalDateTime start = LocalDateTime.now();
        log = getLog();
        List<Tag> tags = new ArrayList<>();
        List<Definition> definitions = new ArrayList<>();
        try {
            ClassLoader classLoader = new ClassLoader(project);
            JavaFileLoader loader = new JavaFileLoader(log);
            List<JavaFile> javaFiles = loader.getJavaFiles(project);
            Parser parser = new Parser(log, classLoader, tags, definitions);
            Swagger swagger = parser.parse(javaFiles);
            setSwaggerVersion(swagger);
            SwaggerWriter swaggerWriter = new SwaggerWriter(log);
            String basePath = project.getBuild().getOutputDirectory();
            swaggerWriter.createSwaggerDefinition(swagger, basePath, tags, definitions);
            log.info("Swagger file created");
            LocalDateTime end = LocalDateTime.now();
            Duration elapsed = Duration.between(start, end);
            log.info("total time elapsed: " + elapsed.toMillis() + " milliseconds");
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Sets the Swagger version if no version provided in Javadoc
     * 
     * @param swagger
     */
    private void setSwaggerVersion(Swagger swagger) {
        if (swagger.getVersion() == null || swagger.getVersion().isEmpty()) {
            swagger.setVersion(project.getVersion());
        }
    }
}
