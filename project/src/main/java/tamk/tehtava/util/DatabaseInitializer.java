package tamk.tehtava.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseInitializer {
    public static void ensureDatabaseExists() {
        String homeDirectory = System.getProperty("user.home");
        Path dbPath = Paths.get(homeDirectory, ".today", "events.sqlite3");
        if (!Files.exists(dbPath)) {
            try (InputStream in = DatabaseInitializer.class.getResourceAsStream("/events.sqlite3")) {
                if (in == null) {
                    System.err.println("Database resource 'events.sqlite3' not found in resources!");
                    return;
                }
                Files.copy(in, dbPath);
                System.out.println("Database copied to " + dbPath.toString());
            } catch (IOException e) {
                System.err.println("Error copying database: " + e.getMessage());
            }
        }
    }
}
