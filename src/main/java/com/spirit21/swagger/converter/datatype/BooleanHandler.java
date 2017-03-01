package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
public class BooleanHandler implements TypeHandler {

    @Override
    public DataType getType(String className) {
        if (className.equals("Boolean") || className.equals("java.lang.Boolean")) {
            return new DataType("boolean");
        }
        return null;
    }
}
