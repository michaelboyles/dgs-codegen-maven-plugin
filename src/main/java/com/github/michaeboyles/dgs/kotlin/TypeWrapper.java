package com.github.michaeboyles.dgs.kotlin;

import com.squareup.kotlinpoet.TypeName;

public interface TypeWrapper {
    TypeName wrap(TypeName typeName);

    static TypeWrapper none() {
        return name -> name;
    }
}
