package com.github.michaeboyles.dgs.kotlin;

import com.github.michaeboyles.dgs.Packages;
import com.netflix.graphql.dgs.DgsQuery;
import com.squareup.kotlinpoet.FileSpec;
import com.squareup.kotlinpoet.FunSpec;
import com.squareup.kotlinpoet.ParameterSpec;
import com.squareup.kotlinpoet.TypeSpec;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.michaeboyles.dgs.kotlin.TypeUtil.convertType;

class QueryInterface {
    public static FileSpec generate(Packages packages, FieldDefinition queryDef) {
        String className = getClassName(queryDef);
        return FileSpec.builder(packages.interfacePackage(), className)
            .addType(getInterfaceType(packages, queryDef))
            .build();
    }

    private static String getClassName(FieldDefinition query) {
        return Character.toUpperCase(query.getName().charAt(0)) + query.getName().substring(1) + "Query";
    }

    private static TypeSpec getInterfaceType(Packages packages, FieldDefinition query) {
        return TypeSpec.interfaceBuilder(getClassName(query))
            .addFunction(
                FunSpec.builder(query.getName())
                    .addAnnotation(DgsQuery.class)
                    .addParameters(getParameters(packages, query))
                    .returns(convertType(packages, query.getType()))
                    .build()
            )
            .build();
    }

    private static List<ParameterSpec> getParameters(Packages packages, FieldDefinition query) {
        return query.getInputValueDefinitions().stream()
            .map(def -> getParameter(packages, def))
            .collect(Collectors.toList());
    }

    private static ParameterSpec getParameter(Packages packages, InputValueDefinition valueDefinition) {
        return ParameterSpec.builder(valueDefinition.getName(), convertType(packages, valueDefinition.getType()))
            .build();
    }
}
