package com.github.michaeboyles.dgs;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FileUtil {
    public static Set<File> getSchemas(File[] paths, Log log) {
        Set<File> schemaPaths = Arrays.stream(paths)
            .map(FileUtil::getCanonicalFile)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        for (File schemaPath : schemaPaths) {
            if (!schemaPath.exists()) {
                log.warn("Schema location " + schemaPath + " does not exist");
            }
        }
        return schemaPaths;
    }

    public static File getCanonicalFile(File file) {
        try {
            return file.getCanonicalFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
