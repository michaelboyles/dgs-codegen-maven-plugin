package com.github.michaeboyles.dgs.java;

import com.github.michaeboyles.dgs.GqlUtil;
import com.github.michaeboyles.dgs.Packages;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.ObjectTypeDefinition;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.michaeboyles.dgs.java.TypeUtil.convertType;
import static java.util.Collections.emptyList;

public class JavaInterfaces {
    public static List<JavaFile> generate(Document document, Packages packages) {
        List<JavaFile> queryFiles = GqlUtil.getQuery(document)
            .map(def -> generateQueryInterfaces(packages, def))
            .orElse(emptyList());
        List<JavaFile> mutationFiles = GqlUtil.getMutation(document)
            .map(def -> generateMutationInterfaces(packages, def))
            .orElse(emptyList());
        return Stream.concat(queryFiles.stream(), mutationFiles.stream())
            .collect(Collectors.toList());
    }

    private static List<JavaFile> generateQueryInterfaces(Packages packages, ObjectTypeDefinition queryDef) {
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generate(packages, def, DgsQuery.class, "Query"))
            .collect(Collectors.toList());
    }

    private static List<JavaFile> generateMutationInterfaces(Packages packages, ObjectTypeDefinition queryDef) {
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generate(packages, def, DgsMutation.class, "Mutation"))
            .collect(Collectors.toList());
    }

    private static JavaFile generate(Packages packages, FieldDefinition query, Class<?> annotation, String suffix) {
        return JavaFile.builder(
            packages.interfacePackage(),
            TypeSpec.interfaceBuilder(getClassName(query, suffix))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getMutationMethod(packages, query, annotation))
                .build()
        ).build();
    }

    private static String getClassName(FieldDefinition query, String suffix) {
        return Character.toUpperCase(query.getName().charAt(0)) + query.getName().substring(1) + suffix;
    }

    private static MethodSpec getMutationMethod(Packages packages, FieldDefinition query, Class<?> annotation) {
        return MethodSpec.methodBuilder(query.getName())
            .addAnnotation(annotation)
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
