package com.github.michaeboyles.dgs.java;

import com.github.michaeboyles.dgs.GqlUtil;
import com.github.michaeboyles.dgs.Packages;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.DgsSubscription;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.michaeboyles.dgs.java.TypeUtil.convertType;

public class JavaInterfaces {
    public static List<JavaFile> generate(Document document, Packages packages) {
        return Stream.of(
                GqlUtil.getQuery(document)
                    .map(def -> generateQueryInterfaces(packages, def)),
                GqlUtil.getMutation(document)
                    .map(def -> generateMutationInterfaces(packages, def)),
                GqlUtil.getSubscription(document)
                    .map(def -> generateSubscriptionInterfaces(packages, def))
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private static List<JavaFile> generateQueryInterfaces(Packages packages, ObjectTypeDefinition queryDef) {
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generate(packages, def, DgsQuery.class, "Query", TypeWrapper.none()))
            .collect(Collectors.toList());
    }

    private static List<JavaFile> generateMutationInterfaces(Packages packages, ObjectTypeDefinition mutationDef) {
        return mutationDef.getFieldDefinitions().stream()
            .map(def -> generate(packages, def, DgsMutation.class, "Mutation", TypeWrapper.none()))
            .collect(Collectors.toList());
    }

    private static List<JavaFile> generateSubscriptionInterfaces(Packages packages, ObjectTypeDefinition subscriptionDef) {
        return subscriptionDef.getFieldDefinitions().stream()
            .map(def -> generate(packages, def, DgsSubscription.class, "Subscription", new PublisherTypeWrapper()))
            .collect(Collectors.toList());
    }

    private static JavaFile generate(Packages packages, FieldDefinition fieldDef, Class<?> annotation, String suffix,
                                     TypeWrapper typeWrapper) {
        return JavaFile.builder(
            packages.interfacePackage(),
            TypeSpec.interfaceBuilder(getClassName(fieldDef, suffix))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getInterfaceMethod(packages, fieldDef, annotation, typeWrapper))
                .build()
        ).build();
    }

    private static String getClassName(FieldDefinition query, String suffix) {
        return Character.toUpperCase(query.getName().charAt(0)) + query.getName().substring(1) + suffix;
    }

    private static MethodSpec getInterfaceMethod(Packages packages, FieldDefinition query, Class<?> annotation,
                                                 TypeWrapper typeWrapStrategy) {
        return MethodSpec.methodBuilder(query.getName())
            .addAnnotation(annotation)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .returns(typeWrapStrategy.wrap(convertType(packages, query.getType())))
            .addParameters(getParameters(packages, query))
            .build();
    }

    private static List<ParameterSpec> getParameters(Packages packages, FieldDefinition fieldDef) {
        return fieldDef.getInputValueDefinitions().stream()
            .map(def -> getParameter(packages, def))
            .collect(Collectors.toList());
    }

    private static ParameterSpec getParameter(Packages packages, InputValueDefinition valueDefinition) {
        return ParameterSpec.builder(convertType(packages, valueDefinition.getType()), valueDefinition.getName())
            .build();
    }
}
