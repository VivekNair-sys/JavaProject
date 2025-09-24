package edu.ccrm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppConfig {
    private static AppConfig instance;
    private final Path dataDir;

    private AppConfig() {
        dataDir = Paths.get("data");
        try {
            Files.createDirectories(dataDir);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create data dir", e);
        }
    }

    public static synchronized AppConfig getInstance() {
        if (instance == null) instance = new AppConfig();
        return instance;
    }

    public Path getDataDir() { return dataDir; }

    @Override
    public String toString() { return "AppConfig{dataDir=" + dataDir.toAbsolutePath() + "}"; }
}
