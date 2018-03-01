package com.terran4j.commons.api2doc.domain;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.terran4j.commons.util.Jsons;
import javafx.beans.binding.ObjectExpression;

import java.util.Date;

/**
 * api的基本数据类型。
 *
 * @author jiangwei
 */
public enum ApiDataType {

    BOOLEAN("boolean") {
        @Override
        public String getDefault() {
            return "false";
        }

        @Override
        public Object parseValue(String text) {
            return Boolean.parseBoolean(text);
        }

        @Override
        public boolean isSimpleType() {
            return true;
        }

        @Override
        public boolean isArrayType() {
            return false;
        }

        @Override
        public boolean isObjectType() {
            return false;
        }
    },

    INT("int") {
        @Override
        public String getDefault() {
            return "0";
        }

        @Override
        public Object parseValue(String text) {
            return Integer.parseInt(text);
        }

        @Override
        public boolean isSimpleType() {
            return true;
        }

        @Override
        public boolean isArrayType() {
            return false;
        }

        @Override
        public boolean isObjectType() {
            return false;
        }
    },

    LONG("long") {
        @Override
        public String getDefault() {
            return "0";
        }

        @Override
        public Object parseValue(String text) {
            return Long.parseLong(text);
        }

        @Override
        public boolean isSimpleType() {
            return true;
        }

        @Override
        public boolean isArrayType() {
            return false;
        }

        @Override
        public boolean isObjectType() {
            return false;
        }
    },

    NUMBER("number") {
        @Override
        public String getDefault() {
            return "0.1";
        }

        @Override
        public Object parseValue(String text) {
            return Double.parseDouble(text);
        }

        @Override
        public boolean isSimpleType() {
            return true;
        }

        @Override
        public boolean isArrayType() {
            return false;
        }

        @Override
        public boolean isObjectType() {
            return false;
        }
    },

    STRING("string") {
        @Override
        public String getDefault() {
            return "my-string";
        }

        @Override
        public Object parseValue(String text) {
            return text;
        }

        @Override
        public boolean isSimpleType() {
            return true;
        }

        @Override
        public boolean isArrayType() {
            return false;
        }

        @Override
        public boolean isObjectType() {
            return false;
        }
    },

    ARRAY("array") {
        @Override
        public String getDefault() {
            return "[]";
        }

        @Override
        public Object parseValue(String text) {
            throw new UnsupportedOperationException("array can't parse from text: " + text);
        }

        @Override
        public boolean isSimpleType() {
            return false;
        }

        @Override
        public boolean isArrayType() {
            return true;
        }

        @Override
        public boolean isObjectType() {
            return false;
        }
    },

    OBJECT("object") {
        @Override
        public String getDefault() {
            return "{}";
        }

        @Override
        public Object parseValue(String text) {
            throw new UnsupportedOperationException("object can't parse from text: " + text);
        }

        @Override
        public boolean isSimpleType() {
            return false;
        }

        @Override
        public boolean isArrayType() {
            return false;
        }

        @Override
        public boolean isObjectType() {
            return true;
        }
    };

    private final String name;

    ApiDataType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract String getDefault();

    public abstract Object parseValue(String text);

    public abstract boolean isSimpleType();

    public abstract boolean isArrayType();

    public abstract boolean isObjectType();

    private static JsonSchemaGenerator schemaGen = null;

    public static final JsonSchemaGenerator getJsonSchemaGenerator() {
        if (schemaGen != null) {
            return schemaGen;
        }
        synchronized (ApiDataType.class) {
            if (schemaGen != null) {
                return schemaGen;
            }
            try {
                schemaGen = new JsonSchemaGenerator(Jsons.getObjectMapper());
                return schemaGen;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ApiDataType toDataType(Class<?> clazz) {

        // 日期类型，按 long 返回。
        if (clazz == Date.class || clazz == java.sql.Date.class) {
            return ApiDataType.LONG;
        }

        try {
            JsonSchema schema = getJsonSchemaGenerator().generateSchema(clazz);
            return toDataType(schema);
        } catch (JsonMappingException e) {
            String msg = "generate schema by class failed, class = " + clazz.getName();
            throw new RuntimeException(msg, e);
        }
    }

    public static ApiDataType toDataType(JsonSchema schema) {
        if (schema == null) {
            return null;
        }
        if (schema.isBooleanSchema()) {
            return ApiDataType.BOOLEAN;
        }
        if (schema.isIntegerSchema()) {
            return ApiDataType.INT;
        }
        if (schema.isStringSchema()) {
            return ApiDataType.STRING;
        }
        if (schema.isNumberSchema()) {
            return ApiDataType.NUMBER;
        }
        if (schema.isObjectSchema()) {
            return ApiDataType.OBJECT;
        }
        if (schema.isArraySchema()) {
            return ApiDataType.ARRAY;
        }
        return null;
    }

}
