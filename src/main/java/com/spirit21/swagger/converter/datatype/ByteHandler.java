package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
public class ByteHandler implements TypeHandler {

    @Override
    public DataType getType(String className) {
        if (className.equals("byte[]") || className.equals("[B")) {
            return new DataType("string", "byte");
        }
        return null;
    }
}
