package com.github.michaeboyles.dgs.java;

import com.squareup.javapoet.TypeName;

interface TypeWrapper {
    TypeName wrap(TypeName name);

    static TypeWrapper none() {
        return name -> name;
    }
}
