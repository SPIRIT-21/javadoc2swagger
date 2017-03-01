package com.spirit21.swagger.converter.parsers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import org.apache.maven.plugin.logging.Log;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spirit21.swagger.converter.datatype.DataTypeFactory;
import com.spirit21.swagger.converter.loader.ClassLoader;
import com.spirit21.swagger.converter.models.DataType;
import com.spirit21.swagger.converter.models.Definition;
import com.spirit21.swagger.converter.models.Property;
import com.spirit21.swagger.converter.models.Tag;

public class DefinitionParser extends AbstractParser {

    public DefinitionParser(Log log, ClassLoader loader, List<Tag> tags, List<Definition> definitions) {
        super(log, loader, tags, definitions);
    }

    /**
     * Checks if a definition is already contained in the definitions
     * 
     * @param className
     *            class name
     * @return bool
     */
    public Definition getDefinitionByClassName(String className) {
        for (Definition definition : definitions) {
            if (definition.getClassName().equals(className)) {
                return definition;
            }
        }
        return null;
    }

    /**
     * Checks if a schema is already defined and creates a {@link Definition} if
     * necessary
     * 
     * @param input
     *            link tag, class name or swagger reference
     * @param imports
     *            imports of the java file
     * @param fileName
     *            file name
     * @return class name
     * @throws ParserException
     *             Error while the parsing process
     */
    public String createDefinitionIfNotExists(String input, List<String> imports, String fileName)
            throws ParserException {
        String className;
        if (input.startsWith("{")) {
            className = getClassNameFromLinkTag(input);
        } else if (input.startsWith("#")) {
            className = input.substring(14);
        } else {
            className = input;
        }
        Definition definition = getDefinitionByClassName(className);
        if (definition == null) {
            definitions.add(createDefinitionByClassName(className, imports, fileName, null));
        }
        return className;
    }

    /**
     * Looks for the package name of a class in the imports of its java file.
     * Tries to load the class and its properties. Creates a {@link Definition}
     * object for the class.
     * 
     * @param className
     *            class name
     * @param imports
     *            imports of the java file
     * @param fileName
     *            file name
     * @param rootDefinition
     *            definition where the recursion started to prevent an endless
     *            loop
     * @return new {@link Definition} object
     * @throws ParserException
     *             Error while the parsing process
     */
    public Definition createDefinitionByClassName(String className, List<String> imports, String fileName,
            Definition rootDefinition) throws ParserException {
        for (String imp : imports) {
            String regex = ".*[.]" + className;
            if (imp.matches(regex)) {
                String classWithPackage = imp;
                try {
                    Class<?> cls = loader.loadClass(classWithPackage);
                    Field[] fields = cls.getDeclaredFields();
                    Definition definition = new Definition();
                    definition.setClassName(className);
                    if (rootDefinition == null) {
                        rootDefinition = definition;
                    }
                    List<Property> properties = processFields(fields, definition, rootDefinition);
                    definition.setProperties(properties);
                    return definition;
                } catch (Exception e) {
                    throw new ParserException("Error loading class '" + classWithPackage + "'", e);
                }
            }
        }
        throw new ParserException("Class not found in imports: " + className + "; file: " + fileName);
    }

    /**
     * Processes all fields of a class
     * 
     * @param fields
     *            array of fields
     * @param definition
     *            current definition
     * @param rootDefinition
     *            definition where the recursion started to prevent an endless
     *            loop
     * @return list of {@link Property} objects
     * @throws ParserException
     *             Error while the parsing process
     */
    public List<Property> processFields(Field[] fields, Definition definition, Definition rootDefinition)
            throws ParserException {
        DataTypeFactory typeHandler = new DataTypeFactory();
        List<Property> properties = new ArrayList<>();
        for (Field field : fields) {
            if (field.getAnnotation(JsonIgnore.class) == null && field.getAnnotation(JsonBackReference.class) == null) {
                Property property = new Property();
                Class<?> typeClass = field.getType();
                Annotation[] annotations = field.getAnnotations();
                Type genericType = field.getGenericType();
                processGenerics(genericType, property, definition, rootDefinition);
                DataType typeObject = typeHandler.getDataType(typeClass.getName());
                String type = typeObject.getType();
                String name = field.getName();
                property.setName(name);
                if (type.length() > 14 && (type.substring(14).equals(definition.getClassName())
                        || type.substring(14).equals(rootDefinition.getClassName()))) {
                    property.setReference(type);
                } else if (type.startsWith("#")) {
                    createDefinitionBySchemaAndPackageIfNotExists(type, typeClass.getTypeName(), rootDefinition);
                    property.setReference(type);
                } else {
                    property.setType(type);
                    property.setFormat(typeObject.getFormat());
                }
                properties.add(property);
                processAnnotations(annotations, definition, name);
            }
        }
        return properties;
    }

    /**
     * Processes the generic type and adds it to the {@link Property} object
     * 
     * @param genericType
     *            generic type
     * @param property
     *            current {@link Property} object
     * @param definition
     *            current definition
     * @param rootDefinition
     *            definition where the recursion started to prevent an endless
     *            loop
     * @throws ParserException
     *             Error while the parsing process
     */
    private void processGenerics(Type genericType, Property property, Definition definition, Definition rootDefinition)
            throws ParserException {
        DataTypeFactory typeHandler = new DataTypeFactory();
        if (genericType.toString().contains("<")) {
            Pattern genericPattern = Pattern.compile("<[^>]*");
            Matcher matcher = genericPattern.matcher(genericType.toString());
            while (matcher.find()) {
                String genericPackageName = genericType.toString().substring(matcher.start() + 1, matcher.end());
                String[] packageSplit = genericPackageName.split("\\.");
                String genericClassName = packageSplit[packageSplit.length - 1];
                DataType genericDataType = typeHandler.getDataType(genericClassName);
                String genericTypeName = genericDataType.getType();
                if (genericTypeName.length() > 14 && (genericTypeName.substring(14).equals(definition.getClassName())
                        || genericTypeName.substring(14).equals(rootDefinition.getClassName()))) {
                    property.setReference(genericTypeName);
                } else if (genericTypeName.startsWith("#")) {
                    createDefinitionBySchemaAndPackageIfNotExists(genericTypeName, genericPackageName, rootDefinition);
                    property.setReference(genericTypeName);
                } else {
                    property.setGenericType(genericDataType.getType());
                    property.setGenericFormat(genericDataType.getFormat());
                }
            }
        }
    }

    /**
     * Processes the annotations and searches for required fields
     * 
     * @param annotations
     *            array of annotations
     * @param definition
     *            current definition
     * @param fieldName
     *            name of the field
     */
    private void processAnnotations(Annotation[] annotations, Definition definition, String fieldName) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof NotNull) {
                List<String> required = definition.getRequired();
                if (required == null) {
                    required = new ArrayList<>();
                }
                required.add(fieldName);
                definition.setRequired(required);
            }
        }
    }

    /**
     * Creates a definition for a class whose package is known
     * 
     * @param schema
     *            class to check if a definition has to be created
     * @param packageName
     *            package of the class
     * @param rootDefinition
     *            definition where the recursion started to prevent an endless
     *            loop
     * @throws ParserException
     *             Error while the parsing process
     */
    public void createDefinitionBySchemaAndPackageIfNotExists(String schema, String packageName,
            Definition rootDefinition) throws ParserException {
        String className = schema.substring(14);
        Definition definition = getDefinitionByClassName(className);
        if (definition == null) {
            List<String> imports = new ArrayList<>();
            imports.add(packageName);
            definitions.add(createDefinitionByClassName(className, imports, "", rootDefinition));
        }
    }
}
