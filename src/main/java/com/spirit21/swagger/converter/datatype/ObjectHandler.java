package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
public class ObjectHandler implements TypeHandler {

    @Override
    public DataType getType(String className) {
        if (className.equals("Object") || className.equals("java.lang.Object")) {
            return new DataType("object");
        }
        return null;
    }
}
