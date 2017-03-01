package com.spirit21.swagger.converter.datatype;

import java.util.ArrayList;
import java.util.List;

import com.spirit21.swagger.converter.models.DataType;

/**
 * 
 * @author dsimon
 *
 */
public class DataTypeFactory {
    List<TypeHandler> handlers;

    public DataTypeFactory() {
        handlers = new ArrayList<>();
        handlers.add(new StringHandler());
        handlers.add(new NumberHandler());
        handlers.add(new ListHandler());
        handlers.add(new DateHandler());
        handlers.add(new BooleanHandler());
        handlers.add(new ObjectHandler());
        handlers.add(new ByteHandler());
    }

    /**
     * Tries all TypeHandlers on a given type
     * 
     * @param type
     *            Given type
     * @return {@link DataType} object
     */
    public DataType getDataType(String type) {
        DataType result = null;
        for (TypeHandler typeHandler : handlers) {
            result = typeHandler.getType(type);
            if (result != null) {
                break;
            }
        }
        if (result == null) {
            String[] packages = type.split("\\.");
            result = new DataType("#/definitions/" + packages[packages.length - 1]);
        }
        return result;
    }
}
