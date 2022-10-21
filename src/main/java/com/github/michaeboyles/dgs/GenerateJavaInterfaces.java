package com.github.michaeboyles.dgs;

import com.squareup.javapoet.JavaFile;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;

public class GenerateJavaInterfaces {
    public static List<JavaFile> generateJava(Document document, String packageName) throws IOException {
        List<JavaFile> queryFiles = document.getDefinitionsOfType(ObjectTypeDefinition.class)
            .stream()
            .filter(def -> def.getName().equals("Query"))
            .findAny()
            .map(def -> generateQueryInterfaces(packageName, def))
            .orElse(emptyList());
        List<JavaFile> mutationFiles = document.getDefinitionsOfType(ObjectTypeDefinition.class)
            .stream()
            .filter(def -> def.getName().equals("Mutation"))
            .findAny()
            .map(def -> generateMutationInterfaces(packageName, def))
            .orElse(emptyList());
        return Stream.concat(queryFiles.stream(), mutationFiles.stream())
            .collect(Collectors.toList());
    }

    private static List<JavaFile> generateQueryInterfaces(String packageName, ObjectTypeDefinition queryDef) {
        GenerateQueryInterface generateQuery = new GenerateQueryInterface();
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generateQuery.generate(packageName, def))
            .collect(Collectors.toList());
    }

    private static List<JavaFile> generateMutationInterfaces(String packageName, ObjectTypeDefinition queryDef) {
        GenerateMutationInterface generateMutation = new GenerateMutationInterface();
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generateMutation.generate(packageName, def))
            .collect(Collectors.toList());
    }
}
