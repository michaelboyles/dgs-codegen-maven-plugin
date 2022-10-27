package com.github.michaeboyles.dgs.kotlin;

import com.github.michaeboyles.dgs.GqlUtil;
import com.github.michaeboyles.dgs.Packages;
import com.squareup.kotlinpoet.FileSpec;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            .map(def -> QueryInterface.generate(packages, def))
            .collect(Collectors.toList());
    }

    private static List<FileSpec> generateMutationInterfaces(Packages packages, ObjectTypeDefinition queryDef) {
        return queryDef.getFieldDefinitions().stream()
            .map(def -> MutationInterface.generate(packages, def))
            .collect(Collectors.toList());
    }
}
