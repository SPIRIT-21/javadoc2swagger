package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
public class StringHandler implements TypeHandler {

    @Override
    public DataType getType(String className) {
        if (className.equals("String") || className.equals("java.lang.String")) {
            return new DataType("string");
        }
        return null;
    }
}
