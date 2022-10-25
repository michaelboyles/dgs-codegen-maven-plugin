package com.github.michaeboyles.dgs.kotlin;

import com.netflix.graphql.dgs.DgsMutation;
import com.squareup.kotlinpoet.FileSpec;
import com.squareup.kotlinpoet.FunSpec;
import com.squareup.kotlinpoet.ParameterSpec;
import com.squareup.kotlinpoet.TypeSpec;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.michaeboyles.dgs.kotlin.TypeUtil.convertType;

class MutationInterface {
    public static FileSpec generate(String packageName, FieldDefinition queryDef) {
        String className = getClassName(queryDef);
        return FileSpec.builder(packageName, className)
            .addType(getInterfaceType(queryDef))
            .build();
    }

    private static String getClassName(FieldDefinition query) {
        return Character.toUpperCase(query.getName().charAt(0)) + query.getName().substring(1) + "Mutation";
    }

    private static TypeSpec getInterfaceType(FieldDefinition query) {
        return TypeSpec.interfaceBuilder(getClassName(query))
            .addFunction(
                FunSpec.builder(query.getName())
                    .addAnnotation(DgsMutation.class)
                    .addParameters(getParameters(query))
                    .returns(convertType(query.getType()))
                    .build()
            )
            .build();
    }

    private static List<ParameterSpec> getParameters(FieldDefinition query) {
        return query.getInputValueDefinitions().stream()
            .map(MutationInterface::getParameter)
            .collect(Collectors.toList());
    }

    private static ParameterSpec getParameter(InputValueDefinition valueDefinition) {
        return ParameterSpec.builder(valueDefinition.getName(), convertType(valueDefinition.getType()))
            .build();
    }
}
