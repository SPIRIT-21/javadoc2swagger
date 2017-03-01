package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
@FunctionalInterface
public interface TypeHandler {
    DataType getType(String className);
}
