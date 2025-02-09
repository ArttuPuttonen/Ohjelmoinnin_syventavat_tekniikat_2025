package viikko4;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class Today {

    private List<Event> events;

    public Today() {
        this.events = new ArrayList<>();
    }

    // The main-method takes two parameters: a date in the format "--mm-dd" and a category
    public static void main(String[] args) {
        // Check that exactly 2 parameters have been provided
        if (args.length != 2) {
            System.err.println("Error: The program must be given two parameters: <--mm-dd> <category>");
            System.exit(1);
        }

        // 1. Process the first parameter: a date without the year in the format "--mm-dd"
        String dateParam = args[0];

        // Verifies the format of the date parameter
        if (!Pattern.matches("^--\\d{2}-\\d{2}$", dateParam)) {
            System.err.println("Error: The date must be in the format \"--mm-dd\", e.g., --01-31");
            System.exit(1);
        }

        try {
            // Format the parameter to extract the month and day
            String mmdd = dateParam.substring(2); // e.g., "mm-dd"
            String[] parts = mmdd.split("-");
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int currentYear = LocalDate.now().getYear();

            // LocalDate object for the criteria date
            LocalDate criteriaDate = LocalDate.of(currentYear, month, day);

            // 2. Process the second parameter: a category in the form "primary/secondary" or just "primary"
            String categoryParam = args[1];
            Category criteriaCategory = parseCategory(categoryParam);

            // Create an instance of Today and load events from storage
            Today app = new Today();
            app.addEvents();

            // Filter events based on the criteria date and category
            List<Event> matchingEvents = app.filterEvents(criteriaDate, criteriaCategory);

            // Sort matching events in descending order by year (newest first)
            Collections.sort(matchingEvents, new Comparator<Event>() {
                @Override
                public int compare(Event e1, Event e2) {
                    return Integer.compare(e2.getDate().getYear(), e1.getDate().getYear());
                }
            });

            // Print the matching events in the format "YYYY: Something happened"
            for (Event event : matchingEvents) {
                System.out.println(event.getDate().getYear() + ": " + event.getDescription());
            }

            // If no matching events, print a message
            if (matchingEvents.isEmpty()) {
                System.out.println("No events found for criteria " + dateParam + " and " + categoryParam);
            }

        } catch (Exception e) {
            System.err.println("Error processing parameters: " + e.getMessage());
            System.exit(1);
        }
    }

     // Helper method to parse a category string into a Category object.
    private static Category parseCategory(String categoryStr) {
        if (categoryStr.contains("/")) {
            String[] parts = categoryStr.split("/", 2);
            return new Category(parts[0], parts[1]);
        } else {
            return new Category(categoryStr, "");
        }
    }

    // Filters the events list based on the criteria date (month and day) and category.
    private List<Event> filterEvents(LocalDate criteriaDate, Category criteriaCategory) {
        List<Event> result = new ArrayList<>();
        for (Event event : events) {
            if (isSameDate(criteriaDate, event.getDate()) && categoryMatches(event.getCategory(), criteriaCategory)) {
                result.add(event);
            }
        }
        return result;
    }

    // Checks if two dates have the same month and day (ignoring the year).
    private boolean isSameDate(LocalDate someDate, LocalDate otherDate) {
        return someDate.getMonth() == otherDate.getMonth()
                && someDate.getDayOfMonth() == otherDate.getDayOfMonth();
    }

    // Checks if the event's category matches the criteria category.
    // If criteriaCategory contains only a primary value (secondary is empty), only the primary values are compared.
    private boolean categoryMatches(Category eventCategory, Category criteriaCategory) {
        if (criteriaCategory.getSecondary() == null || criteriaCategory.getSecondary().trim().isEmpty()) {
            return eventCategory.getPrimary().equalsIgnoreCase(criteriaCategory.getPrimary());
        } else {
            return eventCategory.equals(criteriaCategory);
        }
    }

    // Adds events to the events list. These events simulate a storage source. This is copied from the example in the assignment.
    private void addEvents() {
        List<Event> macOSEvents = new ArrayList<Event>();
        macOSEvents.add(makeEvent("2024-09-16", "macOS 15 Sequoia released", "apple/macos"));
        macOSEvents.add(makeEvent("2023-09-26", "macOS 14 Sonoma released", "apple/macos"));
        macOSEvents.add(makeEvent("2022-10-24", "macOS 13 Ventura released", "apple/macos"));
        macOSEvents.add(makeEvent("2021-10-25", "macOS 12 Monterey released", "apple/macos"));
        macOSEvents.add(makeEvent("2020-11-12", "macOS 11 Big Sur released", "apple/macos"));
        macOSEvents.add(makeEvent("2019-10-07", "macOS 10.15 Catalina released", "apple/macos"));
        macOSEvents.add(makeEvent("2018-09-24", "macOS 10.14 Mojave released", "apple/macos"));
        macOSEvents.add(makeEvent("2017-09-25", "macOS 10.13 High Sierra released","apple/macos"));
        macOSEvents.add(makeEvent("2016-09-20", "macOS 10.12 Sierra released", "apple/macos"));
        macOSEvents.add(makeEvent("2015-09-30", "OS X 10.11 El Capitan released", "apple/macos"));
        macOSEvents.add(makeEvent("2014-10-16", "OS X 10.10 Yosemite released", "apple/macos"));
        macOSEvents.add(makeEvent("2013-10-22", "OS X 10.9 Mavericks released", "apple/macos"));
        macOSEvents.add(makeEvent("2012-07-25", "OS X 10.8 Mountain Lion released", "apple/macos"));
        macOSEvents.add(makeEvent("2011-07-20", "Mac OS X 10.7 Lion released", "apple/macos"));
        macOSEvents.add(makeEvent("2009-08-28", "Mac OS X 10.6 Snow Leopard released", "apple/macos"));
        macOSEvents.add(makeEvent("2007-10-26", "Mac OS X 10.5 Leopard released", "apple/macos"));
        macOSEvents.add(makeEvent("2005-04-29", "Mac OS X 10.4 Tiger released", "apple/macos"));

        // List.of makes an immutable collection
        // (can't add elements).
        List<Event> javaEvents = List.of(
            makeEvent("2023-09-19", "Java SE 21 released", "oracle/java"),
            makeEvent("2023-03-21", "Java SE 20 released", "oracle/java"),
            makeEvent("2022-09-20", "Java SE 19 released", "oracle/java"),
            makeEvent("2022-03-22", "Java SE 18 released", "oracle/java"),
            makeEvent("2021-09-14", "Java SE 17 released", "oracle/java")
        );

        this.events.addAll(macOSEvents);
        this.events.addAll(javaEvents);

        List<String> javaEventRows = List.of(
            "2021-03-16,Java SE 16 released,oracle/java",
            "2020-09-16,Java SE 15 released,oracle/java",
            "2020-03-17,Java SE 14 released,oracle/java",
            "2019-09-17,Java SE 13 released,oracle/java",
            "2019-03-19,Java SE 12 released,oracle/java",
            "2018-09-25,Java SE 11 released,oracle/java",
            "2018-03-20,Java SE 10 released,oracle/java",
            "2017-09-21,Java SE 9 released,oracle/java",
            "2014-03-18,Java SE 8 released,oracle/java",
            "2011-07-28,Java SE 7 released,oracle/java",
            "2006-12-11,Java SE 6 released,oracle/java",
            "2004-09-30,J2SE 5.0 released,oracle/java",
            "2002-02-13,J2SE 1.4 released,oracle/java",
            "2000-05-08,J2SE 1.3 released,oracle/java",
            "1998-12-04,J2SE 1.2 released,oracle/java",
            "1997-02-18,JDK 1.1 released,oracle/java",
            "1996-01-23,JDK 1.0 released,oracle/java");

        for (String row : javaEventRows) {
            this.events.add(makeEvent(row));
        }

        List<String> rustEventRows = List.of(
            "2025-01-09,Rust 1.84.0 released,programming/rust",
            "2024-11-28,Rust 1.83.0 released,programming/rust",
            "2024-10-17,Rust 1.82.0 released,programming/rust",
            "2024-09-05,Rust 1.81.0 released,programming/rust",
            "2024-07-25,Rust 1.80.0 released,programming/rust",
            "2024-06-13,Rust 1.79.0 released,programming/rust",
            "2024-05-02,Rust 1.78.0 released,programming/rust",
            "2024-03-21,Rust 1.77.0 released,programming/rust",
            "2024-02-08,Rust 1.76.0 released,programming/rust",
            "2023-12-28,Rust 1.75.0 released,programming/rust",
            "2023-11-16,Rust 1.74.0 released,programming/rust",
            "2023-10-05,Rust 1.73.0 released,programming/rust",
            "2023-08-24,Rust 1.72.0 released,programming/rust",
            "2023-07-13,Rust 1.71.0 released,programming/rust",
            "2023-06-01,Rust 1.70.0 released,programming/rust",
            "2023-04-20,Rust 1.69.0 released,programming/rust",
            "2023-03-09,Rust 1.68.0 released,programming/rust",
            "2023-01-26,Rust 1.67.0 released,programming/rust");
        for (String row : rustEventRows) {
            this.events.add(makeEvent(row));
        }

        // Add one test event with today's date,
        // so that we can always get at least one match in the report. 
        // This is copied from the example in the assignment.
        this.events.add(
            makeEvent(
                LocalDate.now(), 
                "Test " + LocalDate.now().toString(), 
                new Category("test", "test")));
    }

    // Helper method to make an event from well-defined parts. This is copied from the example in the assignment.
    private Event makeEvent(String dateString, String description, String categoryString) {
        return makeEvent(
            LocalDate.parse(dateString),
            description,
            parseCategory(categoryString));
    }

    private Event makeEvent(LocalDate date, String description, Category category) {
        return new Event(date, description, category);
    }

    private Event makeEvent(String row) {
        String[] parts = row.split(",");
        LocalDate date = LocalDate.parse(parts[0]);
        String description = parts[1];
        String categoryString = parts[2];
        String[] categoryParts = categoryString.split("/");
        String primary = categoryParts[0];
        String secondary = null;
        if (categoryParts.length == 2) {
            secondary = categoryParts[1];
        }
        Category category = new Category(primary, secondary);
        return new Event(date, description, category);
    }
}
