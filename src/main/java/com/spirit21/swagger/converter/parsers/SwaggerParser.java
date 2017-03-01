package com.spirit21.swagger.converter.parsers;

import java.util.List;

import com.spirit21.swagger.converter.models.JavaFile;
import com.spirit21.swagger.converter.models.SwaggerModel;

/**
 * 
 * @author dsimon
 *
 */
@FunctionalInterface
public interface SwaggerParser {
    List<SwaggerModel> parse(List<JavaFile> javadoc) throws ParserException, ClassNotFoundException;
}