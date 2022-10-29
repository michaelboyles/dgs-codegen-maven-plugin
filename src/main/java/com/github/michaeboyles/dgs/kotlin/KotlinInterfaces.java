package com.github.michaeboyles.dgs.kotlin;

import com.github.michaeboyles.dgs.GqlUtil;
import com.github.michaeboyles.dgs.Packages;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.squareup.kotlinpoet.FileSpec;
import com.squareup.kotlinpoet.FunSpec;
import com.squareup.kotlinpoet.KModifier;
import com.squareup.kotlinpoet.ParameterSpec;
import com.squareup.kotlinpoet.TypeSpec;
import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.ObjectTypeDefinition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.michaeboyles.dgs.kotlin.TypeUtil.convertType;
import static java.util.Collections.emptyList;

public class KotlinInterfaces {
    public static List<FileSpec> generate(Document document, Packages packages) {
        List<FileSpec> queryFiles = GqlUtil.getQuery(document)
            .map(def -> generateQueryInterfaces(packages, def))
            .orElse(emptyList());
        List<FileSpec> mutationFiles = GqlUtil.getMutation(document)
            .map(def -> generateMutationInterfaces(packages, def))
            .orElse(emptyList());
        return Stream.concat(queryFiles.stream(), mutationFiles.stream())
            .collect(Collectors.toList());
    }

    private static List<FileSpec> generateQueryInterfaces(Packages packages, ObjectTypeDefinition queryDef) {
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generate(packages, def, DgsQuery.class, "Query"))
            .collect(Collectors.toList());
    }

    private static List<FileSpec> generateMutationInterfaces(Packages packages, ObjectTypeDefinition queryDef) {
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generate(packages, def, DgsMutation.class, "Mutation"))
            .collect(Collectors.toList());
    }

    public static FileSpec generate(Packages packages, FieldDefinition queryDef, Class<?> annotation, String suffix) {
        String className = getClassName(queryDef, suffix);
        return FileSpec.builder(packages.interfacePackage(), className)
            .addType(getInterfaceType(packages, queryDef, annotation, suffix))
            .build();
    }

    private static String getClassName(FieldDefinition query, String suffix) {
        return Character.toUpperCase(query.getName().charAt(0)) + query.getName().substring(1) + suffix;
    }

    private static TypeSpec getInterfaceType(Packages packages, FieldDefinition query, Class<?> annotation, String suffix) {
        return TypeSpec.interfaceBuilder(getClassName(query, suffix))
            .addFunction(
                FunSpec.builder(query.getName())
                    .addAnnotation(annotation)
                    .addModifiers(KModifier.PUBLIC, KModifier.ABSTRACT)
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
