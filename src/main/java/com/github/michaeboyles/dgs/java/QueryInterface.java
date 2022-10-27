package com.github.michaeboyles.dgs.java;

import com.netflix.graphql.dgs.DgsQuery;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.michaeboyles.dgs.java.TypeUtil.convertType;

class QueryInterface {
    public static JavaFile generate(String packageName, FieldDefinition query) {
        return JavaFile.builder(
            packageName,
            TypeSpec.interfaceBuilder(getClassName(query))
                .addMethod(getQueryMethod(query))
                .build()
        ).build();
    }

    private static String getClassName(FieldDefinition query) {
        return Character.toUpperCase(query.getName().charAt(0)) + query.getName().substring(1) + "Query";
    }

    private static MethodSpec getQueryMethod(FieldDefinition query) {
        return MethodSpec.methodBuilder(query.getName())
            .addAnnotation(DgsQuery.class)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(convertType(query.getType()))
            .addParameters(getParameters(query))
            .build();
    }

    private static List<ParameterSpec> getParameters(FieldDefinition query) {
        return query.getInputValueDefinitions().stream()
            .map(QueryInterface::getParameter)
            .collect(Collectors.toList());
    }

    private static ParameterSpec getParameter(InputValueDefinition valueDefinition) {
        return ParameterSpec.builder(convertType(valueDefinition.getType()), valueDefinition.getName())
            .build();
    }
}
