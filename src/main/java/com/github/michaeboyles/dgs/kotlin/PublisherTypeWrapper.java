package com.github.michaeboyles.dgs.kotlin;

import com.squareup.kotlinpoet.ClassNames;
import com.squareup.kotlinpoet.ParameterizedTypeName;
import com.squareup.kotlinpoet.TypeName;
import org.reactivestreams.Publisher;

class PublisherTypeWrapper implements TypeWrapper {
    @Override
    public TypeName wrap(TypeName typeName) {
        return ParameterizedTypeName.get(ClassNames.get(Publisher.class), typeName);
    }
}
