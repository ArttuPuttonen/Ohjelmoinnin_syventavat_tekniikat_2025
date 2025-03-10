package tamk.tehtava.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;

@Command(name = "addevent", description = "Adds a new event to a CSV event provider.")
public class AddEventCommand implements Runnable {

    @Option(names = {"--date"}, description = "Event date. Use full date (YYYY-MM-DD) for singular events or month-day (MM-DD) for annual events.", required = true)
    private String dateStr;

    @Option(names = {"--description"}, description = "Event description.", required = true)
    private String description;

    @Option(names = {"--category"}, description = "Event category.", required = true)
    private String categoryStr;

    // Optional provider identifier; default is "standard"
    @Option(names = {"--provider"}, description = "Event provider identifier. (Default: standard)")
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

        // Prepare the CSV line to append.
        // The expected CSV format is: date,description,category
        // For singular events, we use the full date (YYYY-MM-DD).
        // For annual events, we preserve the "--" prefix so that EventFactory
        // correctly interprets it as a yearless date.
        String csvDate;
        if (isSingular) {
            csvDate = singularDate.toString(); // Format: YYYY-MM-DD
        } else {
            csvDate = annualDate.toString();     // Format: "--MM-DD"
        }
        String csvLine = csvDate + "," + description + "," + categoryStr;

        // Determine which CSV file to append the event to.
        String homeDir = System.getProperty("user.home");
        String filePath;
        if (isSingular) {
            filePath = homeDir + "/.today/singular-events.csv";
        } else {
            filePath = homeDir + "/.today/events.csv";
        }
        // Note: If a provider identifier is specified via --provider, you could choose a different file.
        // For now, we always use the logic above.

        // Append the new event line to the CSV file.
        try (FileWriter fw = new FileWriter(filePath, true)) {
            fw.write(csvLine + "\n");
            System.out.println("Event added to " + filePath + ": " + csvLine);
        } catch (IOException ioe) {
            System.err.println("Error writing to file: " + ioe.getMessage());
        }
    }
}
