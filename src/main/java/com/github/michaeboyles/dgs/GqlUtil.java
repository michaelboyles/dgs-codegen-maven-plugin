package com.github.michaeboyles.dgs;

import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;

import java.util.Optional;

public class GqlUtil {
    public static Optional<ObjectTypeDefinition> getQuery(Document document) {
        return document.getDefinitionsOfType(ObjectTypeDefinition.class)
            .stream()
            .filter(def -> def.getName().equals("Query"))
            .findAny();
    }

    public static Optional<ObjectTypeDefinition> getMutation(Document document) {
        return document.getDefinitionsOfType(ObjectTypeDefinition.class)
            .stream()
            .filter(def -> def.getName().equals("Mutation"))
            .findAny();
    }
}
