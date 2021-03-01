![GitHub Workflow Status](https://img.shields.io/github/workflow/status/michaelboyles/dgs-codegen-maven-plugin/Java%20CI%20with%20Maven) ![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/michaelboyles/dgs-codegen-maven-plugin?sort=semver) ![License](https://img.shields.io/github/license/michaelboyles/dgs-codegen-maven-plugin)

A Maven port of Netflix's [DGS codegen gradle plugin](https://github.com/Netflix/dgs-codegen).
The core code to generate the classes [already exists as its own module](https://github.com/Netflix/dgs-codegen/tree/master/graphql-dgs-codegen-core),
so this is plugin is just a thin wrapper around that.

## Sample Usage

Add the following plugin to your Maven build:

```xml
<plugin>
    <groupId>com.github.michaelboyles</groupId>
    <artifactId>dgs-codegen-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <packageName>com.foo.bar</packageName>
            </configuration>
        </execution>
    </executions>
</plugin>
```

The dependency is available from [jitpack.io](https://jitpack.io/). You can use the following repo declaration:

```xml
<pluginRepository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</pluginRepository>
```

## Goals

### dgs-codegen-maven-plugin:generate

Generates classes for the [DGS framework](https://github.com/Netflix/dgs-framework).

Binds by default to the [lifecycle phase](http://maven.apache.org/ref/3.6.3/maven-core/lifecycles.html): generate-sources. 

**Required Parameters**

| Property    | Type        | Since | Description                                   |
| ----------- | ----------- | ----- | --------------------------------------------- |
| packageName | String      | 1.0.0 | The package name to use for generated classes |

**Optional Parameters**

| Property                   | Type     | Since | Description                                   |
| -------------------------- | -------- | ----- | --------------------------------------------- |
| schemaPaths                | File[]   | 1.0.0 | Path to directory/directories containing GraphQL schemas. Default: `${project.build.sourceDirectory}/../resources/schema` |
| subPackageNameClient       | String   | 1.0.0 | The package under `packageName` to place client classes. Default: `client` |
| subPackageNameDatafetchers | String   | 1.0.0 | The package under `packageName` to place data fetcher classes. Default: `datafetchers` |
| subPackageNameTypes        | String   | 1.0.0 | The package under `packageName` to place types. Default: `types` |
| language                   | String   | 1.0.0 | The programming language that generated classes should use. Valid values are KOTLIN and JAVA. Default is inferred from the classpath |
| typeMapping                | Map      | 1.0.0 | A map from GraphQL type name to Java class name, e.g. for scalars |
| generateBoxedTypes         | boolean  | 1.0.0 | Whether to use boxed types, e.g. `java.lang.Integer`, for non-nullable fields (nullable fields must use boxed types, so that `null` can represent absence of a value). Default: `false` |
| generateClient             | boolean  | 1.0.0 | Whether to generate classes for a GraphQL client. Default: `false` |
| outputDir                  | File     | 1.0.0 | The directory to place generated classes. Default: `${project.build.directory}/generated-sources/annotations/` |
| examplesOutputDir          | File     | 1.0.0 | The directory to place generated examples. Default: `${project.build.directory}/generated-examples` |
| includeQueries             | String[] | 1.0.0 | If present, only generate the queries specified in this list |
| includeMutations           | String[] | 1.0.0 | If present, only generate the mutations in this list |
| skipEntityQueries          | boolean  | 1.0.0 | Whether to skip [entity](https://www.apollographql.com/docs/federation/entities/) queries. Default: `false` |
| shortProjectionNames       | boolean  | 1.0.0 | Whether to shorten projection names. See [`ClassnameShortener`](https://github.com/Netflix/dgs-codegen/blob/master/graphql-dgs-codegen-core/src/main/kotlin/com/netflix/graphql/dgs/codegen/generators/shared/ClassnameShortener.kt). e.g. "ThisIsATest" becomes "ThIsATe". Default: `false` |
