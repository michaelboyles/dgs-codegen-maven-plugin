package com.github.michaeboyles.dgs.java;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.reactivestreams.Publisher;

class PublisherTypeWrapper implements TypeWrapper {
    @Override
    public TypeName wrap(TypeName name) {
        return ParameterizedTypeName.get(ClassName.get(Publisher.class), name);
    }
}
