package com.github.michaeboyles.dgs.kotlin;

import com.github.michaeboyles.dgs.GqlUtil;
import com.squareup.kotlinpoet.FileSpec;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public class KotlinInterfaces {
    public static List<FileSpec> generate(Document document, String packageName) {
        List<FileSpec> queryFiles = GqlUtil.getQuery(document)
            .map(def -> generateQueryInterfaces(packageName, def))
            .orElse(emptyList());
        List<FileSpec> mutationFiles = GqlUtil.getMutation(document)
            .map(def -> generateMutationInterfaces(packageName, def))
            .orElse(emptyList());
        return Stream.concat(queryFiles.stream(), mutationFiles.stream())
            .collect(Collectors.toList());
    }

    private static List<FileSpec> generateQueryInterfaces(String packageName, ObjectTypeDefinition queryDef) {
        return queryDef.getFieldDefinitions().stream()
            .map(def -> QueryInterface.generate(packageName, def))
            .collect(Collectors.toList());
    }

    private static List<FileSpec> generateMutationInterfaces(String packageName, ObjectTypeDefinition queryDef) {
        return queryDef.getFieldDefinitions().stream()
            .map(def -> MutationInterface.generate(packageName, def))
            .collect(Collectors.toList());
    }
}
