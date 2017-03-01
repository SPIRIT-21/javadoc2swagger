package com.spirit21.swagger.converter.datatype;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
public class ListHandler implements TypeHandler {

    @Override
    public DataType getType(String className) {
        if (className.equals("List") || className.equals("java.util.List") || className.equals("Collection")
                || className.equals("java.util.Collection") || className.equals("Set")
                || className.equals("java.util.Set")) {
            return new DataType("array");
        }
        return null;
    }
}
