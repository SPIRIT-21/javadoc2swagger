package com.spirit21.swagger.converter.parsers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import org.apache.maven.plugin.logging.Log;

import com.spirit21.swagger.converter.Regex;
import com.spirit21.swagger.converter.datatype.HttpMethodAnnotationHandler;
import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.JavaFile;
import com.spirit21.swagger.converter.models.Method;
import com.spirit21.swagger.converter.models.Operation;
import com.spirit21.swagger.converter.models.Response;
import com.spirit21.swagger.converter.models.Tag;

/**
 * 
 * @author dsimon
 *
 */
public class OperationParser extends AbstractParser {

    public OperationParser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
        super(log, loader, tags, definitions);
    }

    private enum ResourceAcceptType {
        CONSUMES, PRODUCES
    }

    /**
     * Finds all operations in a {@link JavaFile} and sets all fields if a
     * operation was found.
     * 
     * @param javadoc
     * @param path
     * @return List of {@link Operation}
     * @throws ParserException
     */
    public List<Operation> findOperationsInJavaFile(JavaFile javaFile, String path, Class<?> cls)
            throws ParserException {
        Regex regex = new Regex();
        ParameterParser parameterParser = new ParameterParser(log, loader, tags, definitions);
        ResponseParser responseParser = new ResponseParser(log, loader, tags, definitions);
        TagParser tagParser = new TagParser(log, loader, tags, definitions);
        List<Operation> operations = new ArrayList<>();
        List<Method> methods = javaFile.getFunctions();
        List<String> imports = javaFile.getImports();
        java.lang.reflect.Method[] classMethods = cls.getMethods();
        for (Method met : methods) {
            String httpMethod = met.getHttpMethod();
            java.lang.reflect.Method classMethod = findMethod(httpMethod, classMethods);
            String section = met.getJavadoc();
            Operation operation = new Operation();
            operation.setMethod(httpMethod);
            operation.setDescription(removeUnwantedCharactersFromJavadocDescription(
                    findStringInSectionByRegex(regex.getDescriptionRegex(), 0, section)));
            operation.setSummary(findStringInSectionByRegex("@summary [^@]+", 9, section));
            operation.setConsumes(findMediaTypesInAnnotations(classMethod, ResourceAcceptType.CONSUMES));
            operation.setProduces(findMediaTypesInAnnotations(classMethod, ResourceAcceptType.PRODUCES));
            operation.setTags(tagParser.generateTags(path));
            operation.setOperationId(met.getHttpMethod() + path.replace("/", ""));
            List<com.spirit21.swagger.converter.models.Parameter> parameters = parameterParser
                    .findParametersInMethodHeader(met.getHeader(), imports, met.getJavadoc(), javaFile.getFileName());
            if (parameters != null && !parameters.isEmpty()) {
                operation.setParameters(parameters);
            }
            List<Response> responses = responseParser.findResponsesInJavadocSection(section, imports,
                    javaFile.getFileName());
            if (!responses.isEmpty()) {
                operation.setResponses(responses);
            } else {
                log.warn("No responses defined for operation " + httpMethod + " in path " + path);
            }
            operations.add(operation);
        }
        if (!operations.isEmpty()) {
            return operations;
        } else {
            log.warn("No operation found for resource with path " + path);
            return null;
        }
    }

    /**
     * Searches for the java method where the given http method is annotated
     * 
     * @param httpMethod
     * @param classMethods
     * @return null or the found method
     */
    private java.lang.reflect.Method findMethod(String httpMethod, java.lang.reflect.Method[] classMethods) {
        HttpMethodAnnotationHandler typeHandler = new HttpMethodAnnotationHandler();
        for (java.lang.reflect.Method method : classMethods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<?> cls = annotation.annotationType();
                String annotationType = typeHandler.getHttpMethodAnnotationType(cls);
                if (annotationType != null && annotationType.equals(httpMethod)) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * Find media types of a @Produces or @Consumes Annotation.
     * 
     * @param annotations
     * @param type
     *            annotation type
     * @return list of media types
     * @throws ParserException
     */
    private List<String> findMediaTypesInAnnotations(java.lang.reflect.Method method, ResourceAcceptType type)
            throws ParserException {
        List<String> mediaTypes = new ArrayList<>();
        Consumes consumes = method.getAnnotation(Consumes.class);
        Produces produces = method.getAnnotation(Produces.class);
        String[] values = null;
        if (type.equals(ResourceAcceptType.CONSUMES) && consumes != null && consumes.value().length != 0) {
            values = consumes.value();
        } else if (type.equals(ResourceAcceptType.PRODUCES) && produces != null && produces.value().length != 0) {
            values = produces.value();
        }
        if (values != null) {
            for (String str : values) {
                mediaTypes.add(str);
            }
        }
        return mediaTypes;
    }
}
