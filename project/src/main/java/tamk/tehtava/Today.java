package tamk.tehtava;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.MonthDay;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import tamk.tehtava.commands.AddEventCommand;
import tamk.tehtava.commands.ListEvents;
import tamk.tehtava.commands.ListProviders;
import tamk.tehtava.providers.CSVEventProvider;
import tamk.tehtava.providers.SQLiteEventProvider;
import tamk.tehtava.providers.web.WebEventProvider;
import tamk.tehtava.util.DatabaseInitializer;  // Import the DatabaseInitializer helper

@Command(name = "today", 
         subcommands = { ListProviders.class, ListEvents.class, AddEventCommand.class },
         description = "Shows events from history and annual observations")
public class Today {
    public Today() {
        // Get the singleton manager. Subsequent calls to getInstance will return the same instance.
        EventManager manager = EventManager.getInstance();

        // Construct paths to local event storage files in the ".today" subdirectory in the user's home directory.
        String homeDirectory = System.getProperty("user.home");
        String configDirectory = ".today";
        Path csvPath = Paths.get(homeDirectory, configDirectory, "events.csv");
        Path sqlitePath = Paths.get(homeDirectory, configDirectory, "events.sqlite3");
        
        // Create the CSV events file if it doesn't exist.
        if (!Files.exists(csvPath)) {
            try {
                Files.createFile(csvPath);
            } catch (IOException e) {
                System.err.println("Unable to create CSV events file: " + e.getMessage());
                System.exit(1);
            }
        }
        
        // Check if the SQLite database file exists.
        // If it does not, the helper method will copy it from resources (or create a new one).
        if (!Files.exists(sqlitePath)) {
            System.out.println("SQLite database file does not exist; it will be created automatically.");
            DatabaseInitializer.ensureDatabaseExists();
        }
        
        // Add a CSV event provider that reads from the CSV file.
        String csvProviderId = "standard";
        manager.addEventProvider(new CSVEventProvider(csvPath.toString(), csvProviderId));
        // Try to add the same CSV provider again to avoid duplicate registration.
        if (!manager.addEventProvider(new CSVEventProvider(csvPath.toString(), csvProviderId))) {
            System.err.printf("Event provider '%s' is already registered%n", csvProviderId);
        }
        
        // Add an SQLite event provider that reads from the SQLite database.
        manager.addEventProvider(new SQLiteEventProvider(sqlitePath.toString()));
        
        // Add a Web event provider that fetches events via HTTP from the given server.
        try {
            URI serverUri = new URI("https://todayserver-89bb2a1b2e80.herokuapp.com/");
            manager.addEventProvider(new WebEventProvider(serverUri));
        } catch (Exception e) {
            System.err.println("Error creating WebEventProvider: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Today()).execute(args);
        System.exit(exitCode);
    }
}
