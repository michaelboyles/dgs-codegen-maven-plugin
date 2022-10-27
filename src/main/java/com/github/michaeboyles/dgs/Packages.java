package com.github.michaeboyles.dgs;

public class Packages {
    private final String destinationPackage;
    private final String typesSubPackage;

    public Packages(String destinationPackage, String typesSubPackage) {
        this.destinationPackage = destinationPackage;
        this.typesSubPackage = typesSubPackage;
    }

    public String interfacePackage() {
        return destinationPackage;
    }

    public String typesPackage() {
        return destinationPackage + '.' + typesSubPackage;
    }
}
