package edu.ccrm.util;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class FileUtils {

    public static Path backupFolder(Path sourceDir) throws IOException {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Path dest = sourceDir.resolveSibling("backup-" + ts);
        Files.createDirectories(dest);
        try (Stream<Path> stream = Files.walk(sourceDir)) {
            stream.filter(Files::isRegularFile).forEach(p -> {
                try {
                    Path rel = sourceDir.relativize(p);
                    Path target = dest.resolve(rel);
                    Files.createDirectories(target.getParent());
                    Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
        return dest;
    }

    public static long recursiveSize(Path dir) throws IOException {
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream.filter(Files::isRegularFile).mapToLong(p -> {
                try { return Files.size(p); } catch (Exception e) { return 0L; }
            }).sum();
        }
    }
}
