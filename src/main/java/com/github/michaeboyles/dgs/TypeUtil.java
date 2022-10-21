package com.github.michaeboyles.dgs;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import graphql.language.Type;

import java.util.List;

public class TypeUtil {
    public static TypeName convertType(Type<?> type) {
        return convertType(type, false, 0);
    }

    private static TypeName convertType(Type<?> type, boolean parentIsNonNull, int depth) {
        if (type instanceof graphql.language.TypeName) {
            graphql.language.TypeName t = (graphql.language.TypeName) type;
            return getTypeForName(t.getName(), !(depth == 1 && parentIsNonNull));
        }
        else if (type instanceof graphql.language.ListType) {
            graphql.language.ListType listType = (graphql.language.ListType) type;
            return ParameterizedTypeName.get(
                ClassName.get(List.class), convertType(listType.getType(), false, depth + 1)
            );
        }
        else if (type instanceof graphql.language.NonNullType) {
            graphql.language.NonNullType nonNullType = (graphql.language.NonNullType) type;
            return convertType(nonNullType.getType(), true, depth + 1);
        }
        throw new RuntimeException("Unsupported type " + type.getClass());
    }

    private static TypeName getTypeForName(String name, boolean box) {
        TypeName typeName = null;
        switch (name) {
            case "Int":
                typeName = TypeName.get(int.class);
                break;
            case "Float":
                typeName = TypeName.get(double.class);
                break;
            case "Boolean":
                typeName = TypeName.get(boolean.class);
                break;
            case "String":
            case "ID":
                typeName = ClassName.get(String.class);
        }
        if (typeName != null) {
            return box ? typeName.box() : typeName;
        }
        return ClassName.get("org.example", name);
    }
}
