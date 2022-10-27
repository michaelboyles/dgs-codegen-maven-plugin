package com.github.michaeboyles.dgs.java;

import com.github.michaeboyles.dgs.Packages;
import com.netflix.graphql.dgs.DgsMutation;
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

class MutationInterface {
    public static JavaFile generate(Packages packages, FieldDefinition query) {
        return JavaFile.builder(
            packages.interfacePackage(),
            TypeSpec.interfaceBuilder(getClassName(query))
                .addMethod(getMutationMethod(packages, query))
                .build()
        ).build();
    }

    private static String getClassName(FieldDefinition query) {
        return Character.toUpperCase(query.getName().charAt(0)) + query.getName().substring(1) + "Mutation";
    }

    private static MethodSpec getMutationMethod(Packages packages, FieldDefinition query) {
        return MethodSpec.methodBuilder(query.getName())
            .addAnnotation(DgsMutation.class)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(convertType(packages, query.getType()))
            .addParameters(getParameters(packages, query))
            .build();
    }

    private static List<ParameterSpec> getParameters(Packages packages, FieldDefinition query) {
        return query.getInputValueDefinitions().stream()
            .map(def -> getParameter(packages, def))
            .collect(Collectors.toList());
    }

    private static ParameterSpec getParameter(Packages packages, InputValueDefinition valueDefinition) {
        return ParameterSpec.builder(convertType(packages, valueDefinition.getType()), valueDefinition.getName())
            .build();
    }
}
