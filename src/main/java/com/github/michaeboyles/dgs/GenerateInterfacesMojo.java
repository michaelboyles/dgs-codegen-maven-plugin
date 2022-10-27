package com.github.michaeboyles.dgs;

import com.github.michaeboyles.dgs.java.JavaInterfaces;
import com.github.michaeboyles.dgs.kotlin.KotlinInterfaces;
import com.netflix.graphql.dgs.codegen.Language;
import com.squareup.javapoet.JavaFile;
import com.squareup.kotlinpoet.FileSpec;
import graphql.language.Document;
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

import static com.github.michaeboyles.dgs.LanguageUtil.isProbablyKotlin;

@Mojo(name = "generate-interfaces", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateInterfacesMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.sourceDirectory}/../resources/schema")
    private File[] schemaPaths = {};

    @Parameter(required = true)
    private String packageName;

    @Parameter(defaultValue = "types")
    private String subPackageNameTypes;

    @Parameter
    private String language = isProbablyKotlin() ? "KOTLIN" : "JAVA";

    @Parameter(readonly = true, defaultValue = "${project}")
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/annotations/")
    private File outputDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            Set<File> schemaPaths = FileUtil.getSchemas(this.schemaPaths, getLog());
            MultiSourceReader reader = getReader(schemaPaths);
            Document document = new Parser().parseDocument(reader);
            generateForDocument(document);
            project.addCompileSourceRoot(outputDir.toString());
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
        if (Language.JAVA.name().equalsIgnoreCase(language)) {
            List<JavaFile> files = JavaInterfaces.generate(document, getPackages());
            for (JavaFile file : files) {
                file.writeTo(outputDir);
            }
        }
        else {
            List<FileSpec> files = KotlinInterfaces.generate(document, getPackages());
            for (FileSpec file : files) {
                file.writeTo(outputDir);
            }
        }
    }

    private Packages getPackages() {
        return new Packages(packageName, subPackageNameTypes);
    }
}
