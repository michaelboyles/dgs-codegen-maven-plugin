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
