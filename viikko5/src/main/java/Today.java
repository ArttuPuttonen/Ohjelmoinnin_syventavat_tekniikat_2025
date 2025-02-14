import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.MonthDay;
import java.util.Collections;
import java.util.List;

public class Today {
    public static void main(String[] args) {
        // Check if the user provided month and day as command-line arguments
        if (args.length != 2) {
            System.err.println("Usage: java Today <month> <day>");
            System.err.println("Example: java Today 2 10");
            return;
        }


        
        try {
            // Parse month and day from the command-line arguments
            int month = Integer.parseInt(args[0]);
            int day = Integer.parseInt(args[1]);

            // Validate the month and day
            if (month < 1 || month > 12 || day < 1 || day > 31) {
                System.err.println("Invalid month or day. Please provide valid values.");
                return;
            }

            // Define the date to be used from the provided month and day
            final MonthDay monthDay = MonthDay.of(month, day);

            // Let's find out the home directory of the user using the getHomeDirectory method
            String homeDirectory = getHomeDirectory();

            // Define the path to the events file
            Path eventsFilePath = Paths.get(homeDirectory, ".today", "events.csv");

            // Is the file found?
            File eventsFile = eventsFilePath.toFile();
            if (!eventsFile.exists()) {
                System.err.println("Tiedostoa " + eventsFilePath + " ei löytynyt. Lopetetaan ohjelma.");
                return;
            }

            // Create a new CSVEventProvider
            EventProvider provider = new CSVEventProvider(eventsFilePath.toString());



            // Get the events of the date
            List<Event> events = provider.getEventsOfDate(monthDay);
            Collections.sort(events);
            Collections.reverse(events);

            // Print the events
            for (Event event : events) {
                System.out.println(event);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format. Please provide numeric values for month and day.");
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    // Method to get the home directory of the user
    private static String getHomeDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getenv("USERPROFILE");
        } else {
            return System.getenv("HOME");
        }
    }
}
