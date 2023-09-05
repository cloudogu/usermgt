package de.triology.universeadm.user.imports;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResultRepository is a repository to persist results from the csv import. For this, it uses the filesystem
 * of the system. Based on a base path the results are stored as json files with the convention: "<UUID>.json".
 * The UUID represents the ID of the import process.
 */
public class ResultRepository {

    private static final Logger logger = LoggerFactory.getLogger(ResultRepository.class);
    private final String BASE_PATH;
    private final Gson gson = new Gson();

    /**
     * Constructor for the repository
     * @param basePath - root path of the filesystem for the repository
     */
    public ResultRepository(String basePath) {
        BASE_PATH = basePath;
    }

    /**
     * Writes the result in the filesystem
     * @param result - result of the import, see {@link Result}
     * @throws IOException - exception thrown when something is wrong writing the file.
     */
    public void write(Result result) throws IOException {
        String jsonString = gson.toJson(result);
        Files.write(getFilePath(result.getImportID()), jsonString.getBytes());
    }

    /**
     * Reads the result file
     * @param importID - UUID of the import process
     * @return {@link Result}
     * @throws FileNotFoundException - in case file is not found
     */
    public Result read(UUID importID) throws FileNotFoundException {
        Path filePath = this.getFilePath(importID);
        return gson.fromJson(new FileReader(filePath.toString()), Result.class);
    }

    /**
     * Reads all files under the base path. It filters all result files. Others files that don't match the name
     * convention will be ignored.
     * @return List of {@link Result}
     * @throws IOException - exception thrown when something is wrong reading the base path.
     */
    public List<Result> getAllResults() throws IOException {
        return Files.walk(Paths.get(BASE_PATH), 1)
                .filter(Files::isRegularFile)
                .filter(this::isJsonFile)
                .filter(this::containsUUID)
                .map(this::getUUID)
                .map(uuid -> {
                    try {
                        return (this.read(uuid));
                    } catch (FileNotFoundException e) {
                        logger.warn("Excepted Result file with UUID {} cannot be found", uuid);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Path getFilePath(UUID id) {
        String filename = String.format("%s.json", id.toString());
        return Paths.get(BASE_PATH).resolve(filename);
    }

    private boolean isJsonFile(Path path) {
        return path.getFileName().toString().toLowerCase().endsWith(".json");
    }

    private boolean containsUUID(Path path) {
        String baseFilename = this.getBaseFilename(path);

        try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(baseFilename);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String getBaseFilename(Path path) {
        String filename = path.getFileName().toString().toLowerCase();
        String [] filenameParts = filename.split("\\.");

        return filenameParts[0];
    }

    private UUID getUUID(Path path) {
        String baseFileName = this.getBaseFilename(path);

        return UUID.fromString(baseFileName);
    }

}
