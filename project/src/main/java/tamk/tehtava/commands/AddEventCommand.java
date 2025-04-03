package tamk.tehtava.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tamk.tehtava.EventFactory;
import tamk.tehtava.datamodel.Event;
import tamk.tehtava.providers.SQLiteEventProvider;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;

@Command(name = "addevent", description = "Adds a new event to a CSV or SQLite event provider.")
public class AddEventCommand implements Runnable {

    @Option(names = {"-d", "--date"}, description = "Event date. Use full date (YYYY-MM-DD) for singular events or month-day (MM-DD) for annual events.", required = true)
    private String dateStr;

    @Option(names = {"-D", "--description"}, description = "Event description.", required = true)
    private String description;

    @Option(names = {"-c", "--category"}, description = "Event category.", required = true)
    private String categoryStr;

    // Optional provider identifier; default is "standard" (CSV), "sqlite" indicates database insertion.
    @Option(names = {"p", "--provider"}, description = "Event provider identifier. (Default: standard)")
    private String providerIdentifier = "standard";

    @Override
    public void run() {
        boolean isSingular = false;
        LocalDate singularDate = null;
        MonthDay annualDate = null;
        try {
            // Attempt to parse the full date for singular events.
            singularDate = LocalDate.parse(dateStr);
            isSingular = true;
        } catch (DateTimeParseException e) {
            try {
                // If full date parsing fails, try parsing as MonthDay.
                // MonthDay.parse expects a format like "--MM-DD", so prepend "--" if necessary.
                String mdStr = dateStr.startsWith("--") ? dateStr : "--" + dateStr;
                annualDate = MonthDay.parse(mdStr);
                isSingular = false;
            } catch (DateTimeParseException ex) {
                System.err.println("Invalid date format: " + dateStr);
                return;
            }
        }

        // Build the event using EventFactory.
        String eventDateString;
        if (isSingular) {
            eventDateString = singularDate.toString(); // Format: YYYY-MM-DD
        } else {
            eventDateString = annualDate.toString();     // Format: "--MM-DD"
        }
        // Create the Event. EventFactory will choose the correct type based on the date format.
        Event event = EventFactory.makeEvent(eventDateString, description, categoryStr);

        String homeDir = System.getProperty("user.home");
        // Check which provider to use based on providerIdentifier.
        if (providerIdentifier.equalsIgnoreCase("sqlite")) {
            // For SQLite, insert the event using JDBC.
            String dbPath = homeDir + "/.today/events.sqlite3";
            SQLiteEventProvider sqliteProvider = new SQLiteEventProvider(dbPath);
            boolean success = sqliteProvider.addEvent(event);
            if (success) {
                System.out.println("Event added to SQLite database: " + event);
            } else {
                System.err.println("Failed to add event to SQLite database.");
            }
        } else {
            // Default: add the event to a CSV file.
            String filePath;
            if (isSingular) {
                filePath = homeDir + "/.today/singular-events.csv";
            } else {
                filePath = homeDir + "/.today/events.csv";
            }
            // Prepare the CSV line. For annual events, the date is stored in the same format as created by EventFactory.
            String csvLine = eventDateString + "," + description + "," + categoryStr;
            try (FileWriter fw = new FileWriter(filePath, true)) {
                fw.write(csvLine + "\n");
                System.out.println("Event added to CSV file " + filePath + ": " + csvLine);
            } catch (IOException ioe) {
                System.err.println("Error writing to CSV file: " + ioe.getMessage());
            }
        }
    }
}
