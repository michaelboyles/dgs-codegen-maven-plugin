package com.github.michaeboyles.dgs;

import com.squareup.javapoet.JavaFile;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;
import graphql.parser.MultiSourceReader;
import graphql.parser.Parser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Mojo(name = "generate-interfaces", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateInterfacesMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.sourceDirectory}/../resources/schema")
    private File[] schemaPaths = {};

    @Parameter(required = true)
    private String packageName;

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Set<File> schemaPaths = FileUtil.getSchemas(this.schemaPaths, getLog());
            MultiSourceReader reader = getReader(schemaPaths);
            Document document = new Parser().parseDocument(reader);
            generateForDocument(document);
        }
        catch (IOException e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    private MultiSourceReader getReader(Set<File> schemaPaths) {
        MultiSourceReader.Builder readerBuilder = MultiSourceReader.newMultiSourceReader();
        schemaPaths.stream()
            .flatMap(path -> {
                try {
                    return Files.walk(path.toPath());
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
            .map(Path::toFile)
            .filter(File::isFile)
            .forEach(file -> {
                try {
                    readerBuilder.string(System.lineSeparator(), "codegen");
                    readerBuilder.reader(new InputStreamReader(new FileInputStream(file)), file.getName());
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        return readerBuilder.build();
    }

    private void generateForDocument(Document document) throws IOException {
        Path outputDir = project.getBasedir().toPath().resolve("target/generated-sources/dgs-interfaces");
        List<JavaFile> queryFiles = document.getDefinitionsOfType(ObjectTypeDefinition.class)
            .stream()
            .filter(def -> def.getName().equals("Query"))
            .findAny()
            .map(this::generateQueryInterfaces)
            .orElse(emptyList());
        List<JavaFile> mutationFiles = document.getDefinitionsOfType(ObjectTypeDefinition.class)
            .stream()
            .filter(def -> def.getName().equals("Mutation"))
            .findAny()
            .map(this::generateMutationInterfaces)
            .orElse(emptyList());
        for (JavaFile file : queryFiles) {
            file.writeTo(outputDir);
        }
        for (JavaFile file : mutationFiles) {
            file.writeTo(outputDir);
        }
    }

    private List<JavaFile> generateQueryInterfaces(ObjectTypeDefinition queryDef) {
        GenerateQueryInterface generateQuery = new GenerateQueryInterface();
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generateQuery.generate(packageName, def))
            .collect(Collectors.toList());
    }

    private List<JavaFile> generateMutationInterfaces(ObjectTypeDefinition queryDef) {
        GenerateMutationInterface generateMutation = new GenerateMutationInterface();
        return queryDef.getFieldDefinitions().stream()
            .map(def -> generateMutation.generate(packageName, def))
            .collect(Collectors.toList());
    }
}
