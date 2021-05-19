package com.github.michaeboyles.dgs;

import com.netflix.graphql.dgs.codegen.CodeGen;
import com.netflix.graphql.dgs.codegen.CodeGenConfig;
import com.netflix.graphql.dgs.codegen.Language;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "MismatchedQueryAndUpdateOfCollection", "unused","MismatchedReadAndWriteOfArray"})
public class GenerateMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project.build.sourceDirectory}/../resources/schema")
    private File[] schemaPaths = {};

    @Parameter(required = true)
    private String packageName;

    @Parameter(defaultValue = "client")
    private String subPackageNameClient;

    @Parameter(defaultValue = "datafetchers")
    private String subPackageNameDatafetchers;

    @Parameter(defaultValue = "types")
    private String subPackageNameTypes;

    // Gradle plugin also checks this: project.plugins.hasPlugin(KotlinPluginWrapper::class.java)
    // but I don't know the equivalent and it seems good enough
    @Parameter
    private String language = hasKotlinPluginWrapperClass() ? "KOTLIN" : "JAVA";

    @Parameter
    private Map<String, String> typeMapping = new HashMap<>();

    @Parameter
    private boolean generateBoxedTypes = false;

    @Parameter
    private boolean generateClient = false;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/annotations/")
    private File outputDir;

    @Parameter(defaultValue = "${project.build.directory}/generated-examples")
    private File examplesOutputDir;

    @Parameter
    private List<String> includeQueries = new ArrayList<>();

    @Parameter
    private List<String> includeMutations = new ArrayList<>();

    @Parameter
    private boolean skipEntityQueries = false;

    @Parameter
    private boolean shortProjectionNames = false;

    @Parameter
    private boolean generateDataTypes = true;

    @Parameter
    private int maxProjectionDepth = 10;

    @Parameter
    private boolean omitNullInputFields = false;

    @Parameter
    private boolean generateInterfaces = false;

    @Parameter
    private boolean kotlinAllFieldsOptional = false;

    @Parameter
    private boolean snakeCaseConstantNames = false;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() {

        Set<File> schemaPaths = Arrays.stream(this.schemaPaths)
            .map(this::getCanonicalFile)
            .collect(Collectors.toSet());

        for (File schemaPath : schemaPaths) {
            if (!schemaPath.exists()) {
                getLog().warn("Schema location " + schemaPath + " does not exist");
            }
        }

        CodeGenConfig config = new CodeGenConfig(
            Collections.emptySet(),
            schemaPaths,
            getCanonicalFile(outputDir).toPath(),
            getCanonicalFile(examplesOutputDir).toPath(),
            true,
            packageName,
            subPackageNameClient,
            subPackageNameDatafetchers,
            subPackageNameTypes,
            Language.valueOf(language.toUpperCase()),
            generateBoxedTypes,
            generateClient,
            generateInterfaces,
            typeMapping,
            new HashSet<>(includeQueries),
            new HashSet<>(includeMutations),
            skipEntityQueries,
            shortProjectionNames,
            generateDataTypes,
            omitNullInputFields,
            maxProjectionDepth,
            kotlinAllFieldsOptional,
            snakeCaseConstantNames
        );

        getLog().info("Codegen config: " + config);

        new CodeGen(config).generate();
        project.addCompileSourceRoot(getCanonicalFile(outputDir).getAbsolutePath());
    }

    private File getCanonicalFile(File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasKotlinPluginWrapperClass() {
        try {
            getClass().getClassLoader().loadClass("org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}