package com.team7.hboict.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper-class used for traversing a directory structure which contains the Dutch election data.
 */
public class PathUtils {

    /**
     * Starting from @{code sourceLocation} searches the folder and any folder contained in it for files with the
     * specified {@code prefix}.
     */
    public static List<Path> findFilesToScan(String sourceLocation, String prefix) throws IOException {

        if (sourceLocation == null) {
            throw new IllegalArgumentException("sourceLocation is null -> controleer folderName bij readResults()");
        }

        List<Path> filesToScan = new ArrayList<>();

        Files.walkFileTree(Path.of(sourceLocation), new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                String fileName = file.getFileName().toString();

                if (fileName.startsWith(prefix) && fileName.endsWith(".xml")) {
                    filesToScan.add(file);
                }

                return FileVisitResult.CONTINUE;
            }

        });

        return filesToScan;
    }


    /**
     * Finds the correct absolute path to your electiondata folder.
     */
    public static String getResourcePath(String resourceName) {

        if (resourceName == null) {
            return null;
        }

        // verwijder voorste slash
        while (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }

        try {

            /*
             1️⃣ direct relatief pad vanaf project root
             bv:
             electiondata/TK2025
             */
            Path directPath = Path.of(resourceName);

            if (Files.exists(directPath)) {
                return directPath.toAbsolutePath().toString();
            }

            if (directPath.toFile().isAbsolute() && Files.exists(directPath)) {
                return directPath.toAbsolutePath().toString();
            }


            URL url = PathUtils.class.getResource("/" + resourceName);

            if (url != null) {
                return new File(url.toURI()).getPath();
            }



            url = PathUtils.class.getResource("/");

            if (url == null) {
                return null;
            }

            URI projectRootURI = url.toURI();

            String resourceFilePath = null;

            while (resourceFilePath == null && projectRootURI.getPath().length() > 15) {

                projectRootURI = projectRootURI.resolve("..");

                // data-files map
                String dataFilesPath =
                        new File(projectRootURI.resolve("data-files/").resolve(resourceName)).getPath();

                if (Files.exists(Path.of(dataFilesPath))) {
                    resourceFilePath = dataFilesPath;
                }

                // electiondata map
                if (resourceFilePath == null) {

                    String electionDataPath =
                            new File(projectRootURI.resolve(resourceName)).getPath();

                    if (Files.exists(Path.of(electionDataPath))) {
                        resourceFilePath = electionDataPath;
                    }

                }

                // Downloads map
                if (resourceFilePath == null) {

                    String downloadsPath =
                            new File(projectRootURI.resolve("Downloads/").resolve(resourceName)).getPath();

                    if (Files.exists(Path.of(downloadsPath))) {
                        resourceFilePath = downloadsPath;
                    }

                }

            }

            return resourceFilePath;

        } catch (URISyntaxException e) {

            throw new RuntimeException(e);

        }

    }

}