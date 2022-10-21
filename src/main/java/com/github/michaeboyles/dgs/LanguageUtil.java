package com.github.michaeboyles.dgs;

public class LanguageUtil {
    public static boolean isProbablyKotlin() {
        try {
            // Gradle plugin also checks this: project.plugins.hasPlugin(KotlinPluginWrapper::class.java)
            // but I don't know the equivalent and it seems good enough
            LanguageUtil.class.getClassLoader().loadClass("org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
